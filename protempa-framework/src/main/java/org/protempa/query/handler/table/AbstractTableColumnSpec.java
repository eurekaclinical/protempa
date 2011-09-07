package org.protempa.query.handler.table;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

public abstract class AbstractTableColumnSpec implements TableColumnSpec {
    
    boolean checkCompatible(Proposition proposition,
            PropertyConstraint[] constraints) {
        for (PropertyConstraint ccc : constraints) {
            String propName = ccc.getPropertyName();
            Value value = proposition.getProperty(propName);
            ValueComparator vc = ccc.getValueComparator();
            if (!vc.test(value.compare(ccc.getValue()))) {
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

    /**
     * Traverses links from a proposition to a list of propositions.
     * 
     * @param links the {@link Link}s to traverse.
     * @param proposition the {@link Proposition} from which to start.
     * @param forwardDerivations map of propositions from raw data toward derived propositions.
     * @param backwardDerivations map of propositions from derived propositions 
     * toward raw data.
     * @param references a map of unique id to the corresponding proposition
     * for propositions that are referred to by other propositions.
     * @param knowledgeSource the {@link KnowledgeSource}.
     * @return the list of {@link Propositions} at the end of the traversals.
     * @throws KnowledgeSourceReadException if an error occurred reading from
     * the knowledge source.
     */
    List<Proposition> traverseLinks(Link[] links,
            Proposition proposition,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references,
            KnowledgeSource knowledgeSource) throws KnowledgeSourceReadException {
        LinkedList<Proposition> result = new LinkedList<Proposition>();
        Set<Proposition> cache = new HashSet<Proposition>();
        Logger logger = Util.logger();
        result.add(proposition);
        int num = 1;
        for (Link link : links) {
            int j = 0;
            while (j < num) {
                Proposition prop = result.remove();
                Collection<Proposition> c = link.traverse(prop, 
                        forwardDerivations, backwardDerivations,
                        references, knowledgeSource, cache);
                result.addAll(c);
                j++;
            }
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "{0} traversed to {1} with {2}",
                        new Object[]{getClass().getName(), result, link});
            }
            num = result.size();
            cache.clear();
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "{0} traversed to {1}",
                    new Object[]{getClass().getName(), result});
        }
        return result;
    }
}
