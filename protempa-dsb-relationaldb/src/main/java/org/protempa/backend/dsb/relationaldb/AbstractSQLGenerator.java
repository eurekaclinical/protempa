/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.backend.dsb.relationaldb;

import org.apache.commons.lang3.StringUtils;
import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.collections.Collections;
import org.arp.javautil.sql.ConnectionSpec;
import org.protempa.DataSourceReadException;
import org.protempa.DataStreamingEventIterator;
import org.protempa.UniqueIdPair;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.backend.dsb.filter.PositionFilter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.Unit;
import org.protempa.proposition.value.UnitFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protempa.ProtempaEvent;

/**
 * Abstract class for implement database and driver-specific SQL generators.
 *
 * @author Andrew Post
 */
public abstract class AbstractSQLGenerator implements SQLGenerator {

    static final int FETCH_SIZE = 10000;
    private static final int DEFAULT_QUERY_THREAD_COUNT = 4;
    private static final String readPropositionsSQL = "select {0} from {1} {2}";
    private ConnectionSpec connectionSpec;
    private final Map<String, List<EntitySpec>> primitiveParameterSpecs;
    private EntitySpec[] primitiveParameterEntitySpecs;
    private final Map<String, List<EntitySpec>> eventSpecs;
    private EntitySpec[] eventEntitySpecs;
    private final Map<String, List<EntitySpec>> constantSpecs;
    private EntitySpec[] constantEntitySpecs;
    private GranularityFactory granularities;
    private UnitFactory units;
    private RelationalDbDataSourceBackend backend;
    private int queryThreadCount;

    protected AbstractSQLGenerator() {
        this.primitiveParameterSpecs = new HashMap<>();
        this.eventSpecs = new HashMap<>();
        this.constantSpecs = new HashMap<>();
        this.queryThreadCount = DEFAULT_QUERY_THREAD_COUNT;
    }

    @Override
    public void initialize(
            ConnectionSpec connectionSpec, RelationalDatabaseSpec relationalDatabaseSpec, RelationalDbDataSourceBackend backend) {

        if (relationalDatabaseSpec != null) {
            this.primitiveParameterEntitySpecs = relationalDatabaseSpec.getPrimitiveParameterSpecs();
            populatePropositionMap(this.primitiveParameterSpecs,
                    this.primitiveParameterEntitySpecs);
            this.eventEntitySpecs = relationalDatabaseSpec.getEventSpecs();
            populatePropositionMap(this.eventSpecs, this.eventEntitySpecs);
            this.constantEntitySpecs = relationalDatabaseSpec.getConstantSpecs();
            populatePropositionMap(this.constantSpecs, this.constantEntitySpecs);
            this.granularities = relationalDatabaseSpec.getGranularities();
            this.units = relationalDatabaseSpec.getUnits();
            this.connectionSpec = connectionSpec;
            Integer queryThreadCountSetting = backend.getQueryThreadCount();
            if (queryThreadCountSetting != null) {
                this.queryThreadCount = queryThreadCountSetting;
            }
        } else {
            throw new IllegalArgumentException(
                    "relationalDatabaseSpec cannot be null");
        }

        this.backend = backend;
    }

    public boolean getStreamingMode() {
        return true;
    }

    @Override
    public GranularityFactory getGranularities() {
        return this.granularities;
    }

    @Override
    public UnitFactory getUnits() {
        return this.units;
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
            Logger logger = SQLGenUtil.logger();
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                        "{0} when trying to load {1}.",
                        new Object[]{ex.getClass().getName(), className});
            }
            return false;
        }
    }

    private class SQLExecutorCallable implements Callable<List<StreamingIteratorPair>> {

        private final Map<EntitySpec, SQLGenResultProcessorFactory> allEntitySpecToResultProcessor;
        private final Collection<EntitySpec> allEntitySpecs;
        private final Filter filters;
        private final Set<String> propIds;
        private final Set<String> keyIds;
        private final EntitySpec entitySpec;

        public SQLExecutorCallable(EntitySpec entitySpec,
                Map<EntitySpec, SQLGenResultProcessorFactory> allEntitySpecToResultProcessor,
                Collection<EntitySpec> allEntitySpecs,
                Filter filters,
                Set<String> propIds,
                Set<String> keyIds) {
            this.entitySpec = entitySpec;
            this.allEntitySpecToResultProcessor = allEntitySpecToResultProcessor;
            this.allEntitySpecs = allEntitySpecs;
            this.filters = filters;
            this.propIds = propIds;
            this.keyIds = keyIds;
        }

        @Override
        public List<StreamingIteratorPair> call() throws Exception {
            backend.fireProtempaEvent(
                    new ProtempaEvent(
                            ProtempaEvent.Level.INFO, 
                            ProtempaEvent.Type.DSB_QUERY_START, 
                            backend.getClass(), 
                            new Date(), 
                            this.entitySpec.getName()));
            Connection conn;
            try {
                conn = connectionSpec.getOrCreate();
            } catch (SQLException ex) {
                throw new DataSourceReadException(ex);
            }
            return processEntitySpecStreaming(this.entitySpec,
                    allEntitySpecToResultProcessor,
                    allEntitySpecs, filters,
                    propIds,
                    keyIds, new StreamingSQLExecutor(
                            conn, backendNameForMessages(),
                            backend.getQueryTimeout()));
        }

    }

    @Override
    public DataStreamingEventIterator<Proposition> readPropositionsStreaming(
            final Set<String> keyIds, final Set<String> propIds, final Filter filters)
            throws DataSourceReadException {
        final Map<EntitySpec, List<String>> entitySpecToPropIds
                = entitySpecToPropIds(propIds);
        final Map<EntitySpec, SQLGenResultProcessorFactory> allEntitySpecToResultProcessor = allEntitySpecToResultProcessor();
        final Collection<EntitySpec> allEntitySpecs
                = allEntitySpecToResultProcessor.keySet();

        final List<StreamingIteratorPair> itrs = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(this.queryThreadCount);
        List<Future<List<StreamingIteratorPair>>> list = new ArrayList<>();
        List<Connection> connections = new ArrayList<>();
        for (EntitySpec entitySpec : entitySpecToPropIds.keySet()) {
            list.add(executor.submit(new SQLExecutorCallable(entitySpec, allEntitySpecToResultProcessor, allEntitySpecs, filters, propIds, keyIds)));
        }

        for (Future<List<StreamingIteratorPair>> future : list) {
            try {
                itrs.addAll(future.get());
            } catch (InterruptedException ex) {
                SQLGenUtil.logger().log(Level.FINER, "SQL generation thread interrupted", ex);
            } catch (ExecutionException ex) {
                throw new DataSourceReadException(ex);
            }
        }
        executor.shutdown();

        List<DataStreamingEventIterator<Proposition>> events
                = new ArrayList<>(
                        itrs.size());
        List<DataStreamingEventIterator<UniqueIdPair>> refs
                = new ArrayList<>();
        for (StreamingIteratorPair pair : itrs) {
            events.add(pair.getProps());
            refs.addAll(pair.getRefs());
        }
        RelationalDbDataReadIterator streamingResults
                = new RelationalDbDataReadIterator(refs, events, connections);

        return streamingResults;

    }

    private class StreamingIteratorPair {

        private final DataStreamingEventIterator<Proposition> props;
        private final List<? extends DataStreamingEventIterator<UniqueIdPair>> refs;
        private final Connection connection;

        StreamingIteratorPair(DataStreamingEventIterator<Proposition> props,
                List<? extends DataStreamingEventIterator<UniqueIdPair>> refs,
                Connection connection) {
            this.props = props;
            this.refs = refs;
            this.connection = connection;
        }

        public DataStreamingEventIterator<Proposition> getProps() {
            return props;
        }

        public List<? extends DataStreamingEventIterator<UniqueIdPair>> getRefs() {
            return refs;
        }

        public Connection getConnection() {
            return connection;
        }

    }

    private List<StreamingIteratorPair> processEntitySpecStreaming(EntitySpec entitySpec,
            Map<EntitySpec, SQLGenResultProcessorFactory> allEntitySpecToResultProcessor,
            Collection<EntitySpec> allEntitySpecs, Filter filters,
            Set<String> propIds, Set<String> keyIds,
            StreamingSQLExecutor executor)
            throws DataSourceReadException {
        List<StreamingIteratorPair> result = new ArrayList<>();
        Logger logger = SQLGenUtil.logger();
        logProcessingEntitySpec(logger, entitySpec);

        SQLGenResultProcessorFactory<Proposition> factory
                = getResultProcessorFactory(allEntitySpecToResultProcessor,
                        entitySpec);

        List<EntitySpec> applicableEntitySpecs
                = computeApplicableEntitySpecs(allEntitySpecs, entitySpec);

        Set<Filter> applicableFilters = computeApplicableFilters(filters,
                allEntitySpecs, entitySpec);

        List<Set<Filter>> partitions = constructPartitions(entitySpec,
                applicableFilters);

        LinkedHashMap<String, ReferenceSpec> inboundRefSpecs
                = collectInboundRefSpecs(applicableEntitySpecs, entitySpec, propIds);
        Map<String, ReferenceSpec> bidirRefSpecs = collectBidirectionalReferences(applicableEntitySpecs, entitySpec, propIds);

        String dataSourceBackendId = this.backend.getDataSourceBackendId();
        StreamingMainResultProcessor<Proposition> resultProcessor
                = factory.getStreamingInstance(dataSourceBackendId, entitySpec,
                        inboundRefSpecs, bidirRefSpecs, propIds);

        for (Set<Filter> filterSet : partitions) {
            generateAndExecuteSelectStreaming(entitySpec, null, propIds, filterSet,
                    applicableEntitySpecs, inboundRefSpecs, keyIds,
                    SQLOrderBy.ASCENDING,
                    resultProcessor, executor, true);
            DataStreamingEventIterator<Proposition> results
                    = resultProcessor.getResults();
            List<DataStreamingEventIterator<UniqueIdPair>> refResults
                    = java.util.Collections.singletonList(resultProcessor
                            .getInboundReferenceResults());
            result.add(new StreamingIteratorPair(results, refResults, executor.getConnection()));
        }

        logDoneProcessing(logger, entitySpec);

        return result;
    }

    private static List<EntitySpec> computeApplicableEntitySpecs(
            Collection<EntitySpec> allEntitySpecs, EntitySpec entitySpec) {
        List<EntitySpec> result = new LinkedList<>(allEntitySpecs);
        removeNonApplicableEntitySpecs(entitySpec, result);
        logApplicableEntitySpecs(result);
        assert !result.isEmpty() :
                "allEntitySpecsCopy should have at least one element";
        return result;
    }

    private Set<Filter> computeApplicableFilters(Filter filters,
            Collection<EntitySpec> allEntitySpecs, EntitySpec entitySpec) {
        Set<Filter> filtersCopy = copyFilters(filters);
        removeNonApplicableFilters(allEntitySpecs, filtersCopy, entitySpec);
        return filtersCopy;
    }

    /*
     * Partitions position filters according to the data source backend's
     * configuration. This has the effect of splitting up one query into
     * multiple queries to improve performance. Currently this only works when
     * upper and lower bounds are provided on the same side of the specified
     * proposition's intervals. If there are multiple position filters
     * specified, which one gets chosen to partition is non-deterministic.
     */
    private List<Set<Filter>> constructPartitions(EntitySpec entitySpec,
            Set<Filter> filtersCopy) {
        PositionFilter positionFilter = null;
        for (Filter filter : filtersCopy) {
            if (filter instanceof PositionFilter) {
                positionFilter = (PositionFilter) filter;
                break;
            }
        }
        Unit partitionBy = entitySpec.getPartitionBy();
        List<Set<Filter>> filterList = new ArrayList<>();
        if (partitionBy == null
                || positionFilter == null
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
            Unit finishUnit = finishGran != null ? finishGran.getCorrespondingUnit() : null;
            boolean doLoop = true;
            while (doLoop) {
                Set<Filter> newFiltersCopy = new HashSet<>(filtersCopy);
                newFiltersCopy.remove(positionFilter);
                Long nextStart = partitionBy.addToPosition(start, 1);
                Long finish = finishUnit != null ? finishUnit.addToPosition(
                        nextStart, -1) : -1;
                if (finish.compareTo(actualFinish) >= 0) {
                    finish = actualFinish;
                    doLoop = false;
                }
                PositionFilter newPositionFilter = new PositionFilter(
                        positionFilter.getPropositionIds(), start, startGran,
                        finish, finishGran, positionFilter.getStartSide(),
                        positionFilter.getFinishSide());
                newFiltersCopy.add(newPositionFilter);
                filterList.add(newFiltersCopy);
                start = nextStart;
            }
        }
        return filterList;
    }

    private DataStager doStage(Collection<EntitySpec> allEntitySpecs,
            Filter filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order) throws DataSourceReadException {
        DataStager stager = null;

        try {
            stager = getDataStager(null,
                    new LinkedList<>(allEntitySpecs),
                    copyFilters(filters), propIds, keyIds, order,
                    this.connectionSpec);
            stager.stageTables();
        } catch (SQLException ex) {
            Logger logger = SQLGenUtil.logger();
            logger.log(Level.SEVERE, "Failed to create staging area", ex);
            throw new DataSourceReadException(ex);
        }
        return stager;
    }

    private static SQLGenResultProcessorFactory<Proposition> getResultProcessorFactory(
            Map<EntitySpec, SQLGenResultProcessorFactory> allEntitySpecToResultProcessor,
            EntitySpec entitySpec) {
        // we know that the map contains only factory instances that are
        // parameterized by implementations of Proposition
        @SuppressWarnings("unchecked")
        SQLGenResultProcessorFactory<Proposition> factory
                = allEntitySpecToResultProcessor.get(entitySpec);
        assert factory != null : "factory should never be null";
        return factory;
    }

    private void logDoneProcessing(Logger logger, EntitySpec entitySpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE,
                    "Results of query for {0} in data source backend {1} "
                    + "have been processed",
                    new Object[]{entitySpec.getName(),
                        backendNameForMessages()});
        }
    }

    private void logProcessingEntitySpec(Logger logger, EntitySpec entitySpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(
                    Level.FINE,
                    "Data source backend {0} is processing entity spec {1}",
                    new Object[]{backendNameForMessages(),
                        entitySpec.getName()});
        }
    }

    private static void logApplicableEntitySpecs(List<EntitySpec> allEntitySpecsCopy) {
        Logger logger = SQLGenUtil.logger();
        if (logger.isLoggable(Level.FINER)) {
            String[] allEntitySpecsCopyNames = new String[allEntitySpecsCopy.size()];
            int i = 0;
            for (EntitySpec aesc : allEntitySpecsCopy) {
                allEntitySpecsCopyNames[i++] = aesc.getName();
            }
            logger.log(Level.FINER, "Applicable entity specs are {0}",
                    StringUtils.join(allEntitySpecsCopyNames, ", "));
        }
    }

    private static LinkedHashMap<String, ReferenceSpec> collectInboundRefSpecs(Collection<EntitySpec> entitySpecs, EntitySpec rhsEntitySpec,
            Set<String> propIds) {
        LinkedHashMap<String, ReferenceSpec> result = new LinkedHashMap<>();

        for (EntitySpec lhsReferenceSpec : entitySpecs) {
            if (lhsReferenceSpec.hasReferenceTo(rhsEntitySpec)
                    && Collections.containsAny(propIds, lhsReferenceSpec.getPropositionIds())) {
                boolean isMany = false;
                if (rhsEntitySpec.hasReferenceTo(lhsReferenceSpec)) {
                    for (ReferenceSpec rhsToLhsReferenceSpec : rhsEntitySpec.getReferenceSpecs()) {
                        if (rhsToLhsReferenceSpec.getEntityName().equals(lhsReferenceSpec.getName())
                                && rhsToLhsReferenceSpec.getType() == ReferenceSpec.Type.MANY) {
                            isMany = true;
                            break;
                        }
                    }
                }
                if (!isMany) {
                    for (ReferenceSpec lhsToRhsReferenceSpec : lhsReferenceSpec.getReferenceSpecs()) {
                        if (lhsToRhsReferenceSpec.getEntityName().equals(rhsEntitySpec.getName())) {
                            result.put(lhsReferenceSpec.getName(), lhsToRhsReferenceSpec);
                        }
                    }
                }
            }
        }

        return result;
    }

    private static Map<String, ReferenceSpec> collectBidirectionalReferences(Collection<EntitySpec> entitySpecs, EntitySpec lhsEntitySpec,
            Set<String> propIds) {
        Map<String, ReferenceSpec> result = new HashMap<>();

        for (ReferenceSpec lhsToRhsReferenceSpec : lhsEntitySpec.getReferenceSpecs()) {
            for (EntitySpec rhsEntitySpec : entitySpecs) {
                if (rhsEntitySpec.getName().equals(lhsToRhsReferenceSpec.getEntityName())
                        && Collections.containsAny(propIds, rhsEntitySpec.getPropositionIds())) {
                    if (rhsEntitySpec.hasReferenceTo(lhsEntitySpec)) {
                        for (ReferenceSpec rhsToLhsReferenceSpec : rhsEntitySpec.getReferenceSpecs()) {
                            if (rhsToLhsReferenceSpec.getEntityName().equals(lhsEntitySpec
                                    .getName()) && rhsToLhsReferenceSpec.getType()
                                    == ReferenceSpec.Type.MANY) {
                                result.put(rhsEntitySpec.getName(), lhsToRhsReferenceSpec);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /*
     * We need to store multiple types of result processor factories in the same
     * map
     */
    @SuppressWarnings("rawtypes")
    private Map<EntitySpec, SQLGenResultProcessorFactory> allEntitySpecToResultProcessor() {
        /*
         * The order of the entity specs matters for multiple with the same
         * name. Thus, we use a LinkedHashMap.
         */
        Map<EntitySpec, SQLGenResultProcessorFactory> result = new LinkedHashMap<>();
        PrimitiveParameterResultProcessorFactory ppFactory = new PrimitiveParameterResultProcessorFactory(this.backend);
        for (EntitySpec es : this.primitiveParameterEntitySpecs) {
            result.put(es, ppFactory);
        }
        EventResultProcessorFactory eFactory = new EventResultProcessorFactory(this.backend);
        for (EntitySpec es : this.eventEntitySpecs) {
            result.put(es, eFactory);
        }
        ConstantResultProcessorFactory cFactory = new ConstantResultProcessorFactory(this.backend);
        for (EntitySpec es : this.constantEntitySpecs) {
            result.put(es, cFactory);
        }

        return result;
    }

    private static Set<Filter> copyFilters(Filter filters) {
        Set<Filter> filtersCopy = new HashSet<>();
        if (filters != null) {
            for (Iterator<Filter> itr = filters.andIterator(); itr.hasNext();) {
                filtersCopy.add(itr.next());
            }
        }
        return filtersCopy;
    }

    private <P extends Proposition> void generateAndExecuteSelectStreaming(
            EntitySpec entitySpec, ReferenceSpec referenceSpec,
            Set<String> propIds, Set<Filter> filtersCopy,
            List<EntitySpec> entitySpecsCopy, LinkedHashMap<String, ReferenceSpec> inboundRefSpecs, Set<String> keyIds,
            SQLOrderBy order, StreamingResultProcessor<P> resultProcessor,
            StreamingSQLExecutor executor,
            boolean wrapKeyId) throws DataSourceReadException {
        Logger logger = SQLGenUtil.logger();
        String backendNameForMessages = backendNameForMessages();
        String entitySpecName = entitySpec.getName();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE,
                    "Data source backend {0} is generating query for {1}",
                    new Object[]{backendNameForMessages, entitySpecName});
        }

        String query = getSelectStatement(entitySpec, referenceSpec,
                entitySpecsCopy, inboundRefSpecs, filtersCopy, propIds,
                keyIds, order,
                resultProcessor, wrapKeyId).generateStatement();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(
                    Level.FINE,
                    "Data source backend {0} generated the following query for {1}: {2}",
                    new Object[]{backendNameForMessages, entitySpecName,
                        query});
        }
        executor.executeSelect(entitySpecName, query, resultProcessor);
    }

    private static void removeNonApplicableEntitySpecs(EntitySpec entitySpec,
            Collection<EntitySpec> entitySpecs) {
        for (Iterator<EntitySpec> itr = entitySpecs.iterator(); itr.hasNext();) {
            EntitySpec es = itr.next();
            if (es != entitySpec && !es.hasReferenceTo(entitySpec)) {
                itr.remove();
            }
        }

    }

    /**
     * Remove filters that are not directly applicable to the given entity spec
     * and are not applicable to other entity specs that refer to it.
     *
     * @param entitySpecs
     * @param filtersCopy
     * @param entitySpec
     */
    private static void removeNonApplicableFilters(
            Collection<EntitySpec> entitySpecs, Set<Filter> filtersCopy,
            EntitySpec entitySpec) {
        Set<EntitySpec> entitySpecsSet = new HashSet<>();
        Set<String> filterPropIds = new HashSet<>();
        String[] entitySpecPropIds = entitySpec.getPropositionIds();
        for (Iterator<Filter> itr = filtersCopy.iterator(); itr.hasNext();) {
            Filter f = itr.next();
            Arrays.addAll(filterPropIds, f.getPropositionIds());
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

    private static boolean atLeastOneInInboundReferences(
            Set<EntitySpec> entitySpecsSet, EntitySpec entitySpec) {
        for (EntitySpec es : entitySpecsSet) {
            if (es.hasReferenceTo(entitySpec)) {
                return true;
            }
        }
        return false;
    }

    protected abstract SelectStatement getSelectStatement(
            EntitySpec entitySpec, ReferenceSpec referenceSpec,
            List<EntitySpec> entitySpecs, Map<String, ReferenceSpec> inboundRefSpecs, Set<Filter> filters,
            Set<String> propIds, Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor,
            boolean wrapKeyId);

    protected DataStager getDataStager(
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, ConnectionSpec connectionSpec) {
        throw new UnsupportedOperationException("SQL generator "
                + getClass().getName() + " does not support data staging");
    }

    protected String assembleReadPropositionsQuery(StringBuilder selectClause,
            StringBuilder fromClause, StringBuilder whereClause) {
        return MessageFormat.format(readPropositionsSQL, selectClause,
                fromClause, whereClause);
    }

    /**
     * Returns whether an IN clause containing the proposition ids of interest
     * should be added to the WHERE clause.
     *
     * @param queryPropIds the proposition ids to query.
     * @param entitySpecPropIds the proposition ids corresponding to the current
     * entity spec.
     * @return <code>true</code> if the query contains < 85% of the proposition
     * ids that are known to the data source and if the where clause would
     * contain less than or equal to 2000 codes.
     */
    static boolean needsPropIdInClause(Set<String> queryPropIds,
            String[] entitySpecPropIds) {

        Set<String> entitySpecPropIdsSet = Arrays.asSet(entitySpecPropIds);

        // Filter propIds that are not in the entitySpecPropIds array.
        List<String> filteredPropIds = new ArrayList<>(
                entitySpecPropIds.length);
        for (String propId : queryPropIds) {
            if (entitySpecPropIdsSet.contains(propId)) {
                filteredPropIds.add(propId);
            }
        }
        return (filteredPropIds.size() < entitySpecPropIds.length * 0.85f)
                && (filteredPropIds.size() <= 2000);
    }

    /**
     * Gets a the class name of the driver to load for this SQL generator, or
     * <code>null</code> if the driver is a JDBC 4 driver and does not need to
     * be loaded explicitly. Returning not- <code>null</code> will do no harm if
     * a JDBC 4 driver.
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
            return backendDisplayName + "(" + this.backend.getClass().getName()
                    + ")";
        } else {
            return this.backend.getClass().getName();
        }
    }

    private Map<EntitySpec, List<String>> entitySpecToPropIds(
            Set<String> propIds) throws AssertionError {
        Map<EntitySpec, List<String>> result = new HashMap<>();
        for (String propId : propIds) {
            boolean inDataSource = populateEntitySpecToPropIdMap(
                    new String[]{propId}, result);
            Logger logger = SQLGenUtil.logger();
            if (!inDataSource && logger.isLoggable(Level.FINER)) {
                logger.log(
                        Level.FINER,
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
