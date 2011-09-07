package org.protempa.proposition.interval;

import java.util.Iterator;

import org.arp.javautil.graph.DirectedGraph;
import org.arp.javautil.graph.Edge;
import org.arp.javautil.graph.Weight;

/**
 * An implementation of the directional path consistency (DPC) algorithm for
 * determining if a simple temporal problem (STP) constraint network is
 * consistent, as defined in Dechter, R. et al. Temporal Constraint Networks.
 * Artif. Intell. 1991;49:61-95. This algorithm runs in O(nW*(d^2)) time, where
 * n is the number of vertices in the network, and W*(d) is the maximum number
 * of parents that a vertex possesses in the induced graph, which can be
 * substantially smaller than n.
 * 
 * @author Andrew Post
 */
final class DirectionalPathConsistency {

    /**
     * Private constructor.
     */
    private DirectionalPathConsistency() {
    }

    /**
     * Gets the length of the edge between vertices <code>v1</code> and
     * <code>v2</code>, either as originally specified in a directed graph or
     * as updated by DPC.
     * 
     * @param i
     *            index of <code>v1</code> in the vertex ordering.
     * @param j
     *            index of <code>v2</code> in the vertex ordering.
     * @param v1
     *            a vertex.
     * @param v2
     *            a vertex.
     * @param g
     *            a <code>DirectedGraph</code>.
     * @param updatedEdges
     *            a matrix of updates to the edge lengths as computed by DPC.
     * @return a <code>Weight</code> representing the length of the specified
     *         edge, or <code>null</code> if no edge exists between
     *         <code>v1</code> and <code>v2</code>.
     */
    private static Weight getWeight(int i, int j, Object v1, Object v2,
            DirectedGraph g, Weight[][] updatedEdges) {
        Weight w = updatedEdges[i][j];
        if (w == null) {
            Edge e = g.getEdge(v1, v2);
            if (e != null) {
                w = e.getWeight();
            }
        }
        return w;
    }

    /**
     * Returns whether a directed graph is consistent.
     * 
     * @param g
     *            a <code>DirectedGraph</code>.
     * @return <code>true</code> if the given graph is consistent,
     *         <code>false</code> otherwise.
     */
    static boolean getConsistent(DirectedGraph g) {
        Object[] vertexOrdering = new Object[g.size()];
        int voa = 0;
        for (Iterator itr = g.iterator(); itr.hasNext(); voa++) {
            vertexOrdering[voa] = itr.next();
        }
        int vol = vertexOrdering.length;
        Weight[][] updatedEdges = new Weight[vol][vol];
        for (int k = vol - 1; k > 1; k--) {
            Object vk = vertexOrdering[k];
            for (int i = 0; i < k; i++) {
                Object vi = vertexOrdering[i];
                Weight wik = getWeight(i, k, vi, vk, g, updatedEdges);
                if (wik != null) {
                    /*
                     * wki may be different from wik, so we can't just get the
                     * inverse of wik.
                     */
                    Weight wki = getWeight(k, i, vk, vi, g, updatedEdges);
                    // i > j because we evaluate the vertices in order.
                    for (int j = i + 1; j < k; j++) {
                        Object vj = vertexOrdering[j];
                        Weight wjk = getWeight(j, k, vj, vk, g, updatedEdges);
                        if (wjk != null) {
                            Weight wkj = getWeight(k, j, vk, vj, g,
                                    updatedEdges);
                            Weight wikj = wik.add(wkj);
                            Weight wjki = wjk.add(wki);
                            Weight wij = getWeight(i, j, vi, vj, g,
                                    updatedEdges);
                            Weight wji = getWeight(j, i, vj, vi, g,
                                    updatedEdges);
                            Weight iwjki = wjki.invertSign();
                            Weight iwji = null;
                            if (wji != null) {
                                iwji = wji.invertSign();
                            }
                            if (wij != null) {
                                Weight intersectionMin = Weight.max(iwji, iwjki);
                                Weight intersectionMax = Weight.min(wij, wikj);
                                if (intersectionMin.compareTo(intersectionMax) > 0) {
                                    return false;
                                }
                                updatedEdges[i][j] = intersectionMax;
                                updatedEdges[j][i] = intersectionMin.invertSign();
                            } else {
                                updatedEdges[i][j] = wikj;
                                updatedEdges[j][i] = wjki;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
