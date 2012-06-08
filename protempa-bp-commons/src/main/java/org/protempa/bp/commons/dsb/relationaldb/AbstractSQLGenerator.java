/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 Emory University
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
package org.protempa.bp.commons.dsb.relationaldb;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.arp.javautil.arrays.Arrays;
import org.arp.javautil.collections.Collections;
import org.arp.javautil.io.Retryer;
import org.arp.javautil.sql.ConnectionSpec;
import org.arp.javautil.sql.SQLExecutor;
import org.protempa.DataSourceReadException;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.backend.dsb.filter.PositionFilter;
import org.protempa.bp.commons.dsb.RelationalDbDataSourceBackend;
import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.Constraint;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.Unit;
import org.protempa.proposition.value.UnitFactory;

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
        this.primitiveParameterSpecs = new HashMap<String, List<EntitySpec>>();
        this.eventSpecs = new HashMap<String, List<EntitySpec>>();
        this.constantSpecs = new HashMap<String, List<EntitySpec>>();
    }

    @Override
    public void initialize(RelationalDatabaseSpec relationalDatabaseSpec,
            ConnectionSpec connectionSpec, RelationalDbDataSourceBackend backend) {
        if (relationalDatabaseSpec != null) {
            this.primitiveParameterEntitySpecs = relationalDatabaseSpec
                    .getPrimitiveParameterSpecs();
            populatePropositionMap(this.primitiveParameterSpecs,
                    this.primitiveParameterEntitySpecs);
            this.eventEntitySpecs = relationalDatabaseSpec.getEventSpecs();
            populatePropositionMap(this.eventSpecs, this.eventEntitySpecs);
            this.constantEntitySpecs = relationalDatabaseSpec
                    .getConstantSpecs();
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

    @Override
    public GranularityFactory getGranularities() {
        return this.granularities;
    }

    @Override
    public UnitFactory getUnits() {
        return this.units;
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
            Unit finishUnit = finishGran != null ? finishGran
                    .getCorrespondingUnit() : null;
            boolean doLoop = true;
            while (doLoop) {
                Set<Filter> newFiltersCopy = new HashSet<Filter>(filtersCopy);
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
        List<EntitySpec> allEntitySpecsCopyForRefs = new LinkedList<EntitySpec>();
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
                        new Object[] { backendNameForMessages, entitySpecName });
            }
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                        "Data source backend {0} is executing query for {1}",
                        new Object[] { backendNameForMessages, entitySpecName });
            }
            logQueryTimeout(logger, backendNameForMessages);

            RetryableSQLExecutor operation = new RetryableSQLExecutor(
                    this.connectionSpec, query, resultProcessor,
                    this.backend.getQueryTimeout());
            Retryer<SQLException> retryer = new Retryer<SQLException>(3);
            if (!retryer.execute(operation)) {
                SQLException ex = SQLExecutor.assembleSQLException(retryer
                        .getErrors());
                throw new DataSourceReadException("Error retrieving "
                        + entitySpecName + " from data source backend "
                        + backendNameForMessages, ex);
            }

            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE,
                        "Query for {0} in data source backend {1} is complete",
                        new Object[] { entitySpecName, backendNameForMessages });
            }
        }
    }

    private void logQueryTimeout(Logger logger, 
            String backendNameForMessages) {
        Level level = Level.FINER;
        if (logger.isLoggable(level)) {
            Integer queryTimeout = this.backend.getQueryTimeout();
            if (queryTimeout != null) {
                logger.log(level,
                    "Data source backend {0} has query timeout set to {1,number,integer} seconds",
                    new Object[] {backendNameForMessages, queryTimeout});
            } else {
                logger.log(level,
                    "Query timeout is not set for data source backend {0}",
                    new Object[] {backendNameForMessages});
            }
        }
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

    private void removeStagedFilters(Set<Filter> filtersCopy) {
        for (Iterator<Filter> itr = filtersCopy.iterator(); itr.hasNext();) {
            Filter f = itr.next();
            String[] propIds = f.getPropositionIds();
            FILTER_LOOP: for (String propId : propIds) {
                List<EntitySpec> entitySpecs = new ArrayList<EntitySpec>();
                if (this.constantSpecs.containsKey(propId)) {
                    entitySpecs.addAll(this.constantSpecs.get(propId));
                }
                if (this.eventSpecs.containsKey(propId)) {
                    entitySpecs.addAll(this.eventSpecs.get(propId));
                }
                if (this.primitiveParameterSpecs.containsKey(propId)) {
                    entitySpecs
                            .addAll(this.primitiveParameterSpecs.get(propId));
                }

                for (StagingSpec staged : this.stagedTableSpecs) {
                    for (EntitySpec es : entitySpecs) {
                        for (EntitySpec ses : staged.getEntitySpecs()) {
                            if (SQLGenUtil.somePropIdsMatch(es, ses)) {
                                itr.remove();
                                break FILTER_LOOP;
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * allEntitySpecs() returns a map of non-parameterized
     * SQLGenResultProcessorFactory objects, therefore the resulting map here
     * cannot be parameterized either.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public ResultCache<Proposition> readPropositions(Set<String> keyIds,
            Set<String> propIds, Filter filters, SQLOrderBy order)
            throws DataSourceReadException {
        Map<EntitySpec, List<String>> entitySpecMapFromPropIds = entitySpecMapForPropIds(propIds);

        ResultCache<Proposition> results = new ResultCache<Proposition>();
        Map<EntitySpec, SQLGenResultProcessorFactory> entitySpecToResultProcessorMap = allEntitySpecs();
        Collection<EntitySpec> allEntitySpecs = entitySpecToResultProcessorMap
                .keySet();
        Logger logger = SQLGenUtil.logger();

        DataStager stager = null;
        if (stagingApplies()) {
            try {
                stager = getDataStager(this.stagedTableSpecs, null,
                        new LinkedList<EntitySpec>(allEntitySpecs),
                        copyFilters(filters), propIds, keyIds, order,
                        this.connectionSpec);
                stager.stageTables();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Failed to create staging area", ex);
                throw new DataSourceReadException(ex);
            }
        }

        for (EntitySpec entitySpec : entitySpecMapFromPropIds.keySet()) {
            logProcessingEntitySpec(logger, entitySpec);

            // we know that the map contains only factory instances that are
            // parameterized by implementations of Proposition
            @SuppressWarnings("unchecked")
            SQLGenResultProcessorFactory<Proposition> factory = entitySpecToResultProcessorMap
                    .get(entitySpec);
            assert factory != null : "factory should never be null";
            List<EntitySpec> allEntitySpecsCopy = new LinkedList<EntitySpec>(
                    allEntitySpecs);
            removeNonApplicableEntitySpecs(entitySpec, allEntitySpecsCopy);

            logApplicableEntitySpecs(allEntitySpecsCopy, logger);

            Set<Filter> filtersCopy = copyFilters(filters);
            removeNonApplicableFilters(allEntitySpecs, filtersCopy, entitySpec);
            removeStagedFilters(filtersCopy);
            assert !allEntitySpecsCopy.isEmpty() : "allEntitySpecsCopy should have at least one element";
            String dataSourceBackendId = this.backend.getDataSourceBackendId();
            MainResultProcessor<Proposition> resultProcessor = factory
                    .getInstance(dataSourceBackendId, entitySpec, results);

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
                     * Create a copy of allEntitySpecs with the current
                     * entitySpec the first item of the list. This is to make
                     * sure that its joins make it into the list of column
                     * specs.
                     */
                    for (ReferenceSpec referenceSpec : refSpecs) {
                        RefResultProcessor<Proposition> refResultProcessor = factory
                                .getRefInstance(dataSourceBackendId,
                                        entitySpec, referenceSpec, results);
                        List<EntitySpec> allEntitySpecsCopyForRefs = copyEntitySpecsForRefs(
                                entitySpec, allEntitySpecs);
                        Set<Filter> refFiltersCopy = copyFilters(filters);
                        EntitySpec referredToEntitySpec = entitySpecForName(
                                allEntitySpecsCopyForRefs,
                                referenceSpec.getEntityName());
                        assert referredToEntitySpec != null : "refferedToEntitySpec should not be null";
                        if (Collections.containsAny(propIds,
                                referredToEntitySpec.getPropositionIds())) {
                            logProcessingRef(logger, referenceSpec, entitySpec);
                            retainEntitySpecsWithInboundRefs(
                                    allEntitySpecsCopyForRefs, entitySpec,
                                    referenceSpec);
                            removeNonApplicableFilters(
                                    allEntitySpecsCopyForRefs, refFiltersCopy,
                                    referredToEntitySpec);
                            removeStagedFilters(refFiltersCopy);
                            retainEntitySpecsWithFiltersOrConstraints(
                                    entitySpec, referredToEntitySpec,
                                    allEntitySpecsCopyForRefs, refFiltersCopy,
                                    propIds);
                            List<Set<Filter>> refFilterList = constructFilterSets(
                                    referredToEntitySpec, refFiltersCopy);
                            for (Set<Filter> filterSet : refFilterList) {
                                generateAndExecuteSelect(entitySpec,
                                        referenceSpec, propIds, filterSet,
                                        allEntitySpecsCopyForRefs, keyIds,
                                        order, refResultProcessor);
                            }
                            logDoneProcessingRef(logger, referenceSpec,
                                    entitySpec);
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

        if (stagingApplies()) {
            logger.log(Level.INFO, "Cleaning up staged data");
            try {
                stager.cleanup();
            } catch (SQLException ex) {
                logger.log(Level.WARNING,
                        "Failed to clean up the staging area", ex);
            }
        }

        return results;
    }

    private boolean stagingApplies() {
        return this.stagedTableSpecs != null
                && this.stagedTableSpecs.length > 0;
    }

    private static void retainEntitySpecsWithFiltersOrConstraints(
            EntitySpec entitySpec, EntitySpec referredToEntitySpec,
            Collection<EntitySpec> allEntitySpecsCopyForRefs,
            Collection<Filter> refFiltersCopy, Set<String> propIds) {
        for (Iterator<EntitySpec> itr = allEntitySpecsCopyForRefs.iterator(); itr
                .hasNext();) {
            EntitySpec es = itr.next();
            if (es != entitySpec && es != referredToEntitySpec) {
                Set<String> esPropIds = Arrays.asSet(es.getPropositionIds());
                ColumnSpec codeSpec = es.getCodeSpec();
                if (codeSpec != null) {
                    List<ColumnSpec> codeSpecL = codeSpec.asList();
                    ColumnSpec last = codeSpecL.get(codeSpecL.size() - 1);
                    if (last.getConstraint() != null
                            && (last.getConstraint() != Constraint.EQUAL_TO
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
                    new Object[] { entitySpec.getName(),
                            backendNameForMessages() });
        }
    }

    private void logDoneProcessingEntitySpec(Logger logger,
            EntitySpec entitySpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(
                    Level.FINE,
                    "Data source backend {0} is done processing entity spec {1}",
                    new Object[] { backendNameForMessages(),
                            entitySpec.getName() });
        }
    }

    private void logDoneProcessingRef(Logger logger,
            ReferenceSpec referenceSpec, EntitySpec entitySpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(
                    Level.FINE,
                    "Data source backend {0} is done processing reference {1} for entity spec {2}",
                    new Object[] { backendNameForMessages(),
                            referenceSpec.getReferenceName(),
                            entitySpec.getName() });
        }
    }

    private void logProcessingRef(Logger logger, ReferenceSpec referenceSpec,
            EntitySpec entitySpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(
                    Level.FINE,
                    "Data source backend {0} is processing reference {1} for entity spec {2}",
                    new Object[] { backendNameForMessages(),
                            referenceSpec.getReferenceName(),
                            entitySpec.getName() });
        }
    }

    private void logProcessingEntitySpec(Logger logger, EntitySpec entitySpec) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(
                    Level.FINE,
                    "Data source backend {0} is processing entity spec {1}",
                    new Object[] { backendNameForMessages(),
                            entitySpec.getName() });
        }
    }

    private void logApplicableEntitySpecs(List<EntitySpec> allEntitySpecsCopy,
            Logger logger) {
        if (logger.isLoggable(Level.FINER)) {
            String[] allEntitySpecsCopyNames = new String[allEntitySpecsCopy
                    .size()];
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

    /*
     * We need to store multiple types of result processor factories in the same
     * map
     */
    @SuppressWarnings("rawtypes")
    private Map<EntitySpec, SQLGenResultProcessorFactory> allEntitySpecs() {
        /*
         * The order of the entity specs matters for multiple with the same
         * name. Thus, we use a LinkedHashMap.
         */
        Map<EntitySpec, SQLGenResultProcessorFactory> result = new LinkedHashMap<EntitySpec, SQLGenResultProcessorFactory>();
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
        Set<Filter> filtersCopy = new HashSet<Filter>();
        if (filters != null) {
            for (Iterator<Filter> itr = filters.andIterator(); itr.hasNext();) {
                filtersCopy.add(itr.next());
            }
        }
        return filtersCopy;
    }

    private <P extends Proposition> void generateAndExecuteSelect(
            EntitySpec entitySpec, ReferenceSpec referenceSpec,
            Set<String> propIds, Set<Filter> filtersCopy,
            List<EntitySpec> entitySpecsCopy, Set<String> keyIds,
            SQLOrderBy order, SQLGenResultProcessor resultProcessor)
            throws DataSourceReadException {
        Logger logger = SQLGenUtil.logger();
        String backendNameForMessages = backendNameForMessages();
        String entitySpecName = entitySpec.getName();

        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE,
                    "Data source backend {0} is generating query for {1}",
                    new Object[] { backendNameForMessages, entitySpecName });
        }

        String query = generateSelect(entitySpec, referenceSpec, propIds,
                filtersCopy, entitySpecsCopy, keyIds, order, resultProcessor);

        if (logger.isLoggable(Level.FINE)) {
            logger.log(
                    Level.FINE,
                    "Data source backend {0} generated the following query for {1}: {2}",
                    new Object[] { backendNameForMessages, entitySpecName,
                            query });
        }

        executeSelect(logger, backendNameForMessages, entitySpecName, query,
                resultProcessor);
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
            List<EntitySpec> entitySpecs, Set<Filter> filters,
            Set<String> propIds, Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor, StagingSpec[] stagedTables);

    protected DataStager getDataStager(StagingSpec[] stagingSpecs,
            ReferenceSpec referenceSpec, List<EntitySpec> entitySpecs,
            Set<Filter> filters, Set<String> propIds, Set<String> keyIds,
            SQLOrderBy order, ConnectionSpec connectionSpec) {
        throw new UnsupportedOperationException("SQL generator "
                + getClass().getName() + " does not support data staging");
    }

    private String generateSelect(EntitySpec entitySpec,
            ReferenceSpec referenceSpec, Set<String> propIds,
            Set<Filter> filtersCopy, List<EntitySpec> entitySpecsCopy,
            Set<String> keyIds, SQLOrderBy order,
            SQLGenResultProcessor resultProcessor) {

        return getSelectStatement(entitySpec, referenceSpec, entitySpecsCopy,
                filtersCopy, propIds, keyIds, order, resultProcessor,
                this.stagedTableSpecs).generateStatement();
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
                    new Object[] { ex.getClass().getName(), className });
            return false;
        }
    }

    /**
     * Gets a the class name of the driver to load for this SQL generator, or
     * <code>null</code> if the driver is a JDBC 4 driver and does not need to
     * be loaded explicitly. Returning not-<code>null</code> will do no harm if
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

    private Map<EntitySpec, List<String>> entitySpecMapForPropIds(
            Set<String> propIds) throws AssertionError {
        Map<EntitySpec, List<String>> result = new HashMap<EntitySpec, List<String>>();
        for (String propId : propIds) {
            boolean inDataSource = populateEntitySpecToPropIdMap(
                    new String[] { propId }, result);
            Logger logger = SQLGenUtil.logger();
            if (!inDataSource && logger.isLoggable(Level.FINER)) {
                logger.log(
                        Level.FINER,
                        "Data source backend {0} does not know about proposition {1}",
                        new Object[] { backendNameForMessages(), propId });
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
