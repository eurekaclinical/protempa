package org.protempa;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arp.javautil.arrays.Arrays;
import org.protempa.backend.BackendNewInstanceException;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.InvalidConfigurationException;
import org.protempa.dsb.filter.Filter;
import org.protempa.query.And;
import org.protempa.query.Query;
import org.protempa.query.handler.QueryResultsHandler;

/**
 * Main PROTEMPA API.
 * 
 * @author Andrew Post
 */
public final class Protempa {

    private static final String PROTEMPA_STARTUP_FAILURE_MSG = "PROTEMPA could not start up";
    private final AbstractionFinder abstractionFinder;

    public static Protempa newInstance(String configurationId)
            throws ProtempaStartupException {
        try {
            return newInstance(new SourceFactory(configurationId));
        } catch (ConfigurationsLoadException ex) {
            throw new ProtempaStartupException(PROTEMPA_STARTUP_FAILURE_MSG, ex);
        } catch (BackendProviderSpecLoaderException ex) {
            throw new ProtempaStartupException(PROTEMPA_STARTUP_FAILURE_MSG, ex);
        } catch (InvalidConfigurationException ex) {
            throw new ProtempaStartupException(PROTEMPA_STARTUP_FAILURE_MSG, ex);
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
            throw new ProtempaStartupException(PROTEMPA_STARTUP_FAILURE_MSG, ex);
        } catch (BackendNewInstanceException ex) {
            throw new ProtempaStartupException(PROTEMPA_STARTUP_FAILURE_MSG, ex);
        }
    }

    public static Protempa newInstance(String configurationsId, boolean useCache)
            throws ProtempaStartupException {
        try {
            return newInstance(new SourceFactory(configurationsId), useCache);
        } catch (ConfigurationsLoadException ex) {
            throw new ProtempaStartupException(PROTEMPA_STARTUP_FAILURE_MSG, ex);
        } catch (BackendProviderSpecLoaderException ex) {
            throw new ProtempaStartupException(PROTEMPA_STARTUP_FAILURE_MSG, ex);
        } catch (InvalidConfigurationException ex) {
            throw new ProtempaStartupException(PROTEMPA_STARTUP_FAILURE_MSG, ex);
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
            throw new ProtempaStartupException(PROTEMPA_STARTUP_FAILURE_MSG, ex);
        } catch (BackendNewInstanceException ex) {
            throw new ProtempaStartupException(PROTEMPA_STARTUP_FAILURE_MSG, ex);
        }
    }

    /**
     * Constructor that configures PROTEMPA not to cache found abstract
     * parameters.
     * 
     * @param dataSource
     *            a {@link DataSource}. Will be closed when {@link #close()} is
     *            called.
     * @param knowledgeSource
     *            a {@link KnowledgeSource}. Will be closed when
     *            {@link #close()} is called.
     * @param algorithmSource
     *            an {@link AlgorithmSource}. Will be closed when
     *            {@link #close()} is called.
     * @throws DataSourceFailedValidationException
     *             if the data source's data element mappings are inconsistent
     *             with those defined in the knowledge source.
     * @throws DataSourceValidationIncompleteException
     *             if an error occurred during data source validation.
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
     * @param dataSource
     *            a {@link DataSource}. Will be closed when {@link #close()} is
     *            called.
     * @param knowledgeSource
     *            a {@link KnowledgeSource}. Will be closed when
     *            {@link #close()} is called.
     * @param algorithmSource
     *            an {@link AlgorithmSource}. Will be closed when
     *            {@link #close()} is called.
     * @param termSource
     *            a {@link TermSource}. Will be closed when {@link #close()} is
     *            called.
     * @param cacheFoundAbstractParameters
     *            <code>true</code> to cache found abstract parameters,
     *            <code>false</code> not to cache found abstract parameters.
     * @throws DataSourceFailedValidationException
     *             if the data source's data element mappings are inconsistent
     *             with those defined in the knowledge source.
     * @throws DataSourceValidationIncompleteException
     *             if an error occurred during data source validation.
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

            this.abstractionFinder = new AbstractionFinder(dataSource,
                    knowledgeSource, algorithmSource, termSource,
                    cacheFoundAbstractParameters);
        } catch (KnowledgeSourceReadException ex) {
            throw new ProtempaStartupException(PROTEMPA_STARTUP_FAILURE_MSG, ex);
        }
    }

    /**
     * Gets the data source.
     * 
     * @return a {@link DataSource}. Will be closed when {@link #close()} is
     *         called.
     */
    public DataSource getDataSource() {
        return this.abstractionFinder.getDataSource();
    }

    /**
     * Gets the knowledge source.
     * 
     * @return a {@link KnowledgeSource}. Will be closed when {@link #close()}
     *         is called.
     */
    public KnowledgeSource getKnowledgeSource() {
        return this.abstractionFinder.getKnowledgeSource();
    }

    /**
     * Gets the algorithm source.
     * 
     * @return an {@link AlgorithmSource}. Will be closed when {@link #close()}
     *         is called.
     */
    public AlgorithmSource getAlgorithmSource() {
        return this.abstractionFinder.getAlgorithmSource();
    }

    /**
     * Gets the term source.
     * 
     * @return a {@link TermSource}. Will be closed when {@link #close()} is
     *         called
     */
    public TermSource getTermSource() {
        return this.abstractionFinder.getTermSource();
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
     * @param query
     *            The query object to set up in the database for subsequent
     *            queries
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
     * @param query
     *            a {@link Query}.
     * @param resultHandler
     *            a {@link QueryResultsHandler}.
     * @throws FinderException
     *             if an error occurred during query.
     */
    public void execute(Query query, QueryResultsHandler resultHandler)
            throws FinderException {
        if (query == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
        if (query.getTermIds().length > 0) {
            throw new UnsupportedOperationException(
                    "term id support has not been implemented yet.");
        }
        Logger logger = ProtempaUtil.logger();
        logger.fine("Executing query");
        Set<String> keyIdsSet = Arrays.asSet(query.getKeyIds());
        Set<String> propIds = Arrays.asSet(query.getPropIds());
        Set<And<String>> termIds = Arrays.asSet(query.getTermIds());
        Filter filters = query.getFilters();
        QuerySession qs = new QuerySession(query, this.abstractionFinder);
        this.abstractionFinder.doFind(keyIdsSet, propIds, termIds, filters,
                resultHandler, qs);
        logger.fine("Query execution complete");
    }

    public void retrieveDataAndPersist(Query query, String persistentStoreName)
            throws FinderException {
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
                "Retrieved data will be persisted in store named: {0}",
                persistentStoreName);
        Set<String> keyIdsSet = Arrays.asSet(query.getKeyIds());
        Set<String> propIds = Arrays.asSet(query.getPropIds());
        Set<And<String>> termIds = Arrays.asSet(query.getTermIds());
        Filter filters = query.getFilters();
        QuerySession qs = new QuerySession(query, this.abstractionFinder);
        this.abstractionFinder.retrieveData(keyIdsSet, propIds, termIds,
                filters, qs, persistentStoreName);
        logger.log(Level.FINE, "Data retrieval complete");
    }

    public void processResultsAndPersist(Query origQuery, Query newQuery,
            String propositionStoreName, String workingMemoryStoreName)
            throws FinderException {
        if (origQuery == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
        if (origQuery.getTermIds().length > 0) {
            throw new UnsupportedOperationException(
                    "term id support has not been implemented yet.");
        }
        Logger logger = ProtempaUtil.logger();
        logger.log(Level.FINE, "Processing and persisting results");
        logger.log(Level.INFO,
                "Processed results will be persisted in store named: {0}",
                workingMemoryStoreName);
        logger.log(Level.INFO,
                "Pulling previously retrieved data from store named: {0}",
                propositionStoreName);
        Set<String> propIds = Arrays.asSet(origQuery.getPropIds());
        QuerySession qs = new QuerySession(origQuery, this.abstractionFinder);

        this.abstractionFinder.processStoredResults(propIds, qs,
                propositionStoreName, workingMemoryStoreName);
        logger.log(Level.FINE, "Data processing complete");
    }

    public void outputResults(Query query, Set<String> propositionIds,
            QueryResultsHandler resultHandler, String workingMemoryStoreName)
            throws FinderException {
        if (query == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
        if (query.getTermIds().length > 0) {
            throw new UnsupportedOperationException("term id support has not been implemented yet.");
        }
        Logger logger = ProtempaUtil.logger();
        logger.log(Level.FINE, "Outputting results");
        logger.log(Level.INFO, "Retrieving processed results from store named: {0}", workingMemoryStoreName);
        Set<String> propIds = Arrays.asSet(query.getPropIds());
        QuerySession qs = new QuerySession(query, this.abstractionFinder);
        this.abstractionFinder.outputStoredResults(propIds, resultHandler, qs, workingMemoryStoreName);
        logger.log(Level.FINE, "Output complete");
    }

    /**
     * Runs each data source backend's validation routine.
     * 
     * @throws DataSourceFailedValidationException
     *             if validation failed.
     * @throws DataSourceValidationIncompleteException
     *             if an error occurred during validation that prevented its
     *             completion.
     */
    public void validateDataSource()
            throws DataSourceFailedValidationException,
            DataSourceValidationIncompleteException {
        DataSource dataSource = getDataSource();
        dataSource.validate(getKnowledgeSource());
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
        ProtempaUtil.logger().fine("Protempa closed");
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
