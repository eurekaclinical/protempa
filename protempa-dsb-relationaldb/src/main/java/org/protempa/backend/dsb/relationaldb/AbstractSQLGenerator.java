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
import org.protempa.backend.dsb.relationaldb.Operator;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.Unit;
import org.protempa.proposition.value.UnitFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class for implement database and driver-specific SQL generators.
 *
 * @author Andrew Post
 */
public abstract class AbstractSQLGenerator implements SQLGenerator {

    static final int FETCH_SIZE = 10000;
    private static final String readPropositionsSQL = "select {0} from {1} {2}";
    private ConnectionSpec connectionSpec;
    private final Map<String, List<EntitySpec>> primitiveParameterSpecs;
    private EntitySpec[] primitiveParameterEntitySpecs;
    private final Map<String, List<EntitySpec>> eventSpecs;
    private EntitySpec[] eventEntitySpecs;
    private final Map<String, List<EntitySpec>> constantSpecs;
    private EntitySpec[] constantEntitySpecs;
    private StagingSpec[] stagedTableSpecs;
    private GranularityFactory granularities;
    private UnitFactory units;
    private RelationalDbDataSourceBackend backend;

    protected AbstractSQLGenerator() {
        this.primitiveParameterSpecs = new HashMap<>();
        this.eventSpecs = new HashMap<>();
        this.constantSpecs = new HashMap<>();
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
            this.stagedTableSpecs = relationalDatabaseSpec.getStagedSpecs();
            this.granularities = relationalDatabaseSpec.getGranularities();
            this.units = relationalDatabaseSpec.getUnits();
            this.connectionSpec = connectionSpec;
        } else {
            throw new IllegalArgumentException(
                    "relationalDatabaseSpec cannot be null");
        }

        this.backend = backend;
    }
    
    boolean getStreamingMode() {
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

    @Override
    public DataStreamingEventIterator<Proposition> readPropositionsStreaming(
            final Set<String> keyIds, final Set<String> propIds, final Filter filters)
            throws DataSourceReadException {
        final Map<EntitySpec, List<String>> entitySpecToPropIds =
                entitySpecToPropIds(propIds);
        final Map<EntitySpec, SQLGenResultProcessorFactory> allEntitySpecToResultProcessor = allEntitySpecToResultProcessor();
        final Collection<EntitySpec> allEntitySpecs =
                allEntitySpecToResultProcessor.keySet();
        DataStager stager = null;
        if (stagingApplies()) {
            stager = doStage(allEntitySpecs, filters, propIds, keyIds, null);
        }

        boolean transaction = true;
        Connection connection = null;
        if (this.connectionSpec != null) {
            try {
                connection = this.connectionSpec.getOrCreate();
                try {
                    connection.setAutoCommit(!transaction);
                } catch (SQLException ex) {
                    try {
                        connection.close();
                    } catch (SQLException ignore) {
                    }
                    throw new DataSourceReadException(
                            "Error setting auto commit", ex);
                }
            } catch (SQLException ex) {
                throw new DataSourceReadException(
                        "Error getting a database connection", ex);
            }
        }
        final List<StreamingIteratorPair> itrs =
                new ArrayList<>();
        //if (transaction) {
        final StreamingSQLExecutor streamingExecutor = new StreamingSQLExecutor(
                connection, backendNameForMessages(),
                this.backend.getQueryTimeout());
//        NonRetryingSQLExecutor nonStreamingExecutor =
//                new NonRetryingSQLExecutor(connection, backendNameForMessages(),
//                this.backend.getQueryTimeout());
//        ResultCache<Proposition> cachedResults = null;

        
/*
 * The multithreaded implementation
 */
//        final Queue<EntitySpec> queue = new LinkedList(entitySpecToPropIds.keySet());
//        final List<DataSourceReadException> exes = new ArrayList<DataSourceReadException>(4);
//        Thread[] threads = new Thread[4];
//
//        for (int i = 0; i < threads.length; i++) {
//            threads[i] = new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    Connection connection = null;
//                    try {
//                        connection = connectionSpec.getOrCreate();
//                        StreamingSQLExecutor streamingExecutor = new StreamingSQLExecutor(
//                                connection, backendNameForMessages(),
//                                backend.getQueryTimeout());
//
//                        EntitySpec entitySpec;
//                        do {
//                            synchronized (queue) {
//                                entitySpec = queue.poll();
//                            }
//                            if (entitySpec != null) {
//                                List<StreamingIteratorPair> pair =
//                                        processEntitySpecStreaming(entitySpec,
//                                        allEntitySpecToResultProcessor,
//                                        allEntitySpecs, filters,
//                                        propIds,
//                                        keyIds, streamingExecutor);
//                                synchronized (itrs) {
//                                    itrs.addAll(pair);
//                                }
//                            }
//                        } while (entitySpec != null);
//                    } catch (SQLException ex) {
//                        exes.add(new DataSourceReadException("Error getting connection", ex));
//                    } catch (DataSourceReadException ex) {
//                        try {
//                            connection.close();
//                        } catch (Exception ignore) {}
//                        exes.add(ex);
//                    }
//                }
//            });
//            threads[i].start();
//        }
//        for (int i = 0; i < threads.length; i++) {
//            try {
//                threads[i].join();
//            } catch (InterruptedException ex) {
//                Logger.getLogger(AbstractSQLGenerator.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        for (DataSourceReadException ex : exes) {
//            throw ex;
//        }
/*
 * End multithreaded implementation.
 */
        Set<TableSpec> tableSpecs = new HashSet<>();
        for (EntitySpec entitySpec : entitySpecToPropIds.keySet()) {
           Arrays.addAll(tableSpecs, entitySpec.getTableSpecs());
//            boolean partitionUsed = false;
//            boolean partitionUsed = entitySpec.getPartitionBy() != null;
//            if (!partitionUsed) {
//                for (ReferenceSpec rs : entitySpec.getInboundRefSpecs()) {
//                    EntitySpec res = entitySpecForName(allEntitySpecs, 
//                            rs.getEntityName());
//                    partitionUsed = res.getPartitionBy() != null;
//                    if (partitionUsed) {
//                        break;
//                    }
//                }
//            }
//            if (!partitionUsed) {
            itrs.addAll(processEntitySpecStreaming(entitySpec,
                    allEntitySpecToResultProcessor,
                    allEntitySpecs, filters,
                    propIds,
                    keyIds, streamingExecutor));
//            } else {
//                if (cachedResults == null) {
//                    cachedResults = newResultCache();
//                }
//                processEntitySpec(entitySpec,
//                        allEntitySpecToResultProcessor,
//                        allEntitySpecs, filters,
//                        cachedResults, propIds,
//                        keyIds, null, nonStreamingExecutor);
//            }
        }
        logTableList(tableSpecs);
        List<DataStreamingEventIterator<Proposition>> events =
                new ArrayList<>(
                itrs.size());
        List<DataStreamingEventIterator<UniqueIdPair>> refs =
                new ArrayList<>();
        for (StreamingIteratorPair pair : itrs) {
            events.add(pair.getProps());
            refs.addAll(pair.getRefs());
        }
        RelationalDbDataReadIterator streamingResults =
                new RelationalDbDataReadIterator(refs, events, connection,
                transaction, stagingApplies() ? stager : null);

//        if (cachedResults != null) {
//            return new CombinedRelationalDbDataReadIterator(streamingResults,
//                    cachedResults);
//        } else {
        return streamingResults;
//        }
    }

    private void logTableList(Set<TableSpec> tableSpecs) {
        Logger logger = SQLGenUtil.logger();
        if (logger.isLoggable(Level.FINE)) {
            List<String> tableList = new ArrayList<>(tableSpecs.size());
            for (TableSpec tableSpec : tableSpecs) {
                String schema = tableSpec.getSchema();
                StringBuilder sb = new StringBuilder();
                if (schema != null) {
                    sb.append(schema);
                    sb.append('.');
                }
                sb.append(tableSpec.getTable());
                tableList.add(sb.toString());
            }
            logger.log(Level.FINE, "Tables to be accessed: {0}",
                    StringUtils.join(tableList, ", "));
        }
    }

//    /*
//     * allEntitySpecs() returns a map of non-parameterized
//     * SQLGenResultProcessorFactory objects, therefore the resulting map here
//     * cannot be parameterized either.
//     */
//    @SuppressWarnings("rawtypes")
//    @Override
//    public ResultCache<Proposition> readPropositions(final Set<String> keyIds,
//            final Set<String> propIds, final Filter filters,
//            final SQLOrderBy order) throws DataSourceReadException {
//        final Map<EntitySpec, List<String>> entitySpecToPropIds =
//                entitySpecToPropIds(propIds);
//        final Map<EntitySpec, SQLGenResultProcessorFactory> entitySpecToResultProcessor = allEntitySpecToResultProcessor();
//        final Collection<EntitySpec> entitySpecs =
//                entitySpecToResultProcessor.keySet();
//
//        DataStager stager = null;
//        try {
//            if (stagingApplies()) {
//                stager = doStage(entitySpecs, filters, propIds, keyIds, order);
//            }
//
//            return readEntitySpecs(filters, propIds, keyIds, order,
//                    entitySpecToPropIds,
//                    entitySpecToResultProcessor);
//        } finally {
//            if (stagingApplies()) {
//                cleanupStagingArea(stager);
//            }
//        }
//    }
//
//    private ResultCache<Proposition> readEntitySpecs(final Filter filters,
//            final Set<String> propIds, final Set<String> keyIds,
//            final SQLOrderBy order,
//            final Map<EntitySpec, List<String>> entitySpecToPropIds,
//            final Map<EntitySpec, SQLGenResultProcessorFactory> allEntitySpecToResultProcessor)
//            throws DataSourceReadException {
//        final ResultCache<Proposition> results = newResultCache();
//        final Collection<EntitySpec> allEntitySpecs =
//                allEntitySpecToResultProcessor.keySet();
//
//        boolean transaction = true;
//        if (transaction) {
//            NonRetryingSQLExecutor exec = null;
//            final NonRetryingSQLExecutor executor;
//            try {
//                executor = new NonRetryingSQLExecutor(
//                        this.connectionSpec, backendNameForMessages(),
//                        this.backend.getQueryTimeout());
//                exec = executor;
//                Retryable<DataSourceReadException> operation =
//                        new Retryable<DataSourceReadException>() {
//
//                            @Override
//                            public DataSourceReadException attempt() {
//                                for (EntitySpec entitySpec :
//                                        entitySpecToPropIds.keySet()) {
//                                    try {
//                                        processEntitySpec(entitySpec,
//                                                allEntitySpecToResultProcessor,
//                                                allEntitySpecs, filters,
//                                                results, propIds,
//                                                keyIds, order, executor);
//                                    } catch (DataSourceReadException ex) {
//                                        return ex;
//                                    }
//                                }
//                                return null;
//                            }
//
//                            @Override
//                            public void recover() {
//                                try {
//                                    Thread.sleep(3 * 1000L);
//                                } catch (InterruptedException ex) {
//                                    throw new RuntimeException(ex);
//                                }
//                            }
//                        };
//                Retryer<DataSourceReadException> retryer =
//                        new Retryer<DataSourceReadException>(3);
//                if (!retryer.execute(operation)) {
//                    DataSourceReadException ex =
//                            new DataSourceReadException(retryer.getErrors());
//                    throw new DataSourceReadException(
//                            "Error retrieving data from data source backend "
//                            + executor.getBackendNameForMessages(), ex);
//                }
//                executor.close();
//                exec = null;
//            } catch (SQLException ex) {
//                throw new DataSourceReadException(
//                        "Error getting a database connection", ex);
//            } finally {
//                if (exec != null) {
//                    try {
//                        exec.close();
//                    } catch (SQLException ignore) {
//                    }
//                }
//            }
//        } else {
//            RetryingSQLExecutor executor = new RetryingSQLExecutor(
//                    this.connectionSpec, backendNameForMessages(),
//                    this.backend.getQueryTimeout(), 3);
//            for (EntitySpec entitySpec : entitySpecToPropIds.keySet()) {
//                processEntitySpec(entitySpec,
//                        allEntitySpecToResultProcessor,
//                        allEntitySpecs, filters, results, propIds, keyIds,
//                        order, executor);
//            }
//        }
//
//        return results;
//    }
    private void cleanupStagingArea(DataStager stager)
            throws DataSourceReadException {
        Logger logger = SQLGenUtil.logger();
        logger.log(Level.INFO, "Cleaning up staged data");
        try {
            stager.cleanup();
        } catch (SQLException ex) {
            throw new DataSourceReadException(
                    "Failed to clean up the staging area", ex);
        }
    }

    private void processEntitySpec(EntitySpec entitySpec,
            Map<EntitySpec, SQLGenResultProcessorFactory> allEntitySpecToResultProcessor,
            Collection<EntitySpec> allEntitySpecs, Filter filters,
            ResultCache<Proposition> results, Set<String> propIds,
            Set<String> keyIds, SQLOrderBy order,
            org.protempa.backend.dsb.relationaldb.SQLExecutor executor)
            throws DataSourceReadException {
        Logger logger = SQLGenUtil.logger();
        logProcessingEntitySpec(logger, entitySpec);

        SQLGenResultProcessorFactory<Proposition> factory =
                getResultProcessorFactory(allEntitySpecToResultProcessor,
                entitySpec);

        List<EntitySpec> applicableEntitySpecs =
                computeApplicableEntitySpecs(allEntitySpecs, entitySpec);

        Set<Filter> applicableFilters = computeApplicableFilters(filters,
                allEntitySpecs, entitySpec);

        List<Set<Filter>> partitions = constructPartitions(entitySpec,
                applicableFilters);

        String dataSourceBackendId = this.backend.getDataSourceBackendId();
        MainResultProcessor<Proposition> resultProcessor =
                factory.getInstance(dataSourceBackendId, entitySpec, results);

        for (Set<Filter> partition : partitions) {
            generateAndExecuteSelect(entitySpec, null, propIds, partition,
                    applicableEntitySpecs, new LinkedHashMap<String, ReferenceSpec>(), keyIds, order, resultProcessor,
                    executor, false);
        }

        if (results.anyAdded()) {
            processReferences(entitySpec, factory, dataSourceBackendId,
                    results, allEntitySpecs, propIds, filters, keyIds, order,
                    executor);
        } else {
            logSkippingRefs(logger, entitySpec);
        }

        results.clearTmp();

        logDoneProcessing(logger, entitySpec);
    }

    private class StreamingIteratorPair {

        private final DataStreamingEventIterator<Proposition> props;
        private final List<? extends DataStreamingEventIterator<UniqueIdPair>>
        refs;

        StreamingIteratorPair(DataStreamingEventIterator<Proposition> props,
                List<? extends DataStreamingEventIterator<UniqueIdPair>>
                        refs) {
            this.props = props;
            this.refs = refs;
        }

        public DataStreamingEventIterator<Proposition> getProps() {
            return props;
        }

        public List<? extends DataStreamingEventIterator<UniqueIdPair>> getRefs
                () {
            return refs;
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

        SQLGenResultProcessorFactory<Proposition> factory =
                getResultProcessorFactory(allEntitySpecToResultProcessor,
                entitySpec);

        List<EntitySpec> applicableEntitySpecs =
                computeApplicableEntitySpecs(allEntitySpecs, entitySpec);

        Set<Filter> applicableFilters = computeApplicableFilters(filters,
                allEntitySpecs, entitySpec);

        List<Set<Filter>> partitions = constructPartitions(entitySpec,
                applicableFilters);

        LinkedHashMap<String, ReferenceSpec> inboundRefSpecs =
                collectInboundRefSpecs
                (applicableEntitySpecs, entitySpec, propIds);
        Map<String, ReferenceSpec> bidirRefSpecs = collectBidirectionalReferences
                (applicableEntitySpecs, entitySpec, propIds);

        String dataSourceBackendId = this.backend.getDataSourceBackendId();
        StreamingMainResultProcessor<Proposition> resultProcessor =
                factory.getStreamingInstance(dataSourceBackendId, entitySpec,
                        inboundRefSpecs, bidirRefSpecs);

        for (Set<Filter> filterSet : partitions) {
            generateAndExecuteSelectStreaming(entitySpec, null, propIds, filterSet,
                    applicableEntitySpecs, inboundRefSpecs, keyIds,
                    SQLOrderBy.ASCENDING,
                    resultProcessor, executor, true);
            DataStreamingEventIterator<Proposition> results =
                    resultProcessor.getResults();
            List<DataStreamingEventIterator<UniqueIdPair>> refResults =
                    java.util.Collections.singletonList(resultProcessor
                            .getInboundReferenceResults());
//            List<ReferenceResultSetIterator> refResults =
//                    processReferencesStreaming(entitySpec, factory,
//                    dataSourceBackendId,
//                    allEntitySpecs, propIds, filters, keyIds, executor);
            result.add(new StreamingIteratorPair(results, refResults));
        }

        logDoneProcessing(logger, entitySpec);

        return result;
    }

    private void processReferences(EntitySpec entitySpec,
            SQLGenResultProcessorFactory<Proposition> factory,
            String dataSourceBackendId, ResultCache<Proposition> results,
            Collection<EntitySpec> allEntitySpecs, Set<String> propIds,
            Filter filters, Set<String> keyIds, SQLOrderBy order,
            org.protempa.backend.dsb.relationaldb.SQLExecutor executor)
            throws DataSourceReadException {
        ReferenceSpec[] refSpecs = entitySpec.getReferenceSpecs();
        if (refSpecs != null) {
            Logger logger = SQLGenUtil.logger();

            /*
             * Create a copy of allEntitySpecs with the current entitySpec the
             * first item of the list. This is to make sure that its joins make
             * it into the list of column specs.
             */
            for (ReferenceSpec referenceSpec : refSpecs) {
                RefResultProcessor<Proposition> refResultProcessor =
                        factory.getRefInstance(dataSourceBackendId,
                        entitySpec, referenceSpec, results);
                List<EntitySpec> allEntitySpecsCopyForRefs =
                        copyEntitySpecsForRefs(entitySpec, allEntitySpecs);

                EntitySpec referredToEntitySpec = entitySpecForName(
                        allEntitySpecsCopyForRefs,
                        referenceSpec.getEntityName());
                assert referredToEntitySpec != null :
                        "refferedToEntitySpec should not be null";
                if (Collections.containsAny(propIds,
                        referredToEntitySpec.getPropositionIds())) {
                    logProcessingRef(logger, referenceSpec, entitySpec);

                    retainEntitySpecsWithInboundRefs(
                            allEntitySpecsCopyForRefs, entitySpec,
                            referenceSpec);

                    Set<Filter> refsApplicableFilters =
                            refsComputeApplicableFilters(filters,
                            allEntitySpecsCopyForRefs, referredToEntitySpec);

                    retainEntitySpecsWithFiltersOrConstraints(
                            entitySpec, referredToEntitySpec,
                            allEntitySpecsCopyForRefs, refsApplicableFilters,
                            propIds);

                    List<Set<Filter>> refsFilterSets = constructPartitions(
                            referredToEntitySpec, refsApplicableFilters);

                    SortedMap<String, ReferenceSpec> applicableRefSpecs = new
                            TreeMap<>();

                    for (Set<Filter> filterSet : refsFilterSets) {
                        generateAndExecuteSelect(entitySpec, referenceSpec,
                                propIds, filterSet, allEntitySpecsCopyForRefs,
                                new LinkedHashMap<String, ReferenceSpec>(),
                                keyIds, order, refResultProcessor, executor,
                                false);
                    }
                    logDoneProcessingRef(logger, referenceSpec,
                            entitySpec);
                } else {
                    logSkippingReference(logger, referenceSpec);
                }

            }
            logDoneProcessingEntitySpec(logger, entitySpec);
        }
    }

    private List<ReferenceResultSetIterator> processReferencesStreaming(
            EntitySpec entitySpec,
            SQLGenResultProcessorFactory<Proposition> factory,
            String dataSourceBackendId,
            Collection<EntitySpec> allEntitySpecs, Set<String> propIds,
            Filter filters, Set<String> keyIds,
            StreamingSQLExecutor executor)
            throws DataSourceReadException {
        List<ReferenceResultSetIterator> itrs =
                new ArrayList<>();
        ReferenceSpec[] refSpecs = entitySpec.getReferenceSpecs();
        if (refSpecs != null) {
            Logger logger = SQLGenUtil.logger();

            /*
             * Create a copy of allEntitySpecs with the current entitySpec the
             * first item of the list. This is to make sure that its joins make
             * it into the list of column specs.
             */
            for (ReferenceSpec referenceSpec : refSpecs) {
                StreamingRefResultProcessor<Proposition> refResultProcessor =
                        null;
                List<EntitySpec> allEntitySpecsCopyForRefs =
                        copyEntitySpecsForRefs(entitySpec, allEntitySpecs);
                EntitySpec referredToEntitySpec = entitySpecForName(
                        allEntitySpecsCopyForRefs,
                        referenceSpec.getEntityName());
                assert referredToEntitySpec != null :
                        "refferedToEntitySpec should not be null";
                refResultProcessor =
                        factory.getStreamingRefInstance(referenceSpec,
                        entitySpec, dataSourceBackendId);

                if (Collections.containsAny(propIds,
                        referredToEntitySpec.getPropositionIds())) {
                    logProcessingRef(logger, referenceSpec, entitySpec);

                    retainEntitySpecsWithInboundRefs(
                            allEntitySpecsCopyForRefs, entitySpec,
                            referenceSpec);

                    Set<Filter> refsApplicableFilters =
                            refsComputeApplicableFilters(filters,
                            allEntitySpecsCopyForRefs, referredToEntitySpec);

                    retainEntitySpecsWithFiltersOrConstraints(
                            entitySpec, referredToEntitySpec,
                            allEntitySpecsCopyForRefs, refsApplicableFilters,
                            propIds);

                    List<Set<Filter>> refsFilterSets = constructPartitions(
                            referredToEntitySpec, refsApplicableFilters);

                    for (Set<Filter> filterSet : refsFilterSets) {
                        generateAndExecuteSelectStreaming(entitySpec,
                                referenceSpec,
                                propIds, filterSet,
                                allEntitySpecsCopyForRefs,
                                new LinkedHashMap<String, ReferenceSpec>(),
                                keyIds, SQLOrderBy.ASCENDING,
                                refResultProcessor, executor, true);
                        itrs.add(refResultProcessor.getResult());
                    }
                    logDoneProcessingRef(logger, referenceSpec,
                            entitySpec);
                } else {
                    logSkippingReference(logger, referenceSpec);
                }

            }
            logDoneProcessingEntitySpec(logger, entitySpec);
        }
        return itrs;
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

    private Set<Filter> refsComputeApplicableFilters(Filter filters,
            List<EntitySpec> allEntitySpecsCopyForRefs,
            EntitySpec referredToEntitySpec) {
        Set<Filter> refFiltersCopy = copyFilters(filters);
        removeNonApplicableFilters(
                allEntitySpecsCopyForRefs, refFiltersCopy,
                referredToEntitySpec);
        removeStagedFilters(referredToEntitySpec, refFiltersCopy);
        return refFiltersCopy;
    }

    private Set<Filter> computeApplicableFilters(Filter filters,
            Collection<EntitySpec> allEntitySpecs, EntitySpec entitySpec) {
        Set<Filter> filtersCopy = copyFilters(filters);
        removeNonApplicableFilters(allEntitySpecs, filtersCopy, entitySpec);
        removeStagedFilters(entitySpec, filtersCopy);
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

    private List<EntitySpec> copyEntitySpecsForRefs(EntitySpec entitySpec,
            Collection<EntitySpec> allEntitySpecs) {
        List<EntitySpec> allEntitySpecsCopyForRefs = new LinkedList<>();
        allEntitySpecsCopyForRefs.add(entitySpec);
        for (EntitySpec es : allEntitySpecs) {
            if (es != entitySpec) {
                allEntitySpecsCopyForRefs.add(es);
            }
        }
        return allEntitySpecsCopyForRefs;
    }

    private DataStager doStage(Collection<EntitySpec> allEntitySpecs,
            Filter filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order) throws DataSourceReadException {
        DataStager stager = null;

        try {
            stager = getDataStager(this.stagedTableSpecs, null,
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

    private static SQLGenResultProcessorFactory<Proposition> getResultProcessorFactory(
            Map<EntitySpec, SQLGenResultProcessorFactory> allEntitySpecToResultProcessor,
            EntitySpec entitySpec) {
        // we know that the map contains only factory instances that are
        // parameterized by implementations of Proposition
        @SuppressWarnings("unchecked")
        SQLGenResultProcessorFactory<Proposition> factory =
                allEntitySpecToResultProcessor.get(entitySpec);
        assert factory != null : "factory should never be null";
        return factory;
    }

    private void logSkippingReference(Logger logger, ReferenceSpec referenceSpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Skipping reference {0}",
                    referenceSpec.getReferenceName());
        }
    }

    private void logSkippingRefs(Logger logger, EntitySpec entitySpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(
                    Level.FINE,
                    "Skipping reference queries for entity spec {0} because the query for {0} returned no data",
                    entitySpec.getName());
        }
    }

    private ResultCache<Proposition> newResultCache() throws DataSourceReadException {
        ResultCache<Proposition> results;
        try {
            results = new ResultCache<>();
        } catch (IOException ex) {
            throw new DataSourceReadException(
                    "Could not create the cache", ex);
        }
        return results;
    }

    /**
     * Leave behind a position filter, if one exists and the entity spec has
     * partition by set and the duration of the partition is less than the
     * duration of the filter's time range.
     *
     * @param filtersCopy
     */
    private void removeStagedFilters(EntitySpec entitySpec,
            Set<Filter> filtersCopy) {
        boolean first = true;
        boolean isPositionFilter;
        Unit partitionBy = entitySpec.getPartitionBy();
        for (Iterator<Filter> itr = filtersCopy.iterator(); itr.hasNext();) {
            isPositionFilter = false;
            Filter f = itr.next();
            if (f instanceof PositionFilter) {
                isPositionFilter = true;
            }
            String[] propIds = f.getPropositionIds();
            FILTER_LOOP:
            for (String propId : propIds) {
                List<EntitySpec> entitySpecs = new ArrayList<>();
                if (this.constantSpecs.containsKey(propId)) {
                    entitySpecs.addAll(this.constantSpecs.get(propId));
                }
                if (this.eventSpecs.containsKey(propId)) {
                    entitySpecs.addAll(this.eventSpecs.get(propId));
                }
                if (this.primitiveParameterSpecs.containsKey(propId)) {
                    entitySpecs.addAll(this.primitiveParameterSpecs.get(propId));
                }

                for (StagingSpec staged : this.stagedTableSpecs) {
                    for (EntitySpec es : entitySpecs) {
                        for (EntitySpec ses : staged.getEntitySpecs()) {
                            if (SQLGenUtil.somePropIdsMatch(es, ses)) {
                                if (first && isPositionFilter
                                        && partitionBy != null) {
                                    PositionFilter pf = (PositionFilter) f;
                                    Long start = pf.getStart();
                                    if (start != null) {
                                        long addToPosition =
                                                partitionBy.addToPosition(start, 1);
                                        Long finish = pf.getFinish();
                                        if (finish != null) {
                                            if (addToPosition < finish) {
                                                break FILTER_LOOP;
                                            }
                                        }
                                    }
                                }
                                itr.remove();
                                break FILTER_LOOP;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean stagingApplies() {
        return this.stagedTableSpecs != null
                && this.stagedTableSpecs.length > 0;
    }

    private static void retainEntitySpecsWithFiltersOrConstraints(
            EntitySpec entitySpec, EntitySpec referredToEntitySpec,
            Collection<EntitySpec> allEntitySpecsCopyForRefs,
            Collection<Filter> refFiltersCopy, Set<String> propIds) {
        for (Iterator<EntitySpec> itr = allEntitySpecsCopyForRefs.iterator(); itr.hasNext();) {
            EntitySpec es = itr.next();
            if (es != entitySpec && es != referredToEntitySpec) {
                Set<String> esPropIds = Arrays.asSet(es.getPropositionIds());
                ColumnSpec codeSpec = es.getCodeSpec();
                if (codeSpec != null) {
                    List<ColumnSpec> codeSpecL = codeSpec.asList();
                    ColumnSpec last = codeSpecL.get(codeSpecL.size() - 1);
                    if (last.getConstraint() != null
                            && (last.getConstraint() != Operator.EQUAL_TO
                            || !last.isPropositionIdsComplete() || needsPropIdInClause(
                            propIds, es.getPropositionIds()))) {
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
            logger.log(Level.FINE,
                    "Results of query for {0} in data source backend {1} "
                    + "have been processed",
                    new Object[]{entitySpec.getName(),
                        backendNameForMessages()});
        }
    }

    private void logDoneProcessingEntitySpec(Logger logger,
            EntitySpec entitySpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(
                    Level.FINE,
                    "Data source backend {0} is done processing entity spec {1}",
                    new Object[]{backendNameForMessages(),
                        entitySpec.getName()});
        }
    }

    private void logDoneProcessingRef(Logger logger,
            ReferenceSpec referenceSpec, EntitySpec entitySpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(
                    Level.FINE,
                    "Data source backend {0} is done processing reference {1} for entity spec {2}",
                    new Object[]{backendNameForMessages(),
                        referenceSpec.getReferenceName(),
                        entitySpec.getName()});
        }
    }

    private void logProcessingRef(Logger logger, ReferenceSpec referenceSpec,
            EntitySpec entitySpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(
                    Level.FINE,
                    "Data source backend {0} is processing reference {1} for entity spec {2}",
                    new Object[]{backendNameForMessages(),
                        referenceSpec.getReferenceName(),
                        entitySpec.getName()});
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

    private static void retainEntitySpecsWithInboundRefs(
            Collection<EntitySpec> entitySpecs, EntitySpec entitySpec,
            ReferenceSpec referenceSpec) {
        for (Iterator<EntitySpec> itr = entitySpecs.iterator(); itr.hasNext();) {
            EntitySpec es = itr.next();
            if (es != entitySpec
                    && !referenceSpec.getEntityName().equals(es.getName())
                    && !es.hasReferenceTo(entitySpec)) {
                itr.remove();
            }
        }
    }

    private static LinkedHashMap<String, ReferenceSpec> collectInboundRefSpecs
            (Collection<EntitySpec> entitySpecs, EntitySpec rhsEntitySpec,
            Set<String> propIds) {
        LinkedHashMap<String, ReferenceSpec> result = new LinkedHashMap<>();

        for (EntitySpec lhsReferenceSpec : entitySpecs) {
            if (lhsReferenceSpec.hasReferenceTo(rhsEntitySpec) && 
                    Collections.containsAny(propIds, lhsReferenceSpec.getPropositionIds())) {
                boolean isMany = false;
                if (rhsEntitySpec.hasReferenceTo(lhsReferenceSpec)) {
                    for (ReferenceSpec rhsToLhsReferenceSpec : rhsEntitySpec.getReferenceSpecs()) {
                        if (rhsToLhsReferenceSpec.getEntityName().equals(lhsReferenceSpec.getName()) &&
                        rhsToLhsReferenceSpec.getType() == ReferenceSpec.Type.MANY) {
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

    private static Map<String, ReferenceSpec> collectBidirectionalReferences
            (Collection<EntitySpec> entitySpecs, EntitySpec lhsEntitySpec,
            Set<String> propIds) {
        Map<String, ReferenceSpec> result = new HashMap<>();

        for (ReferenceSpec lhsToRhsReferenceSpec : lhsEntitySpec.getReferenceSpecs()) {
            for (EntitySpec rhsEntitySpec : entitySpecs) {
                if (rhsEntitySpec.getName().equals(lhsToRhsReferenceSpec.getEntityName()) 
                        && Collections.containsAny(propIds, rhsEntitySpec.getPropositionIds())) {
                    if (rhsEntitySpec.hasReferenceTo(lhsEntitySpec)) {
                        for (ReferenceSpec rhsToLhsReferenceSpec : rhsEntitySpec.getReferenceSpecs()) {
                            if (rhsToLhsReferenceSpec.getEntityName().equals(lhsEntitySpec
                                    .getName()) && rhsToLhsReferenceSpec.getType() ==
                                    ReferenceSpec.Type.MANY) {
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
        PrimitiveParameterResultProcessorFactory ppFactory = new PrimitiveParameterResultProcessorFactory();
        for (EntitySpec es : this.primitiveParameterEntitySpecs) {
            result.put(es, ppFactory);
        }
        EventResultProcessorFactory eFactory = new EventResultProcessorFactory();
        for (EntitySpec es : this.eventEntitySpecs) {
            result.put(es, eFactory);
        }
        ConstantResultProcessorFactory cFactory = new ConstantResultProcessorFactory();
        for (EntitySpec es : this.constantEntitySpecs) {
            result.put(es, cFactory);
        }

        // staging queries generate no results
        for (StagingSpec ss : this.stagedTableSpecs) {
            for (EntitySpec es : ss.getEntitySpecs()) {
                result.put(es, null);
            }
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
            List<EntitySpec> entitySpecsCopy, LinkedHashMap<String,
            ReferenceSpec> inboundRefSpecs, Set<String> keyIds,
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
                resultProcessor, this.stagedTableSpecs, wrapKeyId).generateStatement();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(
                    Level.FINE,
                    "Data source backend {0} generated the following query for {1}: {2}",
                    new Object[]{backendNameForMessages, entitySpecName,
                        query});
        }
        executor.executeSelect(entitySpecName, query, resultProcessor);
    }

    private <P extends Proposition> void generateAndExecuteSelect(
            EntitySpec entitySpec, ReferenceSpec referenceSpec,
            Set<String> propIds, Set<Filter> filtersCopy,
            List<EntitySpec> entitySpecsCopy, LinkedHashMap<String,
            ReferenceSpec> inboundRefSpecs,
            Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor,
            org.protempa.backend.dsb.relationaldb.SQLExecutor executor,
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
                resultProcessor, this.stagedTableSpecs, wrapKeyId).generateStatement();

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
            List<EntitySpec> entitySpecs, Map<String,
            ReferenceSpec> inboundRefSpecs, Set<Filter> filters,
            Set<String> propIds, Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor, StagingSpec[] stagedTables,
            boolean wrapKeyId);

    protected DataStager getDataStager(StagingSpec[] stagingSpecs,
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
     * @return
     * <code>true</code> if the query contains < 85% of the proposition ids that
     * are known to the data source and if the where clause would contain less
     * than or equal to 2000 codes.
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
     * be loaded explicitly. Returning not-
     * <code>null</code> will do no harm if a JDBC 4 driver.
     *
     * This implementation returns
     * <code>null</code>. Override it to return a driver's class name.
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
