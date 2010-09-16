package org.protempa;

import java.util.Set;

import org.arp.javautil.arrays.Arrays;
import org.protempa.backend.BackendNewInstanceException;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.InvalidConfigurationsException;
import org.protempa.dsb.filter.Filter;
import org.protempa.query.Query;
import org.protempa.query.handler.QueryResultsHandler;

/**
 * Main object for users of PROTEMPA.
 * 
 * TODO Finders should return an object that implements a Results interface.
 * Objects could store results in-memory or in a database (e.g., an interval
 * database). Ideally, for stateful support, intervals in such a store could be
 * reloaded into working memory. This would be a kind of star schema.
 * 
 * @author Andrew Post
 */
public final class Protempa {

    private final AbstractionFinder abstractionFinder;

    public static Protempa newInstance(String configurationId)
            throws ConfigurationsLoadException, BackendInitializationException,
            BackendNewInstanceException, BackendProviderSpecLoaderException,
            InvalidConfigurationsException {
        return newInstance(new SourceFactory(configurationId));
    }

    public static Protempa newInstance(SourceFactory sourceFactory)
            throws BackendInitializationException, BackendNewInstanceException {
        return new Protempa(sourceFactory.newDataSourceInstance(),
                sourceFactory.newKnowledgeSourceInstance(),
                sourceFactory.newAlgorithmSourceInstance(), false);
    }

    public static Protempa newInstance(String configurationsId, boolean useCache)
            throws ConfigurationsLoadException, BackendInitializationException,
            BackendNewInstanceException, BackendProviderSpecLoaderException,
            InvalidConfigurationsException {
        return newInstance(new SourceFactory(configurationsId), useCache);
    }

    public static Protempa newInstance(SourceFactory sourceFactory,
            boolean useCache) throws BackendInitializationException,
            BackendNewInstanceException {
        return new Protempa(sourceFactory.newDataSourceInstance(),
                sourceFactory.newKnowledgeSourceInstance(),
                sourceFactory.newAlgorithmSourceInstance(), useCache);
    }

    /**
     * Constructor that configures PROTEMPA not to cache found abstract
     * parameters.
     * 
     * @param dataSource
     *            a <code>DataSource</code>.
     * 
     * @param knowledgeSource
     *            a <code>KnowledgeSource</code>.
     * @param algorithmSource
     *            an <code>AlgorithmSource</code>.
     */
    public Protempa(DataSource dataSource, KnowledgeSource knowledgeSource,
            AlgorithmSource algorithmSource) {
        this(dataSource, knowledgeSource, algorithmSource, false);
    }

    /**
     * Constructor that lets the user specify whether or not to cache found
     * abstract parameters.
     * 
     * @param dataSource
     *            a <code>DataSource</code>.
     * 
     * @param knowledgeSource
     *            a <code>KnowledgeSource</code>.
     * @param algorithmSource
     *            an <code>AlgorithmSource</code>.
     * @param cacheFoundAbstractParameters
     *            <code>true</code> to cache found abstract parameters,
     *            <code>false</code> not to cache found abstract parameters.
     */
    public Protempa(DataSource dataSource, KnowledgeSource knowledgeSource,
            AlgorithmSource algorithmSource,
            boolean cacheFoundAbstractParameters) {
        if (dataSource == null) {
            throw new IllegalArgumentException("dataSource cannot be null");
        }
        if (knowledgeSource == null) {
            throw new IllegalArgumentException("knowledgeSource cannot be null");
        }
        if (algorithmSource == null) {
            throw new IllegalArgumentException("algorithmSource cannot be null");
        }
        this.abstractionFinder = new AbstractionFinder(dataSource,
                knowledgeSource, algorithmSource, cacheFoundAbstractParameters);
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

    public void execute(Query query, QueryResultsHandler resultHandler)
            throws FinderException {
        if (query == null)
            throw new IllegalArgumentException("query cannot be null");
        if (query.getTermIds().length > 0)
            throw new UnsupportedOperationException(
                    "term id support has not been implemented yet.");

        Set<String> keyIdsSet = Arrays.asSet(query.getKeyIds());
        Set<String> propIds = Arrays.asSet(query.getPropIds());
        Filter filters = query.getFilters();
        QuerySession qs = new QuerySession(query, this.abstractionFinder);
        this.abstractionFinder.doFind(keyIdsSet, propIds, filters,
                resultHandler, qs);
    }

    /**
     * Closes resources created by this object. Note that the data source,
     * knowledge source, algorithm source, and abstraction mechanism must all be
     * closed separately.
     */
    public void close() {
        ProtempaUtil.logger().fine("Closing Protempa.");
        this.abstractionFinder.close();
        this.abstractionFinder.getAlgorithmSource().close();
        this.abstractionFinder.getDataSource().close();
        this.abstractionFinder.getKnowledgeSource().close();
    }

    public void clear() {
        ProtempaUtil.logger().fine("Clearing Protempa.");
        this.abstractionFinder.clear();
        this.abstractionFinder.getAlgorithmSource().clear();
        this.abstractionFinder.getDataSource().clear();
        this.abstractionFinder.getKnowledgeSource().clear();
    }
}
