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
package org.protempa;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import org.protempa.proposition.interval.Relation;

public class SequentialTemporalPatternDefinition extends AbstractAbstractionDefinition {

    private static final long serialVersionUID = 456697454379224533L;
    private static final SubsequentTemporalExtendedPropositionDefinition[] DEFAULT_REL = new SubsequentTemporalExtendedPropositionDefinition[0];
    private boolean solid;
    private TemporalPatternOffset temporalOffset;
    private boolean concatenable;
    private TemporalExtendedPropositionDefinition firstTemporalExtendedPropositionDefinition;
    private SubsequentTemporalExtendedPropositionDefinition[] subsequentTemporalExtendedPropositionDefinitions;
    private boolean allowPartialMatches;

    public static class SubsequentTemporalExtendedPropositionDefinition implements Serializable {
        private static final long serialVersionUID = 1;

        private final Relation relation;
        private final TemporalExtendedPropositionDefinition relatedTemporalExtendedPropositionDefinition;

        public SubsequentTemporalExtendedPropositionDefinition(Relation relation, 
                TemporalExtendedPropositionDefinition relatedTemporalExtendedPropositionDefinition) {
            this.relation = relation;
            this.relatedTemporalExtendedPropositionDefinition = relatedTemporalExtendedPropositionDefinition;
        }

        public Relation getRelation() {
            return relation;
        }

        public TemporalExtendedPropositionDefinition getRelatedTemporalExtendedPropositionDefinition() {
            return relatedTemporalExtendedPropositionDefinition;
        }
    }

    public SequentialTemporalPatternDefinition(String id) {
        super(id);
        this.concatenable = true;
        this.solid = true;
        this.subsequentTemporalExtendedPropositionDefinitions = DEFAULT_REL;
    }

    public TemporalExtendedPropositionDefinition getFirstTemporalExtendedPropositionDefinition() {
        return firstTemporalExtendedPropositionDefinition;
    }

    public void setFirstTemporalExtendedPropositionDefinition(TemporalExtendedPropositionDefinition firstTemporalExtendedPropositionDefinition) {
        this.firstTemporalExtendedPropositionDefinition = firstTemporalExtendedPropositionDefinition;
    }

    public SubsequentTemporalExtendedPropositionDefinition[] getSubsequentTemporalExtendedPropositionDefinitions() {
        return subsequentTemporalExtendedPropositionDefinitions;
    }

    public void setSubsequentTemporalExtendedPropositionDefinitions(
            SubsequentTemporalExtendedPropositionDefinition[] subsequentTemporalExtendedPropositionDefinitions) {
        if (subsequentTemporalExtendedPropositionDefinitions == null) {
            this.subsequentTemporalExtendedPropositionDefinitions = DEFAULT_REL;
        } else {
            this.subsequentTemporalExtendedPropositionDefinitions = 
                    subsequentTemporalExtendedPropositionDefinitions.clone();
        }
    }

    @Override
    public Set<String> getAbstractedFrom() {
        HashSet<String> set = new HashSet<String>();
        if (this.firstTemporalExtendedPropositionDefinition != null) {
            set.add(
                    this.firstTemporalExtendedPropositionDefinition
                    .getPropositionId());
        }
        for (SubsequentTemporalExtendedPropositionDefinition rel : 
                this.subsequentTemporalExtendedPropositionDefinitions) {
            set.add(
                    rel.getRelatedTemporalExtendedPropositionDefinition()
                    .getPropositionId());
        }
        return set;
    }

    @Override
    public boolean isConcatenable() {
        return this.concatenable;
    }

    /**
     * Sets whether this type of high-level abstraction is concatenable.
     *
     * @param concatenable <code>true</code> if concatenable, <code>false</code>
     * if not.
     */
    public void setConcatenable(boolean concatenable) {
        this.concatenable = concatenable;
    }

    /**
     * Returns whether intervals of this type are solid, i.e., never hold over
     * properly overlapping intervals. By default, pair abstraction intervals
     * are solid.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    @Override
    public boolean isSolid() {
        return this.solid;
    }

    /**
     * Sets whether intervals of this type are solid, i.e., never hold over
     * properly overlapping intervals.
     *
     * @param solid <code>true</code> or <code>false</code>.
     */
    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    /**
     * Returns whether an interval will be generated if only the first, first
     * and second, etc. propositions are present.
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean isAllowPartialMatches() {
        return allowPartialMatches;
    }

    /**
     * Sets whether to match if only the first, first and second, etc. 
     * propositions are present.
     * 
     * @param allowPartialMatches <code>true</code> or <code>false</code>.
     */
    public void setAllowPartialMatches(boolean allowPartialMatches) {
        this.allowPartialMatches = allowPartialMatches;
    }
    
    @Override
    public void accept(PropositionDefinitionVisitor visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException("processor cannot be null.");
        }
        visitor.visit(this);

    }

    @Override
    public void acceptChecked(PropositionDefinitionCheckedVisitor visitor)
            throws ProtempaException {
        if (visitor == null) {
            throw new IllegalArgumentException("processor cannot be null.");
        }
        visitor.visit(this);

    }

    public TemporalPatternOffset getTemporalOffset() {
        return temporalOffset;
    }

    public void setTemporalOffset(TemporalPatternOffset temporalOffset) {
        TemporalPatternOffset old = temporalOffset;
        this.temporalOffset = temporalOffset;
        if (this.changes != null) {
            this.changes.firePropertyChange("temporalOffset", old,
                    this.temporalOffset);
        }
    }

    @Override
    public void reset() {
        super.reset();
        this.concatenable = true;
        this.solid = true;
        this.subsequentTemporalExtendedPropositionDefinitions = DEFAULT_REL;
        this.temporalOffset = null;
        this.firstTemporalExtendedPropositionDefinition = null;
    }

    @Override
    protected void recalculateChildren() {
        String[] old = this.children;
        Set<String> abstractedFrom = getAbstractedFrom();
        String[] inverseIsA = getInverseIsA();
        if (inverseIsA != null) {
            for (String propId : inverseIsA) {
                abstractedFrom.add(propId);
            }
        }
        this.children = 
                abstractedFrom.toArray(new String[abstractedFrom.size()]);
        if (this.changes != null) {
            this.changes.firePropertyChange(CHILDREN_PROPERTY, old,
                    this.children);
        }
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }
}
