package org.protempa.bp.commons.dsb.relationaldb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.arp.javautil.arrays.Arrays;
import org.protempa.backend.dsb.filter.Filter;
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
    private final TableAliaser referenceIndices;
    private final Set<String> keyIds;
    private final SQLOrderBy order;
    private final SQLGenResultProcessor resultProcessor;
    private final SelectClause selectClause;
    private final StagingSpec[] stagedTables;

    protected AbstractWhereClause(Set<String> propIds, ColumnSpecInfo info,
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            TableAliaser referenceIndices, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            SelectClause selectClause, StagingSpec[] stagedTables) {
        this.propIds = propIds;
        this.info = info;
        this.entitySpecs = Collections.unmodifiableList(entitySpecs);
        this.filters = Collections.unmodifiableSet(filters);
        this.referenceIndices = referenceIndices;
        this.keyIds = Collections.unmodifiableSet(keyIds);
        this.order = order;
        this.resultProcessor = resultProcessor;
        this.selectClause = selectClause;
        this.stagedTables = stagedTables;
    }

    protected Set<String> getPropIds() {
        return propIds;
    }

    protected ColumnSpecInfo getInfo() {
        return info;
    }

    protected List<EntitySpec> getEntitySpecs() {
        return entitySpecs;
    }

    protected Set<Filter> getFilters() {
        return filters;
    }

    protected TableAliaser getReferenceIndices() {
        return referenceIndices;
    }

    protected Set<String> getKeyIds() {
        return keyIds;
    }

    protected SQLOrderBy getOrder() {
        return order;
    }

    protected SQLGenResultProcessor getResultProcessor() {
        return resultProcessor;
    }

    protected SelectClause getSelectClause() {
        return selectClause;
    }

    protected StagingSpec[] getStagedTables() {
        return stagedTables;
    }

    public abstract InClause getInClause(ColumnSpec columnSpec,
            Object[] elements, boolean not);

    public abstract OrderByClause getOrderByClause(ColumnSpec startColumnSpec,
            ColumnSpec finishColumnSpec);

    public String generateClause() {
        StringBuilder wherePart = new StringBuilder();

        EntitySpec prevEntitySpec = null;
        boolean first = true;
        boolean inGroup = false;
        for (int j = 0, n = entitySpecs.size(); j < n; j++) {
            EntitySpec entitySpec = entitySpecs.get(j);
            if (n > 1 && j > 0) {
                if (prevEntitySpec.getName().equals(entitySpec.getName())) {
                    if (!inGroup) {
                        if (!first) {
                            wherePart.append(" AND ");
                            first = true;
                        }
                        wherePart.append(" ((");
                        inGroup = true;
                    } else {
                        wherePart.append(") OR (");
                        first = true;
                    }
                    int wherePartLength = wherePart.length();
                    wherePart.append(processForWhereClause(prevEntitySpec,
                            first));
                    if (wherePart.length() > wherePartLength) {
                        first = false;
                    }
                } else {
                    if (inGroup) {
                        first = true;
                        int wherePartLength = wherePart.length();
                        wherePart.append(") OR (");
                        wherePart.append(processForWhereClause(prevEntitySpec,
                                first));
                        wherePart.append(")) ");
                        if (wherePart.length() > wherePartLength) {
                            first = false;
                        }
                        inGroup = false;
                    } else {
                        int wherePartLength = wherePart.length();
                        wherePart.append(processForWhereClause(prevEntitySpec,
                                first));
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
            wherePart.append(") OR (");
            wherePart.append(processForWhereClause(prevEntitySpec, first));
            wherePart.append(")) ");
        } else {
            wherePart.append(processForWhereClause(prevEntitySpec, first));
        }

        processKeyIdConstraintsForWhereClause(info, wherePart, keyIds);

        if (wherePart.length() > 0) {
            wherePart.insert(0, "WHERE ");
        }

        wherePart.append(processOrder(info));

        return wherePart.toString();
    }

    private void processKeyIdConstraintsForWhereClause(ColumnSpecInfo info,
            StringBuilder wherePart, Set<String> keyIds) {
        if (keyIds != null && !keyIds.isEmpty()) {
            if (wherePart.length() > 0) {
                wherePart.append(" AND ");
            }
            ColumnSpec keySpec = info.getColumnSpecs().get(0);

            wherePart.append(getInClause(keySpec, keyIds.toArray(), false)
                    .generateClause());
        }
    }

    private String processForWhereClause(EntitySpec prevEntitySpec,
            boolean first) {
        StringBuilder wherePart = new StringBuilder();
        int wherePartLength = wherePart.length();
        wherePart.append(TimeSpecProcessor.processStartTimeSpec(prevEntitySpec,
                filters, first, referenceIndices));
        if (wherePart.length() > wherePartLength) {
            first = false;
        }
        wherePartLength = wherePart.length();
        wherePart.append(TimeSpecProcessor.processFinishTimeSpec(
                prevEntitySpec, filters, first, referenceIndices));
        if (wherePart.length() > wherePartLength) {
            first = false;
        }
        wherePartLength = wherePart.length();
        if (wherePart.length() > wherePartLength) {
            first = false;
        }
        wherePartLength = wherePart.length();
        wherePart.append(processConstraintSpecsForWhereClause(prevEntitySpec,
                first));

        return wherePart.toString();
    }

    private StringBuilder processConstraintSpecs(EntitySpec entitySpec,
            boolean first) {
        StringBuilder wherePart = new StringBuilder();

        CONSTRAINT_LOOP: for (ColumnSpec constraintSpec : entitySpec
                .getConstraintSpecs()) {
            // Skip any constraints that were included in any staged data
            // Joining against that data implicitly applies the constraint
            for (StagingSpec stagingSpec : getStagedTables()) {
                if (constraintSpec.isSameSchemaAndTable(stagingSpec
                        .getReplacedTable())) {
                    continue CONSTRAINT_LOOP;
                }
            }
            int wherePartLength = wherePart.length();
            wherePart.append(processConstraintSpecForWhereClause(
                    constraintSpec, first));
            if (wherePart.length() > wherePartLength) {
                first = false;
            }
        }

        return wherePart;
    }

    private StringBuilder processPropertySpecs(PropertySpec[] propertySpecs,
            boolean first) {
        StringBuilder wherePart = new StringBuilder();

        for (Filter filter : filters) {
            for (PropertySpec ps : propertySpecs) {
                if (filter instanceof PropertyValueFilter) {
                    PropertyValueFilter pvf = (PropertyValueFilter) filter;

                    if (pvf.getProperty().equals(ps.getName())) {
                        ColumnSpec colSpec = ps.getSpec();
                        int wherePartLength = wherePart.length();
                        wherePart.append(processPropertyValueFilter(colSpec,
                                pvf.getValueComparator(), pvf.getValues(),
                                first));
                        if (wherePart.length() > wherePartLength) {
                            first = false;
                        }

                        break;
                    }
                }
            }
        }

        return wherePart;
    }

    private StringBuilder processCodeSpec(EntitySpec entitySpec, boolean first) {
        StringBuilder wherePart = new StringBuilder();
        ColumnSpec codeSpec = entitySpec.getCodeSpec();

        if (codeSpec != null) {
            List<ColumnSpec> codeSpecL = codeSpec.asList();
            if (codeSpecL.get(codeSpecL.size() - 1).isPropositionIdsComplete()
                    && !needsPropIdInClause(entitySpec.getPropositionIds())) {
                setCaseClauseIfNeeded(codeSpec);
            } else {
                int wherePartLength = wherePart.length();
                wherePart.append(processConstraintSpecForWhereClause(codeSpec,
                        first));
                if (wherePart.length() > wherePartLength) {
                    first = false;
                }
            }
        }

        return wherePart;
    }

    private String processConstraintSpecsForWhereClause(EntitySpec entitySpec,
            boolean first) {

        StringBuilder wherePart = new StringBuilder();

        int wherePartLength = wherePart.length();
        wherePart.append(processConstraintSpecs(entitySpec, first));
        if (first && wherePart.length() > wherePartLength) {
            first = false;
            wherePartLength = wherePart.length();
        }
        wherePart.append(processPropertySpecs(entitySpec.getPropertySpecs(),
                first));
        if (first && wherePart.length() > wherePartLength) {
            first = false;
            wherePartLength = wherePart.length();
        }
        wherePart.append(processCodeSpec(entitySpec, first));

        return wherePart.toString();
    }

    private void setCaseClauseIfNeeded(ColumnSpec columnSpec) {
        if (hasConstraint(columnSpec)) {
            if (resultProcessor != null) {
                resultProcessor
                        .setCasePresent(columnSpec.getConstraint() == ColumnSpec.Constraint.LIKE);
                KnowledgeSourceIdToSqlCode[] filteredConstraintValues = filterKnowledgeSourceIdToSqlCodesById(
                        propIds, columnSpec.getPropositionIdToSqlCodes());
                ;
                selectClause.setCaseClause(
                        filteredSqlCodes(
                                propIds,
                                filterConstraintValues(propIds,
                                        filteredConstraintValues)), columnSpec,
                        filteredConstraintValues);
            }
        }
    }

    private boolean hasConstraint(ColumnSpec columnSpec) {
        if (columnSpec != null) {
            List<ColumnSpec> columnSpecL = columnSpec.asList();
            columnSpec = columnSpecL.get(columnSpecL.size() - 1);
            return columnSpec.getConstraint() != null;
        }

        return false;
    }

    private String processConstraintSpecForWhereClause(ColumnSpec columnSpec,
            boolean first) {

        StringBuilder wherePart = new StringBuilder();
        if (hasConstraint(columnSpec)) {
            setCaseClauseIfNeeded(columnSpec);
            List<ColumnSpec> columnSpecL = columnSpec.asList();
            ColumnSpec tmpColSpec = columnSpecL.get(columnSpecL.size() - 1);
            wherePart
                    .append(processConstraint(tmpColSpec, propIds, null, first));
        }
        return wherePart.toString();
    }

    private String processPropertyValueFilter(ColumnSpec columnSpec,
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
                    constraint, first));
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
            Constraint constraintOverride, boolean first) {
        StringBuilder wherePart = new StringBuilder();
        Constraint constraint = columnSpec.getConstraint();
        if (constraintOverride != null) {
            constraint = constraintOverride;
        }
        ColumnSpec.KnowledgeSourceIdToSqlCode[] propIdToSqlCodes = columnSpec
                .getPropositionIdToSqlCodes();
        if (constraint != null) {
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues = filterConstraintValues(
                    propIds, propIdToSqlCodes);
            if (!first) {
                wherePart.append(" AND ");
            }
            wherePart.append('(');
            Object[] sqlCodes = filteredSqlCodes(propIds,
                    filteredConstraintValues);

            wherePart.append(
                    WhereConstraintProcessor.getInstance(columnSpec,
                            constraint, this, sqlCodes, referenceIndices)
                            .processConstraint()).append(')');
        }

        return wherePart.toString();
    }

    private Object[] filteredSqlCodes(Set<?> propIds,
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues) {
        Object[] sqlCodes = null;
        if (filteredConstraintValues.length > 0) {
            sqlCodes = extractSqlCodes(filteredConstraintValues);
        } else {
            sqlCodes = propIds.toArray();
        }
        return sqlCodes;
    }

    private KnowledgeSourceIdToSqlCode[] filterConstraintValues(Set<?> propIds,
            ColumnSpec.KnowledgeSourceIdToSqlCode[] propIdToSqlCodes) {
        KnowledgeSourceIdToSqlCode[] filteredConstraintValues = filterKnowledgeSourceIdToSqlCodesById(
                propIds, propIdToSqlCodes);
        return filteredConstraintValues;
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
     * @param entitySpecPropIds
     *            the proposition ids corresponding to the current entity spec.
     * @return <code>true</code> if the query contains < 85% of the proposition
     *         ids that are known to the data source and if the where clause
     *         would contain less than or equal to 2000 codes.
     */
    private boolean needsPropIdInClause(String[] entitySpecPropIds) {

        Set<String> entitySpecPropIdsSet = Arrays.asSet(entitySpecPropIds);

        // Filter propIds that are not in the entitySpecPropIds array.
        List<String> filteredPropIds = new ArrayList<String>(
                entitySpecPropIds.length);
        for (String propId : propIds) {
            if (entitySpecPropIdsSet.contains(propId)) {
                filteredPropIds.add(propId);
            }
        }
        return (filteredPropIds.size() < entitySpecPropIds.length * 0.85f)
                && (filteredPropIds.size() <= 2000);
    }

    private String processOrder(ColumnSpecInfo info) {
        StringBuilder wherePart = new StringBuilder();
        if (order != null && info.getStartTimeIndex() >= 0) {
            ColumnSpec startColSpec = info.getColumnSpecs().get(
                    info.getStartTimeIndex());
            ColumnSpec finishColSpec;
            if (info.getFinishTimeIndex() >= 0) {
                finishColSpec = info.getColumnSpecs().get(
                        info.getFinishTimeIndex());
            } else {
                finishColSpec = null;
            }
            wherePart.append(getOrderByClause(startColSpec, finishColSpec)
                    .generateClause());
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
