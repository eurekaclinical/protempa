/*
 * #%L
 * JavaUtil
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
package org.protempa.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * An implementation of the Bellman-Ford single-source shortest paths algorithm.
 * The implementation can look for the shortest paths from a source or to a
 * destination. It runs in O(n^2) time, where n is the number of vertices in the
 * graph.
 * 
 * @author Andrew Post
 */
public final class BellmanFord {

	/**
	 * For setting whether the Bellman-Ford algorithm should look for the
	 * shortest paths from a source or to a destination.
	 * 
	 * @author Andrew Post
	 */
	public static enum Mode {
		SOURCE, DESTINATION
	}

	/**
	 * Constructor for BellmanFord (private).
	 */
	private BellmanFord() {
	}

	/**
	 * Calculates the distance of every object in a graph from a source object.
	 * It calls <code>hasNegativeCycles()</code>, and if no negative cycles
	 * are found, returns the shortest distances.
	 * 
	 * @param sourceOrDest
	 *            a vertex.
	 * @return a newly created <code>Map</code> of objects and their distances
	 *         from the source object, or <code>null</code> if a negative
	 *         cycle was encountered.
	 */
	public static Map<?, Weight> calcShortestDistances(
			Object sourceOrDest, DirectedGraph g, Mode mode) {
		Map<Object, Weight> shortestDistances = new HashMap<>(g
				.size() * 4 / 3 + 1);
		if (hasNegativeCyclePrivate(sourceOrDest, g, shortestDistances, mode)) {
			return null;
		} else {
			return shortestDistances;
		}
	}

	/**
	 * Determines if there are any negative cycles found.
	 * 
	 * @param sourceOrDest
	 *            a vertex.
	 * @return <code>true</code> if a negative cycle was found,
	 *         <code>false</code> otherwise.
	 */
	public static boolean hasNegativeCycle(Object sourceOrDest,
			DirectedGraph g, Mode mode) {
		return calcShortestDistances(sourceOrDest, g, mode) == null;
	}

	private static boolean hasNegativeCyclePrivate(Object sourceOrDest,
			DirectedGraph g, Map<Object, Weight> shortestDistances, Mode mode) {
		init(sourceOrDest, g, shortestDistances);
		int edgeCount = g.getEdgeCount();

		/*
		 * Array for storing distances. The first half of the array is for
		 * starts of edges (finishes if Mode is set to DESTINATION). The second
		 * half is for finishes of edges (starts if Mode is set to SOURCE).
		 * Ordering is that provided by DirectedGraph.edges().
		 */
		Weight[] wc = new Weight[edgeCount << 1];

		Edge[] edges = g.edgesAsArray();

		boolean stale = relax(g, shortestDistances, wc, edges, mode);
		Weight newDistance = new Weight();
		int wcLengthDiv2 = wc.length >>> 1;
		for (int i = 0; i < edges.length; i++) {
			Edge e = edges[i];
			if (stale) {
				newDistance
						.set((Weight) shortestDistances
								.get(mode == Mode.SOURCE ? e.getStart() : e
										.getFinish()));
			} else {
				newDistance.set(wc[i]);
			}
			newDistance.addToSelf(e.getWeight());
			Weight oldDistance = null;
			if (stale) {
				oldDistance = (Weight) shortestDistances
						.get(mode == Mode.SOURCE ? e.getFinish() : e.getStart());
			} else {
				oldDistance = wc[wcLengthDiv2 + i];
			}
			if (oldDistance.compareTo(newDistance) > 0) {
				return true;
			}
		}

		return false;
	}

	private static void init(Object sourceOrDest, DirectedGraph g,
			Map<Object, Weight> shortestDistances) {

		// Distance from source to itself.
		shortestDistances.put(sourceOrDest, WeightFactory.ZERO);

		/*
		 * Initialize all shortest distances to maximum. Iterations of
		 * Bellman-Ford may narrow these distances.
		 */
		for (Iterator itr = g.iterator(); itr.hasNext();) {
			Object vertex = itr.next();
			if (vertex != sourceOrDest) {
				shortestDistances.put(vertex, 
                                        WeightFactory.POS_INFINITY);
			}
		}
	}

	private static boolean relax(DirectedGraph g,
			Map<Object, Weight> shortestDistances, Weight[] wc, Edge[] edges,
			Mode mode) {
		Weight newDistance = new Weight();
		boolean wcStale = true;
		boolean makeStale = false;
		int wcLengthDiv2 = wc.length >>> 1;
		for (int i = 1, n = g.size(); i < n; i++) {
			for (int j = 0; j < edges.length; j++) {
				Edge e = edges[j];
				if (wcStale) {
					wc[j] = (Weight) shortestDistances
							.get(mode == Mode.SOURCE ? e.getStart() : e
									.getFinish());
				}
				newDistance.set(wc[j]);
				newDistance.addToSelf(e.getWeight());

				int jj = wcLengthDiv2 + j;
				if (wcStale) {
					wc[jj] = (Weight) shortestDistances
							.get(mode == Mode.SOURCE ? e.getFinish() : e
									.getStart());
				}
				Weight oldDistance = wc[jj];
				if (oldDistance.compareTo(newDistance) > 0) {
					shortestDistances.put(mode == Mode.SOURCE ? e.getFinish()
							: e.getStart(), new Weight(newDistance));
					makeStale = true;
				}
			}
			if (!makeStale) {
				wcStale = false;
			}
			makeStale = false;
		}
		return wcStale;
	}
}
