package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.arp.javautil.collections.Collections;
import org.arp.javautil.sql.ConnectionSpec;
import org.arp.javautil.sql.SQLExecutor;
import org.arp.javautil.sql.SQLExecutor.ResultProcessor;
import org.protempa.DataSourceReadException;
import org.protempa.bp.commons.dsb.RelationalDatabaseDataSourceBackend;
import org.protempa.bp.commons.dsb.sqlgen.ColumnSpec.Constraint;
import org.protempa.bp.commons.dsb.sqlgen.ColumnSpec.PropositionIdToSqlCode;
import org.protempa.dsb.filter.Filter;
import org.protempa.dsb.filter.PositionFilter;
import org.protempa.dsb.filter.PropertyValueFilter;
import org.protempa.proposition.ConstantParameter;
import org.protempa.proposition.DefaultInterval;
import org.protempa.proposition.Event;
import org.protempa.proposition.LocalUniqueIdentifier;
import org.protempa.proposition.PointInterval;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.ListValue;
import org.protempa.proposition.value.UnitFactory;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueFactory;
import org.protempa.proposition.value.ValueType;

/**
 * Abstract class for implement database and driver-specific SQL generators.
 * 
 * @author Andrew Post
 */
public abstract class AbstractSQLGenerator implements ProtempaSQLGenerator {

    private ConnectionSpec connectionSpec;
    private final Map<String, EntitySpec> primitiveParameterSpecs;
    private final Map<String, EntitySpec> eventSpecs;
    private final Map<String, EntitySpec> constantSpecs;
    private GranularityFactory granularities;
    private UnitFactory units;
    private RelationalDatabaseDataSourceBackend backend;

    public AbstractSQLGenerator() {
        this.primitiveParameterSpecs = new HashMap<String, EntitySpec>();
        this.eventSpecs = new HashMap<String, EntitySpec>();
        this.constantSpecs = new HashMap<String, EntitySpec>();
    }

    @Override
    public void initialize(RelationalDatabaseSpec relationalDatabaseSpec,
            ConnectionSpec connectionSpec,
            RelationalDatabaseDataSourceBackend backend) {
        if (relationalDatabaseSpec != null) {
            populatePropositionMap(this.primitiveParameterSpecs,
                    relationalDatabaseSpec.getPrimitiveParameterSpecs());
            populatePropositionMap(this.eventSpecs,
                    relationalDatabaseSpec.getEventSpecs());
            populatePropositionMap(this.constantSpecs,
                    relationalDatabaseSpec.getConstantParameterSpecs());
            this.granularities = relationalDatabaseSpec.getGranularities();
            this.units = relationalDatabaseSpec.getUnits();
            this.connectionSpec = connectionSpec;
        } else {
            throw new IllegalArgumentException(
                    "relationalDatabaseSpec cannot be null");
        }
        this.backend = backend;
    }

    public ConnectionSpec getConnectionSpec() {
        return this.connectionSpec;
    }

    @Override
    public GranularityFactory getGranularities() {
        return this.granularities;
    }

    @Override
    public UnitFactory getUnits() {
        return this.units;
    }

    private void appendColumnReference(StringBuilder wherePart,
            Map<ColumnSpec, Integer> referenceIndices, ColumnSpec columnSpec) {
        wherePart.append("a");
        Integer tableNumber = referenceIndices.get(columnSpec);
        if (tableNumber == null) {
            throw new AssertionError("tableNumber is null");
        }
        wherePart.append(tableNumber);
        wherePart.append(".");
        wherePart.append(columnSpec.getColumn());
    }

    private <P extends Proposition> Map<String, List<P>> executeSelect(
            Set<String> propIds, Filter filters, Set<String> keyIds,
            SQLOrderBy order, ResultProcessorAllKeyIds<P> resultProcessor)
            throws DataSourceReadException {
        Map<EntitySpec, List<String>> propSpecMapFromPropIds =
                propSpecMapForPropIds(propIds);

        Map<String, List<P>> results = new HashMap<String, List<P>>();
        Set<EntitySpec> entitySpecs = new HashSet<EntitySpec>();
        entitySpecs.addAll(this.eventSpecs.values());
        entitySpecs.addAll(this.primitiveParameterSpecs.values());
        entitySpecs.addAll(this.constantSpecs.values());

        for (EntitySpec entitySpec : propSpecMapFromPropIds.keySet()) {
            removeNonApplicableEntitySpecs(entitySpecs, entitySpec);
            Set<Filter> filtersCopy = new HashSet<Filter>();
            for (Iterator<Filter> itr = filters.andIterator();
                    itr.hasNext();) {
                filtersCopy.add(itr.next());
            }
            removeNonApplicableFilters(entitySpecs, filtersCopy, entitySpec);
            String query = generateSelect(entitySpec, propIds,
                    filtersCopy, entitySpecs, keyIds, order);
            SQLGenUtil.logger().log(Level.INFO,
                    "Data source backend {0} is executing query for {1}: {2}",
                    new Object[]{this.backendNameForMessages(),
                        entitySpec.getName(), query});

            resultProcessor.setEntitySpec(entitySpec);

            try {
                SQLExecutor.executeSQL(getConnectionSpec(),
                        query, resultProcessor);
            } catch (SQLException ex) {
                throw new DataSourceReadException(ex);
            }

            SQLGenUtil.logger().log(Level.INFO,
                    "Query for {0} in data source backend {1} is complete",
                    new Object[]{entitySpec.getName(),
                        this.backendNameForMessages()});

            Map<String, List<P>> resultsMap = resultProcessor.getResults();
            for (Map.Entry<String, List<P>> me2 : resultsMap.entrySet()) {
                if (me2.getValue() == null) {
                    me2.setValue(new ArrayList<P>(0));
                }
                List<P> rList = results.get(me2.getKey());
                if (rList == null) {
                    results.put(me2.getKey(), me2.getValue());
                } else {
                    rList.addAll(me2.getValue());
                }
            }
            results.putAll(resultsMap);

            SQLGenUtil.logger().log(Level.INFO,
                    "Results of query for {0} in data source backend {1} "
                    + "have been processed",
                    new Object[]{entitySpec.getName(),
                        this.backendNameForMessages()});
        }
        return results;
    }

    private void removeNonApplicableEntitySpecs(Set<EntitySpec> entitySpecs,
            EntitySpec entitySpec) {
        for (Iterator<EntitySpec> itr = entitySpecs.iterator();
                itr.hasNext();) {
            EntitySpec es = itr.next();
            if (es != entitySpec
                    && !SQLGenUtil.columnSpecsEquals(es.getBaseSpec(),
                    entitySpec.getBaseSpec())) {
                itr.remove();
            }
        }
    }

    /**
     * Remove filters that are not directly applicable to the given
     * entity spec and are not applicable to other entity specs with the
     * same base spec.
     *
     * @param entitySpecs
     * @param filter
     * @param entitySpec
     */
    private void removeNonApplicableFilters(Set<EntitySpec> entitySpecs,
            Set<Filter> filters, EntitySpec entitySpec) {
        Set<ColumnSpec> columnSpecs = new HashSet<ColumnSpec>();
        for (Iterator<Filter> itr = filters.iterator(); itr.hasNext();) {
            Filter f = itr.next();
            for (EntitySpec es : entitySpecs) {
                for (String propId : es.getPropositionIds()) {
                    for (String propId2 : f.getPropositionIds()) {
                        if (propId.equals(propId2)) {
                            columnSpecs.add(es.getBaseSpec());
                            break;
                        }
                    }
                }
            }
            boolean found = false;
            for (ColumnSpec colSpec : columnSpecs) {
                if (SQLGenUtil.columnSpecsEquals(colSpec,
                        entitySpec.getBaseSpec())) {
                    found = true;
                    break;
                }

            }
            if (!found) {
                itr.remove();
            }
        }
    }

    protected abstract boolean isLimitingSupported();

    @Override
    public Map<String, List<ConstantParameter>> readConstantParameters(
            Set<String> keyIds, Set<String> paramIds, Filter filters)
            throws DataSourceReadException {
        Map<String, List<ConstantParameter>> results =
                new HashMap<String, List<ConstantParameter>>();

        ConstantParameterResultProcessor resultProcessor =
                new ConstantParameterResultProcessor();
        resultProcessor.setResults(results);
        resultProcessor.setDataSourceBackendId(
                this.backend.getDataSourceBackendId());

        return executeSelect(paramIds, filters,
                keyIds, null, resultProcessor);
    }

    @Override
    public Map<String, List<PrimitiveParameter>> readPrimitiveParameters(
            Set<String> keyIds, Set<String> paramIds, Filter filters,
            SQLOrderBy order)
            throws DataSourceReadException {
        Map<String, List<PrimitiveParameter>> results =
                new HashMap<String, List<PrimitiveParameter>>();

        PrimitiveParameterResultProcessorAllKeyIds resultProcessor =
                new PrimitiveParameterResultProcessorAllKeyIds();
        resultProcessor.setResults(results);
        resultProcessor.setDataSourceBackendId(
                this.backend.getDataSourceBackendId());

        return executeSelect(paramIds, filters,
                keyIds, order, resultProcessor);
    }

    @Override
    public Map<String, List<Event>> readEvents(Set<String> keyIds,
            Set<String> eventIds, Filter filters,
            SQLOrderBy order) throws DataSourceReadException {
        Map<String, List<Event>> results = new HashMap<String, List<Event>>();

        EventResultProcessorAllKeyIds resultProcessor =
                new EventResultProcessorAllKeyIds();
        resultProcessor.setResults(results);
        resultProcessor.setDataSourceBackendId(
                this.backend.getDataSourceBackendId());

        return executeSelect(eventIds, filters, keyIds, order,
                resultProcessor);
    }

    public final String generateSelect(EntitySpec entitySpec,
            Set<String> propIds, Set<Filter> filters,
            Set<EntitySpec> entitySpecs, Set<String> keyIds,
            SQLOrderBy order) {
        ColumnSpecInfo info = new ColumnSpecInfoFactory().newInstance(
                entitySpec, entitySpecs, filters);
        Map<ColumnSpec, Integer> referenceIndices =
                computeReferenceIndices(info.getColumnSpecs());
        StringBuilder selectClause = generateSelectClause(info,
                referenceIndices);
        StringBuilder fromClause = generateFromClause(info.getColumnSpecs(),
                referenceIndices);
        StringBuilder whereClause = generateWhereClause(entitySpec, propIds,
                info, entitySpecs, filters, selectClause,
                referenceIndices, keyIds, order);
        String result = assembleReadPropositionsQuery(
                selectClause, fromClause, whereClause);
        return result;
    }

    public abstract String assembleReadPropositionsQuery(
            StringBuilder selectClause, StringBuilder fromClause,
            StringBuilder whereClause);

    private PropositionIdToSqlCode[] filterConstraintValues(
            Set<String> propIds, PropositionIdToSqlCode[] constraintValues) {
        ColumnSpec.PropositionIdToSqlCode[] filteredConstraintValues;
        if (propIds != null) {
            List<ColumnSpec.PropositionIdToSqlCode> constraintValueList =
                    new ArrayList<ColumnSpec.PropositionIdToSqlCode>();
            for (ColumnSpec.PropositionIdToSqlCode constraintValue :
                    constraintValues) {
                if (propIds.contains(constraintValue.getPropositionId())) {
                    constraintValueList.add(constraintValue);
                }
            }
            filteredConstraintValues =
                    constraintValueList.toArray(
                    new ColumnSpec.PropositionIdToSqlCode[constraintValueList.size()]);
        } else {
            filteredConstraintValues = constraintValues;
        }
        return filteredConstraintValues;
    }

    private ColumnSpec findColumnSpecWithMatchingSchemaAndTable(int j,
            List<ColumnSpec> columnSpecs, ColumnSpec columnSpec) {
        ColumnSpec columnSpec2 = null;
        for (int k = 0; k < j; k++) {
            columnSpec2 = columnSpecs.get(k);
            if (columnSpec.isSameSchemaAndTable(columnSpec2)) {
                break;
            } else {
                columnSpec2 = null;
            }
        }
        return columnSpec2;
    }

    private StringBuilder generateSelectClause(ColumnSpecInfo info,
            Map<ColumnSpec, Integer> referenceIndices) {
        StringBuilder selectPart = new StringBuilder();
        int i = 0;
        if (info.getFinishTimeIndex() > 0) {
            i++;
        }
        if (info.getPropertyIndices() != null) {
            i += info.getPropertyIndices().size();
        }
        if (info.getCodeIndex() > 0) {
            i++;
        }
        if (info.getStartTimeIndex() > 0) {
            i++;
        }
        if (info.getUniqueIdIndex() > 0) {
            i += info.getNumberOfUniqueIdColumns();
        }
        i++; //key id
        int[] indices = new int[i];
        String[] names = new String[i];
        int k = 0;
        indices[k] = 0;
        names[k++] = "keyid";
        if (info.getUniqueIdIndex() > 0) {
            indices[k] = info.getUniqueIdIndex();
            for (int m = 0; m < info.getNumberOfUniqueIdColumns(); m++) {
                names[k++] = "uniqueid" + m;
            }
        }
        if (info.getCodeIndex() > 0) {
            indices[k] = info.getCodeIndex();
            names[k++] = "code";
        }
        if (info.getStartTimeIndex() > 0) {
            indices[k] = info.getStartTimeIndex();
            names[k++] = "starttime";
        }
        if (info.getFinishTimeIndex() > 0) {
            indices[k] = info.getFinishTimeIndex();
            names[k++] = "finishtime";
        }
        if (info.getPropertyIndices() != null) {
            for (Map.Entry<String, Integer> e :
                    info.getPropertyIndices().entrySet()) {
                indices[k] = e.getValue();
                names[k++] = e.getKey() + "_value";
            }
        }

        boolean unique = info.isUnique();

        for (int j = 0; j < indices.length; j++) {
            ColumnSpec cs = info.getColumnSpecs().get(indices[j]);
            int index = referenceIndices.get(cs);
            String column = cs.getColumn();
            String name = names[j];
            boolean distinctRequested = (j == 0 && !unique);
            boolean hasNext = j < indices.length - 1;
            if (column == null) {
                throw new AssertionError("column cannot be null: "
                        + "index=" + index + "; name=" + name + "; " + cs);
            }
            if (name == null) {
                throw new AssertionError("name cannot be null");
            }
            generateSelectColumn(distinctRequested, selectPart, index, column,
                    name, hasNext);
        }
        return selectPart;
    }

    public abstract void generateSelectColumn(boolean distinctRequested,
            StringBuilder selectPart, int index, String column, String name,
            boolean hasNext);

    public abstract void generateFromTable(String schema, String table,
            StringBuilder fromPart, int index);

    public abstract void generateFromTableReference(
            int i, StringBuilder fromPart);

    public abstract void generateOn(StringBuilder fromPart, int fromIndex,
            int toIndex, String fromKey, String toKey);

    public abstract void generateJoin(StringBuilder fromPart);

    private StringBuilder generateFromClause(List<ColumnSpec> columnSpecs,
            Map<ColumnSpec, Integer> referenceIndices) {
        Map<Integer, ColumnSpec> columnSpecCache =
                new HashMap<Integer, ColumnSpec>();
        StringBuilder fromPart = new StringBuilder();
        JoinSpec currentJoin = null;
        boolean begin = true;
        for (int j = 0, n = columnSpecs.size(); j < n; j++) {
            ColumnSpec columnSpec = columnSpecs.get(j);
            boolean shouldGenerateTable = begin || currentJoin != null;
            if (shouldGenerateTable) {
                int i = referenceIndices.get(columnSpec);
                if (!columnSpecCache.containsKey(i)) {
                    String schema = columnSpec.getSchema();
                    String table = columnSpec.getTable();
                    generateFromTable(schema, table, fromPart, i);
                    columnSpecCache.put(i, columnSpec);

                    if (currentJoin != null) {
                        int fromIndex = referenceIndices.get(
                                currentJoin.getPrevColumnSpec());
                        int toIndex = referenceIndices.get(
                                currentJoin.getNextColumnSpec());
                        generateOn(fromPart, fromIndex, toIndex,
                                currentJoin.getFromKey(),
                                currentJoin.getToKey());
                    }
                }
                begin = false;
            }



            if (columnSpec.getJoin() != null
                    && !columnSpecCache.containsKey(
                    referenceIndices.get(
                    columnSpec.getJoin().getNextColumnSpec()))) {
                generateJoin(fromPart);
                currentJoin = columnSpec.getJoin();
            } else {
                currentJoin = null;
            }
        }
        return fromPart;
    }

    private StringBuilder generateWhereClause(EntitySpec es,
            Set<String> propIds,
            ColumnSpecInfo info, Collection<EntitySpec> entitySpecs,
            Set<Filter> filters, StringBuilder selectPart,
            Map<ColumnSpec, Integer> referenceIndices,
            Set<String> keyIds, SQLOrderBy order) {
        StringBuilder wherePart = new StringBuilder();
        int i = 1;
        for (EntitySpec entitySpec : entitySpecs) {
            i = processKeySpecForWhereClause(entitySpec, i);
            i = processStartTimeSpecForWhereClause(entitySpec, i,
                    filters, wherePart, referenceIndices, entitySpecs);
            i = processFinishTimeSpecForWhereClause(entitySpec, i,
                    filters, wherePart, referenceIndices, entitySpecs);
            i = processPropertyValueSpecsForWhereClause(entitySpec, i);
            i = processConstraintSpecsForWhereClause(propIds, entitySpec,
                    i, wherePart, selectPart, referenceIndices, filters);
        }
        processKeyIdConstraintsForWhereClause(info, wherePart, keyIds);

        if (wherePart.length() > 0) {
            wherePart.insert(0, "where ");
        }

        if (order != null && info.getStartTimeIndex() >= 0) {
            ColumnSpec startColSpec =
                    info.getColumnSpecs().get(info.getStartTimeIndex());
            int start = referenceIndices.get(startColSpec);
            String startCol = startColSpec.getColumn();

            ColumnSpec finishColSpec;
            String finishCol;
            if (info.getFinishTimeIndex() >= 0) {
                finishColSpec =
                        info.getColumnSpecs().get(info.getFinishTimeIndex());
                finishCol = finishColSpec.getColumn();
            } else {
                finishColSpec = null;
                finishCol = null;
            }

            int finish;
            if (info.getFinishTimeIndex() >= 0) {
                finish = referenceIndices.get(
                        info.getColumnSpecs().get(info.getFinishTimeIndex()));
            } else {
                finish = -1;
            }
            processOrderBy(start, startCol, finish, finishCol, wherePart,
                    order);
        }

        return wherePart;
    }

    private void processConstraints(ColumnSpec columnSpec, Set<String> propIds,
            StringBuilder wherePart, Map<ColumnSpec, Integer> referenceIndices,
            boolean selectPartHasCaseStmt, StringBuilder selectPart,
            ColumnSpec.Constraint constraint,
            ColumnSpec.PropositionIdToSqlCode[] propIdToSqlCodes) {
        if (constraint != null) {
            PropositionIdToSqlCode[] filteredConstraintValues =
                    filterConstraintValues(propIds, propIdToSqlCodes);
            Object[] sqlCodes = new Object[filteredConstraintValues.length];
            for (int i = 0; i < sqlCodes.length; i++) {
                sqlCodes[i] = filteredConstraintValues[i].getSqlCode();
            }
            switch (constraint) {
                case EQUAL_TO:
                case LESS_THAN:
                case LESS_THAN_OR_EQUAL_TO:
                case GREATER_THAN:
                case GREATER_THAN_OR_EQUAL_TO:
                case NOT_EQUAL_TO:
                    if (sqlCodes.length > 1) {
                        generateInClause(wherePart,
                                referenceIndices.get(columnSpec),
                                columnSpec.getColumn(),
                                sqlCodes);
                    } else {
                        appendColumnReference(wherePart, referenceIndices,
                                columnSpec);
                        wherePart.append(constraint.getSqlOperator());
                        appendValue(sqlCodes[0], wherePart);
                    }
                    break;
                case LIKE:
                    if (!selectPartHasCaseStmt) {
                        selectPart.append(", case ");
                        selectPartHasCaseStmt = true;
                    }
                    if (sqlCodes.length > 1) {
                        wherePart.append('(');
                    }
                    for (int k = 0; k < sqlCodes.length; k++) {
                        appendColumnReference(wherePart, referenceIndices,
                                columnSpec);
                        wherePart.append(" LIKE ");
                        appendValue(sqlCodes[k], wherePart);
                        if (k + 1 < sqlCodes.length) {
                            wherePart.append(" or ");
                        }
                        selectPart.append("when ");
                        appendColumnReference(selectPart, referenceIndices,
                                columnSpec);
                        selectPart.append(" like ");
                        appendValue(sqlCodes[k], selectPart);
                        selectPart.append(" then ");
                        selectPart.append("'");
                        appendValue(
                                filteredConstraintValues[k].getPropositionId(),
                                selectPart);
                        selectPart.append("'");
                    }
                    if (sqlCodes.length > 1) {
                        wherePart.append(')');
                    }
                    break;
                default:
                    throw new AssertionError("should not happen");
            }
        }
    }

    private void processAdditionalConstraints(ColumnSpec columnSpec,
            StringBuilder wherePart, Map<ColumnSpec, Integer> referenceIndices,
            ColumnSpec.Constraint constraint,
            Value value) {
        if (constraint != null && value != null) {
            if (value instanceof ListValue) {
                ListValue<Value> lvalue = (ListValue<Value>) value;
                Object[] vals = new Object[lvalue.size()];
                /*
                 * We assume that lvalue does not contain any nested lists
                 * because PropertyValueFilter does not allow them.
                 */
                for (int i = 0; i < vals.length; i++) {
                    vals[i] = lvalue.get(i).getFormatted();
                }
                generateInClause(wherePart,
                        referenceIndices.get(columnSpec),
                        columnSpec.getColumn(), vals);
            } else {
                appendColumnReference(wherePart, referenceIndices,
                        columnSpec);
                wherePart.append(constraint.getSqlOperator());
                appendValue(value.getFormatted(), wherePart);
            }
        }
    }

    private int processPropertyValueSpecsForWhereClause(
            EntitySpec entitySpec, int i) {
        PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
        Map<String, Integer> propertyValueIndices =
                new HashMap<String, Integer>();
        for (PropertySpec propertySpec : propertySpecs) {
            ColumnSpec spec = propertySpec.getSpec();
            if (spec != null) {
                i += spec.asList().size();
                propertyValueIndices.put(propertySpec.getName(), i);
            }
        }
        return i;
    }

    private int processConstraintSpecsForWhereClause(Set<String> propIds,
            EntitySpec entitySpec,
            int i, StringBuilder wherePart, StringBuilder selectPart,
            Map<ColumnSpec, Integer> referenceIndices, Set<Filter> filters) {
        boolean selectPartHasCaseStmt = false;
        ColumnSpec[] constraintSpecs = entitySpec.getConstraintSpecs();
        for (ColumnSpec constraintSpec : constraintSpecs) {
            i = processConstraintSpecForWhereClause(null, constraintSpec,
                    i, wherePart,
                    selectPart, referenceIndices, selectPartHasCaseStmt);
        }
        if (filters != null) {
            for (Iterator<Filter> itr = filters.iterator(); itr.hasNext();) {
                Filter filter = itr.next();
                for (PropertySpec ps : entitySpec.getPropertySpecs()) {
                    if (filter instanceof PropertyValueFilter) {
                        PropertyValueFilter pvf =
                                (PropertyValueFilter) filter;
                        if (pvf.getProperty().equals(ps.getName())) {
                            ColumnSpec colSpec = ps.getSpec();
                            Constraint constraint =
                                    valueComparatorToSqlOp(
                                    pvf.getValueComparator());
                            processPropertyValueFilter(colSpec, i, wherePart,
                                    referenceIndices,
                                    constraint, pvf.getValue());

                            break;
                        }
                    }
                }
            }
        }
        ColumnSpec codeSpec = entitySpec.getCodeSpec();
        i = processConstraintSpecForWhereClause(propIds, codeSpec, i,
                wherePart, selectPart, referenceIndices,
                selectPartHasCaseStmt);
        if (selectPartHasCaseStmt) {
            selectPart.append(" else 'OTHER' end ");
            selectPart.append("code ");
        }
        return i;
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
            default:
                throw new AssertionError("cannot reach");
        }
        return constraint;
    }

    private int processConstraintSpecForWhereClause(Set<String> propIds,
            ColumnSpec columnSpec, int i, StringBuilder wherePart,
            StringBuilder selectPart,
            Map<ColumnSpec, Integer> referenceIndices,
            boolean selectPartHasCaseStmt) {
        if (columnSpec == null) {
            return i;
        }

        while (columnSpec.getJoin() != null) {
            columnSpec = columnSpec.getJoin().getNextColumnSpec();
            i++;
        }

        if (wherePart.length() > 0) {
            wherePart.append(" and ");
        }
        wherePart.append('(');
        processConstraints(columnSpec, propIds, wherePart,
                referenceIndices, selectPartHasCaseStmt, selectPart,
                columnSpec.getConstraint(),
                columnSpec.getPropositionIdToSqlCodes());

        wherePart.append(')');

        return i;
    }

    private int processPropertyValueFilter(ColumnSpec columnSpec, int i,
            StringBuilder wherePart,
            Map<ColumnSpec, Integer> referenceIndices,
            ColumnSpec.Constraint additionalConstraint,
            Value additionalValue) {
        if (columnSpec == null) {
            return i;
        }

        while (columnSpec.getJoin() != null) {
            columnSpec = columnSpec.getJoin().getNextColumnSpec();
            i++;
        }

        if (wherePart.length() > 0) {
            wherePart.append(" and ");
        }

        wherePart.append('(');
        if (additionalConstraint != null
                && additionalValue != null) {
            processAdditionalConstraints(columnSpec, wherePart,
                    referenceIndices, additionalConstraint,
                    additionalValue);
        }
        wherePart.append(')');

        return i;
    }

    private int processFinishTimeSpecForWhereClause(
            EntitySpec entitySpec, int i, Set<Filter> filters,
            StringBuilder wherePart,
            Map<ColumnSpec, Integer> referenceIndices,
            Collection<EntitySpec> entitySpecs) {
        ColumnSpec finishTimeSpec = entitySpec.getFinishTimeSpec();
        if (finishTimeSpec != null) {
            while (true) {
                if (finishTimeSpec.getJoin() != null) {
                    finishTimeSpec =
                            finishTimeSpec.getJoin().getNextColumnSpec();
                    i++;
                } else {
                    if (filters != null) {
                        for (Iterator<Filter> itr = filters.iterator();
                                itr.hasNext();) {
                            Filter filter = itr.next();
                            if (filter instanceof PositionFilter) {
                                PositionFilter pdsc2 =
                                        (PositionFilter) filter;

                                List<ColumnSpec> finish =
                                        new ArrayList<ColumnSpec>();
                                for (EntitySpec es : entitySpecs) {
                                    boolean found = false;
                                    for (String propId :
                                            es.getPropositionIds()) {
                                        for (String propId2 :
                                                pdsc2.getPropositionIds()) {
                                            if (propId.equals(propId2)) {
                                                finish.add(
                                                        es.getFinishTimeSpec());
                                                found = true;
                                                break;
                                            }
                                            if (found) {
                                                break;
                                            }
                                        }
                                    }
                                }

                                boolean outputFinish = false;
                                for (ColumnSpec cs : finish) {
                                    if (SQLGenUtil.columnSpecsEquals(
                                            entitySpec.getFinishTimeSpec(),
                                            cs)) {
                                        outputFinish = true;
                                    }
                                }

                                if (outputFinish) {
                                    if (wherePart.length() > 0) {
                                        wherePart.append(" and ");
                                    }

                                    appendColumnReference(wherePart,
                                            referenceIndices, finishTimeSpec);
                                    wherePart.append(" <= {ts '");
                                    wherePart.append(
                                            AbsoluteTimeGranularity.toSQLString(
                                            pdsc2.getMaximumFinish()));
                                    wherePart.append("'}");
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        return i;
    }

    private int processStartTimeSpecForWhereClause(
            EntitySpec entitySpec, int i, Set<Filter> filters,
            StringBuilder wherePart,
            Map<ColumnSpec, Integer> referenceIndices,
            Collection<EntitySpec> entitySpecs) {
        ColumnSpec startTimeSpec = entitySpec.getStartTimeSpec();
        if (startTimeSpec != null) {
            while (true) {
                if (startTimeSpec.getJoin() != null) {
                    startTimeSpec =
                            startTimeSpec.getJoin().getNextColumnSpec();
                    i++;
                } else {
                    if (filters != null) {
                        for (Iterator<Filter> itr = filters.iterator();
                                itr.hasNext();) {
                            Filter filter = itr.next();
                            if (filter instanceof PositionFilter) {
                                PositionFilter pdsc2 = (PositionFilter) filter;

                                List<ColumnSpec[]> startFinish =
                                        new ArrayList<ColumnSpec[]>();
                                for (EntitySpec es : entitySpecs) {
                                    boolean found = false;
                                    for (String propId :
                                            es.getPropositionIds()) {
                                        for (String propId2 :
                                                pdsc2.getPropositionIds()) {
                                            if (propId.equals(propId2)) {
                                                startFinish.add(
                                                        new ColumnSpec[]{
                                                            es.getStartTimeSpec(),
                                                            es.getFinishTimeSpec()
                                                        });
                                                found = true;
                                                break;
                                            }
                                            if (found) {
                                                break;
                                            }
                                        }
                                    }
                                }

                                boolean outputStart = false;
                                boolean outputFinish = false;
                                for (ColumnSpec[] csArr : startFinish) {
                                    if (SQLGenUtil.columnSpecsEquals(
                                            entitySpec.getStartTimeSpec(),
                                            csArr[0])) {
                                        outputStart = true;
                                    }
                                    if (SQLGenUtil.columnSpecsEquals(
                                            entitySpec.getStartTimeSpec(),
                                            csArr[1])) {
                                        outputFinish = true;
                                    }
                                }



                                if (outputStart) {
                                    if (wherePart.length() > 0) {
                                        wherePart.append(" and ");
                                    }
                                    appendColumnReference(wherePart,
                                            referenceIndices, startTimeSpec);
                                    wherePart.append(" >= {ts '");
                                    wherePart.append(
                                            AbsoluteTimeGranularity.toSQLString(
                                            pdsc2.getMinimumStart()));
                                    wherePart.append("'}");
                                }
                                if (entitySpec.getFinishTimeSpec() == null
                                        || outputFinish) {
                                    if (wherePart.length() > 0) {
                                        wherePart.append(" and ");
                                    }
                                    appendColumnReference(wherePart,
                                            referenceIndices,
                                            startTimeSpec);
                                    wherePart.append(" <= {ts '");
                                    wherePart.append(
                                            AbsoluteTimeGranularity.toSQLString(
                                            pdsc2.getMaximumFinish()));
                                    wherePart.append("'}");
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        return i;
    }

    private int processKeySpecForWhereClause(EntitySpec propositionSpec,
            int i) {
        ColumnSpec baseSpec = propositionSpec.getBaseSpec();
        i += baseSpec.asList().size();
        return i;
    }

    public abstract void generateInClause(StringBuilder wherePart,
            int referenceIndex, String column, Object[] sqlCodes);

    public abstract void appendValue(Object val, StringBuilder wherePart);

    public abstract void generateFromSeparator(StringBuilder fromPart);

    private Map<ColumnSpec, Integer> computeReferenceIndices(
            List<ColumnSpec> columnSpecs) {
        Map<ColumnSpec, Integer> result = new HashMap<ColumnSpec, Integer>();

        int i = 1;
        JoinSpec currentJoin = null;
        boolean begin = true;
        for (int j = 0, n = columnSpecs.size(); j < n; j++) {
            ColumnSpec columnSpec = columnSpecs.get(j);
            boolean shouldGenerateTable = begin || currentJoin != null;
            if (shouldGenerateTable) {
                ColumnSpec columnSpec2 = null;
                if (currentJoin == null) {
                    columnSpec2 = findColumnSpecWithMatchingSchemaAndTable(j,
                            columnSpecs, columnSpec);
                }
                if (columnSpec2 != null) {
                    result.put(columnSpec, result.get(columnSpec2));
                } else {
                    result.put(columnSpec, i);
                }
                begin = false;
            }

            if (columnSpec.getJoin() != null) {
                currentJoin = columnSpec.getJoin();
                i++;
            } else {
                currentJoin = null;
                begin = true;
            }
        }
        return result;
    }

    private void processKeyIdConstraintsForWhereClause(ColumnSpecInfo info,
            StringBuilder wherePart, Set<String> keyIds) {
        if (keyIds != null && !keyIds.isEmpty()) {
            if (wherePart.length() > 0) {
                wherePart.append(" and ");
            }
            ColumnSpec keySpec = info.getColumnSpecs().get(0);
            wherePart.append("a1.").append(keySpec.getColumn()).append(" in ('");
            wherePart.append(
                    org.arp.javautil.collections.Collections.join(
                    keyIds, "','"));
            wherePart.append("')");
        }
    }

    public abstract void processOrderBy(int startReferenceIndex,
            String startColumn, int finishReferenceIndex, String finishColumn,
            StringBuilder wherePart, SQLOrderBy sQLOrderBy);

    @Override
    public final void loadDriverIfNeeded() {
        String className = getDriverClassNameToLoad();
        try {
            Class.forName(className);
        } catch (ClassNotFoundException ex) {
            SQLGenUtil.logger().log(Level.WARNING,
                    "{0} when trying to load {1}.",
                    new Object[]{ex.getClass().getName(), className});
        }
    }

    /**
     * Gets a the class name of the driver to load for this SQL generator, or
     * <code>null</code> if the driver is a JDBC 4 driver and does not need
     * to be loaded explicitly. Returning not-<code>null</code> will do no
     * harm if a JDBC 4 driver.
     *
     * This implementation returns <code>null</code>. Override it to return a
     * driver's class name.
     *
     * @return a class name {@link String}.
     */
    protected String getDriverClassNameToLoad() {
        return null;
    }

    private static class ConstantParameterResultProcessor
            extends ResultProcessorAllKeyIds<ConstantParameter> {

        @Override
        public void process(ResultSet resultSet) throws SQLException {
            Map<String, List<ConstantParameter>> results = getResults();
            EntitySpec entitySpec = getEntitySpec();
            String[] propIds = entitySpec.getPropositionIds();
            while (resultSet.next()) {
                int i = 1;
                String keyId = resultSet.getString(i++);

                String[] uniqueIds = new String[
                        entitySpec.getUniqueIdSpecs().length];
                for (int m = 0; m < uniqueIds.length; m++) {
                    uniqueIds[m] = resultSet.getString(i++);
                }



                ValueType vf = entitySpec.getValueType();
                Value cpVal = ValueFactory.get(vf).parseValue(
                        resultSet.getString(i++));

                String propId;
                if (propIds.length == 1) {
                    propId = propIds[0];
                } else {
                    propId = resultSet.getString(i++);
                }

                ConstantParameter cp =
                        new ConstantParameter(propId);
                cp.setValue(cpVal);


                cp.setUniqueIdentifier(new UniqueIdentifier(
                        getDataSourceBackendId(),
                        new MyUniqueIdentifier(entitySpec.getName(),
                        uniqueIds)));

                PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
                for (PropertySpec propertySpec : propertySpecs) {
                    ValueType vf2 = propertySpec.getValueType();
                    Value value =
                            ValueFactory.get(vf2).parseValue(
                            resultSet.getString(i++));
                    cp.setProperty(propertySpec.getName(), value);
                }

                cp.setDataSourceBackendId(this.getDataSourceBackendId());
                Collections.putList(results, keyId, cp);
            }
        }
    }

    private static class MyUniqueIdentifier implements LocalUniqueIdentifier {
        private static final long serialVersionUID = 3956023315666447630L;

        final String name;
        String[] dbIds;

        MyUniqueIdentifier(String name, String[] dbIds) {
            this.name = name;
            this.dbIds = dbIds;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final MyUniqueIdentifier other = (MyUniqueIdentifier) obj;
            if ((this.name == null) ? (other.name != null) :
                !this.name.equals(other.name)) {
                return false;
            }
            if (!Arrays.deepEquals(this.dbIds, other.dbIds)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 53 * hash + (this.name != null ? this.name.hashCode() : 0);
            hash = 53 * hash + Arrays.deepHashCode(this.dbIds);
            return hash;
        }

        @Override
        public LocalUniqueIdentifier clone() {
            MyUniqueIdentifier result;
            try {
                result = (MyUniqueIdentifier) super.clone();
                result.dbIds = this.dbIds.clone();
                return result;
            } catch (CloneNotSupportedException ex) {
                throw new AssertionError("Never reached!");
            }
            
        }

    }

    private static abstract class ResultProcessorAllKeyIds
            <P extends Proposition> implements ResultProcessor {

        private Map<String, List<P>> results;
        private EntitySpec entitySpec;
        private String dataSourceBackendId;

        public String getDataSourceBackendId() {
            return dataSourceBackendId;
        }

        public void setDataSourceBackendId(String dataSourceBackendId) {
            this.dataSourceBackendId = dataSourceBackendId;
        }

        public void setEntitySpec(EntitySpec entitySpec) {
            this.entitySpec = entitySpec;
        }

        public EntitySpec getEntitySpec() {
            return this.entitySpec;
        }

        Map<String, List<P>> getResults() {
            return this.results;
        }

        void setResults(Map<String, List<P>> results) {
            this.results = results;
        }
    }

    private static class PrimitiveParameterResultProcessorAllKeyIds
            extends ResultProcessorAllKeyIds<PrimitiveParameter> {

        @Override
        public void process(ResultSet resultSet) throws SQLException {
            Map<String, List<PrimitiveParameter>> results = getResults();
            EntitySpec entitySpec = getEntitySpec();
            String[] propIds = entitySpec.getPropositionIds();
            while (resultSet.next()) {
                int i = 1;
                String keyId = resultSet.getString(i++);

                String[] uniqueIds = new String[
                        entitySpec.getUniqueIdSpecs().length];
                for (int m = 0; m < uniqueIds.length; m++) {
                    uniqueIds[m] = resultSet.getString(i++);
                }



                ValueType vf = entitySpec.getValueType();
                Value cpVal = ValueFactory.get(vf).parseValue(
                        resultSet.getString(i++));

                String propId;
                if (propIds.length == 1) {
                    propId = propIds[0];
                } else {
                    propId = resultSet.getString(i++);
                }

                PrimitiveParameter p = new PrimitiveParameter(propId);
                p.setUniqueIdentifier(new UniqueIdentifier(
                        getDataSourceBackendId(),
                        new MyUniqueIdentifier(entitySpec.getName(),
                        uniqueIds)));
                try {
                    p.setTimestamp(entitySpec.getPositionParser()
                            .toLong(resultSet, i++));
                } catch (SQLException e) {
                    SQLGenUtil.logger().log(Level.WARNING,
                            "Could not parse timestamp. Ignoring data value.",
                            e);
                    continue;
                }
                p.setGranularity(entitySpec.getGranularity());
                p.setValue(cpVal);

                PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
                for (PropertySpec propertySpec : propertySpecs) {
                    ValueType vf2 = propertySpec.getValueType();
                    Value value = ValueFactory.get(vf2).parseValue(
                            resultSet.getString(i++));
                    p.setProperty(propertySpec.getName(), value);
                }

                p.setDataSourceBackendId(this.getDataSourceBackendId());
                Collections.putList(results, keyId, p);
            }
        }
    }

    private static class EventResultProcessorAllKeyIds
            extends ResultProcessorAllKeyIds<Event> {

        @Override
        public void process(ResultSet resultSet) throws SQLException {
            Map<String, List<Event>> results = getResults();
            EntitySpec entitySpec = getEntitySpec();
            while (resultSet.next()) {
                int i = 1;
                String keyId = resultSet.getString(i++);
                String[] uniqueIds = new String[
                        entitySpec.getUniqueIdSpecs().length];
                for (int m = 0; m < uniqueIds.length; m++) {
                    uniqueIds[m] = resultSet.getString(i++);
                }
                String propId;
                String[] propIds = entitySpec.getPropositionIds();
                if (propIds.length == 1) {
                    propId = propIds[0];
                } else {
                    propId = resultSet.getString(i++);
                }
                Event event = new Event(propId);
                event.setUniqueIdentifier(new UniqueIdentifier(
                        getDataSourceBackendId(),
                        new MyUniqueIdentifier(entitySpec.getName(),
                        uniqueIds)));
                Granularity gran = entitySpec.getGranularity();
                //ColumnSpec startTimeSpec = entitySpec.getStartTimeSpec();
                ColumnSpec finishTimeSpec = entitySpec.getFinishTimeSpec();
                if (finishTimeSpec == null) {
                    try {
                        long d = entitySpec.getPositionParser()
                                .toLong(resultSet, i++);
                        event.setInterval(new PointInterval(d, gran, d, gran));
                    } catch (SQLException e) {
                        SQLGenUtil.logger().log(Level.WARNING,
                            "Could not parse timestamp. Ignoring data value.",
                            e);
                        continue;
                    }
                } else {
                    long start;
                    try {
                        start = entitySpec.getPositionParser().toLong(
                                resultSet, i++);
                    } catch (SQLException e) {
                        SQLGenUtil.logger().log(Level.WARNING,
                            "Could not parse start time. Ignoring data value.",
                            e);
                        continue;
                    }
                    long finish;
                    try {
                        finish = entitySpec.getPositionParser().toLong(
                                resultSet, i++);
                    } catch (SQLException e) {
                        SQLGenUtil.logger().log(Level.WARNING,
                            "Could not parse start time. Ignoring data value.",
                            e);
                        continue;
                    }
                    try {
                        event.setInterval(
                                new DefaultInterval(start, gran, finish,
                                gran));
                    } catch (IllegalArgumentException e) {
                        SQLGenUtil.logger().log(Level.WARNING,
                                "Could not parse the time of event '" + propId
                                + "' because finish is before start.", e);
                        continue;
                    }
                }
                PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
                for (PropertySpec propertySpec : propertySpecs) {
                    ValueType vf = propertySpec.getValueType();
                    Value value = ValueFactory.get(vf).parseValue(
                            resultSet.getString(i++));
                    event.setProperty(propertySpec.getName(), value);
                }
                // add in the datasourcebackend id to the event here
                event.setDataSourceBackendId(this.getDataSourceBackendId());
                Collections.putList(results, keyId, event);
            }
        }
    }

    private String backendNameForMessages() {
        String backendDisplayName = this.backend.getDisplayName();
        if (backendDisplayName != null) {
            return backendDisplayName + "("
                    + this.backend.getClass().getName() + ")";
        } else {
            return this.backend.getClass().getName();
        }
    }

    private Map<EntitySpec, List<String>> propSpecMapForPropIds(
            Set<String> propIds) throws AssertionError {
        Map<EntitySpec, List<String>> entitySpecToPropIdMapFromPropIds =
                new HashMap<EntitySpec, List<String>>();
        for (String propId : propIds) {
            boolean inDataSource =
                    populateEntitySpecToPropIdMap(new String[]{propId},
                    entitySpecToPropIdMapFromPropIds);
            if (!inDataSource) {
                SQLGenUtil.logger().log(Level.INFO,
                        "Data source backend {0} does not know about proposition {1}",
                        new Object[]{backendNameForMessages(), propId});
            }
        }
        return entitySpecToPropIdMapFromPropIds;
    }

    private EntitySpec entitySpec(String propId) {
        //TODO This is where the code goes for PropertySpecs defining a temp table.
        if (this.primitiveParameterSpecs.containsKey(propId)) {
            return this.primitiveParameterSpecs.get(propId);
        } else if (this.eventSpecs.containsKey(propId)) {
            return this.eventSpecs.get(propId);
        } else if (this.constantSpecs.containsKey(propId)) {
            return this.constantSpecs.get(propId);
        } else {
            return null;
        }
    }

    private boolean populateEntitySpecToPropIdMap(String[] propIds,
            Map<EntitySpec, List<String>> entitySpecToPropIdMap)
            throws AssertionError {
        boolean result = false;
        for (String propId : propIds) {
            EntitySpec entitySpec = entitySpec(propId);
            if (entitySpec != null) {
                Collections.putList(entitySpecToPropIdMap, entitySpec, propId);
                result = true;
            }
        }
        return result;
    }

    private static void populatePropositionMap(Map<String, EntitySpec> map,
            EntitySpec[] entitySpecs) {
        if (entitySpecs != null) {
            for (EntitySpec entitySpec : entitySpecs) {
                for (String code : entitySpec.getPropositionIds()) {
                    map.put(code, entitySpec);
                }
            }
        }
    }
}
