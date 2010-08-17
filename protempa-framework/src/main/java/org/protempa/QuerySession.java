package org.protempa;

import org.protempa.query.Query;
import org.protempa.query.handler.QueryResultsHandler;

public class QuerySession {
    private final String id;
    private final Query query;
    private final AbstractionFinder finder;

    public QuerySession(Query initialQuery, AbstractionFinder abstractionFinder) {
        this.finder = abstractionFinder;
        this.id = this.generateId();
        this.query = initialQuery.clone();
    }

    private String generateId() {
        // TODO: implement a proper key generation routine
        return "TempID";
    }

    public String getId() {
        return this.id;
    }

    public Query getQuery() {
        return query;
    }

     /**
     * This method returns the results from this query session after applying
     * the restrictions and constraints held in the Query object passed in. The
     * result set will be passed to the QueryResultsHandler that is passed in.
     * 
     * @param query
     *            The Query object containing the restrictions/constraints
     * @param handler
     *            The QueryResultsHandler object that will handle the result
     *            set.
     */
    public void execute(Query query, QueryResultsHandler handler) {
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
