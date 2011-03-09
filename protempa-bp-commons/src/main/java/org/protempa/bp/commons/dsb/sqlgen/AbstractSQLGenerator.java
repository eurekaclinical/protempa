package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.collections.Collections;
import org.arp.javautil.sql.ConnectionSpec;
import org.arp.javautil.sql.SQLExecutor;
import org.protempa.DataSourceReadException;
import org.protempa.bp.commons.dsb.RelationalDatabaseDataSourceBackend;
import org.protempa.bp.commons.dsb.sqlgen.ColumnSpec.Constraint;
import org.protempa.bp.commons.dsb.sqlgen.ColumnSpec.KnowledgeSourceIdToSqlCode;
import org.protempa.dsb.filter.Filter;
import org.protempa.dsb.filter.PositionFilter;
import org.protempa.dsb.filter.PositionFilter.Side;
import org.protempa.dsb.filter.PropertyValueFilter;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.ListValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.OrdinalValue;
import org.protempa.proposition.value.UnitFactory;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueVisitor;

/**
 * Abstract class for implement database and driver-specific SQL generators.
 * 
 * @author Andrew Post
 */
public abstract class AbstractSQLGenerator implements ProtempaSQLGenerator {

    private static final String SYSTEM_PROPERTY_SKIP_EXECUTION =
            "protempa.dsb.relationaldatabase.skipexecution";
    private static final int FETCH_SIZE = 1000;

    private static Set<EntitySpec> applicableEntitySpecs(
            Collection<EntitySpec> entitySpecs, Filter f) {
        Set<EntitySpec> entitySpecsSet = new HashSet<EntitySpec>();
        for (EntitySpec es : entitySpecs) {
            PROPIDS:
            for (String propId : es.getPropositionIds()) {
                for (String propId2 : f.getPropositionIds()) {
                    if (propId.equals(propId2)) {
                        entitySpecsSet.add(es);
                        break PROPIDS;
                    }
                }
            }
        }
        return entitySpecsSet;
    }

    private static boolean isInInboundReference(Set<EntitySpec> entitySpecsSet,
            EntitySpec entitySpec) {
        for (EntitySpec es : entitySpecsSet) {
            if (SQLGenUtil.isInReferences(entitySpec,
                    es.getReferenceSpecs())) {
                return true;
            }
        }
        return false;
    }
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
                    relationalDatabaseSpec.getConstantSpecs());
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

    private void appendColumnRef(StringBuilder wherePart,
            Map<ColumnSpec, Integer> referenceIndices, ColumnSpec columnSpec) {
        if (columnSpec.getColumnOp() != null) {
            wherePart.append(introduceOp(columnSpec.getColumnOp(),
                    appendColumnReference(referenceIndices, columnSpec)));
        } else {
            wherePart.append(appendColumnReference(referenceIndices,
                    columnSpec));
        }
    }

    public String introduceOp(ColumnSpec.ColumnOp columnOp,
            String columnRef) {
        StringBuilder builder = new StringBuilder();
        switch (columnOp) {
            case UPPER:
                builder.append("upper");
                break;
            default:
                throw new AssertionError("invalid column op: " + columnOp);
        }
        builder.append('(');
        builder.append(columnRef);
        builder.append(')');
        return builder.toString();
    }

    public String appendColumnReference(
            Map<ColumnSpec, Integer> referenceIndices,
            ColumnSpec columnSpec) {
        StringBuilder builder = new StringBuilder();
        builder.append("a");
        Integer tableNumber = referenceIndices.get(columnSpec);
        assert tableNumber != null : "tableNumber is null";
        builder.append(tableNumber);
        builder.append(".");
        builder.append(columnSpec.getColumn());
        return builder.toString();
    }

    private void executeSelect(Logger logger, String backendNameForMessages,
            String entitySpecName, String query,
            SQLGenResultProcessor resultProcessor)
            throws DataSourceReadException {
        if (Boolean.getBoolean(SYSTEM_PROPERTY_SKIP_EXECUTION)) {
            logger.log(Level.INFO,
                    "Data source backend {0} is skipping query for {1}",
                    new Object[]{backendNameForMessages, entitySpecName});
        } else {
            logger.log(Level.FINE,
                    "Data source backend {0} is executing query for {1}",
                    new Object[]{backendNameForMessages, entitySpecName});

            try {
                SQLExecutor.executeSQL(getConnectionSpec(), query,
                        resultProcessor, true, FETCH_SIZE);
            } catch (SQLException ex) {
                throw new DataSourceReadException(
                        "Error executing query in data source backend "
                        + backendNameForMessages + " for " + entitySpecName, ex);
            }
            logger.log(Level.FINE,
                    "Query for {0} in data source backend {1} is complete",
                    new Object[]{entitySpecName, backendNameForMessages});
        }
    }

    private Object[] extractSqlCodes(KnowledgeSourceIdToSqlCode[] filteredConstraintValues) {
        Object[] sqlCodes = new Object[filteredConstraintValues.length];
        for (int i = 0; i < sqlCodes.length; i++) {
            sqlCodes[i] = filteredConstraintValues[i].getSqlCode();
        }
        return sqlCodes;
    }

    private <P extends Proposition> ResultCache<P> readPropositions(
            Set<String> propIds, Filter filters,
            Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessorFactory<P> factory)
            throws DataSourceReadException {
        Map<EntitySpec, List<String>> entitySpecMapFromPropIds =
                entitySpecMapForPropIds(propIds);

        ResultCache<P> results = new ResultCache<P>();

        Collection<EntitySpec> allEntitySpecs = allEntitySpecs();
        Logger logger = SQLGenUtil.logger();
        for (EntitySpec entitySpec : entitySpecMapFromPropIds.keySet()) {
            logger.log(Level.FINE,
                    "Data source backend {0} is processing entity spec {1}",
                    new Object[]{backendNameForMessages(),
                        entitySpec.getName()});
            List<EntitySpec> allEntitySpecsCopy =
                    new ArrayList<EntitySpec>(allEntitySpecs);
            removeNonApplicableEntitySpecs(entitySpec, allEntitySpecsCopy);

            if (logger.isLoggable(Level.FINER)) {
                String[] allEntitySpecsCopyNames =
                        new String[allEntitySpecsCopy.size()];
                int i = 0;
                for (EntitySpec aesc : allEntitySpecsCopy) {
                    allEntitySpecsCopyNames[i++] = aesc.getName();
                }
                logger.log(Level.FINER,
                        "Applicable entity specs are {0}",
                        StringUtils.join(allEntitySpecsCopyNames, ", "));
            }

            Set<Filter> filtersCopy = copyFilters(filters);
            removeNonApplicableFilters(allEntitySpecs, filtersCopy,
                    entitySpec);
            assert !allEntitySpecsCopy.isEmpty() :
                    "allEntitySpecsCopy should have at least one element";
            String dataSourceBackendId =
                    this.backend.getDataSourceBackendId();
            AbstractMainResultProcessor<P> resultProcessor =
                    factory.getInstance(dataSourceBackendId, entitySpec, results);
            generateAndExecuteSelect(entitySpec, null, propIds, filtersCopy,
                    allEntitySpecsCopy, keyIds, order, resultProcessor);

            ReferenceSpec[] refSpecs = entitySpec.getReferenceSpecs();
            if (refSpecs != null) {
                /*
                 * Create a copy of allEntitySpecs with the current entitySpec
                 * the first item of the list. This is to make sure that
                 * its joins make it into the list of column specs.
                 */
                for (ReferenceSpec referenceSpec : refSpecs) {
                    RefResultProcessor<P> refResultProcessor =
                            factory.getRefInstance(dataSourceBackendId,
                            entitySpec, referenceSpec, results);
                    logger.log(Level.FINE,
                            "Data source backend {0} is processing reference {1} for entity spec {2}",
                            new Object[]{backendNameForMessages(),
                                referenceSpec.getReferenceName(), entitySpec.getName()});
                    List<EntitySpec> allEntitySpecsCopyForRefs =
                            new ArrayList<EntitySpec>(allEntitySpecs.size());
                    allEntitySpecsCopyForRefs.add(entitySpec);
                    for (EntitySpec es : allEntitySpecs) {
                        if (es != entitySpec) {
                            allEntitySpecsCopyForRefs.add(es);
                        }
                    }
                    Set<Filter> refFiltersCopy = copyFilters(filters);
                    EntitySpec referredToEntitySpec = null;
                    for (EntitySpec reffedToSpec : allEntitySpecsCopyForRefs) {
                        if (referenceSpec.getEntityName().equals(
                                reffedToSpec.getName())) {
                            referredToEntitySpec = reffedToSpec;
                            break;
                        }
                    }
                    assert referredToEntitySpec != null :
                            "refferedToEntitySpec should not be null";

                    for (Iterator<EntitySpec> itr =
                            allEntitySpecsCopyForRefs.iterator();
                            itr.hasNext();) {
                        EntitySpec es = itr.next();
                        if (es != entitySpec
                                && !referenceSpec.getEntityName().equals(
                                es.getName())
                                && !SQLGenUtil.isInReferences(entitySpec,
                                es.getReferenceSpecs())) {
                            itr.remove();
                        }
                    }
                    removeNonApplicableFilters(allEntitySpecsCopyForRefs,
                            refFiltersCopy, referredToEntitySpec);
                    generateAndExecuteSelect(entitySpec, referenceSpec, propIds,
                            refFiltersCopy, allEntitySpecsCopyForRefs, keyIds,
                            order, refResultProcessor);
                    logger.log(Level.FINE, "Data source backend {0} is done processing reference {1} for entity spec {2}",
                            new Object[]{backendNameForMessages(),
                                referenceSpec.getReferenceName(), entitySpec.getName()});
                }

                logger.log(Level.FINE,
                        "Data source backend {0} is done processing entity spec {1}",
                        new Object[]{backendNameForMessages(),
                            entitySpec.getName()});
            }

            results.clear();

            logger.log(Level.FINE,
                    "Results of query for {0} in data source backend {1} "
                    + "have been processed",
                    new Object[]{entitySpec.getName(),
                        backendNameForMessages()});
        }
        return results;
    }

    private List<EntitySpec> allEntitySpecs() {
        Set<EntitySpec> entitySpecs = new HashSet<EntitySpec>();
        entitySpecs.addAll(this.eventSpecs.values());
        entitySpecs.addAll(this.primitiveParameterSpecs.values());
        entitySpecs.addAll(this.constantSpecs.values());
        return new ArrayList<EntitySpec>(entitySpecs);
    }

    private static Set<Filter> copyFilters(Filter filters) {
        Set<Filter> filtersCopy = new HashSet<Filter>();
        if (filters != null) {
            for (Iterator<Filter> itr = filters.andIterator();
                    itr.hasNext();) {
                filtersCopy.add(itr.next());
            }
        }
        return filtersCopy;
    }

    private <P extends Proposition> void generateAndExecuteSelect(
            EntitySpec entitySpec, ReferenceSpec referenceSpec,
            Set<String> propIds,
            Set<Filter> filtersCopy, Collection<EntitySpec> entitySpecsCopy,
            Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor)
            throws DataSourceReadException {
        Logger logger = SQLGenUtil.logger();
        String backendNameForMessages = backendNameForMessages();
        String entitySpecName = entitySpec.getName();

        logger.log(Level.FINE,
                "Data source backend {0} is generating query for {1}",
                new Object[]{backendNameForMessages, entitySpecName});

        String query = generateSelect(entitySpec, referenceSpec, propIds,
                filtersCopy, entitySpecsCopy, keyIds, order, resultProcessor);

        logger.log(Level.FINE,
                "Data source backend {0} generated the following query for {1}: {2}",
                new Object[]{backendNameForMessages, entitySpecName, query});

        executeSelect(logger, backendNameForMessages, entitySpecName, query,
                resultProcessor);
    }

    private static void removeNonApplicableEntitySpecs(EntitySpec entitySpec,
            Collection<EntitySpec> entitySpecs) {
        for (Iterator<EntitySpec> itr = entitySpecs.iterator();
                itr.hasNext();) {
            EntitySpec es = itr.next();
            if (es != entitySpec
                    && !SQLGenUtil.isInReferences(entitySpec,
                    es.getReferenceSpecs())) {
                itr.remove();
            }
        }

    }

    /**
     * Remove filters that are not directly applicable to the given
     * entity spec and are not applicable to other entity specs that refer
     * to it.
     *
     * @param entitySpecs
     * @param filters
     * @param entitySpec
     */
    private static void removeNonApplicableFilters(
            Collection<EntitySpec> entitySpecs, Set<Filter> filtersCopy,
            EntitySpec entitySpec) {
        for (Iterator<Filter> itr = filtersCopy.iterator(); itr.hasNext();) {
            Filter f = itr.next();
            Set<EntitySpec> entitySpecsSet =
                    applicableEntitySpecs(entitySpecs, f);
            for (String propId : f.getPropositionIds()) {
                for (String propId2 : entitySpec.getPropositionIds()) {
                    if (propId.equals(propId2)) {
                        return;
                    }
                }
            }
            if (!isInInboundReference(entitySpecsSet, entitySpec)) {
                itr.remove();
            }
        }
    }

    protected abstract boolean isLimitingSupported();

    @Override
    public ResultCache<Constant> readConstants(
            Set<String> keyIds, Set<String> paramIds, Filter filters)
            throws DataSourceReadException {
        ConstantResultProcessorFactory factory =
                new ConstantResultProcessorFactory();

        return readPropositions(paramIds, filters, keyIds, null, factory);
    }

    @Override
    public ResultCache<PrimitiveParameter> readPrimitiveParameters(
            Set<String> keyIds, Set<String> paramIds, Filter filters,
            SQLOrderBy order)
            throws DataSourceReadException {
        PrimitiveParameterResultProcessorFactory factory =
                new PrimitiveParameterResultProcessorFactory();

        return readPropositions(paramIds, filters, keyIds, order, factory);
    }

    @Override
    public ResultCache<Event> readEvents(Set<String> keyIds,
            Set<String> eventIds, Filter filters,
            SQLOrderBy order) throws DataSourceReadException {
        EventResultProcessorFactory factory =
                new EventResultProcessorFactory();

        return readPropositions(eventIds, filters, keyIds, order, factory);
    }

    private final String generateSelect(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, Set<String> propIds,
            Set<Filter> filtersCopy, Collection<EntitySpec> entitySpecsCopy,
            Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor) {
        ColumnSpecInfo info = new ColumnSpecInfoFactory().newInstance(
                entitySpec, entitySpecsCopy, filtersCopy, referenceSpec);
        Map<ColumnSpec, Integer> referenceIndices =
                computeReferenceIndices(info.getColumnSpecs());
        StringBuilder selectClause = generateSelectClause(info,
                referenceIndices, entitySpec);
        StringBuilder fromClause = generateFromClause(info.getColumnSpecs(),
                referenceIndices);
        StringBuilder whereClause = generateWhereClause(propIds,
                info, entitySpecsCopy, filtersCopy, selectClause,
                referenceIndices, keyIds, order, resultProcessor);
        String result = assembleReadPropositionsQuery(
                selectClause, fromClause, whereClause);
        return result;
    }

    public abstract String assembleReadPropositionsQuery(
            StringBuilder selectClause, StringBuilder fromClause,
            StringBuilder whereClause);

    private static KnowledgeSourceIdToSqlCode[] filterKnowledgeSourceIdToSqlCodesById(
            Set<?> propIds, KnowledgeSourceIdToSqlCode[] constraintValues) {
        ColumnSpec.KnowledgeSourceIdToSqlCode[] filteredConstraintValues;
        if (propIds != null) {
            List<ColumnSpec.KnowledgeSourceIdToSqlCode> constraintValueList =
                    new ArrayList<ColumnSpec.KnowledgeSourceIdToSqlCode>();
            for (ColumnSpec.KnowledgeSourceIdToSqlCode constraintValue :
                    constraintValues) {
                if (propIds.contains(constraintValue.getPropositionId())) {
                    constraintValueList.add(constraintValue);
                }
            }
            if (constraintValueList.isEmpty()) {
                filteredConstraintValues = constraintValues;
            } else {
                filteredConstraintValues =
                        constraintValueList.toArray(
                        new ColumnSpec.KnowledgeSourceIdToSqlCode[constraintValueList.size()]);
            }
        } else {
            filteredConstraintValues = constraintValues;
        }
        return filteredConstraintValues;
    }

    private static int findPreviousInstance(int j,
            List<ColumnSpec> columnSpecs, ColumnSpec columnSpec) {
        for (int i = 0; i < j; i++) {
            ColumnSpec columnSpec2 = columnSpecs.get(i);
            if (columnSpec.isSameSchemaAndTable(columnSpec2)) {
                return i;
            }
        }
        return -1;
    }

    private StringBuilder generateSelectClause(ColumnSpecInfo info,
            Map<ColumnSpec, Integer> referenceIndices, EntitySpec entitySpec) {
        StringBuilder selectClause = new StringBuilder();
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
        int[] uniqueIdIndices = info.getUniqueIdIndices();
        if (uniqueIdIndices != null) {
            i += uniqueIdIndices.length;
        }
        if (info.isUsingKeyIdIndex()) {
            i++;
        }
        if (info.getValueIndex() > 0) {
            i++;
        }
        int[] indices = new int[i];
        String[] names = new String[i];
        int k = 0;
        indices[k] = 0;
        if (info.isUsingKeyIdIndex()) {
            names[k++] = "keyid";
        }
        if (uniqueIdIndices != null) {
            for (int m = 0; m < uniqueIdIndices.length; m++) {
                indices[k] = uniqueIdIndices[m];
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
        if (info.getValueIndex() > 0) {
            indices[k] = info.getValueIndex();
            names[k++] = "value";
        }
        if (info.getPropertyIndices() != null) {
            PropertySpec[] propertySpecs = entitySpec.getPropertySpecs();
            for (PropertySpec propertySpec : propertySpecs) {
                String propertyName = propertySpec.getName();
                int propertyIndex =
                        info.getPropertyIndices().get(propertyName);
                indices[k] = propertyIndex;
                names[k++] = propertyName + "_value";
            }
        }

        boolean unique = info.isUnique();
        for (int j = 0; j < indices.length; j++) {
            ColumnSpec cs = info.getColumnSpecs().get(indices[j]);
            Integer index = referenceIndices.get(cs);
            assert index != null : "index is null for " + cs;
            String column = cs.getColumn();
            String name = names[j];
            boolean distinctRequested = (j == 0 && !unique);
            boolean hasNext = j < indices.length - 1;
            if (column == null) {
                throw new AssertionError("column cannot be null: "
                        + "index=" + index + "; name=" + name + "; cs=" + cs);
            }
            if (name == null) {
                throw new AssertionError("name cannot be null");
            }
            generateSelectColumn(distinctRequested, selectClause, index,
                    column, name, hasNext);
        }
        return selectClause;
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

    public void generateJoin(JoinSpec.JoinType joinType,
            StringBuilder fromPart) {
        switch (joinType) {
            case INNER:
                fromPart.append(" join ");
                break;
            case LEFT_OUTER:
                fromPart.append(" left outer join ");
                break;
            default:
                throw new AssertionError("invalid join type: " + joinType);
        }
    }

    private StringBuilder generateFromClause(List<ColumnSpec> columnSpecs,
            Map<ColumnSpec, Integer> referenceIndices) {
        Map<Integer, ColumnSpec> columnSpecCache =
                new HashMap<Integer, ColumnSpec>();
        StringBuilder fromPart = new StringBuilder();
        JoinSpec currentJoin = null;
        boolean begin = true;
        for (int j = 0, n = columnSpecs.size(); j < n; j++) {
            ColumnSpec columnSpec = columnSpecs.get(j);

            currentJoin = null;
            for (int k = j - 1; k >= 0; k--) {
                ColumnSpec prevColumnSpec = columnSpecs.get(k);
                JoinSpec js = prevColumnSpec.getJoin();
                if (js != null && js.getNextColumnSpec() == columnSpec) {
                    currentJoin = js;
                    break;
                }
            }

            if (currentJoin == null) {
                for (int k = 0; k < j; k++) {
                    ColumnSpec prevColumnSpec = columnSpecs.get(k);
                    JoinSpec js = prevColumnSpec.getJoin();
                    if (js != null
                            && js.getNextColumnSpec().isSameSchemaAndTable(columnSpec)) {
                        currentJoin = js;
                        break;
                    }
                }
            }

            Integer i = referenceIndices.get(columnSpec);
            if (i != null && !columnSpecCache.containsKey(i)) {
                assert begin || currentJoin != null :
                        "No 'on' clause can be generated for " + columnSpec
                        + " because there is no incoming join.";
                String schema = columnSpec.getSchema();
                String table = columnSpec.getTable();
                if (!begin) {
                    generateJoin(currentJoin.getJoinType(), fromPart);
                }
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
                begin = false;
            }
        }
        return fromPart;
    }

    private StringBuilder generateWhereClause(Set<String> propIds,
            ColumnSpecInfo info, Collection<EntitySpec> entitySpecs,
            Set<Filter> filtersCopy, StringBuilder selectPart,
            Map<ColumnSpec, Integer> referenceIndices,
            Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor) {
        StringBuilder wherePart = new StringBuilder();
        int i = 1;
        for (EntitySpec entitySpec : entitySpecs) {
            i = processKeySpecForWhereClause(entitySpec, i);
            i = processStartTimeSpecForWhereClause(entitySpec, i,
                    filtersCopy, wherePart, referenceIndices);
            i = processFinishTimeSpecForWhereClause(entitySpec, i,
                    filtersCopy, wherePart, referenceIndices);
            i = processPropertyValueSpecsForWhereClause(entitySpec, i);
            i = processConstraintSpecsForWhereClause(propIds, entitySpec,
                    i, wherePart, selectPart, referenceIndices, filtersCopy,
                    resultProcessor);
        }
        processKeyIdConstraintsForWhereClause(info, wherePart, keyIds, referenceIndices);

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

    /**
     * If wherePart is null, it skips creating the where clause part.
     * 
     * @param columnSpec
     * @param propIds
     * @param wherePart
     * @param referenceIndices
     * @param selectPartHasCaseStmt
     * @param selectPart
     */
    private void processConstraint(ColumnSpec columnSpec,
            Set<?> propIds,
            StringBuilder wherePart, Map<ColumnSpec, Integer> referenceIndices,
            StringBuilder selectPart, Constraint constraintOverride) {
        Constraint constraint = columnSpec.getConstraint();
        if (constraintOverride != null) {
            constraint = constraintOverride;
        }
        ColumnSpec.KnowledgeSourceIdToSqlCode[] propIdToSqlCodes =
                columnSpec.getPropositionIdToSqlCodes();
        if (constraint != null) {
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues =
                    filterKnowledgeSourceIdToSqlCodesById(propIds, propIdToSqlCodes);
            if (wherePart != null) {
                if (wherePart.length() > 0) {
                    wherePart.append(" and ");
                }
                wherePart.append('(');
            }
            Object[] sqlCodes = null;
            if (filteredConstraintValues.length > 0) {
                sqlCodes = extractSqlCodes(filteredConstraintValues);
            } else {
                sqlCodes = propIds.toArray();
            }

            switch (constraint) {
                case EQUAL_TO:
                    if (wherePart != null) {
                        if (sqlCodes.length > 1) {
                            generateInClause(wherePart,
                                    referenceIndices.get(columnSpec),
                                    columnSpec.getColumn(),
                                    sqlCodes, false);
                        } else {
                            assert sqlCodes.length == 1 :
                                    "invalid sqlCodes length";
                            appendColumnRef(wherePart, referenceIndices,
                                    columnSpec);
                            wherePart.append(constraint.getSqlOperator());
                            appendValue(sqlCodes[0], wherePart);
                        }
                    }
                    break;
                case LESS_THAN:
                case LESS_THAN_OR_EQUAL_TO:
                case GREATER_THAN:
                case GREATER_THAN_OR_EQUAL_TO:
                    if (wherePart != null) {
                        appendColumnRef(wherePart, referenceIndices,
                                columnSpec);
                        wherePart.append(constraint.getSqlOperator());
                        appendValue(sqlCodes[0], wherePart);
                    }
                    break;
                case NOT_EQUAL_TO:
                    if (wherePart != null) {
                        if (sqlCodes.length > 1) {
                            generateInClause(wherePart,
                                    referenceIndices.get(columnSpec),
                                    columnSpec.getColumn(),
                                    sqlCodes, true);
                        } else {
                            appendColumnRef(wherePart, referenceIndices,
                                    columnSpec);
                            wherePart.append(constraint.getSqlOperator());
                            appendValue(sqlCodes[0], wherePart);
                        }
                    }
                    break;
                case LIKE:
                    if (selectPart != null) {
                        generateCaseClause(selectPart, sqlCodes, referenceIndices,
                                columnSpec, filteredConstraintValues);
                    }
                    if (wherePart != null && sqlCodes.length > 1) {
                        wherePart.append('(');
                    }
                    for (int k = 0; k < sqlCodes.length; k++) {
                        if (wherePart != null) {
                            appendColumnRef(wherePart, referenceIndices,
                                    columnSpec);
                            wherePart.append(" LIKE ");
                            appendValue(sqlCodes[k], wherePart);
                            if (k + 1 < sqlCodes.length) {
                                wherePart.append(" or ");
                            }
                        }
                    }
                    if (wherePart != null && sqlCodes.length > 1) {
                        wherePart.append(')');
                    }

                    break;
                default:
                    throw new AssertionError("should not happen");
            }
            if (wherePart != null) {
                wherePart.append(')');
            }
        }

    }

    private void generateCaseClause(StringBuilder selectPart,
            Object[] sqlCodes, Map<ColumnSpec, Integer> referenceIndices,
            ColumnSpec columnSpec,
            KnowledgeSourceIdToSqlCode[] filteredConstraintValues) {
        selectPart.append(", case ");
        for (int k = 0; k < sqlCodes.length; k++) {
            selectPart.append("when ");
            appendColumnRef(selectPart, referenceIndices,
                    columnSpec);
            selectPart.append(" like ");
            appendValue(sqlCodes[k], selectPart);
            selectPart.append(" then ");
            appendValue(
                    filteredConstraintValues[k].getPropositionId(),
                    selectPart);
            if (k < sqlCodes.length - 1) {
                selectPart.append(" ");
            }
        }
        selectPart.append(" end ");
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
        public void visit(ListValue<? extends Value> listValue) {
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
    }

    private void processAdditionalConstraints(ColumnSpec columnSpec,
            StringBuilder wherePart, Map<ColumnSpec, Integer> referenceIndices,
            Constraint constraint, Value value) {
        if (constraint != null && value != null) {
            ValueExtractor ve = new ValueExtractor();
            value.accept(ve);
            processConstraint(columnSpec, ve.values, wherePart,
                    referenceIndices, null, constraint);
        }
    }

    private static int processPropertyValueSpecsForWhereClause(
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
            Map<ColumnSpec, Integer> referenceIndices,
            Set<Filter> filtersCopy, SQLGenResultProcessor resultProcessor) {
        SQLGenUtil.logger().log(Level.FINER,
                "Processing constraint specs for entity spec {0}",
                entitySpec.getName());
        SQLGenUtil.logger().log(Level.FINEST,
                "Details of entity spec {0}", entitySpec);
        ColumnSpec[] constraintSpecs = entitySpec.getConstraintSpecs();
        for (ColumnSpec constraintSpec : constraintSpecs) {
            i = processConstraintSpecForWhereClause(null, constraintSpec,
                    i, wherePart,
                    selectPart, referenceIndices, resultProcessor);
        }
        for (Iterator<Filter> itr = filtersCopy.iterator(); itr.hasNext();) {
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

        //If entity spec's proposition ids are all in propIds, then
        //skip the code part of the where clause.
        ColumnSpec codeSpec = entitySpec.getCodeSpec();
        if (codeSpec != null) {
            List<ColumnSpec> codeSpecL = codeSpec.asList();

            if (codeSpecL.get(codeSpecL.size() - 1).isPropositionIdsComplete()
                    && completeOrNoOverlap(propIds,
                    entitySpec.getPropositionIds())) {
                i = processConstraintSpecForWhereClause(propIds, codeSpec, i,
                        null, selectPart, referenceIndices, resultProcessor);
            } else {
                i = processConstraintSpecForWhereClause(propIds, codeSpec, i,
                        wherePart, selectPart, referenceIndices,
                        resultProcessor);
            }
        }

        return i;
    }

    /**
     * Returns <code>true</code> if the query contains >= 85% of the
     * proposition ids that are known to the data source or if the where
     * clause would contain more than 4000 codes.
     *
     * @param propIds
     * @param entitySpecPropIds
     * @return
     */
    private static boolean completeOrNoOverlap(Set<String> propIds,
            String[] entitySpecPropIds) {

        //Everything known to the data source that is not in the query.
        List<String> entitySpecPropIdsL =
                new ArrayList<String>(entitySpecPropIds.length);
        for (String entitySpecPropId : entitySpecPropIds) {
            if (!propIds.contains(entitySpecPropId)) {
                entitySpecPropIdsL.add(entitySpecPropId);
            }
        }
        boolean result = entitySpecPropIdsL.size() <
                entitySpecPropIds.length * 0.15f || entitySpecPropIdsL.size() > 4000;
        return result;
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

    private static class ColumnSpecLooper {

        ColumnSpec lastColumnSpec;
        int i;

        ColumnSpecLooper(ColumnSpec columnSpec, int i) {
            if (columnSpec != null) {
                while (columnSpec.getJoin() != null) {
                    columnSpec = columnSpec.getJoin().getNextColumnSpec();
                    i++;
                }
            }
            this.lastColumnSpec = columnSpec;
            this.i = i;
        }
    }

    private int processConstraintSpecForWhereClause(Set<String> propIds,
            ColumnSpec columnSpec, int i, StringBuilder wherePart,
            StringBuilder selectPart,
            Map<ColumnSpec, Integer> referenceIndices,
            SQLGenResultProcessor resultProcessor) {
        ColumnSpecLooper csl = new ColumnSpecLooper(columnSpec, i);
        if (columnSpec != null) {
            columnSpec = csl.lastColumnSpec;

            resultProcessor.setCasePresent(columnSpec.getConstraint()
                    == ColumnSpec.Constraint.LIKE);
            processConstraint(columnSpec, propIds, wherePart,
                    referenceIndices, selectPart, null);
        }
        return csl.i;
    }

    private int processPropertyValueFilter(ColumnSpec columnSpec, int i,
            StringBuilder wherePart,
            Map<ColumnSpec, Integer> referenceIndices,
            ColumnSpec.Constraint constraint,
            Value value) {

        ColumnSpecLooper csl = new ColumnSpecLooper(columnSpec, i);

        if (columnSpec != null && constraint != null
                && value != null) {

            processAdditionalConstraints(csl.lastColumnSpec, wherePart,
                    referenceIndices, constraint,
                    value);
        }

        return csl.i;
    }

    private int processFinishTimeSpecForWhereClause(
            EntitySpec entitySpec, int i, Set<Filter> filtersCopy,
            StringBuilder wherePart,
            Map<ColumnSpec, Integer> referenceIndices) {
        ColumnSpec finishTimeSpec = entitySpec.getFinishTimeSpec();
        if (finishTimeSpec != null) {
            while (true) {
                if (finishTimeSpec.getJoin() != null) {
                    finishTimeSpec =
                            finishTimeSpec.getJoin().getNextColumnSpec();
                    i++;
                } else {
                    for (Filter filter : filtersCopy) {
                        if (filter instanceof PositionFilter) {
                            Set<String> entitySpecPropIds =
                                    org.arp.javautil.arrays.Arrays.asSet(
                                    entitySpec.getPropositionIds());
                            if (Collections.containsAny(entitySpecPropIds,
                                    filter.getPropositionIds())) {
                                PositionFilter pdsc2 =
                                        (PositionFilter) filter;

                                boolean outputStart =
                                        pdsc2.getMinimumStart() != null
                                        && pdsc2.getStartSide() == Side.FINISH;

                                boolean outputFinish =
                                        pdsc2.getMaximumFinish() != null
                                        && pdsc2.getFinishSide() == Side.FINISH;

                                if (outputStart) {
                                    if (wherePart.length() > 0) {
                                        wherePart.append(" and ");
                                    }

                                    appendColumnRef(wherePart,
                                            referenceIndices, finishTimeSpec);
                                    wherePart.append(" >= {ts '");
                                    wherePart.append(
                                            AbsoluteTimeGranularity.toSQLString(
                                            pdsc2.getMinimumStart()));
                                    wherePart.append("'}");
                                }

                                if (outputFinish) {
                                    if (wherePart.length() > 0) {
                                        wherePart.append(" and ");
                                    }

                                    appendColumnRef(wherePart,
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
            EntitySpec entitySpec, int i, Set<Filter> filtersCopy,
            StringBuilder wherePart,
            Map<ColumnSpec, Integer> referenceIndices) {
        ColumnSpec startTimeSpec = entitySpec.getStartTimeSpec();
        if (startTimeSpec != null) {
            while (true) {
                if (startTimeSpec.getJoin() != null) {
                    startTimeSpec =
                            startTimeSpec.getJoin().getNextColumnSpec();
                    i++;
                } else {
                    for (Iterator<Filter> itr = filtersCopy.iterator();
                            itr.hasNext();) {
                        Filter filter = itr.next();
                        if (filter instanceof PositionFilter) {
                            Set<String> entitySpecPropIds =
                                    org.arp.javautil.arrays.Arrays.asSet(
                                    entitySpec.getPropositionIds());
                            if (Collections.containsAny(entitySpecPropIds,
                                    filter.getPropositionIds())) {
                                PositionFilter pdsc2 = (PositionFilter) filter;

                                boolean outputStart =
                                        pdsc2.getMinimumStart() != null
                                        && (pdsc2.getStartSide() == Side.START
                                        || entitySpec.getFinishTimeSpec() == null);
                                boolean outputFinish =
                                        pdsc2.getMaximumFinish() != null
                                        && (pdsc2.getFinishSide() == Side.START
                                        || entitySpec.getFinishTimeSpec() == null);

                                if (outputStart) {
                                    if (wherePart.length() > 0) {
                                        wherePart.append(" and ");
                                    }
                                    appendColumnRef(wherePart,
                                            referenceIndices, startTimeSpec);
                                    wherePart.append(" >= {ts '");
                                    wherePart.append(
                                            AbsoluteTimeGranularity.toSQLString(
                                            pdsc2.getMinimumStart()));
                                    wherePart.append("'}");
                                }
                                if (outputFinish) {
                                    if (wherePart.length() > 0) {
                                        wherePart.append(" and ");
                                    }
                                    appendColumnRef(wherePart,
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
            int referenceIndex, String column, Object[] sqlCodes, boolean not);

    public abstract void appendValue(Object val, StringBuilder wherePart);

    public abstract void generateFromSeparator(StringBuilder fromPart);

    private static Map<ColumnSpec, Integer> computeReferenceIndices(
            List<ColumnSpec> columnSpecs) {
        Map<ColumnSpec, Integer> result = new HashMap<ColumnSpec, Integer>();

        int index = 1;
        JoinSpec currentJoin = null;
        boolean begin = true;
        for (int j = 0, n = columnSpecs.size(); j < n; j++) {
            ColumnSpec columnSpec = columnSpecs.get(j);
            /*
             * Only generate a table if we're the first table or there is an
             * inbound join.
             */
            boolean shouldGenerateTable = begin || currentJoin != null;
            if (shouldGenerateTable) {
                int previousInstanceIndex = -1;
                //if there's no inbound join, then don't try to reuse an earlier instance.
                if (currentJoin == null/* || columnSpec.getJoin() != null*/) {
                    previousInstanceIndex = findPreviousInstance(j,
                            columnSpecs, columnSpec);
                } else {
                    //If there's an inbound join and an earlier instance, then
                    //use an earlier version only if the inbound join of the earlier
                    //instance is the same
                    int cs2i = findPreviousInstance(j, columnSpecs,
                            columnSpec);
                    if (cs2i > 0) {
                        for (int k = 0; k < cs2i; k++) {
                            ColumnSpec csPrev = columnSpecs.get(k);
                            JoinSpec prevJoin = csPrev.getJoin();
                            if (currentJoin.isSameJoin(prevJoin)) {
                                previousInstanceIndex = cs2i;
                            }
                        }

                    }
                }
                //If we found an earlier instance, then use its index otherwise
                //assign it a new index.
                if (previousInstanceIndex >= 0) {
                    ColumnSpec previousInstance =
                            columnSpecs.get(previousInstanceIndex);
                    assert result.containsKey(previousInstance) :
                            "doesn't contain columnSpec " + previousInstance;
                    result.put(columnSpec, result.get(previousInstance));
                } else {
                    result.put(columnSpec, index++);
                }
                begin = false;
            }

            if (columnSpec.getJoin() != null) {
                currentJoin = columnSpec.getJoin();
            } else {
                currentJoin = null;
                begin = true;
            }
        }
        return result;
    }

    private void processKeyIdConstraintsForWhereClause(ColumnSpecInfo info,
            StringBuilder wherePart, Set<String> keyIds, Map<ColumnSpec, Integer> referenceIndices) {
        if (keyIds != null && !keyIds.isEmpty()) {
            if (wherePart.length() > 0) {
                wherePart.append(" and ");
            }
            ColumnSpec keySpec = info.getColumnSpecs().get(0);

            generateInClause(wherePart, 1, keySpec.getColumn(), keyIds.toArray(), false);
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

    private String backendNameForMessages() {
        String backendDisplayName = this.backend.getDisplayName();
        if (backendDisplayName != null) {
            return backendDisplayName + "("
                    + this.backend.getClass().getName() + ")";
        } else {
            return this.backend.getClass().getName();
        }
    }

    private Map<EntitySpec, List<String>> entitySpecMapForPropIds(
            Set<String> propIds) throws AssertionError {
        Map<EntitySpec, List<String>> entitySpecToPropIdMapFromPropIds =
                new HashMap<EntitySpec, List<String>>();
        for (String propId : propIds) {
            boolean inDataSource =
                    populateEntitySpecToPropIdMap(new String[]{propId},
                    entitySpecToPropIdMapFromPropIds);
            if (!inDataSource) {
                SQLGenUtil.logger().log(Level.FINER,
                        "Data source backend {0} does not know about proposition {1}",
                        new Object[]{backendNameForMessages(), propId});
            }
        }
        return entitySpecToPropIdMapFromPropIds;
    }

    private EntitySpec entitySpec(String propId) {
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
