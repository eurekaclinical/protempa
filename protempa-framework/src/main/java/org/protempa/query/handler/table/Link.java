package org.protempa.query.handler.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.arp.javautil.arrays.Arrays;
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

    private final String[] propositionIds;
    private final PropertyConstraint[] constraints;
    private final Comparator<Proposition> comparator;
    private final int fromIndex;
    private final int toIndex;

    /**
     * Instantiates a link with proposition ids, constraints, a comparator,
     * and an index range for selecting from the list of propositions that
     * match the proposition ids and constraints.
     * 
     * @param propositionIds a {@link String[]} of proposition ids.
     * @param constraints a {@link PropertyConstraint[]} of constraints.
     * @param comparator a {@link Comparator<Proposition>}.
     * @param fromIndex the lower bound of the range (a negative number or zero
     * is interpreted as the beginning of the list).
     * @param toIndex the upper bound of the range exclusive (a negative number
     * is interpreted as the end of the list).
     */
    Link(String[] propositionIds, PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int fromIndex, int toIndex) {
        if (propositionIds == null) {
            this.propositionIds = new String[0];
        } else {
            ProtempaUtil.checkArrayForNullElement(propositionIds,
                    "propositionIds");
            this.propositionIds = propositionIds.clone();
        }
        if (constraints == null) {
            this.constraints = new PropertyConstraint[0];
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
     * @return a proposition id {@link String[]} specifying the propositions
     * of interest. Guaranteed not <code>null</code>.
     */
    public final String[] getPropositionIds() {
        return this.propositionIds.clone();
    }

    /**
     * Constraints on properties of the propositions of interest.
     *
     * @return a {@link PropertyConstraint[]}. Guaranteed not
     * <code>null</code>.
     */
    public final PropertyConstraint[] getConstraints() {
        return this.constraints.clone();
    }

    /**
     * A comparator for ordering the propositions at the end of the traversal
     * step. If <code>null</code>, the propositions will not be provided in
     * any particular order.
     *
     * @return a {@link Comparator<Proposition>}.
     */
    public final Comparator<Proposition> getComparator() {
        return this.comparator;
    }

    /**
     * The start index of the propositions of interest, after applying the
     * proposition ids,  constraints and comparator.
     *
     * @return an <code>int</code>, with zero or a negative number indicating
     * the beginning of the list.
     */
    public final int getFromIndex() {
        return this.fromIndex;
    }

    /**
     * The last index of the propositions of interest exclusive, after
     * applying the proposition ids, constraints and comparator.
     *
     * @return an <code>int</code>, with a negative number indicating the end
     * of the list. Must be greater than the <code>fromIndex</code>.
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
     * @param ref the name {@link String} of the reference of this link.
     * @return a header fragment {@link String}.
     */
    final String createHeaderFragment(String ref) {
        boolean sep1Needed = this.propositionIds.length > 0
                && this.constraints.length > 0;
        String sep1 = sep1Needed ? ", " : "";
        String id = this.propositionIds.length > 0 ? "id=" : "";
        boolean parenNeeded = this.propositionIds.length > 0
                || this.constraints.length > 0;
        String startParen = parenNeeded ? "(" : "";
        String finishParen = parenNeeded ? ")" : "";

        String range = rangeString();

        boolean sep2Needed = sep1Needed && range.length() > 0;
        String sep2 = sep2Needed ? ", " : "";

        return '.' + ref + startParen + id +
                StringUtils.join(this.propositionIds, ',') + sep1 +
                constraintHeaderString(this.constraints) + finishParen +
                sep2 + range;
    }

    /**
     * Returns the list of propositions matching the proposition ids and
     * constraints within the specified index range.
     * 
     * @param propositions a {@link Collection<Proposition>}.
     *
     * @return a {@link List<Proposition>}.
     */
    final List<Proposition> createResults(Collection<Proposition> propositions) {
        List<Proposition> result = new ArrayList<Proposition>();
        for (Proposition derivedProp : propositions) {
            addToResults(derivedProp, result);
        }
        
        result = filterResults(result);

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
        if (this.comparator != null) {
            Collections.sort(result, this.comparator);
        }
        if (this.fromIndex >= 0 || this.toIndex >= 0) {
            return result.subList(this.fromIndex >= 0 ? this.fromIndex : 0,
                    this.toIndex >= 0 ? this.toIndex : result.size());
        } else {
            return result;
        }
    }

    private void addToResults(Proposition prop, Collection<Proposition> result) {
        if (this.propositionIds.length == 0
                || Arrays.contains(this.propositionIds, prop.getId())) {
            boolean compatible = constraintsCheckCompatible(prop,
                    this.constraints);
            if (compatible) {
                result.add(prop);
            }
        }
    }

    private boolean constraintsCheckCompatible(Proposition proposition,
            PropertyConstraint[] constraints) {
        for (int i = 0; i < constraints.length; i++) {
            PropertyConstraint ccc = constraints[i];
            String propName = ccc.getPropertyName();
            Value value = proposition.getProperty(propName);
            ValueComparator vc = ccc.getValueComparator();
            if (!vc.contains(value.compare(ccc.getValue()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Traverses this step.
     *
     * @param proposition a {@link Proposition} at which to start the
     * traversal.
     * @param derivations a {@link Map<Proposition,List<Proposition>>} of
     * derived propositions.
     * @param references a {@link Map<Proposition,Proposition>} of unique
     * identifiers to {@link Proposition}s, used to resolve references.
     * @param knowledgeSource the {@link KnowledgeSource}.
     * @return the {@link Collection<Proposition>} at the end of the traversal
     * step.
     */
    public abstract Collection<Proposition> traverse(Proposition proposition,
            Map<Proposition, List<Proposition>> derivations,
            Map<UniqueIdentifier, Proposition> references,
            KnowledgeSource knowledgeSource);

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
