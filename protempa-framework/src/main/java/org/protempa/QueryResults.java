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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.QueryResults.QueryResultsEntry;
import org.protempa.proposition.Proposition;

/**
 *
 * @author Andrew Post
 */
public final class QueryResults implements Iterable<QueryResultsEntry> {
//    public static QueryResults newInstance(QueryResultReader reader) {
//
//    }

    public static class QueryResultsEntry {
        private String keyId;
        private List<Proposition> propositions;

        private QueryResultsEntry(String keyId, List<Proposition> propositions) {
            this.keyId = keyId;
            this.propositions = propositions;
        }

        public String getKeyId() {
            return this.keyId;
        }

        public List<Proposition> getPropositions() {
            return this.propositions;
        }
    }

    public static class QueryResultsIterator implements Iterator<QueryResultsEntry> {
        private final Iterator<Map.Entry<String, List<Proposition>>> itr;

        private QueryResultsIterator(Map<String,List<Proposition>> results) {
            this.itr = results.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return this.itr.hasNext();
        }

        @Override
        public QueryResultsEntry next() {
            Map.Entry<String, List<Proposition>> me = this.itr.next();
            return new QueryResultsEntry(me.getKey(), me.getValue());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private Map<String, List<Proposition>> results;

    QueryResults(Map<String, List<Proposition>> results) {
        this.results = results;
    }

    public List<Proposition> get(String keyId) {
        return Collections.unmodifiableList(this.results.get(keyId));
    }

    public Set<String> getKeyIds() {
        return this.results.keySet();
    }

    @Override
    public Iterator<QueryResultsEntry> iterator() {
        return new QueryResultsIterator(this.results);
    }


}
