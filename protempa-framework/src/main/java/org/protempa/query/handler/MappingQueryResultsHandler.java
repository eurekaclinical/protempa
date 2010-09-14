package org.protempa.query.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.protempa.FinderException;
import org.protempa.QuerySession;
import org.protempa.proposition.Proposition;

/**
 * An implementation of QueryResultsHandler that stores the results in a map
 * that may be accessed later for post-processing. As each call to
 * handleQueryResult is made, each key-proposition list argument pair is added
 * to the map.
 * 
 * @author Michel Mansour
 *
 */
public class MappingQueryResultsHandler implements QueryResultsHandler {

    private Map<String, List<Proposition>> resultMap;
    private QuerySession querySession;

//	private QueryResults queryResults;

    /**
     * Gets the map of query results that have been handled.
     * 
     * @return the resultMap
     */
    public final Map<String, List<Proposition>> getResultMap() {
        return Collections.unmodifiableMap(resultMap);
    }

    /**
     * Gets the {@link QuerySession}.
     * 
     * @return a {@link QuerySession}.
     */
    protected QuerySession getQuerySession() {
        return this.querySession;
    }

    /**
     * @return the queryResults
     */
//	public QueryResults getQueryResults() {
//		if (queryResults == null) {
//			return new QueryResults(resultMap);
//		}
//		return queryResults;
//	}

    /**
     * No-op.
     * 
     * @throws FinderException should never throw.
     */
    @Override
    public void finish() throws FinderException {
    }

    /**
     * Initializes the map returned by {@link #getResultMap()} and
     * stores the {@link QuerySession}.
     *
     * @param querySession a {@link QuerySession}.
     * @throws FinderException should never throw.
     */
    @Override
    public void init(QuerySession querySession) throws FinderException {
        this.resultMap = new HashMap<String, List<Proposition>>();
        this.querySession = querySession;
    }

    /**
     * Puts handled keys and propositions into the map returned by
     * {@link #getResultMap()}.
     *
     * @param key a key id {@link String}.
     * @param propositions a {@link List<Proposition>} of propositions.
     * @throws FinderException should never throw.
     */
    @Override
    public void handleQueryResult(String key, List<Proposition> propositions)
            throws FinderException {
        resultMap.put(key, propositions);
    }
}
