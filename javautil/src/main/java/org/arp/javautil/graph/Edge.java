package org.arp.javautil.graph;

/**
 * @author Andrew Post
 */
public final class Edge {

    private final Object start;
    private final Object finish;
    private Weight weight;
    private boolean visited;
    private volatile int hashCode;

    Edge(Object start, Object finish, Weight weight) {
        this.start = start;
        this.finish = finish;
        this.weight = weight;
    }

    public Object getStart() {
        return start;
    }

    public Object getFinish() {
        return finish;
    }

    public Weight getWeight() {
        return weight;
    }

    public boolean getVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            int result = 17;
            if (start != null) {
                result = 37 * result + start.hashCode();
            }
            if (finish != null) {
                result = 37 * result + finish.hashCode();
            }
            this.hashCode = result;
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }

        Edge e = (Edge) o;
        return (start == e.start || (start != null && start.equals(e.start)))
                && (finish == e.finish || (finish != null && finish.equals(e.finish)));
    }

    @Override
    public String toString() {
        return "Edge " + start + ", " + finish + ": " + weight;
    }
}
