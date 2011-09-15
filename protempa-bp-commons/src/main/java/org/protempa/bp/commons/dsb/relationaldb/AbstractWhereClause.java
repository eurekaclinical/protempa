package org.protempa.bp.commons.dsb.relationaldb;

import static org.arp.javautil.collections.Collections.containsAny;
import static org.protempa.bp.commons.dsb.relationaldb.SqlGeneratorUtil.appendValue;
import static org.protempa.bp.commons.dsb.relationaldb.SqlGeneratorUtil.appendColumnRef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arp.javautil.arrays.Arrays;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.backend.dsb.filter.PositionFilter;
import org.protempa.backend.dsb.filter.PositionFilter.Side;
import org.protempa.backend.dsb.filter.PropertyValueFilter;
import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.Constraint;
import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.KnowledgeSourceIdToSqlCode;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.DateValue;
import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.OrdinalValue;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueList;
import org.protempa.proposition.value.ValueVisitor;

abstract class AbstractWhereClause implements WhereClause {
    private final Set<String> propIds;
    private final ColumnSpecInfo info;
    private final List<EntitySpec> entitySpecs;
    private final Set<Filter> filters;
    private final Map<ColumnSpec, Integer> referenceIndices;
    private final Set<String> keyIds;
    private final SQLOrderBy order;
    private final SQLGenResultProcessor resultProcessor;
    private final SelectClause selectClause;
    private final SqlStatement stmt;

    protected AbstractWhereClause(Set<String> propIds, ColumnSpecInfo info,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            Map<ColumnSpec, Integer> referenceIndices, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor, SelectClause selectClause,
            SqlStatement stmt) {
        this.propIds = propIds;
        this.info = info;
        this.entitySpecs = Collections.unmodifiableList(entitySpecs);
        this.filters = Collections.unmodifiableSet(filters);
        this.referenceIndices = Collections.unmodifiableMap(referenceIndices);
        this.keyIds = Collections.unmodifiableSet(keyIds);
        this.order = order;
        this.resultProcessor = resultProcessor;
        this.selectClause = selectClause;
        this.stmt = stmt;
    }

    public abstract InClause getInClause(int tableNumber, String columnName,
            Object[] elements, boolean not);

    public abstract CaseClause getCaseClause(Object[] sqlCodes,
            Map<ColumnSpec, Integer> referenceIndices, ColumnSpec columnSpec,
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues);

    public abstract OrderByClause getOrderByClause(int startReferenceIndex,
            String startColumn, int finishReferenceIndex, String finishColumn,
            SQLOrderBy order);

    public String generateClause() {
        StringBuilder wherePart = new StringBuilder();

        EntitySpec prevEntitySpec = null;
        boolean inGroup = false;
        boolean first = true;
        for (int j = 0, n = entitySpecs.size(); j < n; j++) {
            EntitySpec entitySpec = entitySpecs.get(j);
            if (n > 1 && j > 0) {
                if (prevEntitySpec.getName().equals(entitySpec.getName())) {
                    if (!inGroup) {
                        if (!first) {
                            wherePart.append(" and ");
                            first = true;
                        }
                        wherePart.append(" ((");
                        inGroup = true;
                    } else {
                        wherePart.append(") or (");
                        first = true;
                    }
                    int wherePartLength = wherePart.length();
                    wherePart.append(processForWhereClause(prevEntitySpec,
                            filters, referenceIndices, propIds,
                            resultProcessor, first));
                    if (wherePart.length() > wherePartLength) {
                        first = false;
                    }
                } else {
                    if (inGroup) {
                        first = true;
                        int wherePartLength = wherePart.length();
                        wherePart.append(") or (");
                        wherePart.append(processForWhereClause(prevEntitySpec,
                                filters, referenceIndices, propIds,
                                resultProcessor, first));
                        wherePart.append(")) ");
                        if (wherePart.length() > wherePartLength) {
                            first = false;
                        }
                        inGroup = false;
                    } else {
                        int wherePartLength = wherePart.length();
                        wherePart.append(processForWhereClause(prevEntitySpec,
                                filters, referenceIndices, propIds,
                                resultProcessor, first));
                        if (wherePart.length() > wherePartLength) {
                            first = false;
                        }
                    }

                }

            }
            prevEntitySpec = entitySpec;
        }
        if (inGroup) {
            first = true;
            wherePart.append(") or (");
            wherePart.append(processForWhereClause(prevEntitySpec, filters,
                    referenceIndices, propIds, resultProcessor,
                    first));
            wherePart.append(")) ");
        } else {
            wherePart.append(processForWhereClause(prevEntitySpec, filters,
                    referenceIndices, propIds, resultProcessor,
                    first));
        }

        processKeyIdConstraintsForWhereClause(info, wherePart, keyIds);

        if (wherePart.length() > 0) {
            wherePart.insert(0, "where ");
        }

        wherePart.append(processOrder(order, info, referenceIndices));

        return wherePart.toString();
    }

    private void processKeyIdConstraintsForWhereClause(ColumnSpecInfo info,
            StringBuilder wherePart, Set<String> keyIds) {
        if (keyIds != null && !keyIds.isEmpty()) {
            if (wherePart.length() > 0) {
                wherePart.append(" and ");
            }
            ColumnSpec keySpec = info.getColumnSpecs().get(0);

            wherePart.append(getInClause(1, keySpec.getColumn(),
                    keyIds.toArray(), false).toString());
        }
    }

    private String processForWhereClause(EntitySpec prevEntitySpec,
            Set<Filter> filtersCopy, Map<ColumnSpec, Integer> referenceIndices,
            Set<String> propIds,
            SQLGenResultProcessor resultProcessor, boolean first) {
        StringBuilder wherePart = new StringBuilder();
        int wherePartLength = wherePart.length();
        wherePart.append(processStartTimeSpecForWhereClause(prevEntitySpec,
                filtersCopy, referenceIndices, first));
        if (wherePart.length() > wherePartLength) {
            first = false;
        }
        wherePartLength = wherePart.length();
        wherePart.append(processFinishTimeSpecForWhereClause(prevEntitySpec,
                filtersCopy, referenceIndices, first));
        if (wherePart.length() > wherePartLength) {
            first = false;
        }
        wherePartLength = wherePart.length();
        if (wherePart.length() > wherePartLength) {
            first = false;
        }
        wherePartLength = wherePart.length();
        wherePart.append(processConstraintSpecsForWhereClause(propIds,
                prevEntitySpec, referenceIndices, filtersCopy,
                resultProcessor, first));

        return wherePart.toString();
    }

    private int processKeySpecForWhereClause(EntitySpec propositionSpec, int i) {
        ColumnSpec baseSpec = propositionSpec.getBaseSpec();
        i += baseSpec.asList().size();
        return i;
    }

    private static int processPropertyValueSpecsForWhereClause(
            EntitySpec entitySpec, int i) {
        PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
        Map<String, Integer> propertyValueIndices = new HashMap<String, Integer>();
        for (PropertySpec propertySpec : propertySpecs) {
            ColumnSpec spec = propertySpec.getSpec();
            if (spec != null) {
                i += spec.asList().size();
                propertyValueIndices.put(propertySpec.getName(), i);
            }
        }
        return i;
    }

    private String processConstraintSpecsForWhereClause(Set<String> propIds,
            EntitySpec entitySpec,
            Map<ColumnSpec, Integer> referenceIndices, Set<Filter> filtersCopy,
            SQLGenResultProcessor resultProcessor, boolean first) {

        Logger logger = SQLGenUtil.logger();
        logger.log(Level.FINER,
                "Processing constraint specs for entity spec {0}",
                entitySpec.getName());
        logger.log(Level.FINEST, "Details of entity spec {0}", entitySpec);

        StringBuilder wherePart = new StringBuilder();
        ColumnSpec[] constraintSpecs = entitySpec.getConstraintSpecs();
        for (ColumnSpec constraintSpec : constraintSpecs) {
            int wherePartLength = wherePart.length();
            wherePart.append(processConstraintSpecForWhereClause(null,
                    constraintSpec, referenceIndices, null, first));
            if (wherePart.length() > wherePartLength) {
                first = false;
            }
        }

        for (Filter filter : filtersCopy) {
            for (PropertySpec ps : entitySpec.getPropertySpecs()) {
                if (filter instanceof PropertyValueFilter) {
                    PropertyValueFilter pvf = (PropertyValueFilter) filter;

                    if (pvf.getProperty().equals(ps.getName())) {
                        ColumnSpec colSpec = ps.getSpec();
                        int wherePartLength = wherePart.length();
                        wherePart.append(processPropertyValueFilter(colSpec,
                                referenceIndices, pvf.getValueComparator(),
                                pvf.getValues(), first));
                        if (wherePart.length() > wherePartLength) {
                            first = false;
                        }

                        break;
                    }
                }
            }
        }

        // If propIds are all in the entity spec's prop ids, then
        // skip the code part of the where clause.
        ColumnSpec codeSpec = entitySpec.getCodeSpec();
        if (codeSpec != null) {
            List<ColumnSpec> codeSpecL = codeSpec.asList();
            if (codeSpecL.get(codeSpecL.size() - 1).isPropositionIdsComplete()
                    && !needsPropIdInClause(propIds,
                            entitySpec.getPropositionIds())) {
                wherePart.append(processConstraintSpecForWhereClause(propIds,
                        codeSpec, referenceIndices,
                        resultProcessor, first));
            } else {
                int wherePartLength = wherePart.length();
                wherePart.append(processConstraintSpecForWhereClause(propIds,
                        codeSpec, referenceIndices,
                        resultProcessor, first));
                if (wherePart.length() > wherePartLength) {
                    first = false;
                }
            }
        }

        return wherePart.toString();
    }

    private String processConstraintSpecForWhereClause(Set<String> propIds,
            ColumnSpec columnSpec,
            Map<ColumnSpec, Integer> referenceIndices,
            SQLGenResultProcessor resultProcessor, boolean first) {

        StringBuilder wherePart = new StringBuilder();
        if (columnSpec != null) {
            List<ColumnSpec> columnSpecL = columnSpec.asList();
            columnSpec = columnSpecL.get(columnSpecL.size() - 1);
            if (columnSpec.getConstraint() != null) {
                if (resultProcessor != null) {
                    resultProcessor
                            .setCasePresent(columnSpec.getConstraint() == ColumnSpec.Constraint.LIKE);
                }
                wherePart.append(processConstraint(columnSpec, propIds,
                        referenceIndices, null, first));
            }
        }
        return wherePart.toString();
    }

    private String processPropertyValueFilter(ColumnSpec columnSpec,
            Map<ColumnSpec, Integer> referenceIndices,
            ValueComparator comparator, Value[] values, boolean first) {

        StringBuilder wherePart = new StringBuilder();
        List<ColumnSpec> columnSpecL = columnSpec.asList();
        ColumnSpec lastColumnSpec = columnSpecL.get(columnSpecL.size() - 1);

        Constraint constraint = valueComparatorToSqlOp(comparator);

        if (columnSpec != null && constraint != null) {
            ValueExtractor ve = new ValueExtractor();
            for (Value value : values) {
                value.accept(ve);
            }
            wherePart.append(processConstraint(lastColumnSpec, ve.values,
                    referenceIndices, constraint, first));
        }

        return wherePart.toString();
    }

    private static Constraint valueComparatorToSqlOp(
            ValueComparator valueComparator) throws IllegalStateException {
        ColumnSpec.Constraint constraint = null;
        switch (valueComparator) {
            case GREATER_THAN:
                constraint = ColumnSpec.Constraint.GREATER_THAN;
                break;
            case LESS_THAN:
                constraint = ColumnSpec.Constraint.LESS_THAN;
                break;
            case EQUAL_TO:
                constraint = ColumnSpec.Constraint.EQUAL_TO;
                break;
            case GREATER_THAN_OR_EQUAL_TO:
                constraint = ColumnSpec.Constraint.GREATER_THAN_OR_EQUAL_TO;
                break;
            case LESS_THAN_OR_EQUAL_TO:
                constraint = ColumnSpec.Constraint.LESS_THAN_OR_EQUAL_TO;
                break;
            case IN:
                constraint = ColumnSpec.Constraint.EQUAL_TO;
                break;
            case NOT_IN:
                constraint = ColumnSpec.Constraint.NOT_EQUAL_TO;
                break;
            default:
                throw new AssertionError("invalid valueComparator: "
                        + valueComparator);
        }
        return constraint;
    }

    private static KnowledgeSourceIdToSqlCode[] filterKnowledgeSourceIdToSqlCodesById(
            Set<?> propIds, KnowledgeSourceIdToSqlCode[] constraintValues) {
        ColumnSpec.KnowledgeSourceIdToSqlCode[] filteredConstraintValues;
        if (propIds != null) {
            List<ColumnSpec.KnowledgeSourceIdToSqlCode> constraintValueList = new ArrayList<ColumnSpec.KnowledgeSourceIdToSqlCode>();
            for (ColumnSpec.KnowledgeSourceIdToSqlCode constraintValue : constraintValues) {
                if (propIds.contains(constraintValue.getPropositionId())) {
                    constraintValueList.add(constraintValue);
                }
            }
            if (constraintValueList.isEmpty()) {
                filteredConstraintValues = constraintValues;
            } else {
                filteredConstraintValues = constraintValueList
                        .toArray(new ColumnSpec.KnowledgeSourceIdToSqlCode[constraintValueList
                                .size()]);
            }
        } else {
            filteredConstraintValues = constraintValues;
        }
        return filteredConstraintValues;
    }

    private String processConstraint(ColumnSpec columnSpec, Set<?> propIds,
            Map<ColumnSpec, Integer> referenceIndices, Constraint constraintOverride,
            boolean first) {
        StringBuilder wherePart = new StringBuilder();
        Constraint constraint = columnSpec.getConstraint();
        if (constraintOverride != null) {
            constraint = constraintOverride;
        }
        ColumnSpec.KnowledgeSourceIdToSqlCode[] propIdToSqlCodes = columnSpec
                .getPropositionIdToSqlCodes();
        if (constraint != null) {
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues = filterKnowledgeSourceIdToSqlCodesById(
                    propIds, propIdToSqlCodes);
            if (!first) {
                wherePart.append(" and ");
            }
            wherePart.append('(');
            Object[] sqlCodes = null;
            if (filteredConstraintValues.length > 0) {
                sqlCodes = extractSqlCodes(filteredConstraintValues);
            } else {
                sqlCodes = propIds.toArray();
            }

            switch (constraint) {
                case EQUAL_TO:
                    if (sqlCodes.length > 1) {
                        wherePart.append(getInClause(
                                referenceIndices.get(columnSpec),
                                columnSpec.getColumn(), sqlCodes, false)
                                .generateClause());
                    } else {
                        assert sqlCodes.length == 1 : "invalid sqlCodes length";
                        wherePart.append(appendColumnRef(stmt,
                                referenceIndices, columnSpec));
                        wherePart.append(constraint.getSqlOperator());
                        wherePart.append(SqlGeneratorUtil
                                .appendValue(sqlCodes[0]));
                    }
                    break;
                case LESS_THAN:
                case LESS_THAN_OR_EQUAL_TO:
                case GREATER_THAN:
                case GREATER_THAN_OR_EQUAL_TO:
                    wherePart.append(appendColumnRef(stmt, referenceIndices,
                            columnSpec));
                    wherePart.append(constraint.getSqlOperator());
                    wherePart.append(appendValue(sqlCodes[0]));
                    break;
                case NOT_EQUAL_TO:
                    if (sqlCodes.length > 1) {
                        wherePart.append(getInClause(
                                referenceIndices.get(columnSpec),
                                columnSpec.getColumn(), sqlCodes, true)
                                .generateClause());
                    } else {
                        wherePart.append(appendColumnRef(stmt,
                                referenceIndices, columnSpec));
                        wherePart.append(constraint.getSqlOperator());
                        wherePart.append(appendValue(sqlCodes[0]));
                    }
                    break;
                case LIKE:
                    if (this.selectClause != null) {
                        this.selectClause.setCaseClause(getCaseClause(sqlCodes,
                                referenceIndices, columnSpec,
                                filteredConstraintValues));
                    }
                    if (sqlCodes.length > 1) {
                        wherePart.append('(');
                    }
                    for (int k = 0; k < sqlCodes.length; k++) {
                        wherePart.append(appendColumnRef(stmt,
                                referenceIndices, columnSpec));
                        wherePart.append(" LIKE ");
                        wherePart.append(appendValue(sqlCodes[k]));
                        if (k + 1 < sqlCodes.length) {
                            wherePart.append(" or ");
                        }
                    }
                    if (sqlCodes.length > 1) {
                        wherePart.append(')');
                    }

                    break;
                default:
                    throw new AssertionError("should not happen");
            }
            wherePart.append(')');
        }

        return wherePart.toString();
    }

    private Object[] extractSqlCodes(
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues) {
        Object[] sqlCodes = new Object[filteredConstraintValues.length];
        for (int i = 0; i < sqlCodes.length; i++) {
            sqlCodes[i] = filteredConstraintValues[i].getSqlCode();
        }
        return sqlCodes;
    }

    /**
     * Returns whether an IN clause containing the proposition ids of interest
     * should be added to the WHERE clause.
     * 
     * @param queryPropIds
     *            the proposition ids to query.
     * @param entitySpecPropIds
     *            the proposition ids corresponding to the current entity spec.
     * @return <code>true</code> if the query contains < 85% of the proposition
     *         ids that are known to the data source and if the where clause
     *         would contain less than or equal to 2000 codes.
     */
    static boolean needsPropIdInClause(Set<String> queryPropIds,
            String[] entitySpecPropIds) {

        Set<String> entitySpecPropIdsSet = Arrays.asSet(entitySpecPropIds);

        // Filter propIds that are not in the entitySpecPropIds array.
        List<String> filteredPropIds = new ArrayList<String>(
                entitySpecPropIds.length);
        for (String propId : queryPropIds) {
            if (entitySpecPropIdsSet.contains(propId)) {
                filteredPropIds.add(propId);
            }
        }
        return (filteredPropIds.size() < entitySpecPropIds.length * 0.85f)
                && (filteredPropIds.size() <= 2000);
    }

    private String processStartTimeSpecForWhereClause(EntitySpec entitySpec,
            Set<Filter> filtersCopy, Map<ColumnSpec, Integer> referenceIndices,
            boolean first) {
        StringBuilder wherePart = new StringBuilder();
        ColumnSpec startTimeSpec = entitySpec.getStartTimeSpec();
        if (startTimeSpec != null) {
            while (true) {
                if (startTimeSpec.getJoin() != null) {
                    startTimeSpec = startTimeSpec.getJoin().getNextColumnSpec();
                } else {
                    for (Iterator<Filter> itr = filtersCopy.iterator(); itr
                            .hasNext();) {
                        Filter filter = itr.next();
                        if (filter instanceof PositionFilter) {
                            Set<String> entitySpecPropIds = org.arp.javautil.arrays.Arrays
                                    .asSet(entitySpec.getPropositionIds());
                            if (containsAny(entitySpecPropIds,
                                    filter.getPropositionIds())) {
                                PositionFilter pdsc2 = (PositionFilter) filter;

                                boolean outputStart = pdsc2.getMinimumStart() != null
                                        && (pdsc2.getStartSide() == Side.START || entitySpec
                                                .getFinishTimeSpec() == null);
                                boolean outputFinish = pdsc2.getMaximumFinish() != null
                                        && (pdsc2.getFinishSide() == Side.START || entitySpec
                                                .getFinishTimeSpec() == null);

                                if (outputStart) {
                                    if (!first) {
                                        wherePart.append(" and ");
                                    }
                                    wherePart.append(appendColumnRef(stmt,
                                            referenceIndices, startTimeSpec));
                                    wherePart.append(" >= ");
                                    wherePart.append(entitySpec
                                            .getPositionParser().format(
                                                    pdsc2.getMinimumStart()));
                                }
                                if (outputFinish) {
                                    if (!first || outputStart) {
                                        wherePart.append(" and ");
                                    }
                                    wherePart.append(appendColumnRef(stmt,
                                            referenceIndices, startTimeSpec));
                                    wherePart.append(" <= ");
                                    wherePart.append(entitySpec
                                            .getPositionParser().format(
                                                    pdsc2.getMaximumFinish()));
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        return wherePart.toString();
    }

    private String processFinishTimeSpecForWhereClause(EntitySpec entitySpec,
            Set<Filter> filtersCopy, Map<ColumnSpec, Integer> referenceIndices,
            boolean first) {
        StringBuilder wherePart = new StringBuilder();
        ColumnSpec finishTimeSpec = entitySpec.getFinishTimeSpec();
        if (finishTimeSpec != null) {
            while (true) {
                if (finishTimeSpec.getJoin() != null) {
                    finishTimeSpec = finishTimeSpec.getJoin()
                            .getNextColumnSpec();
                } else {
                    for (Filter filter : filtersCopy) {
                        if (filter instanceof PositionFilter) {
                            Set<String> entitySpecPropIds = org.arp.javautil.arrays.Arrays
                                    .asSet(entitySpec.getPropositionIds());
                            if (containsAny(entitySpecPropIds,
                                    filter.getPropositionIds())) {
                                PositionFilter pdsc2 = (PositionFilter) filter;

                                boolean outputStart = pdsc2.getMinimumStart() != null
                                        && pdsc2.getStartSide() == Side.FINISH;

                                boolean outputFinish = pdsc2.getMaximumFinish() != null
                                        && pdsc2.getFinishSide() == Side.FINISH;

                                if (outputStart) {
                                    if (!first) {
                                        wherePart.append(" and ");
                                    }

                                    wherePart.append(appendColumnRef(stmt,
                                            referenceIndices, finishTimeSpec));
                                    wherePart.append(" >= ");
                                    wherePart.append(entitySpec
                                            .getPositionParser().format(
                                                    pdsc2.getMinimumStart()));
                                }

                                if (outputFinish) {
                                    if (!first || outputStart) {
                                        wherePart.append(" and ");
                                    }

                                    wherePart.append(appendColumnRef(stmt,
                                            referenceIndices, finishTimeSpec));
                                    wherePart.append(" <= ");
                                    wherePart.append(entitySpec
                                            .getPositionParser().format(
                                                    pdsc2.getMaximumFinish()));
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }

        return wherePart.toString();
    }

    private String processOrder(SQLOrderBy order, ColumnSpecInfo info,
            Map<ColumnSpec, Integer> referenceIndices) {
        StringBuilder wherePart = new StringBuilder();
        if (order != null && info.getStartTimeIndex() >= 0) {
            ColumnSpec startColSpec = info.getColumnSpecs().get(
                    info.getStartTimeIndex());
            int start = referenceIndices.get(startColSpec);
            String startCol = startColSpec.getColumn();
            ColumnSpec finishColSpec;
            String finishCol;
            if (info.getFinishTimeIndex() >= 0) {
                finishColSpec = info.getColumnSpecs().get(
                        info.getFinishTimeIndex());
                finishCol = finishColSpec.getColumn();
            } else {
                finishColSpec = null;
                finishCol = null;
            }
            int finish;
            if (info.getFinishTimeIndex() >= 0) {
                finish = referenceIndices.get(info.getColumnSpecs().get(
                        info.getFinishTimeIndex()));
            } else {
                finish = -1;
            }
            wherePart.append(getOrderByClause(start, startCol, finish,
                    finishCol, order).generateClause());
        }
        return wherePart.toString();
    }

    private static class ValueExtractor implements ValueVisitor {

        Set<Object> values = new HashSet<Object>();

        @Override
        public void visit(NominalValue nominalValue) {
            values.add(nominalValue.getString());
        }

        @Override
        public void visit(OrdinalValue ordinalValue) {
            values.add(ordinalValue.getValue());
        }

        @Override
        public void visit(BooleanValue booleanValue) {
            values.add(booleanValue.getBoolean());
        }

        @Override
        public void visit(ValueList<? extends Value> listValue) {
            for (Value val : listValue) {
                val.accept(this);
            }
        }

        @Override
        public void visit(NumberValue numberValue) {
            values.add(numberValue.getNumber());
        }

        @Override
        public void visit(InequalityNumberValue inequalityNumberValue) {
            throw new UnsupportedOperationException(
                    "inequalityNumberValue not supported");
        }

        @Override
        public void visit(DateValue dateValue) {
            values.add(dateValue.getDate());
        }
    }
}
