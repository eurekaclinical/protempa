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

    @Override
    public Set<String> getAbstractedFrom() {
        HashSet<String> set = new HashSet<String>();
        set.add(this.leftHandProposition.getPropositionId());
        set.add(this.rightHandProposition.getPropositionId());
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
        this.changes.firePropertyChange("temporalOffset", old,
                this.temporalOffset);
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
        this.directChildren = abstractedFrom.toArray(new String[abstractedFrom
                .size()]);
        this.changes.firePropertyChange(DIRECT_CHILDREN_PROPERTY, old,
                this.directChildren);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString())
                .append("temporalOffset", this.temporalOffset)
                .append("abstractedFrom", getAbstractedFrom())
                .append("left temporal proposition", this.leftHandProposition)
                .append("relation", this.relation).toString();
    }

    @Override
    public GapFunction getGapFunction() {
        return gapFunction;
    }

    public void setGapFunction(GapFunction gapFunction) {
        if (gapFunction == null) {
            gapFunction = GapFunction.DEFAULT;
        }
        GapFunction old = this.gapFunction;
        this.gapFunction = gapFunction;
        this.changes.firePropertyChange("gapFunction", old, this.gapFunction);
    }

    public TemporalExtendedPropositionDefinition getLeftHandProposition() {
        return this.leftHandProposition;
    }

    public void setLeftHandProposition(
            TemporalExtendedPropositionDefinition proposition) {
        this.leftHandProposition = proposition;
    }

    public TemporalExtendedPropositionDefinition getRightHandProposition() {
        return rightHandProposition;
    }

    public void setRightHandProposition(
            TemporalExtendedPropositionDefinition rightHandProposition) {
        this.rightHandProposition = rightHandProposition;
    }
}
