package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.collections.Collections;
import org.arp.javautil.sql.ConnectionSpec;
import org.arp.javautil.sql.SQLExecutor;
import org.arp.javautil.sql.SQLExecutor.ResultProcessor;
import org.protempa.DataSourceReadException;
import org.protempa.bp.commons.dsb.RelationalDatabaseDataSourceBackend;
import org.protempa.bp.commons.dsb.sqlgen.ColumnSpec.ConstraintValue;
import org.protempa.dsb.datasourceconstraint.AbstractDataSourceConstraintVisitor;
import org.protempa.dsb.datasourceconstraint.DataSourceConstraint;
import org.protempa.dsb.datasourceconstraint.PositionDataSourceConstraint;
import org.protempa.proposition.ConstantParameter;
import org.protempa.proposition.DefaultInterval;
import org.protempa.proposition.Event;
import org.protempa.proposition.PointInterval;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueFactory;

/**
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
    
    private <P extends Proposition> Map<String, List<P>> executeSelect(
            Set<String> propIds,
            DataSourceConstraint dataSourceConstraints,
            Set<String> keyIds,
            SQLOrderBy order, ResultProcessorAllKeyIds<P> resultProcessor)
            throws DataSourceReadException{
        Map<EntitySpec, List<String>> propSpecMapFromConstraints =
                propSpecMapForConstraints(dataSourceConstraints);
        Map<EntitySpec, List<String>> propSpecMapFromPropIds =
                propSpecMapForPropIds(propIds);

        //Collection<Map<PropertySpec, List<String>>> batchMap =
        //        generateBatches(propertySpecToPropIdMapFromConstraints);

        Map<String, List<P>> results = new HashMap<String, List<P>>();
        //for (Map<PropertySpec, List<String>> m : batchMap) {
        for (EntitySpec entitySpec : propSpecMapFromPropIds.keySet()) {
            Set<EntitySpec> entitySpecs = new HashSet<EntitySpec>(
                    propSpecMapFromConstraints.keySet());
            entitySpecs.add(entitySpec);
            String query = generateSelect(entitySpec, propIds,
                    dataSourceConstraints, entitySpecs, keyIds, order);

            SQLGenUtil.logger().log(Level.INFO, "Executing query: {0}", query);

            resultProcessor.setEntitySpec(entitySpec);

            try {
                SQLExecutor.executeSQL(getConnectionSpec(),
                        query, resultProcessor);
            } catch (SQLException ex) {
                throw new DataSourceReadException(ex);
            }

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
        }
        return results;
    }
    
    protected abstract boolean isLimitingSupported();

    @Override
    public Map<String, List<ConstantParameter>> readConstantParameters(
            Set<String> keyIds, Set<String> paramIds,
            DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        Map<String, List<ConstantParameter>> results =
                new HashMap<String, List<ConstantParameter>>();

        ConstantParameterResultProcessor resultProcessor =
                new ConstantParameterResultProcessor();
        resultProcessor.setResults(results);

        return executeSelect(paramIds, dataSourceConstraints, 
                keyIds, null, resultProcessor);
    }

    @Override
    public Map<String, List<PrimitiveParameter>> readPrimitiveParameters(
            Set<String> keyIds, Set<String> paramIds,
            DataSourceConstraint dataSourceConstraints, SQLOrderBy order)
            throws DataSourceReadException {
        Map<String, List<PrimitiveParameter>> results =
                new HashMap<String, List<PrimitiveParameter>>();

        PrimitiveParameterResultProcessorAllKeyIds resultProcessor =
                new PrimitiveParameterResultProcessorAllKeyIds();
        resultProcessor.setResults(results);

        return executeSelect(paramIds, dataSourceConstraints,
                keyIds, order, resultProcessor);
    }

    @Override
    public Map<String, List<Event>> readEvents(Set<String> keyIds,
            Set<String> eventIds, DataSourceConstraint dataSourceConstraints,
            SQLOrderBy order) throws DataSourceReadException {
        Map<String, List<Event>> results = new HashMap<String, List<Event>>();

        EventResultProcessorAllKeyIds resultProcessor =
                new EventResultProcessorAllKeyIds();
        resultProcessor.setResults(results);

        return executeSelect(eventIds, dataSourceConstraints,
                keyIds, order, resultProcessor);
    }
    
    public final String generateSelect(EntitySpec entitySpec,
            Set<String> propIds,
            DataSourceConstraint dataSourceConstraints,
            Set<EntitySpec> entitySpecs, Set<String> keyIds,
            SQLOrderBy order) {
        ColumnSpecInfo info = new ColumnSpecInfoFactory().newInstance(
                entitySpec, entitySpecs);
        Map<ColumnSpec, Integer> referenceIndices =
                computeReferenceIndices(info.getColumnSpecs());
        StringBuilder selectClause = generateSelectClause(info,
                referenceIndices);
        StringBuilder fromClause = generateFromClause(info.getColumnSpecs(),
                referenceIndices);
        StringBuilder whereClause = generateWhereClause(propIds, info,
                entitySpecs, dataSourceConstraints, selectClause,
                referenceIndices, keyIds, order);
        String result = assembleReadPropositionsQuery(
                selectClause, fromClause, whereClause);
        return result;
    }

    public abstract String assembleReadPropositionsQuery(
            StringBuilder selectClause, StringBuilder fromClause,
            StringBuilder whereClause);

    private ConstraintValue[] filterConstraintValues(Set<String> propIds,
            ConstraintValue[] constraintValues) {
        ColumnSpec.ConstraintValue[] filteredConstraintValues;
        if (propIds != null) {
            List<ColumnSpec.ConstraintValue> lccv =
                    new ArrayList<ColumnSpec.ConstraintValue>();
            for (ColumnSpec.ConstraintValue ccv : constraintValues) {
                if (propIds.contains(ccv.getCode())) {
                    lccv.add(ccv);
                }
            }
            filteredConstraintValues =
                    lccv.toArray(new ColumnSpec.ConstraintValue[lccv.size()]);
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
        i++; //key id
        int[] indices = new int[i];
        String[] names = new String[i];
        int k = 0;
        indices[k] = 0;
        names[k++] = "keyid";
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
                ColumnSpec colSpecFromCache = columnSpecCache.get(i);
                if (colSpecFromCache != null) {
                    generateFromTableReference(i, fromPart);
                } else {
                    String schema = columnSpec.getSchema();
                    String table = columnSpec.getTable();
                    generateFromTable(schema, table, fromPart, i);
                    columnSpecCache.put(i, columnSpec);
                }
                begin = false;
            }

            if (currentJoin != null) {
                int fromIndex = referenceIndices.get(
                        currentJoin.getPrevColumnSpec());
                int toIndex = referenceIndices.get(
                        currentJoin.getNextColumnSpec());
                generateOn(fromPart, fromIndex, toIndex,
                        currentJoin.getFromKey(),
                        currentJoin.getToKey());
            }

            if (columnSpec.getJoin() != null) {
                generateJoin(fromPart);
                currentJoin = columnSpec.getJoin();
            } else {
                currentJoin = null;
            }
        }
        return fromPart;
    }

    private StringBuilder generateWhereClause(Set<String> propIds,
            ColumnSpecInfo info,
            Collection<EntitySpec> propertySpecs,
            DataSourceConstraint dataSourceConstraints,
            StringBuilder selectPart,
            Map<ColumnSpec, Integer> referenceIndices,
            Set<String> keyIds, SQLOrderBy order) {
        StringBuilder wherePart = new StringBuilder();
        int i = 1;
        for (EntitySpec propositionSpec : propertySpecs) {
            i = processKeySpecForWhereClause(propositionSpec, i);
            i = processStartTimeSpecForWhereClause(propositionSpec, i,
                    dataSourceConstraints, wherePart, referenceIndices);
            i = processFinishTimeSpecForWhereClause(propositionSpec, i,
                    dataSourceConstraints, wherePart, referenceIndices);
            i = processPropertyValueSpecsForWhereClause(propositionSpec, i);
            i = processConstraintSpecsForWhereClause(propIds, propositionSpec,
                    i, wherePart, selectPart, referenceIndices);
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

    private int processPropertyValueSpecsForWhereClause(
            EntitySpec entitySpec, int i) {
        PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
        if (propertySpecs != null) {
            Map<String, Integer> propertyValueIndices =
                    new HashMap<String, Integer>();
            for (PropertySpec propertySpec : propertySpecs) {
                ColumnSpec spec = propertySpec.getCodeSpec();
                if (spec != null) {
                    i += spec.asList().size();
                    propertyValueIndices.put(propertySpec.getName(), i);
                }
            }
        }
        return i;
    }

    private int processConstraintSpecsForWhereClause(
            Set<String> propIds,
            EntitySpec propositionSpec,
            int i, StringBuilder wherePart, StringBuilder selectPart,
            Map<ColumnSpec, Integer> referenceIndices) {
        boolean selectPartHasCaseStmt = false;
        ColumnSpec[] constraintSpecs = propositionSpec.getConstraintSpecs();
        if (constraintSpecs != null) {
            for (ColumnSpec cs3 : constraintSpecs) {
                i = processConstraintSpecForWhereClause(null, cs3, i, wherePart,
                        selectPart, referenceIndices, selectPartHasCaseStmt);
            }
        }
        ColumnSpec codeSpec = propositionSpec.getCodeSpec();
        i = processConstraintSpecForWhereClause(propIds, codeSpec, i, wherePart,
                selectPart, referenceIndices, selectPartHasCaseStmt);
        if (selectPartHasCaseStmt) {
            selectPart.append(" else 'OTHER' end ");
            selectPart.append("code ");
        }
        return i;
    }

    private int processConstraintSpecForWhereClause(
            Set<String> propIds,
            ColumnSpec columnSpec,
            int i, StringBuilder wherePart, StringBuilder selectPart,
            Map<ColumnSpec, Integer> referenceIndices,
            boolean selectPartHasCaseStmt) {
        if (columnSpec == null) {
            return i;
        }
        while (true) {
            if (columnSpec.getJoin() != null) {
                columnSpec = columnSpec.getJoin().getNextColumnSpec();
                i++;
            } else {
                if (wherePart.length() > 0) {
                    wherePart.append(" and ");
                }
                wherePart.append('(');
                ColumnSpec.Constraint csc = columnSpec.getConstraint();
                if (csc != null) {
                    ColumnSpec.ConstraintValue[] constraintValues =
                            columnSpec.getConstraintValues();
                    ConstraintValue[] filteredConstraintValues =
                            filterConstraintValues(propIds, constraintValues);
                    switch (csc) {
                        case EQUAL_TO:
                            if (filteredConstraintValues.length > 1) {
                                generateInClause(wherePart,
                                        referenceIndices.get(columnSpec),
                                        columnSpec.getColumn(),
                                        filteredConstraintValues);
                            } else {
                                wherePart.append("a");
                                wherePart.append(
                                        referenceIndices.get(columnSpec));
                                wherePart.append(".");
                                wherePart.append(columnSpec.getColumn());
                                wherePart.append("=");
                                appendValue(
                                        filteredConstraintValues[0].getValue(),
                                        wherePart);
                            }
                            break;
                        case LIKE:
                            if (!selectPartHasCaseStmt) {
                                selectPart.append(", case ");
                                selectPartHasCaseStmt = true;
                            }
                            if (filteredConstraintValues.length > 1) {
                                wherePart.append('(');
                            }
                            for (int k = 0; k < filteredConstraintValues.length; k++) {
                                wherePart.append("a");
                                wherePart.append(
                                        referenceIndices.get(columnSpec));
                                wherePart.append(".");
                                wherePart.append(columnSpec.getColumn());
                                wherePart.append(" LIKE ");
                                appendValue(filteredConstraintValues[k].getValue(),
                                        wherePart);
                                if (k + 1 < filteredConstraintValues.length) {
                                    wherePart.append(" or ");
                                }
                                selectPart.append("when ");
                                selectPart.append("a");
                                selectPart.append(
                                        referenceIndices.get(columnSpec));
                                selectPart.append(".");
                                selectPart.append(columnSpec.getColumn());
                                selectPart.append(" like ");
                                appendValue(filteredConstraintValues[k].getValue(),
                                        selectPart);
                                selectPart.append(" then ");
                                selectPart.append("'");
                                appendValue(filteredConstraintValues[k].getCode(),
                                        selectPart);
                                selectPart.append("'");
                            }
                            if (filteredConstraintValues.length > 1) {
                                wherePart.append(')');
                            }
                            break;
                        default:
                            throw new AssertionError(
                                    "should not happen");
                    }
                }
                wherePart.append(')');
                break;
            }
        }
        return i;
    }

    private int processFinishTimeSpecForWhereClause(
            EntitySpec propertySpec, int i,
            DataSourceConstraint dataSourceConstraints,
            StringBuilder wherePart,
            Map<ColumnSpec, Integer> referenceIndices) {
        ColumnSpec finishTimeSpec = propertySpec.getFinishTimeSpec();
        if (finishTimeSpec != null) {
            while (true) {
                if (finishTimeSpec.getJoin() != null) {
                    finishTimeSpec = finishTimeSpec.getJoin().getNextColumnSpec();
                    i++;
                } else {
                    if (dataSourceConstraints != null) {
                        for (Iterator<DataSourceConstraint> itr =
                                dataSourceConstraints.andIterator();
                                itr.hasNext();) {
                            DataSourceConstraint dsc = itr.next();
                            if (!Arrays.contains(propertySpec.getCodes(),
                                    dsc.getPropositionId())) {
                                continue;
                            }
                            if (dsc instanceof PositionDataSourceConstraint) {
                                PositionDataSourceConstraint pdsc2 =
                                        (PositionDataSourceConstraint) dsc;

                                if (wherePart.length() > 0) {
                                    wherePart.append(" and ");
                                }

                                wherePart.append('a');
                                wherePart.append(
                                        referenceIndices.get(finishTimeSpec));
                                wherePart.append('.');
                                wherePart.append(finishTimeSpec.getColumn());
                                wherePart.append(" <= {ts '");
                                wherePart.append(
                                        AbsoluteTimeGranularity.toSQLString(
                                        pdsc2.getMaximumFinish()));
                                wherePart.append("'}");
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
            EntitySpec propertySpec, int i,
            DataSourceConstraint dataSourceConstraints,
            StringBuilder wherePart,
            Map<ColumnSpec, Integer> referenceIndices) {
        ColumnSpec startTimeSpec = propertySpec.getStartTimeSpec();
        if (startTimeSpec != null) {
            while (true) {
                if (startTimeSpec.getJoin() != null) {
                    startTimeSpec = startTimeSpec.getJoin().getNextColumnSpec();
                    i++;
                } else {
                    if (dataSourceConstraints != null) {
                        for (Iterator<DataSourceConstraint> itr =
                                dataSourceConstraints.andIterator();
                                itr.hasNext();) {
                            DataSourceConstraint dsc = itr.next();
                            if (!Arrays.contains(propertySpec.getCodes(),
                                    dsc.getPropositionId())) {
                                continue;
                            }
                            if (dsc instanceof PositionDataSourceConstraint) {
                                PositionDataSourceConstraint pdsc2 =
                                        (PositionDataSourceConstraint) dsc;

                                if (wherePart.length() > 0) {
                                    wherePart.append(" and ");
                                }

                                wherePart.append('a');
                                wherePart.append(
                                        referenceIndices.get(startTimeSpec));
                                wherePart.append('.');
                                wherePart.append(startTimeSpec.getColumn());
                                wherePart.append(" >= {ts '");
                                wherePart.append(
                                        AbsoluteTimeGranularity.toSQLString(
                                        pdsc2.getMinimumStart()));
                                wherePart.append("'}");
                                if (propertySpec.getFinishTimeSpec() == null) {
                                    wherePart.append(" and ");
                                    wherePart.append('a');
                                    wherePart.append(
                                            referenceIndices.get(startTimeSpec));
                                    wherePart.append('.');
                                    wherePart.append(startTimeSpec.getColumn());
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
            int referenceIndex, String column,
            ColumnSpec.ConstraintValue[] constraintValue);

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

    private class GetAllKeyIdsDataSourceConstraintVisitor
            extends AbstractDataSourceConstraintVisitor {

        private Long minStart;
        private Long maxFinish;
        private String propId;

        @Override
        public void visit(PositionDataSourceConstraint constraint) {
            this.minStart = constraint.getMinimumStart();
            this.maxFinish = constraint.getMaximumFinish();
            this.propId = constraint.getPropositionId();
        }

        /**
         * @return the minStart
         */
        Long getMinimumStart() {
            return this.minStart;
        }

        /**
         * @return the minFinish
         */
        Long getMaximumFinish() {
            return this.maxFinish;
        }

        String getPropositionId() {
            return this.propId;
        }
    }

    @Override
    public final void loadDriverIfNeeded() {
        String className = getDriverClassNameToLoad();
        try {
            Class.forName(className);
        } catch (ClassNotFoundException ex) {
            SQLGenUtil.logger().log(Level.WARNING,
                    "{0} when trying to load {1}.",
                    new Object[] {ex.getClass().getName(), className});
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
            String[] codes = entitySpec.getCodes();
            while (resultSet.next()) {
                String propId;
                
                if (codes.length == 1) {
                    propId = codes[0];
                } else {
                    propId = resultSet.getString(3);
                }
                
                ValueFactory vf = entitySpec.getValueType();
                ConstantParameter cp =
                        new ConstantParameter(propId);
                String keyId = resultSet.getString(1);
                cp.setValue(vf.getInstance(resultSet.getString(2)));

                int i = 4;
                PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
                if (propertySpecs != null) {
                    for (PropertySpec propertySpec : propertySpecs) {
                        ValueFactory vf2 = propertySpec.getValueType();
                        Value value = vf2.getInstance(resultSet.getString(i++));
                        cp.setProperty(propertySpec.getName(), value);
                    }
                }

                Collections.putList(results, keyId, cp);
            }
        }
    }

    private static abstract class ResultProcessorAllKeyIds<P extends Proposition> implements ResultProcessor {

        private Map<String, List<P>> results;
        private EntitySpec entitySpec;

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
            String[] codes = entitySpec.getCodes();
            while (resultSet.next()) {
                String keyId = resultSet.getString(1);
                String propId;
                if (codes.length == 1) {
                    propId = codes[0];
                } else {
                    propId = resultSet.getString(2);
                }
                ValueFactory vf = entitySpec.getValueType();
                PrimitiveParameter p = new PrimitiveParameter(propId);
                try {
                    p.setTimestamp(entitySpec.getPositionParser().toLong(resultSet, 3));
                } catch (SQLException e) {
                    SQLGenUtil.logger().log(Level.WARNING,
                            "Could not parse timestamp. Ignoring data value.",
                            e);
                    continue;
                }
                p.setGranularity(entitySpec.getGranularity());
                p.setValue(vf.getInstance(resultSet.getString(3)));

                int i = 4;
                PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
                if (propertySpecs != null) {
                    for (PropertySpec propertySpec : propertySpecs) {
                        ValueFactory vf2 = propertySpec.getValueType();
                        Value value = vf2.getInstance(resultSet.getString(i++));
                        p.setProperty(propertySpec.getName(), value);
                    }
                }

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
                String propId;
                String[] codes = entitySpec.getCodes();
                if (codes.length == 1) {
                    propId = codes[0];
                } else {
                    propId = resultSet.getString(i++);
                }
                Event event = new Event(propId);
                Granularity gran = entitySpec.getGranularity();
                //ColumnSpec startTimeSpec = entitySpec.getStartTimeSpec();
                ColumnSpec finishTimeSpec = entitySpec.getFinishTimeSpec();
                if (finishTimeSpec == null) {
                    try {
                        long d = entitySpec.getPositionParser().toLong(resultSet,
                                i++);
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
                        start = entitySpec.getPositionParser().toLong(resultSet,
                                i++);
                    } catch (SQLException e) {
                        SQLGenUtil.logger().log(Level.WARNING,
                            "Could not parse start time. Ignoring data value.",
                                e);
                        continue;
                    }
                    long finish;
                    try {
                        finish = entitySpec.getPositionParser().toLong(resultSet,
                                i++);
                    } catch (SQLException e) {
                        SQLGenUtil.logger().log(Level.WARNING,
                            "Could not parse start time. Ignoring data value.",
                                e);
                        continue;
                    }
                    try {
                        event.setInterval(
                            new DefaultInterval(start, gran, finish, gran));
                    } catch (IllegalArgumentException e) {
                        SQLGenUtil.logger().log(Level.WARNING,
                        "Could not parse the time of event '" + propId + 
                                "' because finish is before start.", e);
                        continue;
                    }
                }
                PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
                if (propertySpecs != null) {
                    for (PropertySpec propertySpec : propertySpecs) {
                        ValueFactory vf = propertySpec.getValueType();
                        Value value = vf.getInstance(resultSet.getString(i++));
                        event.setProperty(propertySpec.getName(), value);
                    }
                }
                Collections.putList(results, keyId, event);
            }
        }
    }

    private String backendNameForErrors() {
        String backendDisplayName = this.backend.getDisplayName();
        if (backendDisplayName != null)
            return backendDisplayName + "(" +
                    this.backend.getClass().getName() + ")";
        else
            return this.backend.getClass().getName();
    }

    private Map<EntitySpec, List<String>> propSpecMapForPropIds(
            Set<String> propIds) throws AssertionError {
        Map<EntitySpec, List<String>> propertySpecToPropIdMapFromPropIds =
                new HashMap<EntitySpec, List<String>>();
        for (String propId : propIds) {
            boolean inDataSource =
                    populatePropertySpecToPropIdMap(propId,
                    propertySpecToPropIdMapFromPropIds);
            if (!inDataSource) {
                SQLGenUtil.logger().log(Level.INFO,
                 "Data source backend {0} does not know about proposition {1}",
                        new Object[] {backendNameForErrors(), propId});
            }
        }
        return propertySpecToPropIdMapFromPropIds;
    }

    private EntitySpec propertySpec(String propId) {
        //TODO This is where the code goes for PropertySpecs defining a temp table.
        if (this.primitiveParameterSpecs.containsKey(propId))
            return this.primitiveParameterSpecs.get(propId);
        else if (this.eventSpecs.containsKey(propId))
            return this.eventSpecs.get(propId);
        else if (this.constantSpecs.containsKey(propId))
            return this.constantSpecs.get(propId);
        else
            return null;
    }

    private boolean populatePropertySpecToPropIdMap(String propId,
            Map<EntitySpec, List<String>> propertySpecToPropIdMap)
            throws AssertionError {
        EntitySpec propertySpec = propertySpec(propId);
        if (propertySpec == null)
            return false;
        Collections.putList(propertySpecToPropIdMap, propertySpec, propId);
        return true;
    }

    private Map<EntitySpec, List<String>> propSpecMapForConstraints(
            DataSourceConstraint dataSourceConstraints)
            throws DataSourceReadException {
        Map<EntitySpec, List<String>> propertySpecToPropIdMap =
                new HashMap<EntitySpec, List<String>>();
        if (dataSourceConstraints != null) {
            for (Iterator<DataSourceConstraint> itr =
                    dataSourceConstraints.andIterator(); itr.hasNext();) {
                DataSourceConstraint dsc = itr.next();
                String propId = dsc.getPropositionId();
                boolean inDataSource =
                        populatePropertySpecToPropIdMap(propId,
                        propertySpecToPropIdMap);
                if (!inDataSource) {
                    //FIXME this error message should refer to the data source
                    //backend, but we have not reference to it.
                    String msg =
                            "Data source constraint {0} on proposition {1} "
                            + "cannot be applied to this data source backend "
                            + "because the backend does not know about {1}";
                    MessageFormat mf = new MessageFormat(msg);
                    throw new DataSourceReadException(
                            mf.format(new Object[]{dsc, propId}));
                }
            }
        }
        return propertySpecToPropIdMap;
    }

    private static void populatePropositionMap(Map<String, EntitySpec> map,
            EntitySpec[] entitySpecs) {
        if (entitySpecs != null) {
            for (EntitySpec entitySpec : entitySpecs) {
                for (String code : entitySpec.getCodes()) {
                    map.put(code, entitySpec);
                }
            }
        }
    }
}
