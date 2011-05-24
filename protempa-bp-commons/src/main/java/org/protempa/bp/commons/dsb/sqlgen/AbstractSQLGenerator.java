package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
import org.protempa.bp.commons.dsb.SQLGenerator;
import org.protempa.bp.commons.dsb.sqlgen.ColumnSpec.Constraint;
import org.protempa.bp.commons.dsb.sqlgen.ColumnSpec.KnowledgeSourceIdToSqlCode;
import org.protempa.dsb.filter.Filter;
import org.protempa.dsb.filter.PositionFilter;
import org.protempa.dsb.filter.PositionFilter.Side;
import org.protempa.dsb.filter.PropertyValueFilter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.ListValue;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.OrdinalValue;
import org.protempa.proposition.value.Unit;
import org.protempa.proposition.value.UnitFactory;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueVisitor;

/**
 * Abstract class for implement database and driver-specific SQL generators.
 * 
 * @author Andrew Post
 */
public abstract class AbstractSQLGenerator implements SQLGenerator {

    private static final int FETCH_SIZE = 10000;
    private static final String readPropositionsSQL =
            "select {0} from {1} {2}";
    private ConnectionSpec connectionSpec;
    private final Map<String, List<EntitySpec>> primitiveParameterSpecs;
    private EntitySpec[] primitiveParameterEntitySpecs;
    private final Map<String, List<EntitySpec>> eventSpecs;
    private EntitySpec[] eventEntitySpecs;
    private final Map<String, List<EntitySpec>> constantSpecs;
    private EntitySpec[] constantEntitySpecs;
    private GranularityFactory granularities;
    private UnitFactory units;
    private RelationalDatabaseDataSourceBackend backend;

    public AbstractSQLGenerator() {
        this.primitiveParameterSpecs = new HashMap<String, List<EntitySpec>>();
        this.eventSpecs = new HashMap<String, List<EntitySpec>>();
        this.constantSpecs = new HashMap<String, List<EntitySpec>>();
    }

    @Override
    public void initialize(RelationalDatabaseSpec relationalDatabaseSpec,
            ConnectionSpec connectionSpec,
            RelationalDatabaseDataSourceBackend backend) {
        if (relationalDatabaseSpec != null) {
            this.primitiveParameterEntitySpecs = relationalDatabaseSpec.getPrimitiveParameterSpecs();
            populatePropositionMap(this.primitiveParameterSpecs,
                    this.primitiveParameterEntitySpecs);
            this.eventEntitySpecs = relationalDatabaseSpec.getEventSpecs();
            populatePropositionMap(this.eventSpecs, this.eventEntitySpecs);
            this.constantEntitySpecs = relationalDatabaseSpec.getConstantSpecs();
            populatePropositionMap(this.constantSpecs,
                    this.constantEntitySpecs);
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
            Integer tableNumber = referenceIndices.get(columnSpec);
            assert tableNumber != null : "tableNumber is null";
            generateColumnReference(columnSpec.getColumnOp(), tableNumber,
                    columnSpec, wherePart);
        } else {
            appendColumnReference(referenceIndices, columnSpec, wherePart);
        }
    }

    public void generateColumnReference(ColumnSpec.ColumnOp columnOp,
            int tableNumber, ColumnSpec columnSpec, StringBuilder stmt) {
        if (columnOp != null) {
            switch (columnOp) {
                case UPPER:
                    stmt.append("upper");
                    break;
                default:
                    throw new AssertionError("invalid column op: " + columnOp);
            }
            stmt.append('(');
        }
        generateColumnReference(tableNumber, columnSpec.getColumn(), stmt);
        if (columnOp != null) {
            stmt.append(')');
        }
    }

    private void appendColumnReference(
            Map<ColumnSpec, Integer> referenceIndices,
            ColumnSpec columnSpec, StringBuilder stmt) {
        Integer tableNumber = referenceIndices.get(columnSpec);
        assert tableNumber != null : "tableNumber is null";
        generateColumnReference(tableNumber, columnSpec.getColumn(), stmt);
    }

    /*
     * Partitions position filters according to the data source backend's
     * configuration. This has the effect of splitting up one query into
     * multiple queries to improve performance. Currently this only works when
     * upper and lower bounds are provided on the same side of the specified
     * proposition's intervals. If there are multiple position filters
     * specified, which one gets chosen to partition is non-deterministic.
     */
    private List<Set<Filter>> constructFilterSets(EntitySpec entitySpec,
            Set<Filter> filtersCopy) {
        PositionFilter positionFilter = null;
        for (Filter filter : filtersCopy) {
            if (filter instanceof PositionFilter) {
                positionFilter = (PositionFilter) filter;
                break;
            }
        }
        Unit partitionBy = entitySpec.getPartitionBy();
        List<Set<Filter>> filterList = new ArrayList<Set<Filter>>();
        if (partitionBy == null || positionFilter == null 
                || positionFilter.getStart() == null
                || positionFilter.getFinish() == null
                || !positionFilter.getStartSide().equals(
                    positionFilter.getFinishSide())) {
            filterList.add(filtersCopy);
        } else {
            Long start = positionFilter.getStart();
            Long actualFinish = positionFilter.getFinish();
            Granularity startGran = positionFilter.getStartGranularity();
            Granularity finishGran = positionFilter.getFinishGranularity();
            Unit finishUnit = finishGran != null ?
                finishGran.getCorrespondingUnit() : null;
            boolean doLoop = true;
            while (doLoop) {
                Set<Filter> newFiltersCopy = new HashSet<Filter>(filtersCopy);
                newFiltersCopy.remove(positionFilter);
                Long nextStart = partitionBy.addToPosition(start, 1);
                Long finish = finishUnit != null ?
                    finishUnit.addToPosition(nextStart, -1) : -1;
                if (finish.compareTo(actualFinish) >= 0) {
                    finish = actualFinish;
                    doLoop = false;
                }
                PositionFilter newPositionFilter = new PositionFilter(
                        positionFilter.getPropositionIds(),
                        start, startGran, finish, finishGran,
                        positionFilter.getStartSide(),
                        positionFilter.getFinishSide());
                newFiltersCopy.add(newPositionFilter);
                filterList.add(newFiltersCopy);
                start = nextStart;
            }
        }
        return filterList;
    }

    private List<EntitySpec> copyEntitySpecsForRefs(EntitySpec entitySpec,
            Collection<EntitySpec> allEntitySpecs) {
        List<EntitySpec> allEntitySpecsCopyForRefs =
                new LinkedList<EntitySpec>();
        allEntitySpecsCopyForRefs.add(entitySpec);
        for (EntitySpec es : allEntitySpecs) {
            if (es != entitySpec) {
                allEntitySpecsCopyForRefs.add(es);
            }
        }
        return allEntitySpecsCopyForRefs;
    }

    private EntitySpec entitySpecForName(Collection<EntitySpec> entitySpecs,
            String entitySpecName) {
        EntitySpec referredToEntitySpec = null;
        for (EntitySpec reffedToSpec : entitySpecs) {
            if (entitySpecName.equals(reffedToSpec.getName())) {
                referredToEntitySpec = reffedToSpec;
                break;
            }
        }
        return referredToEntitySpec;
    }

    private void executeSelect(Logger logger, String backendNameForMessages,
            String entitySpecName, String query,
            SQLGenResultProcessor resultProcessor)
            throws DataSourceReadException {
        if (Boolean.getBoolean(SQLGenUtil.SYSTEM_PROPERTY_SKIP_EXECUTION)) {
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO,
                        "Data source backend {0} is skipping query for {1}",
                        new Object[]{backendNameForMessages, entitySpecName});
            }
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                        "Data source backend {0} is executing query for {1}",
                        new Object[]{backendNameForMessages, entitySpecName});
            }

            try {
                Connection con = getConnectionSpec().getOrCreate();
                con.setReadOnly(true);
                try {
                    Statement stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                    stmt.setFetchSize(FETCH_SIZE);
                    try {
                        SQLExecutor.executeSQL(con, stmt, query, resultProcessor);
                        stmt.close();
                        stmt = null;
                    } finally {
                        if (stmt != null) {
                            try {
                                stmt.close();
                            } catch (SQLException e) {
                            }
                        }
                    }
                    con.close();
                    con = null;
                } finally {
                    if (con != null) {
                        try {
                            con.close();
                        } catch (SQLException e) {
                        }
                    }
                }
            } catch (SQLException ex) {
                throw new DataSourceReadException(
                        "Error executing query in data source backend "
                        + backendNameForMessages + " for " + entitySpecName, ex);
            }
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                        "Query for {0} in data source backend {1} is complete",
                        new Object[]{entitySpecName, backendNameForMessages});
            }
        }
    }

    private Object[] extractSqlCodes(KnowledgeSourceIdToSqlCode[] filteredConstraintValues) {
        Object[] sqlCodes = new Object[filteredConstraintValues.length];
        for (int i = 0; i < sqlCodes.length; i++) {
            sqlCodes[i] = filteredConstraintValues[i].getSqlCode();
        }
        return sqlCodes;
    }

    private void logSkippingReference(Logger logger, ReferenceSpec referenceSpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Skipping reference {0}", referenceSpec.getReferenceName());
        }
    }

    private void logSkippingRefs(Logger logger, EntitySpec entitySpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Skipping reference queries for entity spec {0} because the query for {0} returned no data", entitySpec.getName());
        }
    }

    private int processForWhereClause(EntitySpec prevEntitySpec, int i,
            Set<Filter> filtersCopy, StringBuilder wherePart,
            Map<ColumnSpec, Integer> referenceIndices, Set<String> propIds,
            StringBuilder selectPart, SQLGenResultProcessor resultProcessor,
            boolean first) {
        i = processKeySpecForWhereClause(prevEntitySpec, i);
        int wherePartLength = wherePart.length();
        i = processStartTimeSpecForWhereClause(prevEntitySpec, i, filtersCopy,
                wherePart, referenceIndices, first);
        if (wherePart.length() > wherePartLength) {
            first = false;
        }
        wherePartLength = wherePart.length();
        i = processFinishTimeSpecForWhereClause(prevEntitySpec, i, filtersCopy,
                wherePart, referenceIndices, first);
        if (wherePart.length() > wherePartLength) {
            first = false;
        }
        wherePartLength = wherePart.length();
        i = processPropertyValueSpecsForWhereClause(prevEntitySpec, i);
        if (wherePart.length() > wherePartLength) {
            first = false;
        }
        wherePartLength = wherePart.length();
        i = processConstraintSpecsForWhereClause(propIds, prevEntitySpec, i,
                wherePart, selectPart, referenceIndices, filtersCopy,
                resultProcessor, first);
        return i;
    }

    private void processOrder(SQLOrderBy order, ColumnSpecInfo info,
            Map<ColumnSpec, Integer> referenceIndices,
            StringBuilder wherePart) {
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
            processOrderBy(start, startCol, finish, finishCol, wherePart,
                    order);
        }
    }

    @Override
    public ResultCache<Proposition> readPropositions(Set<String> keyIds,
            Set<String> propIds, Filter filters, SQLOrderBy order)
            throws DataSourceReadException {
        Map<EntitySpec, List<String>> entitySpecMapFromPropIds =
                entitySpecMapForPropIds(propIds);

        ResultCache<Proposition> results = new ResultCache<Proposition>();
        Map<EntitySpec, SQLGenResultProcessorFactory> entitySpecToResultProcessorMap =
                allEntitySpecs();
        Collection<EntitySpec> allEntitySpecs = entitySpecToResultProcessorMap.keySet();
        Logger logger = SQLGenUtil.logger();

        for (EntitySpec entitySpec : entitySpecMapFromPropIds.keySet()) {
            logProcessingEntitySpec(logger, entitySpec);
            SQLGenResultProcessorFactory factory =
                    entitySpecToResultProcessorMap.get(entitySpec);
            assert factory != null : "factory should never be null";
            List<EntitySpec> allEntitySpecsCopy =
                    new LinkedList<EntitySpec>(allEntitySpecs);
            removeNonApplicableEntitySpecs(entitySpec, allEntitySpecsCopy);

            logApplicableEntitySpecs(allEntitySpecsCopy, logger);

            Set<Filter> filtersCopy = copyFilters(filters);
            removeNonApplicableFilters(allEntitySpecs, filtersCopy,
                    entitySpec);
            assert !allEntitySpecsCopy.isEmpty() :
                    "allEntitySpecsCopy should have at least one element";
            String dataSourceBackendId =
                    this.backend.getDataSourceBackendId();
            MainResultProcessor<Proposition> resultProcessor =
                    factory.getInstance(dataSourceBackendId, entitySpec, results);


            List<Set<Filter>> filterList = constructFilterSets(entitySpec,
                    filtersCopy);
            for (Set<Filter> filterSet : filterList) {
                generateAndExecuteSelect(entitySpec, null, propIds, filterSet,
                        allEntitySpecsCopy, keyIds, order, resultProcessor);
            }
            if (results.anyAdded()) {
                ReferenceSpec[] refSpecs = entitySpec.getReferenceSpecs();
                if (refSpecs != null) {
                    /*
                     * Create a copy of allEntitySpecs with the current entitySpec
                     * the first item of the list. This is to make sure that
                     * its joins make it into the list of column specs.
                     */
                    for (ReferenceSpec referenceSpec : refSpecs) {
                        RefResultProcessor<Proposition> refResultProcessor =
                                factory.getRefInstance(dataSourceBackendId,
                                entitySpec, referenceSpec, results);
                        List<EntitySpec> allEntitySpecsCopyForRefs =
                                copyEntitySpecsForRefs(entitySpec, allEntitySpecs);
                        Set<Filter> refFiltersCopy = copyFilters(filters);
                        EntitySpec referredToEntitySpec =
                                entitySpecForName(allEntitySpecsCopyForRefs,
                                referenceSpec.getEntityName());
                        assert referredToEntitySpec != null :
                                "refferedToEntitySpec should not be null";
                        if (Collections.containsAny(propIds,
                                referredToEntitySpec.getPropositionIds())) {
                            logProcessingRef(logger, referenceSpec, entitySpec);
                            retainEntitySpecsWithInboundRefs(allEntitySpecsCopyForRefs,
                                    entitySpec, referenceSpec);
                            removeNonApplicableFilters(allEntitySpecsCopyForRefs,
                                    refFiltersCopy, referredToEntitySpec);
                            retainEntitySpecsWithFiltersOrConstraints(entitySpec,
                                    referredToEntitySpec, allEntitySpecsCopyForRefs,
                                    refFiltersCopy, propIds);
                            List<Set<Filter>> refFilterList =
                                    constructFilterSets(referredToEntitySpec,
                                    refFiltersCopy);
                            for (Set<Filter> filterSet : refFilterList) {
                                generateAndExecuteSelect(entitySpec, referenceSpec, propIds,
                                        filterSet, allEntitySpecsCopyForRefs, keyIds,
                                        order, refResultProcessor);
                            }
                            logDoneProcessingRef(logger, referenceSpec, entitySpec);
                        } else {
                            logSkippingReference(logger, referenceSpec);
                        }

                    }
                    logDoneProcessingEntitySpec(logger, entitySpec);
                }
            } else {
                logSkippingRefs(logger, entitySpec);
            }

            results.clearTmp();

            logDoneProcessing(logger, entitySpec);
        }
        return results;
    }

    private static void retainEntitySpecsWithFiltersOrConstraints(
            EntitySpec entitySpec, EntitySpec referredToEntitySpec,
            Collection<EntitySpec> allEntitySpecsCopyForRefs,
            Collection<Filter> refFiltersCopy, Set<String> propIds) {
        for (Iterator<EntitySpec> itr = allEntitySpecsCopyForRefs.iterator();
                itr.hasNext();) {
            EntitySpec es = itr.next();
            if (es != entitySpec && es != referredToEntitySpec) {
                Set<String> esPropIds = Arrays.asSet(es.getPropositionIds());
                ColumnSpec codeSpec = es.getCodeSpec();
                if (codeSpec != null) {
                    List<ColumnSpec> codeSpecL = codeSpec.asList();
                    ColumnSpec last = codeSpecL.get(codeSpecL.size() - 1);
                    if (last.getConstraint() != null
                            && (last.getConstraint() != Constraint.EQUAL_TO
                            || !last.isPropositionIdsComplete()
                            || !completeOrNoOverlap(propIds,
                            es.getPropositionIds()))) {
                        return;
                    }
                }
                for (Filter filter : refFiltersCopy) {
                    if (Collections.containsAny(esPropIds,
                            filter.getPropositionIds())) {
                        return;
                    }
                }
                itr.remove();
            }
        }
    }

    private void logDoneProcessing(Logger logger, EntitySpec entitySpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Results of query for {0} in data source backend {1} " + "have been processed", new Object[]{entitySpec.getName(), backendNameForMessages()});
        }
    }

    private void logDoneProcessingEntitySpec(Logger logger, EntitySpec entitySpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Data source backend {0} is done processing entity spec {1}", new Object[]{backendNameForMessages(), entitySpec.getName()});
        }
    }

    private void logDoneProcessingRef(Logger logger, ReferenceSpec referenceSpec, EntitySpec entitySpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Data source backend {0} is done processing reference {1} for entity spec {2}", new Object[]{backendNameForMessages(), referenceSpec.getReferenceName(), entitySpec.getName()});
        }
    }

    private void logProcessingRef(Logger logger, ReferenceSpec referenceSpec, EntitySpec entitySpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Data source backend {0} is processing reference {1} for entity spec {2}", new Object[]{backendNameForMessages(), referenceSpec.getReferenceName(), entitySpec.getName()});
        }
    }

    private void logProcessingEntitySpec(Logger logger, EntitySpec entitySpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Data source backend {0} is processing entity spec {1}", new Object[]{backendNameForMessages(), entitySpec.getName()});
        }
    }

    private void logApplicableEntitySpecs(List<EntitySpec> allEntitySpecsCopy, Logger logger) {
        if (logger.isLoggable(Level.FINER)) {
            String[] allEntitySpecsCopyNames = new String[allEntitySpecsCopy.size()];
            int i = 0;
            for (EntitySpec aesc : allEntitySpecsCopy) {
                allEntitySpecsCopyNames[i++] = aesc.getName();
            }
            logger.log(Level.FINER, "Applicable entity specs are {0}", StringUtils.join(allEntitySpecsCopyNames, ", "));
        }
    }

    private static void retainEntitySpecsWithInboundRefs(
            Collection<EntitySpec> entitySpecs, EntitySpec entitySpec,
            ReferenceSpec referenceSpec) {
        for (Iterator<EntitySpec> itr = entitySpecs.iterator();
                itr.hasNext();) {
            EntitySpec es = itr.next();
            if (es != entitySpec
                    && !referenceSpec.getEntityName().equals(es.getName())
                    && !es.hasReferenceTo(entitySpec)) {
                itr.remove();
            }
        }
    }

    private Map<EntitySpec, SQLGenResultProcessorFactory> allEntitySpecs() {
        /*
         * The order of the entity specs matters for multiple with the same
         * name. Thus, we use a LinkedHashMap.
         */
        Map<EntitySpec, SQLGenResultProcessorFactory> result =
                new LinkedHashMap<EntitySpec, SQLGenResultProcessorFactory>();
        PrimitiveParameterResultProcessorFactory ppFactory =
                new PrimitiveParameterResultProcessorFactory();
        for (EntitySpec es : this.primitiveParameterEntitySpecs) {
            result.put(es, ppFactory);
        }
        EventResultProcessorFactory eFactory =
                new EventResultProcessorFactory();
        for (EntitySpec es : this.eventEntitySpecs) {
            result.put(es, eFactory);
        }
        ConstantResultProcessorFactory cFactory =
                new ConstantResultProcessorFactory();
        for (EntitySpec es : this.constantEntitySpecs) {
            result.put(es, cFactory);
        }
        return result;
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
            Set<Filter> filtersCopy, List<EntitySpec> entitySpecsCopy,
            Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor)
            throws DataSourceReadException {
        Logger logger = SQLGenUtil.logger();
        String backendNameForMessages = backendNameForMessages();
        String entitySpecName = entitySpec.getName();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE,
                    "Data source backend {0} is generating query for {1}",
                    new Object[]{backendNameForMessages, entitySpecName});
        }

        String query = generateSelect(entitySpec, referenceSpec, propIds,
                filtersCopy, entitySpecsCopy, keyIds, order, resultProcessor);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE,
                    "Data source backend {0} generated the following query for {1}: {2}",
                    new Object[]{backendNameForMessages, entitySpecName, query});
        }

        executeSelect(logger, backendNameForMessages, entitySpecName, query,
                resultProcessor);
    }

    private static void removeNonApplicableEntitySpecs(EntitySpec entitySpec,
            Collection<EntitySpec> entitySpecs) {
        for (Iterator<EntitySpec> itr = entitySpecs.iterator();
                itr.hasNext();) {
            EntitySpec es = itr.next();
            if (es != entitySpec
                    && !es.hasReferenceTo(entitySpec)) {
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
        Set<EntitySpec> entitySpecsSet = new HashSet<EntitySpec>();
        Set<String> filterPropIds = new HashSet<String>();
        String[] entitySpecPropIds = entitySpec.getPropositionIds();
        for (Iterator<Filter> itr = filtersCopy.iterator(); itr.hasNext();) {
            Filter f = itr.next();
            for (String filterPropId : f.getPropositionIds()) {
                filterPropIds.add(filterPropId);
            }
            for (EntitySpec es : entitySpecs) {
                if (Collections.containsAny(filterPropIds,
                        es.getPropositionIds())) {
                    entitySpecsSet.add(es);
                }
            }
            if (Collections.containsAny(filterPropIds, entitySpecPropIds)) {
                return;
            }
            if (!atLeastOneInInboundReferences(entitySpecsSet, entitySpec)) {
                itr.remove();
            }
            entitySpecsSet.clear();
            filterPropIds.clear();
        }
    }

    private static boolean atLeastOneInInboundReferences(Set<EntitySpec> entitySpecsSet,
            EntitySpec entitySpec) {
        for (EntitySpec es : entitySpecsSet) {
            if (es.hasReferenceTo(entitySpec)) {
                return true;
            }
        }
        return false;
    }

    private String generateSelect(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, Set<String> propIds,
            Set<Filter> filtersCopy, List<EntitySpec> entitySpecsCopy,
            Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor) {
        ColumnSpecInfo info = new ColumnSpecInfoFactory().newInstance(propIds,
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

    public String assembleReadPropositionsQuery(StringBuilder selectClause,
            StringBuilder fromClause, StringBuilder whereClause) {
        return MessageFormat.format(readPropositionsSQL,
                selectClause, fromClause, whereClause);
    }

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

    private static int findPreviousInstance(int i, int j,
            List<ColumnSpec> columnSpecs, ColumnSpec columnSpec) {
        for (; i < j; i++) {
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

    public void generateSelectColumn(boolean distinctRequested,
            StringBuilder selectPart, int index, String column, String name,
            boolean hasNext) {
        if (distinctRequested) {
            selectPart.append("distinct ");
        }
        generateColumnReference(index, column, selectPart);
        selectPart.append(" as ");
        selectPart.append(name);
        if (hasNext) {
            selectPart.append(',');
        }
    }

    public void processOrderBy(int startReferenceIndex, String startColumn,
            int finishReferenceIndex, String finishColumn,
            StringBuilder wherePart, SQLOrderBy order) {
        wherePart.append(" order by ");
        generateColumnReference(startReferenceIndex, startColumn, wherePart);
        if (finishReferenceIndex > 0) {
            wherePart.append(',');
            generateColumnReference(finishReferenceIndex, finishColumn,
                    wherePart);
        }
        wherePart.append(' ');
        if (order == SQLOrderBy.ASCENDING) {
            wherePart.append("ASC");
        } else {
            wherePart.append("DESC");
        }
    }

    public abstract void generateFromTable(String schema, String table,
            StringBuilder fromPart, int index);

    public void generateTableReference(int tableNumber, StringBuilder stmt) {
        stmt.append(" a").append(tableNumber);
    }

    public void generateColumnReference(int tableNumber, String columnName,
            StringBuilder stmt) {
        generateTableReference(tableNumber, stmt);
        stmt.append('.');
        stmt.append(columnName);
    }

    public void generateOn(StringBuilder fromPart, int fromIndex,
            int toIndex, String fromKey,
            String toKey) {
        fromPart.append("on (");
        generateColumnReference(fromIndex, fromKey, fromPart);
        fromPart.append(" = ");
        generateColumnReference(toIndex, toKey, fromPart);
        fromPart.append(") ");
    }

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

    /**
     * Generate an IN clause.
     *
     * @param wherePart the SQL statement {@link StringBuilder}.
     * @param tableNumber the table number.
     * @param columnName the column name {@link String}.
     * @param elements the elements of the IN clause.
     * @param not set to <code>true</code> to generate <code>NOT IN</code>.
     */
    public void generateInClause(StringBuilder wherePart, int tableNumber,
            String columnName, Object[] elements, boolean not) {
        generateColumnReference(tableNumber, columnName, wherePart);
        if (not) {
            wherePart.append(" NOT");
        }
        wherePart.append(" IN (");
        for (int k = 0; k < elements.length; k++) {
            Object sqlCode = elements[k];
            appendValue(sqlCode, wherePart);
            if (k + 1 < elements.length) {
                wherePart.append(',');
            }
        }
        wherePart.append(')');
    }

    private StringBuilder generateFromClause(List<ColumnSpec> columnSpecs,
            Map<ColumnSpec, Integer> referenceIndices) {
        Map<Integer, ColumnSpec> columnSpecCache =
                new HashMap<Integer, ColumnSpec>();
        StringBuilder fromPart = new StringBuilder();
        boolean begin = true;
        for (int j = 0, n = columnSpecs.size(); j < n; j++) {
            ColumnSpec columnSpec = columnSpecs.get(j);

            JoinSpec currentJoin = null;

            /*
             * To find something to join to, first we see if there is a join to
             * it.
             */
            for (int k = j - 1; k >= 0; k--) {
                ColumnSpec prevColumnSpec = columnSpecs.get(k);
                JoinSpec js = prevColumnSpec.getJoin();
                if (js != null && js.getNextColumnSpec() == columnSpec) {
                    currentJoin = js;
                    break;
                }
            }

            /*
             * Next, if there is not a join, we see if there is a
             * join specified to another column spec with the same schema and
             * table as this one.
             */
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
                fromPart.append(' ');
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
            ColumnSpecInfo info, List<EntitySpec> entitySpecs,
            Set<Filter> filtersCopy, StringBuilder selectPart,
            Map<ColumnSpec, Integer> referenceIndices,
            Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor) {
        StringBuilder wherePart = new StringBuilder();

        int i = 1;

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
                    i = processForWhereClause(prevEntitySpec, i,
                            filtersCopy, wherePart, referenceIndices, propIds,
                            selectPart, resultProcessor, first);
                    if (wherePart.length() > wherePartLength) {
                        first = false;
                    }
                } else {
                    if (inGroup) {
                        first = true;
                        int wherePartLength = wherePart.length();
                        wherePart.append(") or (");
                        i = processForWhereClause(prevEntitySpec, i,
                                filtersCopy, wherePart, referenceIndices, propIds,
                                selectPart, resultProcessor, first);
                        wherePart.append(")) ");
                        if (wherePart.length() > wherePartLength) {
                            first = false;
                        }
                        inGroup = false;
                    } else {
                        int wherePartLength = wherePart.length();
                        i = processForWhereClause(prevEntitySpec, i,
                                filtersCopy, wherePart, referenceIndices, propIds,
                                selectPart, resultProcessor, first);
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
            i = processForWhereClause(prevEntitySpec, i,
                    filtersCopy, wherePart, referenceIndices, propIds,
                    selectPart, resultProcessor, first);
            wherePart.append(")) ");
        } else {
            i = processForWhereClause(prevEntitySpec, i,
                    filtersCopy, wherePart, referenceIndices, propIds,
                    selectPart, resultProcessor, first);
        }

        processKeyIdConstraintsForWhereClause(info, wherePart, keyIds);

        if (wherePart.length() > 0) {
            wherePart.insert(0, "where ");
        }

        processOrder(order, info, referenceIndices, wherePart);

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
            StringBuilder selectPart, Constraint constraintOverride,
            boolean first) {
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
                if (!first) {
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
            Set<Filter> filtersCopy, SQLGenResultProcessor resultProcessor,
            boolean first) {
        Logger logger = SQLGenUtil.logger();
        logger.log(Level.FINER,
                "Processing constraint specs for entity spec {0}",
                entitySpec.getName());
        logger.log(Level.FINEST,
                "Details of entity spec {0}", entitySpec);
        ColumnSpec[] constraintSpecs = entitySpec.getConstraintSpecs();
        for (ColumnSpec constraintSpec : constraintSpecs) {
            int wherePartLength = wherePart.length();
            i = processConstraintSpecForWhereClause(null, constraintSpec,
                    i, wherePart,
                    null, referenceIndices, null, first);
            if (wherePart.length() > wherePartLength) {
                first = false;
            }
        }

        for (Filter filter : filtersCopy) {
            for (PropertySpec ps : entitySpec.getPropertySpecs()) {
                if (filter instanceof PropertyValueFilter) {
                    PropertyValueFilter pvf =
                            (PropertyValueFilter) filter;

                    if (pvf.getProperty().equals(ps.getName())) {
                        ColumnSpec colSpec = ps.getSpec();
                        int wherePartLength = wherePart.length();
                        processPropertyValueFilter(colSpec, wherePart,
                                referenceIndices,
                                pvf.getValueComparator(), pvf.getValues(), first);
                        if (wherePart.length() > wherePartLength) {
                            first = false;
                        }

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
                        null, selectPart, referenceIndices, resultProcessor,
                        first);
            } else {
                int wherePartLength = wherePart.length();
                i = processConstraintSpecForWhereClause(propIds, codeSpec, i,
                        wherePart, selectPart, referenceIndices,
                        resultProcessor, first);
                if (wherePart.length() > wherePartLength) {
                    first = false;
                }
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
    static boolean completeOrNoOverlap(Set<String> propIds,
            String[] entitySpecPropIds) {

        //Everything known to the data source that is not in the query.
        List<String> entitySpecPropIdsL =
                new ArrayList<String>(entitySpecPropIds.length);
        for (String entitySpecPropId : entitySpecPropIds) {
            if (!propIds.contains(entitySpecPropId)) {
                entitySpecPropIdsL.add(entitySpecPropId);
            }
        }
        boolean result = entitySpecPropIdsL.size()
                < entitySpecPropIds.length * 0.15f || entitySpecPropIdsL.size() > 2000;
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

    private int processConstraintSpecForWhereClause(Set<String> propIds,
            ColumnSpec columnSpec, int i, StringBuilder wherePart,
            StringBuilder selectPart,
            Map<ColumnSpec, Integer> referenceIndices,
            SQLGenResultProcessor resultProcessor, boolean first) {

        if (columnSpec != null) {
            List<ColumnSpec> columnSpecL = columnSpec.asList();
            columnSpec = columnSpecL.get(columnSpecL.size() - 1);
            if (columnSpec.getConstraint() != null) {
                i += columnSpecL.size();
                if (resultProcessor != null) {
                    resultProcessor.setCasePresent(columnSpec.getConstraint()
                            == ColumnSpec.Constraint.LIKE);
                }
                processConstraint(columnSpec, propIds, wherePart,
                        referenceIndices, selectPart, null, first);
            }
        }
        return i;
    }

    private void processPropertyValueFilter(ColumnSpec columnSpec,
            StringBuilder wherePart,
            Map<ColumnSpec, Integer> referenceIndices,
            ValueComparator comparator, Value[] values, boolean first) {

        List<ColumnSpec> columnSpecL = columnSpec.asList();
        ColumnSpec lastColumnSpec = columnSpecL.get(columnSpecL.size() - 1);

        Constraint constraint = valueComparatorToSqlOp(comparator);

        if (columnSpec != null && constraint != null) {
            ValueExtractor ve = new ValueExtractor();
            for (Value value : values) {
                value.accept(ve);
            }
            processConstraint(lastColumnSpec, ve.values, wherePart,
                    referenceIndices, null, constraint, first);
        }
    }

    private int processFinishTimeSpecForWhereClause(
            EntitySpec entitySpec, int i, Set<Filter> filtersCopy,
            StringBuilder wherePart,
            Map<ColumnSpec, Integer> referenceIndices, boolean first) {
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
                                    if (!first) {
                                        wherePart.append(" and ");
                                    }

                                    appendColumnRef(wherePart,
                                            referenceIndices, finishTimeSpec);
                                    wherePart.append(" >= ");
                                    wherePart.append(entitySpec.getPositionParser().format(pdsc2.getMinimumStart()));
                                }

                                if (outputFinish) {
                                    if (!first || outputStart) {
                                        wherePart.append(" and ");
                                    }

                                    appendColumnRef(wherePart,
                                            referenceIndices, finishTimeSpec);
                                    wherePart.append(" <= ");
                                    wherePart.append(entitySpec.getPositionParser().format(pdsc2.getMaximumFinish()));
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
            Map<ColumnSpec, Integer> referenceIndices, boolean first) {
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
                                    if (!first) {
                                        wherePart.append(" and ");
                                    }
                                    appendColumnRef(wherePart,
                                            referenceIndices, startTimeSpec);
                                    wherePart.append(" >= ");
                                    wherePart.append(entitySpec.getPositionParser().format(pdsc2.getMinimumStart()));
                                }
                                if (outputFinish) {
                                    if (!first || outputStart) {
                                        wherePart.append(" and ");
                                    }
                                    appendColumnRef(wherePart,
                                            referenceIndices,
                                            startTimeSpec);
                                    wherePart.append(" <= ");
                                    wherePart.append(entitySpec.getPositionParser().format(pdsc2.getMaximumFinish()));
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

    public void appendValue(Object val, StringBuilder wherePart) {
        boolean numberOrBoolean;
        if (!(val instanceof Number) && !(val instanceof Boolean)) {
            numberOrBoolean = false;
            wherePart.append("'");
        } else {
            numberOrBoolean = true;
        }
        if (val instanceof Boolean) {
            Boolean boolVal = (Boolean) val;
            if (boolVal.equals(Boolean.TRUE)) {
                wherePart.append(1);
            } else {
                wherePart.append(0);
            }
        } else {
            wherePart.append(val);
        }
        if (!numberOrBoolean) {
            wherePart.append("'");
        }
    }

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
                    previousInstanceIndex = findPreviousInstance(0, j,
                            columnSpecs, columnSpec);
                    //System.out.println("previousInstanceIndex 1: " + previousInstanceIndex);
                } else {
                    //If there's an inbound join and an earlier instance, then
                    //use an earlier version only if the inbound join of the earlier
                    //instance is the same
                    int startIndex = 0;
                    int cs2i = -1;
                    do {
                        cs2i = findPreviousInstance(startIndex, j, columnSpecs,
                                columnSpec);
                        startIndex = cs2i + 1;
                        if (cs2i >= 0) {
                            //System.out.println("found previous instance at " + cs2i);
                            for (int k = 0; k < cs2i; k++) {
                                ColumnSpec csPrev = columnSpecs.get(k);
                                JoinSpec prevJoin = csPrev.getJoin();
                                if (currentJoin.isSameJoin(prevJoin)) {
                                    previousInstanceIndex = cs2i;
                                    //System.out.println("setting previousIstanceIndex=" + previousInstanceIndex);
                                }
                            }
                        }
                    } while (cs2i >= 0);
                    //System.out.println("previousInstanceIndex 2: " + previousInstanceIndex);
                }
                //If we found an earlier instance, then use its index otherwise
                //assign it a new index.
                if (previousInstanceIndex >= 0) {
                    ColumnSpec previousInstance =
                            columnSpecs.get(previousInstanceIndex);
                    assert result.containsKey(previousInstance) :
                            "doesn't contain columnSpec " + previousInstance;
                    int prevIndex = result.get(previousInstance);
                    result.put(columnSpec, prevIndex);
                    //System.err.println("assigning " + columnSpec.getTable() + " to  previous index " + prevIndex);
                } else {
                    result.put(columnSpec, index++);
                    //System.err.println("assigning " + columnSpec.getTable() + " to " + (index - 1));
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
            StringBuilder wherePart, Set<String> keyIds) {
        if (keyIds != null && !keyIds.isEmpty()) {
            if (wherePart.length() > 0) {
                wherePart.append(" and ");
            }
            ColumnSpec keySpec = info.getColumnSpecs().get(0);

            generateInClause(wherePart, 1, keySpec.getColumn(),
                    keyIds.toArray(), false);
        }
    }

    @Override
    public final boolean loadDriverIfNeeded() {
        String className = getDriverClassNameToLoad();
        if (className == null) {
            return true;
        }
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException ex) {
            SQLGenUtil.logger().log(Level.WARNING,
                    "{0} when trying to load {1}.",
                    new Object[]{ex.getClass().getName(), className});
            return false;
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
        Map<EntitySpec, List<String>> result =
                new HashMap<EntitySpec, List<String>>();
        for (String propId : propIds) {
            boolean inDataSource =
                    populateEntitySpecToPropIdMap(new String[]{propId},
                    result);
            Logger logger = SQLGenUtil.logger();
            if (!inDataSource && logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER,
                        "Data source backend {0} does not know about proposition {1}",
                        new Object[]{backendNameForMessages(), propId});
            }
        }
        return result;
    }

    private List<EntitySpec> entitySpecs(String propId) {
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
            List<EntitySpec> entitySpecs = entitySpecs(propId);
            if (entitySpecs != null) {
                for (EntitySpec entitySpec : entitySpecs) {
                    Collections.putList(entitySpecToPropIdMap, entitySpec,
                            propId);
                    result = true;
                }
            }
        }
        return result;
    }

    private static void populatePropositionMap(
            Map<String, List<EntitySpec>> map, EntitySpec[] entitySpecs) {
        if (entitySpecs != null) {
            for (EntitySpec entitySpec : entitySpecs) {
                for (String code : entitySpec.getPropositionIds()) {
                    Collections.putList(map, code, entitySpec);
                }
            }
        }
    }
}
