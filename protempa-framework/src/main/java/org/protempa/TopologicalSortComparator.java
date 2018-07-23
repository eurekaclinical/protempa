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
class TopologicalSortComparator implements Comparator<TemporalPropositionDefinition>, Serializable {

    private static final long serialVersionUID = 924247928684751479L;

    private final Map<String, Integer> rule2Index = new HashMap<>();

    /**
     * Constructs the topological sort class. The constructor sorts the 
     * provided abstraction definitions.
     *
     * @param allNarrowerDescendants cache of all possible proposition definitions
     * involved in the current query.
     * @param abstractionDefinitions the {@link Set} of all
     * {@link AbstractionDefinition}s.
     * @throws CycleDetectedException if a cycle in the abstraction
     * definitions is detected.
     */
    TopologicalSortComparator(Collection<? extends PropositionDefinition> allNarrowerDescendants,
            Collection<? extends TemporalPropositionDefinition> abstractionDefinitions)
            throws CycleDetectedException {
        // build the graph, a map of node name -> list of neighbors.
        Map<String, PropositionDefinition> andMap = new HashMap<>();
        for (PropositionDefinition pd : allNarrowerDescendants) {
            andMap.put(pd.getId(), pd);
        }
        HashMap<String, List<String>> nodes = 
                new HashMap<>(
                abstractionDefinitions.size() * 4 / 3 + 1);
        Set<String> abstractionDefPropIds = new HashSet<>();
        for (TemporalPropositionDefinition apd : abstractionDefinitions) {
            abstractionDefPropIds.add(apd.getId());
        }
        for (TemporalPropositionDefinition tpd : abstractionDefinitions) {
            if (tpd instanceof AbstractionDefinition) {
                AbstractionDefinition ad = (AbstractionDefinition) tpd;
                List<String> ads = new ArrayList<>();
                for (String id : ad.getAbstractedFrom()) {
                    if (abstractionDefPropIds.contains(id)) {
                        ads.add(id);
                    }
                }
                nodes.put(tpd.getId(), ads);
            } else if (tpd instanceof ContextDefinition) {
                ContextDefinition cd = (ContextDefinition) tpd;
                List<String> ibs = new ArrayList<>();
                for (TemporalExtendedPropositionDefinition tepd : 
                        cd.getInducedBy()) {
                    String propId = tepd.getPropositionId();
                    PropositionDefinition propDef = andMap.get(propId);
                    if (propDef instanceof AbstractionDefinition 
                            || propDef instanceof ContextDefinition) {
                        ibs.add(propId);
                    }
                }
                nodes.put(cd.getId(), ibs);
            }
        }
        // topological sort on graph
        ArrayList<String> sortedAbstractions = 
                new ArrayList<>(nodes.size());
        HashSet<String> toBeRemoved = new HashSet<>(); // nodes with no
        // neighbors
        String[] zeroOutDegree = new String[nodes.size()];
        while (!nodes.isEmpty()) {
            toBeRemoved.clear();
            int pos = 0;
            for (Map.Entry<String, List<String>> entry : nodes.entrySet()) {
                String abstr = entry.getKey();
                List<String> neighbors = entry.getValue();
                if (neighbors.isEmpty()) {
                    toBeRemoved.add(abstr);
                    zeroOutDegree[pos++] = abstr;
                }
            }
            if (toBeRemoved.isEmpty()) {
                throw new CycleDetectedException(nodes.keySet());
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
     * Compares two temporal proposition definitions topologically according to 
     * the inverse of their abstractedFrom or inducedBy relationships.
     * 
     * @param a1 an {@link TemporalPropositionDefinition}.
     * @param a2 another {@link TemporalPropositionDefinition}.
     * @return a negative integer, zero, or a positive integer as the first
     * argument is less than, equal to, or greater than the second.
     */
    @Override
    public int compare(TemporalPropositionDefinition a1, 
            TemporalPropositionDefinition a2) {
        if (a1 == a2) {
            return 0;
        } else {
            Integer index1 = rule2Index.get(a1.getId());
            Integer index2 = rule2Index.get(a2.getId());
            return index1.compareTo(index2);
        }
    }
}
