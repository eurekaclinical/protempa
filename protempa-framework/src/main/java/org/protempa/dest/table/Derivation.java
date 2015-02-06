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
package org.protempa.dest.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.arp.javautil.arrays.Arrays;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.proposition.Parameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.interval.Relation;
import org.protempa.proposition.value.Value;

/**
 * Traverses traversals from a proposition to/from a derived proposition.
 *
 * @author Andrew Post
 */
public final class Derivation extends Link {
    private static final Value[] EMPTY_VALUE_ARRAY = new Value[0];

    /**
     * For configuring the direction of the traversal and how many derivation
     * steps to traverse.
     */
    public static enum Behavior {

        /**
         * Forward chaining, toward derived propositions.
         */
        SINGLE_FORWARD,
        /**
         * Backward chaining, toward raw data.
         */
        SINGLE_BACKWARD,
        /**
         * Forward chaining, toward derived propositions.
         */
        MULT_FORWARD,
        /**
         * Backward chaining, toward raw data.
         */
        MULT_BACKWARD
    }
    private final Behavior behavior;
    private final Value[] allowedValues;
    private Set<String> knowledgeTree;
    private final Relation relation;
    private final Queue<Proposition> internalDerived;

    public Derivation(String[] propositionIds, Behavior behavior) {
        this(propositionIds, null, null, behavior, null);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints, Behavior behavior) {
        this(propositionIds, constraints, null, -1, -1, null,
                behavior, null);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints, Value[] allowedValues,
            Behavior behavior) throws KnowledgeSourceReadException {
        this(propositionIds, constraints, null, -1, -1, allowedValues,
                behavior, null);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints, Value[] allowedValues,
            Behavior behavior, Relation relation) {
        this(propositionIds, constraints, null, -1, -1, allowedValues,
                behavior, relation);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int index,
            Behavior behavior) throws KnowledgeSourceReadException {
        this(propositionIds, constraints, comparator, index,
                index >= 0 ? index + 1 : -1, null, behavior, null);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int index,
            Value[] allowedValues, Behavior behavior) {
        this(propositionIds, constraints, comparator, index,
                index >= 0 ? index + 1 : -1, allowedValues, behavior, null);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int fromIndex, int toIndex,
            Behavior behavior) throws KnowledgeSourceReadException {
        this(propositionIds, constraints, comparator, fromIndex, toIndex,
                null, behavior, null);

    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int fromIndex, int toIndex,
            Value[] allowedValues, Behavior behavior, Relation relation) {
        super(propositionIds, constraints, comparator, fromIndex, toIndex);
        if (allowedValues == null) {
            this.allowedValues = EMPTY_VALUE_ARRAY;
        } else {
            this.allowedValues = allowedValues.clone();
        }
        if (behavior == null) {
            throw new IllegalArgumentException("behavior cannot be null");
        }
        this.behavior = behavior;
        this.relation = relation;
        this.internalDerived = new LinkedList<>();
    }

    @Override
    public String[] getInferredPropositionIds(KnowledgeSource knowledgeSource,
            String[] inPropIds) throws KnowledgeSourceReadException {
        String[] explicitPropIds = getPropositionIds();
        if (explicitPropIds.length > 0) {
            return explicitPropIds;
        } else {
            Set<String> result = new HashSet<>();
            for (String propId : inPropIds) {
                PropositionDefinition propDef =
                        knowledgeSource.readPropositionDefinition(propId);
                if (propDef == null) {
                    throw new IllegalArgumentException("Invalid propId: "
                            + propId);
                }
                switch (this.behavior) {
                    case SINGLE_BACKWARD:
                        Arrays.addAll(result, propDef.getChildren());
                        break;
                    case MULT_BACKWARD:
                        Queue<String> backwardProps =
                                new LinkedList<>();
                        Arrays.addAll(backwardProps, propDef.getChildren());
                        String pId;
                        while ((pId = backwardProps.poll()) != null) {
                            PropositionDefinition pDef =
                                    knowledgeSource.readPropositionDefinition(pId);
                            Arrays.addAll(backwardProps, pDef.getChildren());
                        }
                        result.addAll(backwardProps);
                        break;
                    case SINGLE_FORWARD:
                        for (PropositionDefinition def :
                                knowledgeSource.readParents(propDef)) {
                            result.add(def.getId());
                        }
                        break;
                    case MULT_FORWARD:
                        Queue<String> forwardProps =
                                new LinkedList<>();
                        for (PropositionDefinition def :
                                knowledgeSource.readParents(propDef)) {
                            forwardProps.add(def.getId());
                        }
                        // pId is declared in MULT_BACKWARD case.
                        while ((pId = forwardProps.poll()) != null) {
                            PropositionDefinition pDef =
                                    knowledgeSource.readPropositionDefinition(pId);
                            for (PropositionDefinition def :
                                    knowledgeSource.readParents(pDef)) {
                                forwardProps.add(def.getId());
                            }
                        }
                        result.addAll(forwardProps);
                        break;
                    default:
                        throw new AssertionError(
                                "Invalid derivation behavior specified");
                }
            }
            return result.toArray(new String[result.size()]);
        }
    }

    @Override
    String headerFragment() {
        return createHeaderFragment("derived");
    }

    /**
     * Traverses a derivation.
     *
     * @param proposition a {@link Proposition} at which to start the traversal.
     * @param forwardDerivations a {@link Map<Proposition,List<Proposition>>} of
     * derived propositions.
     * @param backwardDerivations a {@link Map<Proposition,List<Proposition>>}
     * of derived propositions.
     * @param references a {@link Map<Proposition,Proposition>} of unique
     * identifiers to {@link Proposition}s, used to resolve references.
     * @param knowledgeSource the {@link KnowledgeSource}.
     * @param cache a {@link Set<Proposition>} for convenience in checking if
     * duplicate propositions are traversed to. It is cleared in between calls
     * to this method.
     * @return the {@link Collection<Proposition>} at the end of the traversal
     * step.
     */
    @Override
    Collection<Proposition> traverse(Proposition proposition,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references,
            KnowledgeSource knowledgeSource, final Set<Proposition> cache)
            throws KnowledgeSourceReadException {
        //First, aggregate derived values according to the specified behavior.
        List<Proposition> derived = null;
        switch (this.behavior) {
            case SINGLE_FORWARD:
                derived = filterMatches(proposition,
                        forwardDerivations.get(proposition),
                        cache);
                break;
            case SINGLE_BACKWARD:
                derived = filterMatches(proposition,
                        backwardDerivations.get(proposition),
                        cache);
                break;
            case MULT_FORWARD:
                populateKnowledgeTree(knowledgeSource);
                if (this.knowledgeTree.contains(proposition.getId())) {
                    /*
                     * For performance, pull the specified proposition ids,
                     * cache the hierarchy, and check to see if each
                     * traversed-to proposition's id is in the hierarchy. This
                     * is much faster than traversing the whole hierarchy in
                     * most cases.
                     */
                    derived = new ArrayList<>();
                    internalDerived.add(proposition);
                    while (!internalDerived.isEmpty()) {
                        Proposition prop = internalDerived.remove();
                        /*
                         * If (in hierarchy or ???) and not in cache, then add
                         * to derived.
                         */
                        Collection<Proposition> c =
                                forwardDerivations.get(prop);
                        if (c != null) {
                            for (Proposition p : c) {
                                if (cache.add(p)) {
                                    internalDerived.add(p);
                                    if (isMatch(p) && hasAllowedValue(p)) {
                                        derived.add(p);
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case MULT_BACKWARD:
                derived = new ArrayList<>();
                internalDerived.add(proposition);
                while (!internalDerived.isEmpty()) {
                    Proposition prop = internalDerived.remove();
                    Collection<Proposition> c =
                            backwardDerivations.get(prop);
                    if (c != null) {
                        for (Proposition p : c) {
                            if (cache.add(p)) {
                                internalDerived.add(p);
                                if (isMatch(p) && hasAllowedValue(p)) {
                                    derived.add(p);
                                }
                            }
                        }
                    }
                }
                break;
            default:
                throw new AssertionError("Unexpected behavior: "
                        + this.behavior);
        }

        this.internalDerived.clear();
        return createResults(derived);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    private void populateKnowledgeTree(KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        if (this.knowledgeTree == null) {
            this.knowledgeTree = new HashSet<>();
            for (String propId : getPropositionIds()) {
                this.knowledgeTree.addAll(
                        knowledgeSource.collectPropIdDescendantsUsingAllNarrower(true, propId));
            }
        }
    }

    private boolean hasAllowedValue(Proposition proposition) {
        if (this.allowedValues.length == 0) {
            return true;
        } else {
            if (proposition instanceof Parameter
                    && Arrays.contains(this.allowedValues,
                    ((Parameter) proposition).getValue())) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Filters
     * <code>propositions</code>. Removes duplicates, propositions not in the
     * proposition id list, and parameters that do not meet any value
     * constraints set.
     *
     * @param propositions the {@link Collection<Proposition>} to filter.
     * @param cache a {@link Set<Proposition>} that is used to find duplicates.
     *
     * @return a newly-created {@link List<Proposition>}.
     */
    private List<Proposition> filterMatches(Proposition inProposition,
            Collection<Proposition> propositions, Set<Proposition> cache) {
        TemporalProposition inTp = null;
        if (this.relation != null) {
            if (inProposition instanceof TemporalProposition) {
                inTp = (TemporalProposition) inProposition;
            }
        }
        List<Proposition> result = new ArrayList<>();
        if (propositions != null) {
            for (Proposition proposition : propositions) {
                if (cache.add(proposition)
                        && isMatch(proposition)
                        && hasAllowedValue(proposition)) {
                    if (inTp != null && proposition instanceof TemporalProposition) {
                        TemporalProposition tp = (TemporalProposition) proposition;
                        if (!this.relation.hasRelation(tp.getInterval(), inTp.getInterval())) {
                            continue;
                        }
                    }
                    result.add(proposition);
                }
            }
        }
        return result;
    }

    public Behavior getBehavior() {
        return behavior;
    }

    public Value[] getAllowedValues() {
        return allowedValues;
    }

    public Relation getRelation() {
        return relation;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + java.util.Arrays.hashCode(allowedValues);
        result = prime * result + ((behavior == null) ? 0 : behavior.hashCode());
        result = prime * result + ((knowledgeTree == null) ? 0 : knowledgeTree.hashCode());
        result = prime * result + ((relation == null) ? 0 : relation.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Derivation other = (Derivation) obj;
        if (!java.util.Arrays.equals(allowedValues, other.allowedValues)) {
            return false;
        }
        if (behavior != other.behavior) {
            return false;
        }
        if (knowledgeTree == null) {
            if (other.knowledgeTree != null) {
                return false;
            }
        } else if (!knowledgeTree.equals(other.knowledgeTree)) {
            return false;
        }
        if (relation == null) {
            if (other.relation != null) {
                return false;
            }
        } else if (!relation.equals(other.relation)) {
            return false;
        }
        return true;
    }
}
