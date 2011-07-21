package org.protempa.query.handler.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.KnowledgeSource;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

/**
 * Convenience class for creating {@link Link} implementations. It is only
 * intended for internal use.
 * 
 * @author Andrew Post
 */
public abstract class Link {

    private static final PropertyConstraint[] EMPTY_PROPERTY_CONSTRAINT_ARR = new PropertyConstraint[0];

    private final Set<String> propIdsAsSet;
    private final PropertyConstraint[] constraints;
    private final Comparator<Proposition> comparator;
    private final int fromIndex;
    private final int toIndex;

    /**
     * Instantiates a link with proposition ids, constraints, a comparator, and
     * an index range for selecting from the list of propositions that match the
     * proposition ids and constraints.
     * 
     * @param propositionIds
     *            a {@link String[]} of proposition ids.
     * @param constraints
     *            a {@link PropertyConstraint[]} of constraints.
     * @param comparator
     *            a {@link Comparator<Proposition>}.
     * @param fromIndex
     *            the lower bound of the range (a negative number or zero is
     *            interpreted as the beginning of the list).
     * @param toIndex
     *            the upper bound of the range exclusive (a negative number is
     *            interpreted as the end of the list).
     */
    Link(String[] propositionIds, PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int fromIndex, int toIndex) {
        if (propositionIds == null) {
            this.propIdsAsSet = Collections.emptySet();
        } else {
            ProtempaUtil.checkArrayForNullElement(propositionIds,
                    "propositionIds");
            this.propIdsAsSet = new HashSet<String>();
            for (String propId : propositionIds) {
                this.propIdsAsSet.add(propId.intern());
            }
        }
        if (constraints == null) {
            this.constraints = EMPTY_PROPERTY_CONSTRAINT_ARR;
        } else {
            ProtempaUtil.checkArrayForNullElement(constraints, "constraints");
            this.constraints = constraints.clone();
        }
        if (fromIndex >= 0 && toIndex >= 0 && fromIndex >= toIndex) {
            throw new IllegalArgumentException(
                    "fromIndex cannot be greater than or equal to toIndex");
        }

        this.comparator = comparator;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    /**
     * The propositions of interest at the end of a traversal step. An empty
     * array indicates that all propositions are of interest.
     * 
     * @return a proposition id {@link String[]} specifying the propositions of
     *         interest. Guaranteed not <code>null</code>.
     */
    public final String[] getPropositionIds() {
        return this.propIdsAsSet.toArray(new String[this.propIdsAsSet.size()]);
    }

    /**
     * Constraints on properties of the propositions of interest.
     * 
     * @return a {@link PropertyConstraint[]}. Guaranteed not <code>null</code>.
     */
    public final PropertyConstraint[] getConstraints() {
        return this.constraints.clone();
    }

    /**
     * A comparator for ordering the propositions at the end of the traversal
     * step. If <code>null</code>, the propositions will not be provided in any
     * particular order.
     * 
     * @return a {@link Comparator<Proposition>}.
     */
    public final Comparator<Proposition> getComparator() {
        return this.comparator;
    }

    /**
     * The start index of the propositions of interest, after applying the
     * proposition ids, constraints and comparator.
     * 
     * @return an <code>int</code>, with zero or a negative number indicating
     *         the beginning of the list.
     */
    public final int getFromIndex() {
        return this.fromIndex;
    }

    /**
     * The last index of the propositions of interest exclusive, after applying
     * the proposition ids, constraints and comparator.
     * 
     * @return an <code>int</code>, with a negative number indicating the end of
     *         the list. Must be greater than the <code>fromIndex</code>.
     */
    public final int getToIndex() {
        return this.toIndex;
    }

    /**
     * Generates a string for query results handler field headers.
     * 
     * @return a {@link String}.
     */
    abstract String headerFragment();

    /**
     * Returns the default header fragment for this link.
     * 
     * @param ref
     *            the name {@link String} of the reference of this link.
     * @return a header fragment {@link String}.
     */
    final String createHeaderFragment(String ref) {
        int size = this.propIdsAsSet.size();
        boolean sep1Needed = size > 0 && this.constraints.length > 0;
        String sep1 = sep1Needed ? ", " : "";
        String id = size > 0 ? "id=" : "";
        boolean parenNeeded = size > 0 || this.constraints.length > 0;
        String startParen = parenNeeded ? "(" : "";
        String finishParen = parenNeeded ? ")" : "";

        String range = rangeString();

        boolean sep2Needed = sep1Needed && range.length() > 0;
        String sep2 = sep2Needed ? ", " : "";

        return '.' + ref + startParen + id
                + StringUtils.join(this.propIdsAsSet, ',') + sep1
                + constraintHeaderString(this.constraints) + finishParen + sep2
                + range;
    }

    /**
     * Returns the list of propositions matching the proposition ids and
     * constraints within the specified index range.
     * 
     * @param propositions
     *            a {@link Collection<Proposition>}.
     * 
     * @return a {@link List<Proposition>}.
     */
    protected final List<Proposition> createResults(
            Collection<Proposition> propositions) {
        List<Proposition> result = new ArrayList<Proposition>();
        if (propositions != null) {
            for (Proposition derivedProp : propositions) {
                addToResults(derivedProp, result);
            }

            result = filterResults(result);
        }

        return result;
    }

    private String constraintHeaderString(PropertyConstraint[] constraints) {
        List<String> constraintsL = new ArrayList<String>(constraints.length);
        for (int i = 0; i < constraints.length; i++) {
            PropertyConstraint ccc = constraints[i];
            constraintsL.add(ccc.getFormatted());
        }
        return StringUtils.join(constraintsL, ',');
    }

    private String rangeString() {
        boolean rangeSpecified = this.fromIndex >= 0 || this.toIndex >= 0;
        String range = rangeSpecified ? "range=" : "";
        if (rangeSpecified) {
            if (this.fromIndex >= 0) {
                range += this.fromIndex;
            } else {
                range += 0;
            }
            range += ",";
            if (this.toIndex >= 0) {
                range += this.toIndex;
            } else {
                range += "end";
            }
        }
        return range;
    }

    private List<Proposition> filterResults(List<Proposition> result) {
        assert result != null : "result should not be null in sliceResults";
        if (!result.isEmpty()) {
            if (this.comparator != null) {
                Collections.sort(result, this.comparator);
            }
            if (this.fromIndex >= 0 || this.toIndex >= 0) {

                return result.subList(
                        this.fromIndex >= 0 ? this.fromIndex : 0,
                        this.toIndex >= 0 ? Math.min(this.toIndex,
                                result.size()) : result.size());
            }
        }
        return result;
    }

    private void addToResults(Proposition prop, Collection<Proposition> result) {
        assert prop != null : "prop cannot be null";
        assert result != null : "result cannot be null";
        if (this.propIdsAsSet.isEmpty()
                || this.propIdsAsSet.contains(prop.getId())) {
            boolean compatible = constraintsCheckCompatible(prop,
                    this.constraints);
            if (compatible) {
                result.add(prop);
            }
        }
    }

    private boolean constraintsCheckCompatible(Proposition proposition,
            PropertyConstraint[] constraints) {
        for (PropertyConstraint ccc : constraints) {
            boolean constraintMatches = false;
            String propName = ccc.getPropertyName();
            Value value = proposition.getProperty(propName);
            if (value != null) {
                ValueComparator vc = ccc.getValueComparator();
                Util.logger()
                        .log(Level.FINE,
                                "Proposition is {0}; Property is {1}; Value is {2}; Comparator: {3}",
                                new Object[] { proposition.getId(), propName,
                                        value, vc });
                for (Value v : ccc.getValues()) {
                    if (vc.is(value.compare(v))) {
                        constraintMatches = true;
                        break;
                    }
                }
                if (!constraintMatches) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Traverses this step.
     * 
     * @param proposition
     *            a {@link Proposition} at which to start the traversal.
     * @param derivations
     *            a {@link Map<Proposition,List<Proposition>>} of derived
     *            propositions.
     * @param references
     *            a {@link Map<Proposition,Proposition>} of unique identifiers
     *            to {@link Proposition}s, used to resolve references.
     * @param knowledgeSource
     *            the {@link KnowledgeSource}.
     * @return the {@link Collection<Proposition>} at the end of the traversal
     *         step.
     */
    public abstract Collection<Proposition> traverse(Proposition proposition,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueIdentifier, Proposition> references,
            KnowledgeSource knowledgeSource);

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
