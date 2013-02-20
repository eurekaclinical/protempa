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
