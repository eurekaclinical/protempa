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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.arp.javautil.arrays.Arrays;

import org.arp.javautil.collections.Collections;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.proposition.Proposition;
import org.protempa.query.Query;
import org.protempa.query.handler.QueryResultsHandler;

/**
 * Models a query followed by data exploration.
 *
 * @author Himanshu Rathod
 */
public class QuerySession {
    private final String id;
    private final Query query;
//    private final AbstractionFinder finder;
    private final Map<Object, Proposition> propositionCache;
    private final Map<Proposition, List<Proposition>> derivationCache;
    private final boolean cachingEnabled;

    /**
     * Initializes the query session with an intial query and the
     * abstraction finder.
     *
     * Ultimately, this probably should be an interface with implementations
     * loaded in through a mechanism similar to the backends.
     *
     * @param initialQuery a {@link Query}.
     * @param abstractionFinder an {@link AbstractionFinder}.
     * @param cachingEnabled <code>boolean</code> to indicate whether the
     * proposition and derived caches are in use.
     */
    QuerySession(Query initialQuery, AbstractionFinder abstractionFinder) {
        assert initialQuery != null : "initialQuery cannot be null";
        assert abstractionFinder != null : "abstractionFinder cannot be null";
//        this.finder = abstractionFinder;
        this.id = this.generateId();
        this.query = initialQuery;
        this.propositionCache = new HashMap<Object, Proposition>();
        this.derivationCache = new HashMap<Proposition, List<Proposition>>();
        this.cachingEnabled = false;
    }

    private String generateId() {
        // TODO: implement a proper key generation routine, possibly
        // as follows:
        // return java.util.UUID.randomUUID().toString();
        return "TempID";
    }

    /**
     * Gets the query session's unique identifier.
     *
     * @return an id {@link String}.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the initial query.
     *
     * @return a {@link Query}.
     */
    public Query getQuery() {
        return this.query;
    }

    /**
     * Returns whether the proposition and derived caches are enabled.
     * @return <code>true</code> if enabled, <code>false</code> if disabled.
     */
    public boolean isCachingEnabled() {
        return this.cachingEnabled;
    }

    /**
     * Gets the references for a proposition with the specified reference name.
     * Propositions store the unique identifiers of propositions to which they
     * refer. This method resolves those unique identifiers. Caching must be
     * enabled for this to work.
     * 
     * @param prop a {@link Proposition}.
     * @param name a reference name {@link String}.
     * @return a {@link List<Proposition>} of propositions, guaranteed not
     * <code>null</code>.
     * @throws DataSourceReadException if some of the reference unique
     * identifiers could not be resolved.
     * @throws UnsupportedOperationException if caching is disabled.
     */
    public List<Proposition> getReferences(Proposition prop, String name)
            throws DataSourceReadException {
        if (!this.cachingEnabled)
            throw new UnsupportedOperationException("Caching is disabled.");
        List<Proposition> references = new LinkedList<Proposition>();
        List<String> notFound = new LinkedList<String>();
        for (Object key : prop.getReferences(name)) {
            if (this.propositionCache.containsKey(key)) {
                references.add(this.propositionCache.get(key));
            } else {
                notFound.add(name + " from " + key);
            }
        }
        if (notFound.size() > 0) {
            throw new DataSourceReadException(
                    "Could not find the following propositions in the cache "
                            + StringUtils.join(notFound, ','));
        }
        return references;
    }

    /**
     * Gets the abstract propositions that were derived from the given
     * proposition. Caching must be enabled for this to work.
     *
     * @param proposition a {@link Proposition}.
     *
     * @return a newly created list of derived {@link Proposition}s. Will never
     * be <code>null</code>.
     * @throws UnsupportedOperationException if caching is disabled.
     */
    public List<Proposition> getDerived(Proposition proposition) {
        if (!this.cachingEnabled)
            throw new UnsupportedOperationException("Caching is disabled.");
        if (proposition == null)
            throw new IllegalArgumentException("proposition cannot be null");
        List<Proposition> derived = this.derivationCache.get(proposition);
        if (derived != null) {
            return new ArrayList<Proposition>(derived);
        } else {
            return new ArrayList<Proposition>(0);
        }
    }

    /**
     * Gets the abstract propositions with the given ids that were derived from
     * the given proposition. Caching must be enabled for this to work.
     *
     * @param proposition a {@link Proposition}.
     * @param propId a proposition id {@link String[]}.
     * @return a newly created list of derived {@link Proposition}s. Will never
     * be <code>null</code>.
     * @throws UnsupportedOperationException if caching is disabled.
     */
    public List<Proposition> getDerived(Proposition proposition,
            String[] propId) {
        if (!this.cachingEnabled)
            throw new UnsupportedOperationException("Caching is disabled.");
        if (proposition == null)
            throw new IllegalArgumentException("proposition cannot be null");
        List<Proposition> derived = this.derivationCache.get(proposition);
        List<Proposition> result = new ArrayList<Proposition>(derived.size());
        if (derived != null && propId != null) {
            for (Proposition p : derived) {
                if (Arrays.contains(propId, p.getId()))
                    result.add(p);
            }
        }
        return result;
    }

    // /**
    // * This method returns the results from this query session after applying
    // * the restrictions and constraints held in the Query object passed in.
    // The
    // * result set will be passed to the QueryResultsHandler that is passed in.
    // *
    // * @param query
    // * The Query object containing the restrictions/constraints
    // * @param handler
    // * The QueryResultsHandler object that will handle the result
    // * set.
    // */
    // public void execute(Query query, QueryResultsHandler handler) {
    // // TODO: implement me
    // }

    public void filter(Filter constraints, QueryResultsHandler handler) {
        // TODO: implement me
    }

    public void drillDown(String propositionId, QueryResultsHandler handler) {
        // TODO: implement me
    }

    /**
     * This method returns the current data for the query session without any
     * filter or drill down. NOTE: This method returns the <b>original</b> data
     * set, not the result set from the most recently run query.
     * 
     * @param handler
     *            The QueryResultsHandler that will be responsible for handling
     *            the result set.
     */
    public void execute(QueryResultsHandler handler) {
        // TODO: implement me
    }

    //query results handlers should have this too but on a per-key basis.
    /**
     * Adds the given proposition to a unique identifier -> proposition cache.
     *
     * @param proposition a {@link Proposition}.
     */
    void addPropositionToCache (Proposition proposition) {
        if (!this.cachingEnabled)
            throw new UnsupportedOperationException("Caching is disabled.");
        assert proposition != null : "proposition cannot be null";
        this.propositionCache.put(proposition.getUniqueId(),
                proposition);
    }

    /**
     * Adds the given propositions to a unique identifier -> proposition cache.
     *
     * @param propositions a {@link List<Proposition>}. This will be passed
     * an unmodifiable list.
     */
    void addPropositionsToCache (List<Proposition> propositions) {
        assert propositions != null : "propositions cannot be null";
        for (Proposition p : propositions) {
            addPropositionToCache(p);
        }
    }

    //query results handlers should have this too but on a per-key basis.
    void addDerivationToCache(Proposition proposition, Proposition derived) {
        if (!this.cachingEnabled)
            throw new UnsupportedOperationException("Caching is disabled");
        assert proposition != null : "proposition cannot be null";
        assert derived != null : "derived cannot be null";
        Collections.putList(this.derivationCache, proposition, derived);
    }

    void addDerivationsToCache(Proposition proposition,
            List<Proposition> deriveds) {
        assert deriveds != null : "deriveds cannot be null";
        for (Proposition d : deriveds) {
            addDerivationToCache(proposition, d);
        }
    }
}
