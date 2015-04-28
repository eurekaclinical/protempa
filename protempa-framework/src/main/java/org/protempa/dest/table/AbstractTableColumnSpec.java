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
package org.protempa.dest.table;

import java.util.List;
import java.util.Map;

import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceCache;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

public abstract class AbstractTableColumnSpec implements TableColumnSpec {
    private final LinkTraverser linkTraverser;
    
    public AbstractTableColumnSpec() {
        this.linkTraverser = new LinkTraverser();
    }
    
    boolean checkCompatible(Proposition proposition,
            PropertyConstraint[] constraints) {
        for (PropertyConstraint ccc : constraints) {
            String propName = ccc.getPropertyName();
            Value value = proposition.getProperty(propName);
            ValueComparator vc = ccc.getValueComparator();
            if (!vc.compare(value, ccc.getValue())) {
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
     * @param forwardDerivations map of propositions from raw data toward 
     * derived propositions.
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
            KnowledgeSourceCache ksCache) {
        return this.linkTraverser.traverseLinks(links, proposition, 
                forwardDerivations, backwardDerivations, references, 
                ksCache);
    }
}
