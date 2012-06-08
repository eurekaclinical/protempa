/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Performs a topological sort on abstraction definitions using the inverse of
 * their abstractedFrom relations.
 *
 * @author Nora Sovarel
 */
class TopologicalSortComparator implements Comparator<AbstractionDefinition>, Serializable {

    private static final long serialVersionUID = 924247928684751479L;

    private final Map<String, Integer> rule2Index =
            new HashMap<String, Integer>();

    /**
     * Constructs the topological sort class.
     *
     * @param knowledgeSource a {@link KnowledgeSource} (cannot be
     * <code>null</code>.
     * @param abstractionDefinitions the {@link Set} of all
     * {@link AbstractionDefinition}s.
     * @throws KnowledgeSourceReadException if an error occurs in reading
     * from the knowledge source.
     */
    TopologicalSortComparator(KnowledgeSource knowledgeSource,
            Collection<AbstractionDefinition> abstractionDefinitions)
            throws KnowledgeSourceReadException {
        // build the graph, a map of node name -> list of neighbors.
        HashMap<String, List<String>> nodes = 
                new HashMap<String, List<String>>(
                abstractionDefinitions.size() * 4 / 3 + 1);
        for (AbstractionDefinition apd : abstractionDefinitions) {
            List<String> ads = new ArrayList<String>();
            for (String id : apd.getAbstractedFrom()) {
                if (knowledgeSource.hasAbstractionDefinition(id)) {
                    ads.add(id);
                }
            }
            nodes.put(apd.getId(), ads);
        }
        // topological sort on graph
        ArrayList<String> sortedAbstractions = 
                new ArrayList<String>(nodes.size());
        HashSet<String> toBeRemoved = new HashSet<String>(); // nodes with no
        // neighbors
        String[] zeroOutDegree = new String[nodes.size()];
        while (!nodes.isEmpty()) {
            toBeRemoved.clear();
            int pos = 0;
            for (Map.Entry<String, List<String>> entry : nodes.entrySet()) {
                String abstr = entry.getKey();
                List<String> neighbors = entry.getValue();
                if (neighbors.size() == 0) {
                    toBeRemoved.add(abstr);
                    zeroOutDegree[pos++] = abstr;
                }
            }
            if (toBeRemoved.isEmpty()) {
                throw new IllegalStateException(
                        "Circular definition of low-level abstractions! "
                        + nodes);
            }
            // lexicographical sort between those at the same level
            Arrays.sort(zeroOutDegree, 0, pos, null);
            for (int i = 0; i < pos; i++) {
                sortedAbstractions.add(zeroOutDegree[i]);
            }
            // remove the nodes we already added ( nodes and edges pointing to
            // them )
            for (List<String> neighbors : nodes.values()) {
                neighbors.removeAll(toBeRemoved);
            }
            for (String r : toBeRemoved) {
                nodes.remove(r);
            }
        }

        for (int i = 0; i < sortedAbstractions.size(); i++) {
            rule2Index.put(sortedAbstractions.get(i), i);
        }
    }

    /**
     * Compares two abstraction definitions topologically according to the
     * inverse of their abstractedFrom relationships.
     * 
     * @param a1 an {@link AbstractionDefinition}.
     * @param a2 another {@link AbstractionDefinition}.
     * @return a negative integer, zero, or a positive integer as the first
     * argument is less than, equal to, or greater than the second.
     */
    @Override
    public int compare(AbstractionDefinition a1, AbstractionDefinition a2) {
        if (a1 == a2) {
            return 0;
        } else {
            Integer index1 = rule2Index.get(a1.getId());
            Integer index2 = rule2Index.get(a2.getId());
            return index1.compareTo(index2);
        }
    }
}
