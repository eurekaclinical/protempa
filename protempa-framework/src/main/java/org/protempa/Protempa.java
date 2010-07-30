package org.protempa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protempa.backend.BackendNewInstanceException;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.InvalidConfigurationsException;
import org.protempa.dsb.datasourceconstraint.DataSourceConstraint;
import org.protempa.proposition.Proposition;
import org.protempa.query.Query;
import org.protempa.query.handler.QueryResultsHandler;


/**
 * Main object for users of PROTEMPA.
 * 
 * TODO Finders should return an object that implements a Results interface.
 * Objects could store results in-memory or in a database (e.g., an interval
 * database). Ideally, for stateful support, intervals in such a store could
 * be reloaded into working memory. This would be a kind of star schema.
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

    public static Protempa newInstance(String configurationsId,
            boolean useCache)
            throws ConfigurationsLoadException, BackendInitializationException,
            BackendNewInstanceException, BackendProviderSpecLoaderException,
            InvalidConfigurationsException {
        return newInstance(new SourceFactory(configurationsId), useCache);
    }

    public static Protempa newInstance(SourceFactory sourceFactory,
            boolean useCache)
            throws BackendInitializationException, BackendNewInstanceException {
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
            throw new IllegalArgumentException(
                    "knowledgeSource cannot be null");
        }
        if (algorithmSource == null) {
            throw new IllegalArgumentException(
                    "algorithmSource cannot be null");
        }
        this.abstractionFinder = new AbstractionFinder(dataSource,
                knowledgeSource, algorithmSource,
                cacheFoundAbstractParameters);
    }

    public void execute(Query query, QueryResultsHandler resultHandler) throws FinderException {
        if (query == null)
            throw new IllegalArgumentException("query cannot be null");

//        Map<String, List<Proposition>> results =
//                new HashMap<String, List<Proposition>>();

        Set<String> keyIdsSet = asSet(query.getKeyIds());
        Set<String> propIds = asSet(query.getPropIds());
        DataSourceConstraint dataSourceConstraints =
                    query.getDataSourceConstraints();
        
//        results.putAll()
        		this.abstractionFinder.doFind(keyIdsSet,
                propIds, dataSourceConstraints, resultHandler);
        
//        return new QueryResults(results);
    }

    private Set<String> asSet(String[] propIds) {
        Set<String> propIdsSet = new HashSet<String>();
        if (propIds != null) {
            propIdsSet = new HashSet<String>();
            for (String propId : propIds) {
                propIdsSet.add(propId);
            }
        } else {
            propIdsSet = null;
        }
        return propIdsSet;
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
