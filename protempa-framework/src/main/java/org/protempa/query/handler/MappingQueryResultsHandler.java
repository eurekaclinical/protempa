package org.protempa.query.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.protempa.FinderException;
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
//	private QueryResults queryResults;
	
	/**
	 * Gets the map of query results that have been handled
	 * @return the resultMap
	 */
	public Map<String, List<Proposition>> getResultMap() {
		return Collections.unmodifiableMap(resultMap);
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

	/* (non-Javadoc)
	 * @see org.protempa.query.handler.QueryResultsHandler#finish()
	 */
	@Override
	public void finish() throws FinderException {
		this.resultMap.clear();
	}

	/* (non-Javadoc)
	 * @see org.protempa.query.handler.QueryResultsHandler#init()
	 */
	@Override
	public void init() throws FinderException {
		this.resultMap = new HashMap<String, List<Proposition>>();		
	}

	@Override
	public void handleQueryResult(String key, List<Proposition> propositions)
			throws FinderException {
		resultMap.put(key, propositions);
	}

}
