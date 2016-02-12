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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.CollectionUtils;

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
import org.protempa.dest.QueryResultsHandler;
import org.protempa.dest.Destination;
import org.protempa.dest.GetSupportedPropositionIdsException;

/**
 * Main PROTEMPA API.
 *
 * @author Andrew Post
 */
public final class Protempa implements AutoCloseable {

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
     * @throws ProtempaStartupException if an error occur in starting Protempa.
     * There frequently will be a nested exception that provides more detail.
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
     * @throws ProtempaStartupException if an error occur in starting Protempa.
     * There frequently will be a nested exception that provides more detail.
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

    public String[] getSupportedPropositionIds(Destination destination) throws GetSupportedPropositionIdsException {
        return destination.getSupportedPropositionIds(this.abstractionFinder.getDataSource(), this.abstractionFinder.getKnowledgeSource());
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
     * @param destination a destination. Cannot be <code>null</code>.
     * @throws QueryException if an error occurred during query.
     */
    public void execute(Query query, Destination destination)
            throws QueryException {
        if (query == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
        if (query.getTermIds().length > 0) {
            throw new UnsupportedOperationException(
                    "term id support has not been implemented yet.");
        }
        if (destination == null) {
            throw new IllegalArgumentException("resultsHandler cannot be null");
        }
        Logger logger = ProtempaUtil.logger();
        logger.log(Level.INFO, "Executing query {0}", query.getName());
        QuerySession qs = new QuerySession(query, this.abstractionFinder);
        this.abstractionFinder.doFind(query, destination, qs);
        logger.log(Level.INFO, "Query {0} execution complete", query.getName());
    }

    /**
     * Cancels an executing query. If a query is not running, this method has no
     * effect. Is intended to be called from a different thread from the one
     * that called {@link #execute(org.protempa.query.Query, org.protempa.dest.Destination)
     * }.
     */
    public void cancel() {
        this.abstractionFinder.cancel();
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
        List<DataValidationEvent> validationEvents
                = new ArrayList<>();
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
     *
     * @throws org.protempa.CloseException if an error occurs while closing
     * resources.
     */
    @Override
    public void close() throws CloseException {
        this.abstractionFinder.close();
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
