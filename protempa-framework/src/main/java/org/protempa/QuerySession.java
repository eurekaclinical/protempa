package org.protempa;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.arp.javautil.collections.Collections;
import org.protempa.dsb.filter.Filter;
import org.protempa.proposition.Proposition;
import org.protempa.query.Query;
import org.protempa.query.handler.QueryResultsHandler;

public class QuerySession {
    private final String id;
    private final Query query;
    private final AbstractionFinder finder;
    private final Map<Object, Proposition> propositionCache;

    QuerySession(Query initialQuery, AbstractionFinder abstractionFinder) {
        this.finder = abstractionFinder;
        this.id = this.generateId();
        this.query = initialQuery.clone();
        this.propositionCache = new HashMap<Object, Proposition>();
    }

    private String generateId() {
        // TODO: implement a proper key generation routine, possibly
        // as follows:
        // return java.util.UUID.randomUUID().toString();
        return "TempID";
    }

    public String getId() {
        return this.id;
    }

    public Query getQuery() {
        return query;
    }

    public List<Proposition> getReferences(Proposition prop, String name) throws DataSourceReadException {
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
                            + Collections.join(notFound, ","));
        }
        return references;
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
}
