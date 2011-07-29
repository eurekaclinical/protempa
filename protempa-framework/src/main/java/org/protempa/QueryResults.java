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
