package org.protempa.query.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.protempa.FinderException;
import org.protempa.KnowledgeSource;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

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
     * Gets the map of query results that have been handled.
     * 
     * @return the resultMap
     */
    public final Map<String, List<Proposition>> getResultMap() {
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

    /**
     * No-op.
     * 
     * @throws FinderException should never throw.
     */
    @Override
    public void finish() throws FinderException {
    }

    /**
     * Initializes the map returned by {@link #getResultMap()}.
     *
     * @throws FinderException should never throw.
     */
    @Override
    public void init(KnowledgeSource knowledgeSource) throws FinderException {
        this.resultMap = new HashMap<String, List<Proposition>>();
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
    public void handleQueryResult(String key, List<Proposition> propositions,
            Map<Proposition,List<Proposition>> forwardDerivations,
            Map<Proposition,List<Proposition>> backwardDerivations,
            Map<UniqueIdentifier, Proposition> references)
            throws FinderException {
        resultMap.put(key, propositions);
    }
}
