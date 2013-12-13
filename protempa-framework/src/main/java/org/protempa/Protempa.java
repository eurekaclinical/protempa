/*
 * #%L
 * Protempa Framework
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
package org.protempa;

import java.util.ArrayList;
import java.util.List;
import org.protempa.backend.BackendInitializationException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.CollectionUtils;

import org.arp.javautil.arrays.Arrays;
import org.protempa.backend.BackendNewInstanceException;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.ConfigurationsNotFoundException;
import org.protempa.backend.DataSourceBackendFailedConfigurationValidationException;
import org.protempa.backend.DataSourceBackendFailedDataValidationException;
import org.protempa.backend.InvalidConfigurationException;
import org.protempa.backend.asb.AlgorithmSourceBackend;
import org.protempa.backend.dsb.DataSourceBackend;
import org.protempa.backend.dsb.DataValidationEvent;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.backend.tsb.TermSourceBackend;
import org.protempa.query.Query;
import org.protempa.query.QueryBuildException;
import org.protempa.query.QueryBuilder;
import org.protempa.query.handler.QueryResultsHandler;
import org.protempa.query.handler.QueryResultsHandlerFactory;

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
        } catch (ConfigurationsNotFoundException | InvalidConfigurationException | BackendProviderSpecLoaderException | ConfigurationsLoadException ex) {
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
        } catch (BackendInitializationException | BackendNewInstanceException ex) {
            throw new ProtempaStartupException(STARTUP_FAILURE_MSG, ex);
        }
    }

    public static Protempa newInstance(String configurationsId, boolean useCache)
            throws ProtempaStartupException {
        try {
            return newInstance(new SourceFactory(configurationsId), useCache);
        } catch (ConfigurationsNotFoundException | InvalidConfigurationException | BackendProviderSpecLoaderException | ConfigurationsLoadException ex) {
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
        } catch (BackendInitializationException | BackendNewInstanceException ex) {
            throw new ProtempaStartupException(STARTUP_FAILURE_MSG, ex);
        }
    }

    /**
     * Constructor that configures PROTEMPA not to cache found abstract
     * parameters.
     *
     * @param dataSource a {@link DataSource}. Will be closed when
     * {@link #close()} is called. May be <code>null</code> if you're not
     * retrieving data from a data source (for example, you're only working with
     * a persistent store).
     * @param knowledgeSource a {@link KnowledgeSource}. Will be closed when
     * {@link #close()} is called.
     * @param algorithmSource an {@link AlgorithmSource}. Will be closed when
     * {@link #close()} is called. May be <code>null</code> if you're not
     * computing any low-level abstractions.
     * @param termSource a {@link TermSource}. Will be closed when
     * {@link #close()} is called. May be <code>null</code> if you're not using
     * terms.
     *
     * @throws ProtempaException if an error occur in starting Protempa. There
     * frequently will be a nested exception that provides more detail.
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
     * {@link #close()} is called. May be <code>null</code> if you're not
     * retrieving data from a data source (for example, you're only working with
     * a persistent store).
     * @param knowledgeSource a {@link KnowledgeSource}. Will be closed when
     * {@link #close()} is called.
     * @param algorithmSource an {@link AlgorithmSource}. Will be closed when
     * {@link #close()} is called. May be <code>null</code> if you're not
     * computing any low-level abstractions.
     * @param termSource a {@link TermSource}. Will be closed when
     * {@link #close()} is called. May be <code>null</code> if you're not using
     * terms.
     * @param cacheFoundAbstractParameters <code>true</code> to cache found
     * abstract parameters, <code>false</code> not to cache found abstract
     * parameters.
     *
     * @throws ProtempaException if an error occur in starting Protempa. There
     * frequently will be a nested exception that provides more detail.
     */
    public Protempa(DataSource dataSource, KnowledgeSource knowledgeSource,
            AlgorithmSource algorithmSource, TermSource termSource,
            boolean cacheFoundAbstractParameters)
            throws ProtempaStartupException {
        DataSource ds;
        if (dataSource == null) {
            ds = new DataSourceImpl(new DataSourceBackend[0]);
        } else {
            ds = dataSource;
        }

        KnowledgeSource ks;
        if (knowledgeSource == null) {
            ks = new KnowledgeSourceImpl(new KnowledgeSourceBackend[0]);
        } else {
            ks = knowledgeSource;
        }

        AlgorithmSource as;
        if (algorithmSource == null) {
            as = new AlgorithmSourceImpl(new AlgorithmSourceBackend[0]);
        } else {
            as = algorithmSource;
        }

        TermSource ts;
        if (termSource == null) {
            ts = new TermSourceImpl(new TermSourceBackend[0]);
        } else {
            ts = termSource;
        }

        try {
            this.abstractionFinder = new AbstractionFinder(ds, ks, as, ts,
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
     * @param resultsHandler a {@link QueryResultsHandler}. Cannot * * * * * *
     * be <code>null</code>.
     * @throws FinderException if an error occurred during query.
     */
    public void execute(Query query, QueryResultsHandlerFactory resultsHandler)
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
        try {
            Logger logger = ProtempaUtil.logger();
            logger.log(Level.INFO, "Executing query {0}", query.getId());
            QuerySession qs = new QuerySession(query, this.abstractionFinder);
            this.abstractionFinder.doFind(query, resultsHandler, qs);
            logger.log(Level.INFO, "Query {0} execution complete", query.getId());
        } catch (FinderException e) {
            this.abstractionFinder.getDataSource().failureOccurred(e);
            throw e;
        }
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
            QueryResultsHandlerFactory resultHandler,
            String retrievalStoreEnvironment,
            String processStoreName) throws FinderException {
        if (query == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
        if (query.getTermIds().length > 0) {
            throw new UnsupportedOperationException(
                    "term id support has not been implemented yet.");
        }
        try {
            Logger logger = ProtempaUtil.logger();
            logger.log(
                    Level.INFO,
                    "Executing all PROTEMPA phases. Will store retrieved propositions in {0} and derived propositions in {1}",
                    new String[]{retrievalStoreEnvironment, processStoreName});

            QuerySession qs = new QuerySession(query, this.abstractionFinder);

            logger.log(Level.INFO, "Beginning data retrieval stage");
            this.abstractionFinder.retrieveAndStoreData(query, qs,
                    retrievalStoreEnvironment);
            logger.log(Level.INFO, "Data retrieval complete");
            logger.log(Level.INFO, "Beginning processing stage");
            this.abstractionFinder.processStoredResults(query, qs,
                    retrievalStoreEnvironment, processStoreName);
            logger.log(Level.INFO, "Processing complete");
            logger.log(Level.INFO, "Beginning output stage");
            this.abstractionFinder.outputStoredResults(query,
                    resultHandler, qs, processStoreName);
            logger.log(Level.INFO, "Output complete");
        } catch (FinderException e) {
            this.abstractionFinder.getDataSource().failureOccurred(e);
            throw e;
        }
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
        try {
            Logger logger = ProtempaUtil.logger();
            logger.log(Level.FINE, "Retrieving and persisting data");
            logger.log(Level.INFO,
                    "Retrieved data will be persisted in store: {0}",
                    retrievalStoreEnvironment);

            QuerySession qs = new QuerySession(query, this.abstractionFinder);
            this.abstractionFinder.retrieveAndStoreData(query, qs,
                    retrievalStoreEnvironment);
            logger.log(Level.FINE, "Data retrieval complete");
        } catch (FinderException e) {
            this.abstractionFinder.getDataSource().failureOccurred(e);
            throw e;
        }
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
    public void processResultsAndPersist(Query query,
            String retrievalStoreEnvironment,
            String workingMemoryStoreEnvironment) throws FinderException {
        if (query == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
        if (query.getTermIds().length > 0) {
            throw new UnsupportedOperationException(
                    "term id support has not been implemented yet.");
        }
        try {
            Logger logger = ProtempaUtil.logger();
            logger.log(Level.FINE, "Processing and persisting results");
            logger.log(Level.INFO,
                    "Processed results will be persisted in store: {0}",
                    workingMemoryStoreEnvironment);
            logger.log(Level.INFO,
                    "Pulling previously retrieved data from store: {0}",
                    retrievalStoreEnvironment);
            QuerySession qs = new QuerySession(query, this.abstractionFinder);

            this.abstractionFinder.processStoredResults(query, qs,
                    retrievalStoreEnvironment, workingMemoryStoreEnvironment);
            logger.log(Level.FINE, "Data processing complete");
        } catch (FinderException e) {
            this.abstractionFinder.getDataSource().failureOccurred(e);
            throw e;
        }
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
    public void outputResults(Query query, QueryResultsHandlerFactory resultHandler,
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
            Set<String> propositionIds, QueryResultsHandlerFactory resultHandler,
            String workingMemoryStoreEnvironment) throws FinderException {
        if (query == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
        if (query.getTermIds().length > 0) {
            throw new UnsupportedOperationException(
                    "term id support has not been implemented yet.");
        }
        try {
            Logger logger = ProtempaUtil.logger();
            logger.log(Level.FINE, "Outputting results");
            logger.log(Level.INFO,
                    "Retrieving processed results from store named: {0}",
                    workingMemoryStoreEnvironment);
            QuerySession qs = new QuerySession(query, this.abstractionFinder);
            this.abstractionFinder.outputStoredResults(query, resultHandler, qs,
                    workingMemoryStoreEnvironment);
            logger.log(Level.FINE, "Output complete");
        } catch (FinderException e) {
            this.abstractionFinder.getDataSource().failureOccurred(e);
            throw e;
        }
    }

    /**
     * Performs the second and third stages (process and output) of PROTEMPA's
     * life cycle without storing the processed results.
     *
     * @param newQuery the query to execute.
     * @param resultHandler a result handler that specifies how the output
     * should be produced
     * @param propositionStoreEnvironment the name of the persistent store
     * containing the retrieved propositions; should be the same as the one
     * provided to {@link #retrieveDataAndPersist(Query, String)}
     * @throws FinderException if the processing and output fail to complete
     */
    public void processResultsAndOutput(Query query,
            QueryResultsHandlerFactory resultHandler,
            String propositionStoreEnvironment) throws FinderException {
        if (query == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
        if (query.getTermIds().length > 0) {
            throw new UnsupportedOperationException(
                    "term id support has not been implemented yet.");
        }
        try {
            QuerySession qs = new QuerySession(query, this.abstractionFinder);
            this.abstractionFinder.processAndOutputStoredResults(query,
                    resultHandler, qs, propositionStoreEnvironment);
        } catch (FinderException e) {
            this.abstractionFinder.getDataSource().failureOccurred(e);
            throw e;
        }
    }

    public void validateDataSourceBackendConfigurations()
            throws DataSourceValidationIncompleteException,
            DataSourceFailedConfigurationValidationException {
        KnowledgeSource knowledgeSource = getKnowledgeSource();
        try {
            for (DataSourceBackend backend : getDataSource().getBackends()) {
                backend.validateConfiguration(knowledgeSource);
            }
        } catch (DataSourceBackendFailedConfigurationValidationException ex) {
            throw new DataSourceFailedConfigurationValidationException(
                    "Data source configuration failed validation", ex);
        } catch (KnowledgeSourceReadException ex) {
            throw new DataSourceValidationIncompleteException(
                    "An error occurred during validation", ex);
        }
    }

    /**
     * Runs each data source backend's data validation routine.
     *
     * @throws DataSourceFailedDataValidationException if validation failed.
     * @throws DataSourceValidationIncompleteException if an error occurred
     * during validation that prevented its completion.
     */
    public DataValidationEvent[] validateDataSourceBackendData()
            throws DataSourceFailedDataValidationException,
            DataSourceValidationIncompleteException {
        KnowledgeSource knowledgeSource = getKnowledgeSource();
        List<DataValidationEvent> validationEvents =
                new ArrayList<>();
        try {
            for (DataSourceBackend backend : getDataSource().getBackends()) {
                CollectionUtils.addAll(validationEvents,
                        backend.validateData(knowledgeSource));
            }
        } catch (DataSourceBackendFailedDataValidationException ex) {
            throw new DataSourceFailedDataValidationException(
                    "Data source failed validation", ex, validationEvents.toArray(new DataValidationEvent[validationEvents.size()]));
        } catch (KnowledgeSourceReadException ex) {
            throw new DataSourceValidationIncompleteException(
                    "An error occurred during validation", ex);
        }
        return validationEvents.toArray(new DataValidationEvent[validationEvents.size()]);
    }

    /**
     * Closes resources created by this object and the data source, knowledge
     * source, and algorithm source.
     */
    public void close() throws CloseException {
        boolean abstractionFinderClosed = false;
        boolean algorithmSourceClosed = false;
        boolean knowledgeSourceClosed = false;
        boolean termSourceClosed = false;
        try {
            this.abstractionFinder.close();
            abstractionFinderClosed = true;
            this.abstractionFinder.getAlgorithmSource().close();
            algorithmSourceClosed = true;
            this.abstractionFinder.getKnowledgeSource().close();
            knowledgeSourceClosed = true;
            this.abstractionFinder.getTermSource().close();
            termSourceClosed = true;
        } catch (CloseException e) {
            this.abstractionFinder.getDataSource().failureOccurred(e);
            throw e;
        } finally {
            if (!algorithmSourceClosed) {
                try {
                    this.abstractionFinder.getAlgorithmSource().close();
                } catch (CloseException ignored) {}
            }
            if (!knowledgeSourceClosed) {
                try {
                    this.abstractionFinder.getKnowledgeSource().close();
                } catch (CloseException ignored) {}
            }
            if (!termSourceClosed) {
                try {
                    this.abstractionFinder.getTermSource().close();
                } catch (CloseException ignored) {}
            }
        }
        if (abstractionFinderClosed && algorithmSourceClosed && knowledgeSourceClosed && termSourceClosed) {
            this.abstractionFinder.getDataSource().close();
        } else {
            try {
                this.abstractionFinder.getDataSource().close();
            } catch (CloseException ignored) {}
        }
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
