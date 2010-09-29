package org.protempa;

import java.util.Set;
import java.util.logging.Logger;

import org.arp.javautil.arrays.Arrays;
import org.protempa.backend.BackendNewInstanceException;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.InvalidConfigurationException;
import org.protempa.dsb.filter.Filter;
import org.protempa.query.Query;
import org.protempa.query.handler.QueryResultsHandler;

/**
 * Main PROTEMPA API.
 * 
 * @author Andrew Post
 */
public final class Protempa {

    private final AbstractionFinder abstractionFinder;

    public static Protempa newInstance(String configurationId)
            throws ConfigurationsLoadException, BackendInitializationException,
            BackendNewInstanceException, BackendProviderSpecLoaderException,
            InvalidConfigurationException,
            DataSourceFailedValidationException, 
            DataSourceValidationIncompleteException {
        return newInstance(new SourceFactory(configurationId));
    }

    public static Protempa newInstance(SourceFactory sourceFactory)
            throws BackendInitializationException, BackendNewInstanceException,
            DataSourceFailedValidationException, 
            DataSourceValidationIncompleteException {
        return new Protempa(sourceFactory.newDataSourceInstance(),
                sourceFactory.newKnowledgeSourceInstance(),
                sourceFactory.newAlgorithmSourceInstance(), false);
    }

    public static Protempa newInstance(String configurationsId, boolean useCache)
            throws ConfigurationsLoadException, BackendInitializationException,
            BackendNewInstanceException, BackendProviderSpecLoaderException,
            InvalidConfigurationException, DataSourceFailedValidationException,
            DataSourceValidationIncompleteException {
        return newInstance(new SourceFactory(configurationsId), useCache);
    }

    public static Protempa newInstance(SourceFactory sourceFactory,
            boolean useCache) throws BackendInitializationException,
            BackendNewInstanceException,
            DataSourceFailedValidationException,
            DataSourceValidationIncompleteException {
        return new Protempa(sourceFactory.newDataSourceInstance(),
                sourceFactory.newKnowledgeSourceInstance(),
                sourceFactory.newAlgorithmSourceInstance(), useCache);
    }

    /**
     * Constructor that configures PROTEMPA not to cache found abstract
     * parameters.
     * 
     * @param dataSource a {@link DataSource}. Will be closed
     * when {@link #close()} is called.
     * @param knowledgeSource a {@link KnowledgeSource}. Will be closed when
     * {@link #close()} is called.
     * @param algorithmSource an {@link AlgorithmSource}. Will be closed when
     * {@link #close()} is called.
     * @throws DataSourceFailedValidationException if the data source's
     * data element mappings are inconsistent with those defined in the
     * knowledge source.
     * @throws DataSourceValidationIncompleteException if an error occurred
     * during data source validation.
     */
    public Protempa(DataSource dataSource, KnowledgeSource knowledgeSource,
            AlgorithmSource algorithmSource) 
            throws DataSourceFailedValidationException,
            DataSourceValidationIncompleteException {
        this(dataSource, knowledgeSource, algorithmSource, false);
    }

    /**
     * Constructor that lets the user specify whether or not to cache found
     * abstract parameters.
     * 
     * @param dataSource a {@link DataSource}. Will be closed
     * when {@link #close()} is called.
     * @param knowledgeSource a {@link KnowledgeSource}. Will be closed when
     * {@link #close()} is called.
     * @param algorithmSource an {@link AlgorithmSource}. Will be closed when
     * {@link #close()} is called.
     * @param cacheFoundAbstractParameters
     *            <code>true</code> to cache found abstract parameters,
     *            <code>false</code> not to cache found abstract parameters.
     * @throws DataSourceFailedValidationException if the data source's
     * data element mappings are inconsistent with those defined in the
     * knowledge source.
     * @throws DataSourceValidationIncompleteException if an error occurred
     * during data source validation.
     */
    public Protempa(DataSource dataSource, KnowledgeSource knowledgeSource,
            AlgorithmSource algorithmSource,
            boolean cacheFoundAbstractParameters) 
            throws DataSourceFailedValidationException,
            DataSourceValidationIncompleteException {
        if (dataSource == null) {
            throw new IllegalArgumentException("dataSource cannot be null");
        }
        if (knowledgeSource == null) {
            throw new IllegalArgumentException("knowledgeSource cannot be null");
        }
        if (algorithmSource == null) {
            throw new IllegalArgumentException("algorithmSource cannot be null");
        }
        Logger logger = ProtempaUtil.logger();
        if (!Boolean.getBoolean("protempa.skip.datasource.validation")) {
            logger.fine("Beginning data source validation");
            dataSource.validate(knowledgeSource);
            logger.fine(
              "Data source validation completed with no validation failures");
        } else {
            logger.fine("Skipping data source validation");
        }
        this.abstractionFinder = new AbstractionFinder(dataSource,
            knowledgeSource, algorithmSource, cacheFoundAbstractParameters);
    }

    /**
     * Gets the data source.
     *
     * @return a {@link DataSource}. WIll be closed when {@link #close()} is
     * called.
     */
    public DataSource getDataSource() {
        return this.abstractionFinder.getDataSource();
    }

    /**
     * Gets the knowledge source.
     *
     * @return a {@link KnowledgeSource}. WIll be closed when {@link #close()}
     * is called.
     */
    public KnowledgeSource getKnowledgeSource() {
        return this.abstractionFinder.getKnowledgeSource();
    }

    /**
     * Gets the algorithm source.
     *
     * @return an {@link AlgorithmSource}. WIll be closed when {@link #close()}
     * is called.
     */
    public AlgorithmSource getAlgorithmSource() {
        return this.abstractionFinder.getAlgorithmSource();
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
     * @param query a {@link Query}.
     * @param resultHandler a {@link QueryResultsHandler}.
     * @throws FinderException if an error occurred during query.
     */
    public void execute(Query query, QueryResultsHandler resultHandler)
            throws FinderException {
        if (query == null)
            throw new IllegalArgumentException("query cannot be null");
        if (query.getTermIds().length > 0)
            throw new UnsupportedOperationException(
                    "term id support has not been implemented yet.");
        Logger logger = ProtempaUtil.logger();
        logger.fine("Executing query");
        Set<String> keyIdsSet = Arrays.asSet(query.getKeyIds());
        Set<String> propIds = Arrays.asSet(query.getPropIds());
        Filter filters = query.getFilters();
        QuerySession qs = new QuerySession(query, this.abstractionFinder);
        this.abstractionFinder.doFind(keyIdsSet, propIds, filters,
                resultHandler, qs);
        logger.fine("Query execution complete");
    }

    /**
     * Closes resources created by this object and the data source,
     * knowledge source, and algorithm source.
     */
    public void close() {
        this.abstractionFinder.close();
        this.abstractionFinder.getAlgorithmSource().close();
        this.abstractionFinder.getDataSource().close();
        this.abstractionFinder.getKnowledgeSource().close();
        ProtempaUtil.logger().fine("Protempa closed");
    }

    /**
     * Clears resources created by this object and the data source,
     * knowledge source and algorithm source.
     */
    public void clear() {
        this.abstractionFinder.clear();
        this.abstractionFinder.getAlgorithmSource().clear();
        this.abstractionFinder.getDataSource().clear();
        this.abstractionFinder.getKnowledgeSource().clear();
        ProtempaUtil.logger().fine("Protempa cleared");
    }
}
