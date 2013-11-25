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
package org.protempa.query.handler.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.query.handler.QueryResultsHandlerValidationFailedException;

/**
 * Representation of links between propositions in the PROTEMPA virtual data
 * model. Links may be derivations or references. This class represents a link
 * as being from one proposition to a collection of propositions. The 
 * collection of propositions may be filtered by proposition id, the values of 
 * its properties, and then by index (according to some specified ordering of 
 * the propositions).
 * 
 * @author Andrew Post
 */
public abstract class Link {

    private static final PropertyConstraint[] EMPTY_PROPERTY_CONSTRAINT_ARR = 
            new PropertyConstraint[0];
    
    private final Set<String> propIdsAsSet;
    private final PropertyConstraint[] constraints;
    private final Comparator<Proposition> comparator;
    private final int fromIndex;
    private final int toIndex;

    /**
     * Specifies a link with proposition ids, constraints, 
     * an index range for selecting from the list of propositions that match 
     * the proposition ids and constraints, and a comparator for ordering the 
     * list.
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
     * The ids of the propositions to traverse to. An empty array indicates 
     * that all propositions are of interest.
     * 
     * @return a proposition id {@link String[]} Guaranteed not 
     * <code>null</code>.
     */
    public final String[] getPropositionIds() {
        return this.propIdsAsSet.toArray(new String[this.propIdsAsSet.size()]);
    }
    
    /**
     * Aggregates and returns the possible proposition ids on the right-hand
     * side of the link.
     * 
     * @param inPropIds the proposition ids inferred from the previous link,
     * or the row proposition id if this is the first link.
     * 
     * @return an array of proposition id {@link String}s. 
     */
    public abstract String[] getInferredPropositionIds(
            KnowledgeSource knowledgeSource, String[] inPropIds)
            throws KnowledgeSourceReadException;
    
    /**
     * Returns whether a proposition has one of the proposition ids specified
     * by the link.
     * 
     * @param proposition a {@link Proposition}. Cannot be <code>null</code>.
     * @return <code>true</code> or <code>false</code>.
     */
    protected boolean isMatch(Proposition proposition) {
        return this.propIdsAsSet.isEmpty() || 
                this.propIdsAsSet.contains(proposition.getId());
    }

    /**
     * Constraints on properties of the propositions at the end of the 
     * traversal.
     * 
     * @return a {@link PropertyConstraint[]}. Guaranteed not <code>null</code>.
     */
    public final PropertyConstraint[] getConstraints() {
        return this.constraints.clone();
    }

    /**
     * A comparator for ordering the propositions at the end of the traversal. 
     * If <code>null</code>, the propositions will not be provided in any
     * particular order.
     * 
     * @return a {@link Comparator<Proposition>}.
     */
    public final Comparator<Proposition> getComparator() {
        return this.comparator;
    }

    /**
     * The start index of the propositions of interest at the end of the 
     * traversal step, after applying the proposition id constraints, property 
     * constraints and a comparator.
     * 
     * @return an <code>int</code>, with zero or a negative number indicating
     *         the beginning of the list.
     */
    public final int getFromIndex() {
        return this.fromIndex;
    }

    /**
     * The last index of the propositions of interest at the end of the
     * traversal step exclusive, after applying the proposition id constraints, 
     * property constraints and a comparator.
     * 
     * @return an <code>int</code>, with a negative number indicating the end of
     *         the list. Must be greater than the <code>fromIndex</code>.
     */
    public final int getToIndex() {
        return this.toIndex;
    }
    
    /**
     * Validates the fields of this link specification against the
     * knowledge source.
     * 
     * @param knowledgeSource a {@link KnowledgeSource}. Guaranteed not
     * <code>null</code>.
     * 
     * @throws QueryResultsHandlerValidationFailedException if validation
     * failed.
     * @throws KnowledgeSourceReadException if the knowledge source could
     * not be read.
     */
    void validate(KnowledgeSource knowledgeSource) throws 
            LinkValidationFailedException, KnowledgeSourceReadException {
        List<String> invalidPropIds = new ArrayList<String>();
        for (String propId : this.propIdsAsSet) {
            if (!knowledgeSource.hasPropositionDefinition(propId)) {
                invalidPropIds.add(propId);
            }
        }
        if (!invalidPropIds.isEmpty()) {
            throw new LinkValidationFailedException(
                    "Invalid proposition id(s): " + 
                    StringUtils.join(invalidPropIds, ", "));
        }
    }

    /**
     * Generates a string for 
     * {@link org.protempa.query.handler.TableQueryResultsHandler} column 
     * headers.
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
     * @see #headerFragment() 
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
     * Filters out propositions by applying the property constraint and index
     * constraints. Modifies <code>propositions</code> in-place!
     * 
     * @param propositions
     *            a {@link Collection<Proposition>}. If <code>null</code>, an
     *            empty list is returned.
     * 
     * @return a {@link List<Proposition>}. Not guaranteed to be modifiable.
     */
    protected final List<Proposition> createResults(
            List<Proposition> propositions) {
        List<Proposition> result;
        if (propositions != null) {
            if (this.constraints.length > 0) {
                result = new ArrayList<Proposition>();
                for (Proposition prop : propositions) {
                    applyPropertyConstraints(prop, result);
                }
            } else {
                result = propositions;
            }

            result = applyComparatorAndIndices(result);
        } else {
            result = Collections.emptyList();
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

    private List<Proposition> applyComparatorAndIndices(
            List<Proposition> result) {
        assert result != null : "result should not be null in sliceResults";
        if (!result.isEmpty()) {
            if (this.comparator != null) {
                Collections.sort(result, this.comparator);
            }
            if (this.fromIndex >= 0 || this.toIndex >= 0) {
                int resultSize = result.size();
                return result.subList(
                        this.fromIndex >= 0 ? this.fromIndex : 0,
                        this.toIndex >= 0 ? Math.min(this.toIndex, resultSize) 
                            : resultSize);
            }
        }
        return result;
    }

    private void applyPropertyConstraints(Proposition prop, 
            Collection<Proposition> result) {
        assert prop != null : "prop cannot be null";
        assert result != null : "result cannot be null";

        boolean compatible = constraintsCheckCompatible(prop,this.constraints);
        if (compatible) {
            result.add(prop);
        }
    }

    private boolean constraintsCheckCompatible(Proposition proposition,
            PropertyConstraint[] constraints) {
        Logger logger = Util.logger();
        for (PropertyConstraint ccc : constraints) {
            String propName = ccc.getPropertyName();
            Value value = proposition.getProperty(propName);
            if (value != null) {
                ValueComparator vc = ccc.getValueComparator();
                if (logger.isLoggable(Level.FINER)) {
                    logger.log(Level.FINER,
                                "Proposition is {0}; Property is {1}; Value is {2}; Comparator: {3}",
                                new Object[] { proposition.getId(), propName,
                                        value, vc });
                }
                if (!vc.test(value.compare(ccc.getValue()))) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Traverses the specified link from a proposition to a collection of 
     * propositions.
     * 
     * @param proposition
     *            a {@link Proposition} at which to start the traversal.
     * @param forwardDerivations
     *            a {@link Map<Proposition,List<Proposition>>} of derived
     *            propositions.
     * @param backwardDerivations 
     *            a {@link Map<Proposition,List<Proposition>>} of derived
     *            propositions.
     * @param references
     *            a {@link Map<Proposition,Proposition>} of unique identifiers
     *            to {@link Proposition}s, used to resolve references.
     * @param knowledgeSource
     *            the {@link KnowledgeSource}.
     * @param cache
     *            a {@link Set<Proposition>} for convenience in checking if
     *            duplicate propositions are traversed to. It is cleared 
     *            in between calls to this method.
     * @return the {@link Collection<Proposition>} at the end of the traversal
     *            step. Not guaranteed to be modifiable.
     */
    abstract Collection<Proposition> traverse(Proposition proposition,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references,
            KnowledgeSource knowledgeSource, Set<Proposition> cache)
            throws KnowledgeSourceReadException;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comparator == null) ? 0 : comparator.hashCode());
		result = prime * result + Arrays.hashCode(constraints);
		result = prime * result + fromIndex;
		result = prime * result + ((propIdsAsSet == null) ? 0 : propIdsAsSet.hashCode());
		result = prime * result + toIndex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Link other = (Link) obj;
		if (comparator == null) {
			if (other.comparator != null)
				return false;
		} else if (!comparator.equals(other.comparator))
			return false;
		if (!Arrays.equals(constraints, other.constraints))
			return false;
		if (fromIndex != other.fromIndex)
			return false;
		if (propIdsAsSet == null) {
			if (other.propIdsAsSet != null)
				return false;
		} else if (!propIdsAsSet.equals(other.propIdsAsSet))
			return false;
		if (toIndex != other.toIndex)
			return false;
		return true;
	}
}
