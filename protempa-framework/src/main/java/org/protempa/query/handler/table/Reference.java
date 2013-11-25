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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.ProtempaUtil;
import org.protempa.ReferenceDefinition;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

/**
 * Specifies reference traversals from one proposition to other propositions.
 *
 * @author Andrew Post
 */
public final class Reference extends Link {

    private final String[] referenceNames;

    /**
     * Creates an instance that specifies traversals using the specified
     * references from a proposition to all referred-to propositions.
     *
     * @param referenceNames a {@link String[]} of reference names. An empty *
     * or <code>null</code> array specifies that all references should be
     * traversed.
     */
    public Reference(String[] referenceNames) {
        this(referenceNames, null);
    }

    /**
     * Creates an instance that specifies traversals using the specified
     * references from a proposition only to propositions with the given ids.
     *
     * @param referenceNames a {@link String[]} of reference names. An empty *
     * or <code>null</code> array specifies that all references should be
     * traversed.
     * @param propositionIds a {@link String[]} of proposition ids. An empty *
     * or <code>null</code> array specifies that all propositions on the
     * right-hand-side of the reference should be used.
     */
    public Reference(String[] referenceNames, String[] propositionIds) {
        this(referenceNames, propositionIds, null);
    }

    /**
     * Creates an instance that specifies traversals using the specified
     * references from a proposition only to propositions with the given ids and
     * that satisfy the specified constraints.
     *
     * @param referenceNames a {@link String[]} of reference names. An empty *
     * or <code>null</code> array specifies that all references should be
     * traversed.
     * @param propositionIds a {@link String[]} of proposition ids. An empty *
     * or <code>null</code> array specifies that all propositions on the
     * right-hand-side of a reference should be used.
     * @param constraints a {@link PropertyConstraint[]} of property constraints
     * on the propositions on the right-hand-side of a reference. An empty or
     * null array specifies that no constraints should be applied.
     */
    public Reference(String[] referenceNames, String[] propositionIds,
            PropertyConstraint[] constraints) {
        this(referenceNames, propositionIds, constraints, null, -1, -1);
    }

    /**
     * Creates an instance that specifies traversals using the specified
     * references from a proposition only to propositions with the given ids and
     * that satisfy the specified constraints. It also allows specifying the
     * first, second, or other proposition on the right-hand-side of the
     * reference.
     *
     * @param referenceNames a {@link String[]} of reference names. An empty *
     * or <code>null</code> array specifies that all references should be
     * traversed.
     * @param propositionIds a {@link String[]} of proposition ids. An empty *
     * or <code>null</code> array specifies that all propositions on the
     * right-hand-side of a reference should be used.
     * @param constraints a {@link PropertyConstraint[]} of property constraints
     * on the propositions on the right-hand-side of a reference. An empty      * or <code>null</code> array specifies that no constraints should be
     * applied.
     * @param comparator a comparison function representing the total order to
     * apply to propositions on the right-hand-side of the reference when using
     * the <code>index</code> parameter. If <code>null</code> and an
     * <code>index</code> is specified, the total order is unspecified.
     * @param index the position of the proposition on the right-hand-side of
     * the reference to traverse to, using the total order specified by the
     * <code>comparator</code> argument. An index of <code>-1</code> means that
     * all propositions on the right-hand-side will be traversed to.
     */
    public Reference(String[] referenceNames, String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int index) {
        this(referenceNames, propositionIds, constraints, comparator, index,
                index >= 0 ? index + 1 : -1);
    }

    /**
     * Creates an instance that specifies traversals using the specified
     * references from a proposition only to propositions with the given ids and
     * that satisfy the specified constraints. It also allows specifying a range
     * of propositions on the right-hand-side of the reference to be traversed
     * to (first and second, first through third, etc.).
     *
     * @param referenceNames a {@link String[]} of reference names. An empty *
     * or <code>null</code> array specifies that all references should be
     * traversed.
     * @param propositionIds a {@link String[]} of proposition ids. An empty *
     * or <code>null</code> array specifies that all propositions on the
     * right-hand-side of a reference should be used.
     * @param constraints a {@link PropertyConstraint[]} of property constraints
     * on the propositions on the right-hand-side of a reference. An empty      * or <code>null</code> array specifies that no constraints should be
     * applied.
     * @param comparator a comparison function representing the total order to
     * apply to propositions on the right-hand-side of the reference when using
     * the <code>index</code> parameter. If <code>null</code> and an
     * <code>index</code> is specified, the total order is unspecified.
     * @param fromIndex the lower-bound on the positions of the propositions on
     * the right-hand-side of the reference to traverse to, inclusive, using the
     * total order specified by the <code>comparator</code> argument. An index
     * of <code>-1</code> means that all propositions on the right-hand-side
     * will be traversed to.
     * @param toIndex the upper-bound on the positions of the propositions on
     * the right-hand-side of the reference to traverse to, exclusive, using the
     * total order specified by the <code>comparator</code> argument. An index
     * of <code>-1</code> means that all propositions on the right-hand-side
     * will be traversed to.
     */
    public Reference(String[] referenceNames, String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int fromIndex, int toIndex) {
        super(propositionIds, constraints, comparator, fromIndex, toIndex);
        if (referenceNames != null) {
            ProtempaUtil.checkArrayForNullElement(referenceNames,
                    "referenceNames");
            String[] refNames = referenceNames.clone();
            for (int i = 0; i < refNames.length; i++) {
                refNames[i] = refNames[i].intern();
            }
            this.referenceNames = refNames;
        } else {
            this.referenceNames = ArrayUtils.EMPTY_STRING_ARRAY;
        }
    }

    /**
     * Creates an instance that specifies traversals using the specified
     * reference from a proposition to all referred-to propositions.
     *
     * @param referenceName a reference name {@link String} A value of
     * <code>null</code> specifies that all references should be traversed.
     */
    public Reference(String referenceName) {
        this(referenceName, null);
    }

    /**
     * Creates an instance that specifies traversals using the specified
     * reference from a proposition only to propositions with the given ids.
     *
     * @param referenceName a reference name {@link String} A value of
     * <code>null</code> specifies that all references should be traversed.
     * @param propositionIds a {@link String[]} of proposition ids. An empty *
     * or <code>null</code> array specifies that all propositions on the
     * right-hand-side of the reference should be used.
     */
    public Reference(String referenceName, String[] propositionIds) {
        this(referenceName, propositionIds, null);
    }

    /**
     * Creates an instance that specifies traversals using the specified
     * references from a proposition only to propositions with the given ids and
     * that satisfy the specified property constraints.
     *
     * @param referenceName a reference name {@link String} A value of
     * <code>null</code> specifies that all references should be traversed.
     * @param propositionIds a {@link String[]} of proposition ids. An empty *
     * or <code>null</code> array specifies that all propositions on the
     * right-hand-side of a reference should be used.
     * @param constraints a {@link PropertyConstraint[]} of property constraints
     * on the propositions on the right-hand-side of a reference. An empty or
     * null array specifies that no constraints should be applied.
     */
    public Reference(String referenceName, String[] propositionIds,
            PropertyConstraint[] constraints) {
        this(referenceName, propositionIds, constraints, null, -1, -1);
    }

    /**
     * Creates an instance that specifies traversals using the specified
     * reference from a proposition only to propositions with the given ids and
     * that satisfy the specified property constraints. It also allows
     * specifying the first, second, or other proposition on the right-hand-side
     * of the reference.
     *
     * @param referenceName a reference name {@link String} A value of
     * <code>null</code> specifies that all references should be traversed.
     * @param propositionIds a {@link String[]} of proposition ids. An empty *
     * or <code>null</code> array specifies that all propositions on the
     * right-hand-side of a reference should be used.
     * @param constraints a {@link PropertyConstraint[]} of property constraints
     * on the propositions on the right-hand-side of a reference. An empty      * or <code>null</code> array specifies that no constraints should be
     * applied.
     * @param comparator a comparison function representing the total order to
     * apply to propositions on the right-hand-side of the reference when using
     * the <code>index</code> parameter. If <code>null</code> and an
     * <code>index</code> is specified, the total order is unspecified.
     * @param index the position of the proposition on the right-hand-side of
     * the reference to traverse to, using the total order specified by the
     * <code>comparator</code> argument. An index of <code>-1</code> means that
     * all propositions on the right-hand-side will be traversed to.
     */
    public Reference(String referenceName, String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int index) {
        this(referenceName, propositionIds, constraints, comparator, index,
                index >= 0 ? index + 1 : -1);
    }

    /**
     * Creates an instance that specifies traversals using the specified
     * reference from a proposition only to propositions with the given ids and
     * that satisfy the specified constraints. It also allows specifying a range
     * of propositions on the right-hand-side of the reference to be traversed
     * to (first and second, first through third, etc.).
     *
     * @param referenceName a reference name {@link String} A value of
     * <code>null</code> specifies that all references should be traversed.
     * @param propositionIds a {@link String[]} of proposition ids. An empty *
     * or <code>null</code> array specifies that all propositions on the
     * right-hand-side of a reference should be used.
     * @param constraints a {@link PropertyConstraint[]} of property constraints
     * on the propositions on the right-hand-side of a reference. An empty      * or <code>null</code> array specifies that no constraints should be
     * applied.
     * @param comparator a comparison function representing the total order to
     * apply to propositions on the right-hand-side of the reference when using
     * the <code>index</code> parameter. If <code>null</code> and an
     * <code>index</code> is specified, the total order is unspecified.
     * @param fromIndex the lower-bound on the positions of the propositions on
     * the right-hand-side of the reference to traverse to, inclusive, using the
     * ordering specified by the <code>comparator</code> argument. An index      * of <code>-1</code> means that all propositions on the
     * right-hand-side will be traversed to.
     * @param toIndex the upper-bound on the positions of the propositions on
     * the right-hand-side of the reference to traverse to, exclusive, using the
     * ordering specified by the <code>comparator</code> argument. An index      * of <code>-1</code> means that all propositions on the
     * right-hand-side will be traversed to.
     */
    public Reference(String referenceName, String[] propositionIds,
            PropertyConstraint[] constraints,
            Comparator<Proposition> comparator, int fromIndex, int toIndex) {
        super(propositionIds, constraints, comparator, fromIndex, toIndex);
        if (referenceName == null) {
            throw new IllegalArgumentException("referenceName cannot be null");
        }
        this.referenceNames = new String[]{referenceName.intern()};
    }

    /**
     * Gets the names of the reference(s) to be traversed.
     *
     * @return a {@link String[]} of reference names.
     */
    public String[] getReferenceNames() {
        if (this.referenceNames != null) {
            return this.referenceNames.clone();
        } else {
            return this.referenceNames;
        }
    }

    @Override
    String headerFragment() {
        String references = StringUtils.join(this.referenceNames, ',');
        return createHeaderFragment(references);
    }

    /**
     * Traverses a reference.
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
            KnowledgeSource knowledgeSource, Set<Proposition> cache) {
        List<Proposition> props = new ArrayList<>();
        String[] refNames =
                this.referenceNames.length > 0
                ? this.referenceNames
                : proposition.getReferenceNames();
        for (String referenceName : refNames) {
            List<UniqueId> uids = proposition.getReferences(
                    referenceName);
            for (UniqueId uid : uids) {
                Proposition prop = references.get(uid);
                if (prop != null && cache.add(prop) && isMatch(prop)) {
                    props.add(prop);
                }
            }
        }

        return createResults(props);
    }

    @Override
    public String[] getInferredPropositionIds(KnowledgeSource knowledgeSource,
            String[] inPropIds) throws KnowledgeSourceReadException {
        String[] explicitPropIds = getPropositionIds();
        if (explicitPropIds.length > 0) {
            return explicitPropIds.clone();
        } else {
            Set<String> result = new HashSet<>();
            for (String propId : inPropIds) {
                PropositionDefinition propDef =
                        knowledgeSource.readPropositionDefinition(propId);
                if (propDef == null) {
                    throw new IllegalArgumentException("Invalid propId: " + 
                            propId);
                }
                for (String refName : this.referenceNames) {
                    ReferenceDefinition refDef =
                            propDef.referenceDefinition(refName);
                    if (refDef != null) {
                        org.arp.javautil.arrays.Arrays.addAll(result,
                                refDef.getPropositionIds());
                    }
                }
            }
            return result.toArray(new String[result.size()]);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Arrays.hashCode(referenceNames);
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
        Reference other = (Reference) obj;
        if (!Arrays.equals(referenceNames, other.referenceNames)) {
            return false;
        }
        return true;
    }
}
