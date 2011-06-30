package org.protempa;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.proposition.Relation;

public class PairDefinition extends AbstractAbstractionDefinition {

    private static final long serialVersionUID = 456697454379224533L;
    private boolean solid;
    private Relation relation;
    private GapFunction gapFunction;
    private Offsets temporalOffset;
    private boolean concatenable;
    private TemporalExtendedPropositionDefinition rightHandProposition;
    private TemporalExtendedPropositionDefinition leftHandProposition;
    private boolean secondRequired;

    public PairDefinition(KnowledgeBase kb, String id) {
        super(kb, id);
        this.concatenable = true;
        this.solid = true;
    }

    public Relation getRelation() {
        return relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }
    
    public boolean isSecondRequired() {
        return this.secondRequired;
    }
    
    public void setSecondRequired(boolean secondRequired) {
        this.secondRequired = secondRequired;
    }

    @Override
    public Set<String> getAbstractedFrom() {
        HashSet<String> set = new HashSet<String>();
        if (this.leftHandProposition != null) {
            set.add(this.leftHandProposition.getPropositionId());
        }
        if (this.rightHandProposition != null) {
            set.add(this.rightHandProposition.getPropositionId());
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
     * @param concatenable
     *            <code>true</code> if concatenable, <code>false</code> if not.
     */
    public void setConcatenable(boolean concatenable) {
        this.concatenable = concatenable;
    }

    @Override
    public boolean isSolid() {
        return this.solid;
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

    public Offsets getTemporalOffset() {
        return temporalOffset;
    }

    public void setTemporalOffset(Offsets temporalOffset) {
        Offsets old = temporalOffset;
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
        this.relation = null;
        this.temporalOffset = null;
        this.rightHandProposition = null;
        this.leftHandProposition = null;
    }

    @Override
    protected void recalculateDirectChildren() {
        String[] old = this.directChildren;
        Set<String> abstractedFrom = getAbstractedFrom();
        String[] inverseIsA = getInverseIsA();
        if (inverseIsA != null) {
            for (String propId : inverseIsA) {
                abstractedFrom.add(propId);
            }
        }
        this.directChildren = abstractedFrom.toArray(new String[abstractedFrom.size()]);
        if (this.changes != null) {
            this.changes.firePropertyChange(DIRECT_CHILDREN_PROPERTY, old,
                    this.directChildren);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("temporalOffset", this.temporalOffset).append("abstractedFrom", getAbstractedFrom()).append("left temporal proposition", this.leftHandProposition).append("right temporal proposition",
                this.rightHandProposition).append("relation", this.relation).toString();
    }

    @Override
    public GapFunction getGapFunction() {
        return gapFunction;
    }

    @Override
    public void setGapFunction(GapFunction gapFunction) {
        if (gapFunction == null) {
            gapFunction = GapFunction.DEFAULT;
        }
        GapFunction old = this.gapFunction;
        this.gapFunction = gapFunction;
        if (this.changes != null) {
            this.changes.firePropertyChange("gapFunction", old, this.gapFunction);
        }
    }

    public TemporalExtendedPropositionDefinition getLeftHandProposition() {
        return this.leftHandProposition;
    }

    public void setLeftHandProposition(
            TemporalExtendedPropositionDefinition proposition) {
        TemporalExtendedPropositionDefinition old = this.leftHandProposition;
        this.leftHandProposition = proposition;
        recalculateDirectChildren();
        if (this.changes != null) {
            this.changes.firePropertyChange("leftHandProposition", old,
                    this.leftHandProposition);
        }
    }

    public TemporalExtendedPropositionDefinition getRightHandProposition() {
        return rightHandProposition;
    }

    public void setRightHandProposition(
            TemporalExtendedPropositionDefinition rightHandProposition) {
        TemporalExtendedPropositionDefinition old = this.rightHandProposition;
        this.rightHandProposition = rightHandProposition;
        recalculateDirectChildren();
        if (this.changes != null) {
            this.changes.firePropertyChange("rightHandProposition", old,
                    this.rightHandProposition);
        }
    }
}
