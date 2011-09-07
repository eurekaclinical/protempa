package org.protempa.query;

import org.protempa.AlgorithmSource;
import org.protempa.KnowledgeSource;

/**
 * A query specification. Once specified, call 
 * {@link #build(org.protempa.KnowledgeSource, org.protempa.AlgorithmSource) } 
 * to build the actual query. It is recommended that you use
 * {@link org.protempa.Protempa#buildQuery(org.protempa.query.QueryBuilder) },
 * which calls this method with a Protempa instance's knowledge source backend 
 * and algorithm source backend.
 * 
 * @author Andrew Post
 */
public interface QueryBuilder {
    /**
     * Builds a {@link Query}. This method could validate the query
     * specification using the knowledge source and algorithm source.
     * 
     * @param knowledgeSource a {@link KnowledgeSource}.
     * @param algorithmSource a {@link AlgorithmSource}.
     * @return a {@link Query}. Guaranteed not <code>null</code>.
     * @throws QueryBuildException If the query specification failed 
     * validation or some other error occurred.
     */
    Query build(KnowledgeSource knowledgeSource, 
            AlgorithmSource algorithmSource) throws QueryBuildException;
}
