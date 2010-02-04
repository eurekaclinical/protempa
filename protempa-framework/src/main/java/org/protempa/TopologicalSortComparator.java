package org.protempa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


class TopologicalSortComparator implements Comparator<AbstractionDefinition> {

	private final Map<String, Integer> rule2Index =
            new HashMap<String, Integer>();

    /**
	 * @param knowledgeSource a {@link KnowledgeSource}, cannot be
     * <code>null</code>.
	 */
	TopologicalSortComparator(KnowledgeSource knowledgeSource,
            Set<AbstractionDefinition> abstractions) 
            throws KnowledgeSourceReadException {
		// build the graph, a map of node name -> list of neighbors.
		HashMap<String, List<String>> nodes = new HashMap<String, List<String>>(
				abstractions.size() * 4 / 3 + 1);
		for (AbstractionDefinition apd : abstractions) {
			List<String> ads = new ArrayList<String>();
			for (String id : apd.getAbstractedFrom()) {
				if (knowledgeSource.readPrimitiveParameterDefinition(id) == null
						&& knowledgeSource.readEventDefinition(id) == null) {
					ads.add(id);
				}
			}
			nodes.put(apd.getId(), ads);
		}
		// topological sort on graph
		ArrayList<String> sortedAbstractions = new ArrayList<String>(nodes
				.size());
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
						"Circular definition of low-level abstractions! " +
                        nodes);
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