/*
 * #%L
 * Protempa Framework
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
package org.protempa;

import java.util.HashSet;
import org.protempa.backend.BackendInitializationException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arp.javautil.arrays.Arrays;
import org.protempa.backend.BackendNewInstanceException;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.DataSourceBackendFailedValidationException;
import org.protempa.backend.InvalidConfigurationException;
import org.protempa.backend.dsb.DataSourceBackend;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.query.And;
import org.protempa.query.Query;
import org.protempa.query.QueryBuildException;
import org.protempa.query.QueryBuilder;
import org.protempa.query.handler.QueryResultsHandler;

/**
 * Main PROTEMPA API.
 *
 * @author Andrew Post
 */
public final class Protempa {

    private static final String STARTUP_FAILURE_MSG = "PROTEMPA could not start up";
    private final AbstractionFinder abstractionFinder;

    public static Protempa newInstance(String configurationId)
            throws ProtempaStartupException {
        try {
            return newInstance(new SourceFactory(configurationId));
        } catch (ConfigurationsLoadException ex) {
            throw new ProtempaStartupException(STARTUP_FAILURE_MSG, ex);
        } catch (BackendProviderSpecLoaderException ex) {
            throw new ProtempaStartupException(STARTUP_FAILURE_MSG, ex);
        } catch (InvalidConfigurationException ex) {
            throw new ProtempaStartupException(STARTUP_FAILURE_MSG, ex);
        }
    }

    public static Protempa newInstance(SourceFactory sourceFactory)
            throws ProtempaStartupException {
        try {
            return new Protempa(sourceFactory.newDataSourceInstance(),
                    sourceFactory.newKnowledgeSourceInstance(),
                    sourceFactory.newAlgorithmSourceInstance(),
                    sourceFactory.newTermSourceInstance(), false);
        } catch (BackendInitializationException ex) {
            throw new ProtempaStartupException(STARTUP_FAILURE_MSG, ex);
        } catch (BackendNewInstanceException ex) {
            throw new ProtempaStartupException(STARTUP_FAILURE_MSG, ex);
        }
    }

    public static Protempa newInstance(String configurationsId, boolean useCache)
            throws ProtempaStartupException {
        try {
            return newInstance(new SourceFactory(configurationsId), useCache);
        } catch (ConfigurationsLoadException ex) {
            throw new ProtempaStartupException(STARTUP_FAILURE_MSG, ex);
        } catch (BackendProviderSpecLoaderException ex) {
            throw new ProtempaStartupException(STARTUP_FAILURE_MSG, ex);
        } catch (InvalidConfigurationException ex) {
            throw new ProtempaStartupException(STARTUP_FAILURE_MSG, ex);
        }
    }

    public static Protempa newInstance(SourceFactory sourceFactory,
            boolean useCache) throws ProtempaStartupException {
        try {
            return new Protempa(sourceFactory.newDataSourceInstance(),
                    sourceFactory.newKnowledgeSourceInstance(),
                    sourceFactory.newAlgorithmSourceInstance(),
                    sourceFactory.newTermSourceInstance(), useCache);
        } catch (BackendInitializationException ex) {
            throw new ProtempaStartupException(STARTUP_FAILURE_MSG, ex);
        } catch (BackendNewInstanceException ex) {
            throw new ProtempaStartupException(STARTUP_FAILURE_MSG, ex);
        }
    }

    /**
     * Constructor that configures PROTEMPA not to cache found abstract
     * parameters.
     *
     * @param dataSource a {@link DataSource}. Will be closed when
     * {@link #close()} is called.
     * @param knowledgeSource a {@link KnowledgeSource}. Will be closed when
     * {@link #close()} is called.
     * @param algorithmSource an {@link AlgorithmSource}. Will be closed when
     * {@link #close()} is called.
     * @throws DataSourceFailedValidationException if the data source's data
     * element mappings are inconsistent with those defined in the knowledge
     * source.
     * @throws DataSourceValidationIncompleteException if an error occurred
     * during data source validation.
     */
    public Protempa(DataSource dataSource, KnowledgeSource knowledgeSource,
            AlgorithmSource algorithmSource, TermSource termSource)
            throws ProtempaStartupException {
        this(dataSource, knowledgeSource, algorithmSource, termSource, false);
    }

    /**
     * Constructor that lets the user specify whether or not to cache found
     * abstract parameters.
     *
     * @param dataSource a {@link DataSource}. Will be closed when
     * {@link #close()} is called.
     * @param knowledgeSource a {@link KnowledgeSource}. Will be closed when
     * {@link #close()} is called.
     * @param algorithmSource an {@link AlgorithmSource}. Will be closed when
     * {@link #close()} is called.
     * @param termSource a {@link TermSource}. Will be closed when
     * {@link #close()} is called.
     * @param cacheFoundAbstractParameters <code>true</code> to cache found
     * abstract parameters, <code>false</code> not to cache found abstract
     * parameters.
     * @throws DataSourceFailedValidationException if the data source's data
     * element mappings are inconsistent with those defined in the knowledge
     * source.
     * @throws DataSourceValidationIncompleteException if an error occurred
     * during data source validation.
     */
    public Protempa(DataSource dataSource, KnowledgeSource knowledgeSource,
            AlgorithmSource algorithmSource, TermSource termSource,
            boolean cacheFoundAbstractParameters)
            throws ProtempaStartupException {
        try {
            if (dataSource == null) {
                throw new IllegalArgumentException("dataSource cannot be null");
            }
            if (knowledgeSource == null) {
                throw new IllegalArgumentException(
                        "knowledgeSource cannot be null");
            }
            if (algorithmSource == null) {
                throw new IllegalArgumentException(
                        "algorithmSource cannot be null");
            }
            if (termSource == null) {
                throw new IllegalArgumentException(
                        "termSource cannot be null");
            }

            this.abstractionFinder = new AbstractionFinder(dataSource,
                    knowledgeSource, algorithmSource, termSource,
                    cacheFoundAbstractParameters);
        } catch (KnowledgeSourceReadException ex) {
            throw new ProtempaStartupException(STARTUP_FAILURE_MSG, ex);
        }
    }

    /**
     * Gets the data source.
     *
     * @return a {@link DataSource}. Will be closed when {@link #close()} is
     * called.
     */
    public DataSource getDataSource() {
        return this.abstractionFinder.getDataSource();
    }

    /**
     * Gets the knowledge source.
     *
     * @return a {@link KnowledgeSource}. Will be closed when {@link #close()}
     * is called.
     */
    public KnowledgeSource getKnowledgeSource() {
        return this.abstractionFinder.getKnowledgeSource();
    }

    /**
     * Gets the algorithm source.
     *
     * @return an {@link AlgorithmSource}. Will be closed when {@link #close()}
     * is called.
     */
    public AlgorithmSource getAlgorithmSource() {
        return this.abstractionFinder.getAlgorithmSource();
    }

    /**
     * Gets the term source.
     *
     * @return a {@link TermSource}. Will be closed when {@link #close()} is
     * called
     */
    public TermSource getTermSource() {
        return this.abstractionFinder.getTermSource();
    }

    /**
     * Convenience method for calling
     * {@link QueryBuilder#build(org.protempa.KnowledgeSource, org.protempa.AlgorithmSource) }
     * with this Protempa instance's knowledge source and algorithm source.
     *
     * @param queryBuilder a query specification.
     * @return the query.
     * @throws QueryBuildException if the query specification failed validation
     * or some other error occurred.
     */
    public Query buildQuery(QueryBuilder queryBuilder)
            throws QueryBuildException {
        return this.abstractionFinder.buildQuery(queryBuilder);
    }

    public void detachSession(QuerySession querySession) {
        // TODO: implement me.
    }

    public void reattachSession(QuerySession querySession) {
        // TODO: implement me
    }

    public void deleteSession(QuerySession querySession) {
        // TODO: implement me
    }

    /**
     * Create a QuerySession object, which can be used by the caller to further
     * filter or drill the results of the initial query that is passed in.
     *
     * @param query The query object to set up in the database for subsequent
     * queries
     * @return A QuerySession object, containing initial query state
     */
    public QuerySession prepare(Query query) {
        // TODO: implement me
        if (query == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
        return new QuerySession(query, this.abstractionFinder);
    }

    /**
     * Executes a query.
     *
     * Protempa determines which propositions to retrieve from the underlying
     * data sources and compute as the union of the proposition ids specified in
     * the supplied {@link Query} and the proposition ids returned from the
     * results handler's {@link QueryResultsHandler#getPropositionIdsNeeded() }
     * method.
     *
     * @param query a {@link Query}. Cannot be <code>null</code>.
     * @param resultsHandler a {@link QueryResultsHandler}. Cannot * *
     * be <code>null</code>.
     * @throws FinderException if an error occurred during query.
     */
    public void execute(Query query, QueryResultsHandler resultsHandler)
            throws FinderException {
        if (query == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
        if (query.getTermIds().length > 0) {
            throw new UnsupportedOperationException(
                    "term id support has not been implemented yet.");
        }
        if (resultsHandler == null) {
            throw new IllegalArgumentException("resultsHandler cannot be null");
        }
        Logger logger = ProtempaUtil.logger();
        logger.info("Executing query");
        Set<String> keyIdsSet = Arrays.asSet(query.getKeyIds());
        Set<String> propIds = Arrays.asSet(query.getPropositionIds());
        Set<And<String>> termIds = Arrays.asSet(query.getTermIds());
        Filter filters = query.getFilters();
        PropositionDefinition[] propDefs = query.getPropositionDefinitions();
        QuerySession qs = new QuerySession(query, this.abstractionFinder);
        this.abstractionFinder.doFind(keyIdsSet, propIds, termIds, filters,
                resultsHandler, propDefs, qs);
        logger.info("Query execution complete");
    }

    /**
     * Executes all three phases of the PROTEMPA lifecycle (data retrieval,
     * processing, and output) while also persisting intermediate results,
     * specifically the results of the proposition retrieval and processing.
     *
     * Protempa determines which propositions to retrieve from the underlying
     * data sources and compute as the union of the proposition ids specified in
     * the supplied {@link Query} and the proposition ids returned from the
     * results handler's {@link QueryResultsHandler#getPropositionIdsNeeded() }
     * method.
     *
     * @param query the query to execute
     * @param resultHandler a result handler specifying how the output should be
     * produced
     * @param retrievalStoreEnvironment the name of the persistent store that
     * will hold the propositions retrieved from the data source
     * @param processStoreName the name of the persistent store that will hold
     * the results of PROTEMPA processing the retrieved propositions
     * @throws FinderException if PROTEMPA fails to complete for any reason
     */
    public void executeWithPersistence(Query query,
            QueryResultsHandler resultHandler, String retrievalStoreEnvironment,
            String processStoreName) throws FinderException {
        if (query == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
        if (query.getTermIds().length > 0) {
            throw new UnsupportedOperationException(
                    "term id support has not been implemented yet.");
        }
        Logger logger = ProtempaUtil.logger();
        logger.log(
                Level.INFO,
                "Executing all PROTEMPA phases. Will store retrieved propositions in {0} and derived propositions in {1}",
                new String[]{retrievalStoreEnvironment, processStoreName});

        Set<String> keyIds = Arrays.asSet(query.getKeyIds());
        Set<String> propIds = Arrays.asSet(query.getPropositionIds());
        Set<And<String>> termIds = Arrays.asSet(query.getTermIds());
        Filter filters = query.getFilters();
        QuerySession qs = new QuerySession(query, this.abstractionFinder);

        logger.log(Level.INFO, "Beginning data retrieval stage");
        this.abstractionFinder.retrieveData(keyIds, propIds, termIds, filters,
                qs, retrievalStoreEnvironment);
        logger.log(Level.INFO, "Data retrieval complete");
        logger.log(Level.INFO, "Beginning processing stage");
        this.abstractionFinder.processStoredResults(keyIds, propIds, qs,
                retrievalStoreEnvironment, processStoreName);
        logger.log(Level.INFO, "Processing complete");
        logger.log(Level.INFO, "Beginning output stage");
        this.abstractionFinder.outputStoredResults(keyIds, propIds,
                resultHandler, qs, processStoreName);
        logger.log(Level.INFO, "Output complete");
    }

    /**
     * Executes the first stage in PROTEMPA's lifecycle. Executes a PROTEMPA
     * query in the data source and persistently stores the resulting
     * propositions.
     *
     * @param query the query to execute in the data source
     * @param retrievalStoreEnvironment the name of the persistent store for
     * holding the propositions. This name should be provided to subsequent
     * calls to {@link #processResultsAndPersist(Query, String, String)} as the
     * name of the proposition data store.
     * @throws FinderException if the retrieval could not complete
     */
    public void retrieveDataAndPersist(Query query,
            String retrievalStoreEnvironment) throws FinderException {
        if (query == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
        if (query.getTermIds().length > 0) {
            throw new UnsupportedOperationException(
                    "term id support has not been implemented yet.");
        }
        Logger logger = ProtempaUtil.logger();
        logger.log(Level.FINE, "Retrieving and persisting data");
        logger.log(Level.INFO,
                "Retrieved data will be persisted in store: {0}",
                retrievalStoreEnvironment);
        Set<String> keyIdsSet = Arrays.asSet(query.getKeyIds());
        Set<String> propIds = Arrays.asSet(query.getPropositionIds());
        Set<And<String>> termIds = Arrays.asSet(query.getTermIds());
        Filter filters = query.getFilters();
        QuerySession qs = new QuerySession(query, this.abstractionFinder);
        this.abstractionFinder.retrieveData(keyIdsSet, propIds, termIds,
                filters, qs, retrievalStoreEnvironment);
        logger.log(Level.FINE, "Data retrieval complete");
    }

    /**
     * Executes the second stage in PROTEMPA's lifecycle. Processes the results
     * of a data retrieval with respect the given query. The results of the
     * processing are persisted for later use.
     *
     * @param query the original query used for data retrieval
     * @param keyIds if not empty or null, the processing will be restricted to
     * the key IDs in this set
     * @param propositionIds the proposition IDs with respect to which
     * processing should be done
     * @param retrievalStoreEnvironment the name of the persistent store
     * containing the propositions to process; should be the same name as the
     * one provided to {@link #retrieveDataAndPersist(Query, String)}.
     * @param workingMemoryStoreEnvironment the name of the persistent store to
     * use the processed data
     * @throws FinderException if processing fails to complete
     */
    public void processResultsAndPersist(Query query, Set<String> keyIds,
            Set<String> propositionIds, String retrievalStoreEnvironment,
            String workingMemoryStoreEnvironment) throws FinderException {
        if (query == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
        if (query.getTermIds().length > 0) {
            throw new UnsupportedOperationException(
                    "term id support has not been implemented yet.");
        }
        Logger logger = ProtempaUtil.logger();
        logger.log(Level.FINE, "Processing and persisting results");
        logger.log(Level.INFO,
                "Processed results will be persisted in store: {0}",
                workingMemoryStoreEnvironment);
        logger.log(Level.INFO,
                "Pulling previously retrieved data from store: {0}",
                retrievalStoreEnvironment);
        QuerySession qs = new QuerySession(query, this.abstractionFinder);

        this.abstractionFinder.processStoredResults(keyIds, propositionIds, qs,
                retrievalStoreEnvironment, workingMemoryStoreEnvironment);
        logger.log(Level.FINE, "Data processing complete");
    }

    /**
     * Identical to
     * {@link #processResultsAndPersist(Query, Set, Set, String, String)} except
     * uses the key IDs and propositions defined in the query for processing.
     *
     * @param query the original query used to retrieve the data
     * @param propositionStoreName name of the persistent store holding the
     * propositions
     * @param workingMemoryStoreEnvironment name of the persistent store
     * intended to hold the processed data
     * @throws FinderException if processing fails to complete
     */
    public void processResultsAndPersist(Query query,
            String propositionStoreName, String workingMemoryStoreEnvironment)
            throws FinderException {
        processResultsAndPersist(query, Arrays.asSet(query.getKeyIds()),
                Arrays.asSet(query.getPropositionIds()), propositionStoreName,
                workingMemoryStoreEnvironment);
    }

    /**
     * The third and final stage in PROTEMPA's lifecylce. Identical to
     * {@link #outputResults(Query, Set, Set, QueryResultsHandler, String)}
     * except the proposition IDs are simply taken from the query. Outputs
     * previously processed results, with respect to the given query. The query
     * does not have to be the same as those in either of the previous two
     * stages.
     *
     * @param query the original query used to retrieve the data
     * @param resultHandler a result handler that specifies how the output
     * should be produced
     * @param workingMemoryEnvironment the name of the persistent store
     * containing the processed propositions; should be the same as the one
     * provided to {@link #processResultsAndPersist}.
     * @throws FinderException if the output fails to complete
     */
    public void outputResults(Query query, QueryResultsHandler resultHandler,
            String workingMemoryEnvironment) throws FinderException {
        outputResults(query, Arrays.asSet(query.getKeyIds()),
                Arrays.asSet(query.getPropositionIds()), resultHandler,
                workingMemoryEnvironment);
    }

    /**
     * The third and final stage in PROTEMPA's lifecylce. Outputs previously
     * processed results, with respect to the given proposition IDs. The query
     * does not have to be the same as those in either of the previous two
     * stages.
     *
     * Protempa determines which propositions to output as the union of the
     * proposition ids specified in the supplied {@link Query} and the
     * proposition ids returned from the results handler's 
     * {@link QueryResultsHandler#getPropositionIdsNeeded() }
     * method.
     *
     * @param query the original query used to retrieve the data
     * @param keyIds if not empty or null, the output will be restricted to the
     * key IDs in this set
     * @param propositionIds the propositions in the processed data to output
     * @param resultHandler a result handler that specifies how the output
     * should be produced
     * @param workingMemoryStoreEnvironment the name of the persistent store
     * containing the processed propositions; should be the same as the one
     * provided to
     * {@link #processResultsAndPersist(Query, Set, Set, String, String)}
     * @throws FinderException if the output fails to complete
     */
    public void outputResults(Query query, Set<String> keyIds,
            Set<String> propositionIds, QueryResultsHandler resultHandler,
            String workingMemoryStoreEnvironment) throws FinderException {
        if (query == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
        if (query.getTermIds().length > 0) {
            throw new UnsupportedOperationException(
                    "term id support has not been implemented yet.");
        }
        Logger logger = ProtempaUtil.logger();
        logger.log(Level.FINE, "Outputting results");
        logger.log(Level.INFO,
                "Retrieving processed results from store named: {0}",
                workingMemoryStoreEnvironment);
        QuerySession qs = new QuerySession(query, this.abstractionFinder);
        this.abstractionFinder.outputStoredResults(keyIds, propositionIds,
                resultHandler, qs, workingMemoryStoreEnvironment);
        logger.log(Level.FINE, "Output complete");
    }

    /**
     * Performs the second and third stages (process and output) of PROTEMPA's
     * life cycle without storing the processed results.
     *
     * @param origQuery the query originally used to query the data
     * @param newQuery UNUSED
     * @param resultHandler a result handler that specifies how the output
     * should be produced
     * @param propositionStoreEnvironment the name of the persistent store
     * containing the retrieved propositions; should be the same as the one
     * provided to {@link #retrieveDataAndPersist(Query, String)}
     * @throws FinderException if the processing and output fail to complete
     */
    public void processResultsAndOutput(Query origQuery, Query newQuery,
            QueryResultsHandler resultHandler,
            String propositionStoreEnvironment) throws FinderException {
        QuerySession qs = new QuerySession(origQuery, this.abstractionFinder);
        this.abstractionFinder.processAndOutputStoredResults(
                Arrays.asSet(origQuery.getKeyIds()),
                Arrays.asSet(origQuery.getPropositionIds()), resultHandler, qs,
                propositionStoreEnvironment);
    }

    /**
     * Runs each data source backend's validation routine.
     *
     * @throws DataSourceFailedValidationException if validation failed.
     * @throws DataSourceValidationIncompleteException if an error occurred
     * during validation that prevented its completion.
     */
    public void validateDataSource()
            throws DataSourceFailedValidationException,
            DataSourceValidationIncompleteException {
        KnowledgeSource knowledgeSource = getKnowledgeSource();
        try {
            for (DataSourceBackend backend : getDataSource().getBackends()) {
                backend.validate(knowledgeSource);
            }
        } catch (DataSourceBackendFailedValidationException ex) {
            throw new DataSourceFailedValidationException(
                    "Data source failed validation", ex);
        } catch (KnowledgeSourceReadException ex) {
            throw new DataSourceValidationIncompleteException(
                    "An error occurred during validation", ex);
        }
    }

    /**
     * Closes resources created by this object and the data source, knowledge
     * source, and algorithm source.
     */
    public void close() {
        this.abstractionFinder.close();
        this.abstractionFinder.getAlgorithmSource().close();
        this.abstractionFinder.getDataSource().close();
        this.abstractionFinder.getKnowledgeSource().close();
        this.abstractionFinder.getTermSource().close();
        ProtempaUtil.logger().info("Protempa closed");
    }

    /**
     * Clears resources created by this object and the data source, knowledge
     * source and algorithm source.
     */
    public void clear() {
        this.abstractionFinder.clear();
        this.abstractionFinder.getAlgorithmSource().clear();
        this.abstractionFinder.getDataSource().clear();
        this.abstractionFinder.getKnowledgeSource().clear();
        this.abstractionFinder.getTermSource().clear();
        ProtempaUtil.logger().fine("Protempa cleared");
    }
}
