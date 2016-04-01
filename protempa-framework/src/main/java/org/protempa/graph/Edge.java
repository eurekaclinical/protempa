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
