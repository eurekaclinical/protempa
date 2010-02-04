package org.arp.javautil.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.arp.javautil.collections.Iterators;

import junit.framework.TestCase;

/**
 * Test the methods of DirectedGraph.
 * 
 * @author Nora Sovarel
 * 
 */
public class DirectedGraphTest extends TestCase {

	private ArrayList<InternalEdge> expected = new ArrayList<InternalEdge>();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		expected.clear();
	}

	public void testNeighbor() {
		DirectedGraph graph = new DirectedGraph(3);
		graph.add("1");
		graph.add("2");
		graph.add("3");
		addEdge(graph, "1", "2", 2);
		addEdge(graph, "2", "1", 3);
		addEdge(graph, "2", "3", 4);
		assertEquals(Arrays.asList(new String[] { "2" }), Iterators
				.asList(graph.neighbors("1")));
	}

	public void testEdges() {
		/*
		 * Creates a graph with edges X2X 3X4 56X
		 */
		DirectedGraph graph = new DirectedGraph(3);
		graph.add("1");
		graph.add("2");
		graph.add("3");
		addEdge(graph, "1", "2", 2);
		addEdge(graph, "2", "1", 3);
		addEdge(graph, "2", "3", 4);
		addEdge(graph, "3", "1", 5);
		addEdge(graph, "3", "2", 6);

		verifyEdgesResult(graph);
	}

	public void testEdges1() {
		/*
		 * Creates a graph with XXX 3X4 XXX
		 */
		DirectedGraph graph = new DirectedGraph(3);
		graph.add("1");
		graph.add("2");
		graph.add("3");
		addEdge(graph, "2", "1", 3);
		addEdge(graph, "2", "3", 4);

		verifyEdgesResult(graph);
	}

	public void testEdges2() {
		// Creates a graph with X2X XXX 56X
		DirectedGraph graph = new DirectedGraph(3);
		graph.add("1");
		graph.add("2");
		graph.add("3");
		addEdge(graph, "1", "2", 2);
		addEdge(graph, "3", "1", 5);
		addEdge(graph, "3", "2", 6);

		verifyEdgesResult(graph);
	}

	public void testEdges3() {
		/*
		 * Creates a graph with 12XX 3X47 X6X8 XXXX
		 */
		DirectedGraph graph = new DirectedGraph(3);
		graph.add("1");
		graph.add("2");
		graph.add("3");
		graph.add("4");
		addEdge(graph, "1", "2", 2);
		addEdge(graph, "2", "1", 3);
		addEdge(graph, "2", "3", 4);
		addEdge(graph, "2", "4", 7);
		addEdge(graph, "3", "2", 6);
		addEdge(graph, "3", "4", 8);

		verifyEdgesResult(graph);
	}

	public void testEdgesConcurrentModification() {
		DirectedGraph graph = new DirectedGraph(3);
		graph.add("1");
		graph.add("2");
		graph.add("3");
		graph.add("4");
		addEdge(graph, "1", "2", 2);
		addEdge(graph, "2", "1", 3);
		addEdge(graph, "2", "3", 4);
		addEdge(graph, "2", "4", 7);
		addEdge(graph, "3", "2", 6);

		for (Iterator it = graph.edges(); it.hasNext();) {
			addEdge(graph, "3", "4", 8);
			try {
				it.next();
			} catch (ConcurrentModificationException e) {
				return;
			}
		}
		fail();
	}

	private void verifyEdgesResult(DirectedGraph graph) {
		int size = 0;
		for (Iterator<Edge> it = graph.edges(); it.hasNext();) {
			assertTrue(containsEdge(it.next()));
			size++;
		}

		assertEquals(expected.size(), size);
	}

	private void addEdge(DirectedGraph graph, String v1, String v2, int w) {
		graph.setEdge(v1, v2, new Weight(w));
		expected.add(new InternalEdge(v1, v2, w));
	}

	private boolean containsEdge(Edge edge) {
        for (InternalEdge internalEdge : expected) {
			if (internalEdge.v1.equals(edge.getStart())
					&& internalEdge.v2.equals(edge.getFinish())
					&& internalEdge.w == edge.getWeight().value()) {
				return true;
			}
		}
		return false;
	}

	private class InternalEdge {
		private String v1;

		private String v2;

		private int w;

		public InternalEdge(String v1, String v2, int w) {
			this.v1 = v1;
			this.v2 = v2;
			this.w = w;
		}

	}
}