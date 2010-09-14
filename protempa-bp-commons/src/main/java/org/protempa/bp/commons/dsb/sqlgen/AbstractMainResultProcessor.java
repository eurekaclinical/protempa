package org.protempa.bp.commons.dsb.sqlgen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

abstract class AbstractMainResultProcessor<P extends Proposition>
        extends AbstractResultProcessor {

    private Map<String, List<P>> results;

    final Map<String, List<P>> getResults() {
        return this.results;
    }

    final void setResults(Map<String, List<P>> results) {
        this.results = results;
    }

    final Map<UniqueIdentifier,P> createCache() {
        Map<UniqueIdentifier,P> result =
                new HashMap<UniqueIdentifier,P>();
        if (this.results != null) {
            for (List<P> props : this.results.values()) {
                for (P prop : props) {
                    result.put(prop.getUniqueIdentifier(), prop);
                }
            }
        }
        return result;
    }
}
