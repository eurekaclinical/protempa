package org.protempa;

/*-
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2018 Emory University
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
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.arp.javautil.arrays.Arrays;
import org.protempa.proposition.Proposition;

/**
 *
 * @author Andrew Post
 */
public class WorkingMemoryFactStore implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Proposition> propositions;
    private Map<Proposition, Set<Proposition>> forwardDerivations;
    private Map<Proposition, Set<Proposition>> backwardDerivations;

    public List<Proposition> getPropositions() {
        return propositions;
    }

    public void setPropositions(List<Proposition> propositions) {
        this.propositions = propositions;
    }

    public Map<Proposition, Set<Proposition>> getForwardDerivations() {
        return forwardDerivations;
    }

    public void setForwardDerivations(Map<Proposition, Set<Proposition>> forwardDerivations) {
        this.forwardDerivations = forwardDerivations;
    }

    public Map<Proposition, Set<Proposition>> getBackwardDerivations() {
        return backwardDerivations;
    }

    public void setBackwardDerivations(Map<Proposition, Set<Proposition>> backwardDerivations) {
        this.backwardDerivations = backwardDerivations;
    }

    void removeAll(String[] propositionIds) {
        Set<String> propIds = Arrays.asSet(propositionIds);
        Queue<Proposition> queue = new LinkedList<>(this.forwardDerivations != null ? this.forwardDerivations.keySet() : Collections.emptySet());
        Set<String> removed = new HashSet<>(Arrays.asSet(propositionIds));
        while (!queue.isEmpty()) {
            Proposition prop = queue.remove();
            if (prop != null && propIds.contains(prop.getId())) {
                Collection<Proposition> toRemove = this.forwardDerivations != null ? this.forwardDerivations.remove(prop) : null;
                removed.add(prop.getId());
                if (toRemove != null) {
                    queue.addAll(toRemove);
                }
            }
        }
        for (Collection<Proposition> values : this.forwardDerivations.values()) {
            for (Iterator<Proposition> itr = values.iterator(); itr.hasNext();) {
                Proposition prop = itr.next();
                if (removed.contains(prop.getId())) {
                    itr.remove();
                }
            }
        }
        for (Iterator<Proposition> itr = backwardDerivations.keySet().iterator(); itr.hasNext();) {
            Proposition prop = itr.next();
            if (prop == null || removed.contains((prop.getId()))) {
                itr.remove();
            }
        }
        for (Collection<Proposition> values : this.backwardDerivations.values()) {
            for (Iterator<Proposition> itr = values.iterator(); itr.hasNext();) {
                Proposition prop = itr.next();
                if (prop == null || removed.contains(prop.getId())) {
                    itr.remove();
                }
            }
        }
        if (this.propositions != null) {
            for (Iterator<Proposition> itr = this.propositions.iterator(); itr.hasNext();) {
                Proposition prop = itr.next();
                if (prop == null || removed.contains(prop.getId())) {
                    itr.remove();
                }
            }
        }
    }

}
