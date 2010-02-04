package org.arp.javautil.graph;

import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.arp.javautil.arrays.Arrays;


/**
 * A directed graph implementation.
 * 
 * @author Andrew Post
 */
public class DirectedGraph implements Serializable {

	private static final long serialVersionUID = -5958203298365112320L;

	private static int DEFAULT_INITIAL_CAPACITY = 10;

	/**
	 * Capacity.
	 */
	private int capacity;

	/**
	 * Matrix of edges.
	 */
	private Edge edges[][];

	private transient Edge[] edgesArr;

	private transient int edgeModCount;

	private int edgeCount;

	/**
	 * Label --> vertex mapping. Map of all known vertices.
	 */
	private final Map<Object, VertexMetadata> vertices;

	private transient int vertexModCount;

	/**
	 * List of free positions in the matrix.
	 */
	private final FreeList freeList;

	/**
	 * A simple list of ints, for storing the indices of empty matrix entries.
	 * 
	 * @author Andrew Post
	 */
	private static class FreeList {
		private Entry header;

		private static class Entry {
			int element;

			Entry next;

			Entry(int element, Entry next) {
				this.element = element;
				this.next = next;
			}
		}

		void add(int element) {
			header = new Entry(element, header);
		}

		void clear() {
			header = null;
		}

		int removeFirst() {
			int first = header.element;
			Entry oldHeader = header;
			header = header.next;
			oldHeader.next = null;
			return first;
		}

		boolean isEmpty() {
			return header == null;
		}

	}

	private static class VertexMetadata {
		int index;

		VertexMetadata(int index) {
			this.index = index;
		}
	}

	/**
	 * Constructs an empty <code>DirectedGraph</code> with the specified
	 * initial vertex capacity.
	 * 
	 * @param initialCapacity
	 *            the initial vertex capacity.
	 */
	public DirectedGraph(int initialCapacity) {
		if (initialCapacity > 0) {
			this.capacity = initialCapacity;
		} else {
			this.capacity = DEFAULT_INITIAL_CAPACITY;
		}
		edges = new Edge[initialCapacity][initialCapacity];
		vertices = new HashMap<Object, VertexMetadata>(initialCapacity);
		freeList = new FreeList();
		for (int row = capacity - 1; row >= 0; row--) {
			freeList.add(row);
		}
	}

	/**
	 * Constructs an empty <code>DirectedGraph</code> with the default initial
	 * vertex capacity (10).
	 */
	public DirectedGraph() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	/**
	 * Remove all vertices and edges.
	 */
	public void clear() {
		vertices.clear();
		vertexModCount++;
		Arrays.matrixFill(edges, null);
		edgeCount = 0;
		edgesArr = null;
		edgeModCount++;
		freeList.clear();
		for (int row = capacity - 1; row >= 0; row--) {
			freeList.add(row);
		}
	}

	/**
	 * Adds a vertex.
	 * 
	 * @param vertex
	 *            a vertex.
	 */
	public void add(Object vertex) {
		if (vertex == null || vertices.containsKey(vertex)) {
			return;
		}

		checkGraphSize();

		vertices.put(vertex, new VertexMetadata(freeList.removeFirst()));
		vertexModCount++;
	}

	/**
	 * Remove a vertex and all edges between the vertex and other vertices.
	 * 
	 * @param vertex
	 *            a vertex.
	 * @return the value of the vertex, if it existed.
	 */
	public Object remove(Object vertex) {
		VertexMetadata vm = (VertexMetadata) vertices.get(vertex);
		if (vm == null) {
			return null;
		}
		vertices.remove(vertex);
		vertexModCount++;
		int index = vm.index;
		for (int row = 0; row < capacity; row++) {
			edges[row][index] = null;
			edges[index][row] = null;
			edgeModCount++;
		}
		edgesArr = null;
		freeList.add(index);
		return vertex;
	}

	/**
	 * Check to see if an object is a vertex.
	 * 
	 * @param vertex
	 *            the object.
	 * @return true if the object is a vertex, false otherwise.
	 */
	public boolean contains(Object vertex) {
		return vertices.containsKey(vertex);
	}

	/**
	 * Get the number of vertices.
	 * 
	 * @return the number of vertices.
	 */
	public int size() {
		return vertices.size();
	}

	/**
	 * Check to see if there are any vertices defined.
	 * 
	 * @return true if vertices are defined, false otherwise.
	 */
	public boolean isEmpty() {
		return vertices.isEmpty();
	}

	public Edge getEdge(Object vertex1, Object vertex2) {
		VertexMetadata vm1 = (VertexMetadata) vertices.get(vertex1);
		VertexMetadata vm2 = (VertexMetadata) vertices.get(vertex2);
		if (vm1 == null || vm2 == null) {
			return null;
		}
		return edges[vm1.index][vm2.index];
	}

	/**
	 * Set the edge between two vertices. If the two given vertices don't exist,
	 * nothing happens. A <code>null</code> <code>weight</code> unsets the
	 * edge.
	 * 
	 * @param vertex1
	 *            a vertex.
	 * @param vertex2
	 *            a vertex.
	 * @param weight
	 *            the value of the edge.
	 */
	public void setEdge(Object vertex1, Object vertex2, Weight weight) {
		VertexMetadata vm1 = (VertexMetadata) vertices.get(vertex1);
		VertexMetadata vm2 = (VertexMetadata) vertices.get(vertex2);
		if (vm1 == null || vm2 == null) {
			return;
		}
		int index1 = vm1.index;
		int index2 = vm2.index;
		Edge e = new Edge(vertex1, vertex2, weight);
		Edge prev = edges[index1][index2];
		edges[index1][index2] = e;
		edgesArr = null;
		edgeModCount++;
		if (prev == null) {
			edgeCount++;
		}
	}

	/**
	 * Check to see if an edge has been set between two vertices.
	 * 
	 * @param label1
	 *            a vertex.
	 * @param label2
	 *            a vertex.
	 * @return true if an edge has been set, false otherwise or if one or both
	 *         of the objects are not vertices.
	 */
	public boolean containsEdge(Object label1, Object label2) {
		return getEdge(label1, label2) != null;

	}

	/**
	 * Get the total number of edges.
	 * 
	 * @return the total number of edges.
	 */
	public int getEdgeCount() {
		return edgeCount;
	}

	/**
	 * Create an iterator of vertices.
	 * 
	 * @return an iterator of vertices.
	 */
	public Iterator<?> iterator() {
		return vertices.keySet().iterator();
	}

	/**
	 * Create an iterator of all vertices that share an edge with a vertex.
	 * 
	 * @param label
	 *            a vertex.
	 * @return an iterator of vertices.
	 */
	public Iterator<?> neighbors(Object label) {
		return new NeighborIterator(label);
	}

	public Edge[] edgesAsArray() {
		if (edgesArr == null) {
			edgesArr = new Edge[edgeCount];
			int i = 0;
			for (Iterator itr = edges(); i < edgeCount; i++) {
				edgesArr[i] = (Edge) itr.next();
			}
		}
		return edgesArr;
	}

	/**
	 * Create an iterator of edges.
	 * 
	 * @return an iterator of edges.
	 */
	public Iterator<Edge> edges() {
		return new EdgeIterator();
	}

	private void checkGraphSize() {
		if (freeList.isEmpty()) {
			int oldSize = capacity;
			capacity += 10;
			Edge[][] newdata = new Edge[capacity][capacity];
			Arrays.matrixCopy(edges, newdata);
			edges = newdata;
			for (int row = capacity - 1; row >= oldSize; row--) {
				freeList.add(row);
			}
		}
	}

	private class NeighborIterator implements Iterator {
		final Object vertex;

		final int index;

		int row = capacity;

		Object neighbor;

		int expectedModCount = vertexModCount;

		private NeighborIterator(Object vertex) {
			this.vertex = vertex;
			this.index = ((VertexMetadata) vertices.get(vertex)).index;
			try {
				advance();
			} catch (IndexOutOfBoundsException e) {
				checkForComodification();
				throw new NoSuchElementException();
			}
		}

		public boolean hasNext() {
			return neighbor != null;
		}

		public Object next() {
			checkForComodification();
			try {
				Object result = neighbor;
				advance();
				return result;
			} catch (IndexOutOfBoundsException e) {
				checkForComodification();
				throw new NoSuchElementException();
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		private void advance() {
			neighbor = null;
			if (vertex != null) {
				while (--row >= 0) {
					Edge e = edges[index][row];
					if (e != null) {
						if (e.getStart().equals(vertex)) {
							neighbor = e.getFinish();
						} else {
							neighbor = e.getStart();
						}
						break;
					}
				}
			}
		}

		final void checkForComodification() {
			if (vertexModCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
		}
	}

	private class EdgeIterator implements Iterator<Edge> {

		int row;

		int column = -1;

		int expectedModCount = edgeModCount;

		private EdgeIterator() {
			try {
				advance();
			} catch (IndexOutOfBoundsException e) {
				checkForComodification();
				throw new NoSuchElementException();
			}
		}

		public boolean hasNext() {
			return row < edges.length && column < edges[row].length;
		}

		public Edge next() {
			checkForComodification();
			try {
				Edge result = edges[row][column];
				advance();
				return result;
			} catch (IndexOutOfBoundsException e) {
				checkForComodification();
				throw new NoSuchElementException();
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		private void advance() {
			do {
				while (++column < edges[row].length) {
					if (edges[row][column] != null) {
						return;
					}
				}
				row++;
				column = -1;
			} while (row < edges.length);
		}

		final void checkForComodification() {
			if (edgeModCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
		}
	}

}
