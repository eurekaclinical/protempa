package org.protempa.query.handler.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.arp.javautil.arrays.Arrays;
import org.protempa.KnowledgeSource;
import org.protempa.proposition.Parameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.value.Value;

/**
 *
 * @author Andrew Post
 */
public final class Derivation extends Link {

    public static enum Behavior {

        /**
         * Forward chaining, toward abstractions.
         */
        SINGLE_FORWARD,
        /**
         * Backward chaining, toward raw data.
         */
        SINGLE_BACKWARD,
        /**
         * Forward chaining, toward abstractions.
         */
        MULT_FORWARD,
        /**
         * Backward chaining, toward raw data.
         */
        MULT_BACKWARD
    }
    private final Behavior behavior;
    private Value[] allowedValues;

    public Derivation(String[] propositionIds, Behavior behavior) {
        this(propositionIds, null, null, behavior);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints, Behavior behavior) {
        this(propositionIds, constraints, null, -1, -1, null,
                behavior);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints, Value[] allowedValues,
            Behavior behavior) {
        this(propositionIds, constraints, null, -1, -1, allowedValues,
                behavior);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int index,
            Behavior behavior) {
        this(propositionIds, constraints, comparator, index,
                index >= 0 ? index + 1 : -1, null, behavior);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int index,
            Value[] allowedValues, Behavior behavior) {
        this(propositionIds, constraints, comparator, index,
                index >= 0 ? index + 1 : -1, allowedValues, behavior);
    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int fromIndex, int toIndex,
            Behavior behavior) {
        this(propositionIds, constraints, comparator, fromIndex, toIndex,
                null, behavior);

    }

    public Derivation(String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int fromIndex, int toIndex,
            Value[] allowedValues, Behavior behavior) {
        super(propositionIds, constraints, comparator, fromIndex, toIndex);
        if (allowedValues == null) {
            this.allowedValues = new Value[0];
        } else {
            this.allowedValues = allowedValues.clone();
        }
        if (behavior == null) {
            throw new IllegalArgumentException("behavior cannot be null");
        }
        this.behavior = behavior;
    }

    @Override
    String headerFragment() {
        return createHeaderFragment("derived");
    }

    @Override
    public Collection<Proposition> traverse(Proposition proposition,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueIdentifier, Proposition> references,
            KnowledgeSource knowledgeSource) {
        //First, aggregate derived values according to the specified behavior.
        List<Proposition> derived;
        switch (this.behavior) {
            case SINGLE_FORWARD:
                derived = forwardDerivations.get(proposition);
                break;
            case SINGLE_BACKWARD:
                derived = backwardDerivations.get(proposition);
                break;
            case MULT_FORWARD:
                derived = new ArrayList<Proposition>();
                Set<Proposition> cache = new HashSet<Proposition>();
                derived.add(proposition);
                int startPos = 0;
                boolean added = true;
                while (added) {
                    added = false;
                    int j = 0;
                    int size = derived.size();
                    while (startPos + j < size) {
                        Proposition prop = derived.get(startPos + j);
                        Collection<Proposition> c =
                                forwardDerivations.get(prop);
                        if (c != null) {
                            for (Proposition p : c) {
                                if (cache.add(p)) {
                                    derived.add(p);
                                    added = true;
                                }
                            }
                        }
                        j++;
                    }
                    startPos += j;
                }
                //System.out.println("derived: " + derived);
                break;
            case MULT_BACKWARD:
                derived = new ArrayList<Proposition>();
                cache = new HashSet<Proposition>();
                derived.add(proposition);
                startPos = 0;
                added = true;
                while (added) {
                    added = false;
                    int j = 0;
                    int size = derived.size();
                    while (startPos + j < size) {
                        Proposition prop = derived.get(startPos + j);
                        Collection<Proposition> c =
                                backwardDerivations.get(prop);
                        if (c != null) {
                            for (Proposition p : c) {
                                if (cache.add(p)) {
                                    derived.add(p);
                                    added = true;
                                }
                            }
                        }
                        j++;
                    }
                    startPos += j;
                }
                break;
            default:
                throw new AssertionError("Unexpected behavior: "
                        + this.behavior);
        }

        //Second, after filtering by allowed values, filter by proposition id.
        return createResults(filterAllowedValues(derived));
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    private Collection<Proposition> filterAllowedValues(
            List<Proposition> propositions) {
        if (propositions == null) {
            return null;
        } else if (this.allowedValues.length == 0) {
            return propositions;
        } else {
            List<Proposition> result =
                    new ArrayList<Proposition>(propositions.size());
            for (Proposition prop : propositions) {
                if (prop instanceof Parameter
                        && Arrays.contains(this.allowedValues,
                        ((Parameter) prop).getValue())) {
                    result.add(prop);
                }
            }
            return result;
        }
    }
}
