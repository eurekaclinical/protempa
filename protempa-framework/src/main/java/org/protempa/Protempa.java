package org.protempa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.protempa.backend.BackendNewInstanceException;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.InvalidConfigurationsException;
import org.protempa.proposition.Proposition;


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
        if (dataSource == null)
            throw new IllegalArgumentException("dataSource cannot be null");
        if (knowledgeSource == null)
            throw new IllegalArgumentException(
                    "knowledgeSource cannot be null");
        if (algorithmSource == null)
            throw new IllegalArgumentException(
                    "algorithmSource cannot be null");
		this.abstractionFinder = new AbstractionFinder(dataSource,
				knowledgeSource, algorithmSource, cacheFoundAbstractParameters);
	}

    public QueryResults execute(Query query) throws FinderException {
        if (query == null)
            throw new IllegalArgumentException("query cannot be null");
        Map<String, List<Proposition>> results =
                new HashMap<String, List<Proposition>>();
        String[] keyIds = query.getKeyIds();
        String[] propIds = query.getPropIds();
        Set<String> propIdsSet;
        if (propIds != null) {
            propIdsSet = new HashSet<String>();
            for (String propId : propIds)
                propIdsSet.add(propId);
        } else {
            propIdsSet = null;
        }
        if (keyIds != null) {
            for (String keyId : keyIds) {
                results.put(keyId,
                        this.abstractionFinder.doFind(keyId,
                        propIdsSet, query.getStart(), query.getFinish()));
            }
        } else {
            int i = 0;
            try {
                while (true) {
                    Set<String> kids =
                            new HashSet<String>(this.abstractionFinder.getDataSource()
                            .getAllKeyIds(i, 10000));
//                	List<String> kids = this.abstractionFinder.getDataSource().getAllKeyIds(i, 1000);
                    if (kids.isEmpty()) 
                    	break;
                    Logger logger = ProtempaUtil.logger();
                    
                    // BATCH QUERY
                    results.putAll(this.abstractionFinder.doFind(kids, propIdsSet, query.getStart(), query.getFinish()));
                    
                    // ONE PATIENT AT A TIME QUERIES
//                    for (String keyId : kids) {
//                    	if (logger.isLoggable(Level.FINER))
//                    		logger.finer("Processing key " + keyId);
//                    	results.put(keyId,
//                    			this.abstractionFinder.doFind(keyId,
//                    					propIdsSet, query.getStart(),
//                    					query.getFinish()));
//                    }
                    i += kids.size();
                    if (logger.isLoggable(Level.FINE))
                        logger.fine("Processed " + i + " keys");
                }
            } catch (DataSourceReadException ex) {
                throw new FinderException(ex);
            }
        }
        return new QueryResults(results);
    }

	/**
	 * Find instances of propositions using a given set of propositions as
	 * input.
	 * 
	 * @param propIds
	 *            a <code>Set</code> of proposition id <code>String</code>s.
	 * @param inPropositions
	 *            a <code>List</code> of <code>Proposition</code>s.
	 * @return a <code>List</code> of <code>Proposition</code>s found.
	 * @throws FinderException
	 *             wraps any exceptions that occur during abstraction finding.
	 */
    @Deprecated
	public List<Proposition> doFindMultipleIn(Set<String> propIds,
			List<? extends Proposition> inPropositions) throws FinderException {
		return this.abstractionFinder.doFindIn(propIds, inPropositions);
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