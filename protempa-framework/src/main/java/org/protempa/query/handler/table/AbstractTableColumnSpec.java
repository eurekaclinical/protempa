package org.protempa.query.handler.table;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.protempa.KnowledgeSource;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

public abstract class AbstractTableColumnSpec implements TableColumnSpec {

    boolean checkCompatible(Proposition proposition,
            PropertyConstraint[] constraints) {
        for (int i = 0; i < constraints.length; i++) {
            PropertyConstraint ccc = constraints[i];
            String propName = ccc.getPropertyName();
            Value value = proposition.getProperty(propName);
            ValueComparator vc = ccc.getValueComparator();
            if (!vc.subsumes(value.compare(ccc.getValue()))) {
                return false;
            }
        }
        return true;
    }

    String generateLinksHeaderString(Link[] links) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < links.length; i++) {
            result.append(links[i].headerFragment());
        }
        return result.toString();
    }

    Collection<Proposition> traverseLinks(Link[] links,
            Proposition proposition,
            Map<Proposition, List<Proposition>> derivations,
            Map<UniqueIdentifier, Proposition> references,
            KnowledgeSource knowledgeSource) {
        Queue<Proposition> result = new LinkedList<Proposition>();
        result.add(proposition);
        int num = 1;
        for (Link link : links) {
            int j = 0;
            while (j < num) {
                Proposition prop = result.poll();
                Collection<Proposition> c = link.traverse(prop, derivations,
                        references, knowledgeSource);
                for (Proposition p : c) {
                    if (!result.contains(p)) {
                        result.add(p);
                    }
                }
                j++;
            }
            num = result.size();
        }
        return result;
    }
}
