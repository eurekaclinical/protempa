package org.protempa;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.protempa.proposition.value.Unit;

import org.arp.javautil.graph.Weight;

/**
 * Defines external acts upon an entity such as a patient.
 * 
 * @author Andrew Post
 * 
 */
public final class EventDefinition extends AbstractPropositionDefinition
        implements TemporalPropositionDefinition {

    private static final long serialVersionUID = 5251628049452634144L;

    public static class HasPartOffset implements Serializable {

        private static final long serialVersionUID = -7732861608412376178L;
        private String eventDefinitionId;
        private IntervalSide side;
        private Weight offset;
        private Unit offsetUnits;

        public HasPartOffset(String eventDefinitionId, IntervalSide side,
                Weight offset, Unit offsetUnits) {
            this.eventDefinitionId = eventDefinitionId;
            this.side = side;
            this.offset = offset;
            this.offsetUnits = offsetUnits;
        }

        /**
         * @return the event
         */
        public String getEventDefinitionId() {
            return eventDefinitionId;
        }

        /**
         * @param event
         *            the event to set
         */
        public void setEventDefinitionId(String eventDefinitionId) {
            this.eventDefinitionId = eventDefinitionId;
        }

        /**
         * @return the offset
         */
        public Weight getOffset() {
            return offset;
        }

        /**
         * @param offset
         *            the offset to set
         */
        public void setOffset(Weight offset) {
            this.offset = offset;
        }

        /**
         * @return the offsetUnits
         */
        public Unit getOffsetUnits() {
            return offsetUnits;
        }

        /**
         * @param offsetUnits
         *            the offsetUnits to set
         */
        public void setOffsetUnits(Unit offsetUnits) {
            this.offsetUnits = offsetUnits;
        }

        /**
         * @return the side
         */
        public IntervalSide getSide() {
            return side;
        }

        /**
         * @param side
         *            the side to set
         */
        public void setSide(IntervalSide side) {
            this.side = side;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
    /**
     * A <code>Set</code> of <code>HasPartOffsets</code>.
     */
    private final Set<HasPartOffset> hasPart;

    public EventDefinition(KnowledgeBase kb, String id) {
        super(kb, id);

        this.hasPart = new HashSet<HasPartOffset>();
        kb.addEventDefinition(this);
    }

    /**
     * @return a <code>Set</code> of <code>HasPartOffsets</code>
     */
    public Set<HasPartOffset> getHasPart() {
        return Collections.unmodifiableSet(hasPart);
    }

    public boolean addHasPart(HasPartOffset offsets) {
        if (offsets != null) {
            boolean result = this.hasPart.add(offsets);
            if (result) {
                recalculateDirectChildren();
            }
            return result;
        } else {
            return false;
        }
    }

    public boolean addAllHasPart(Collection<HasPartOffset> offsets) {
        if (offsets != null) {
            this.directChildren = null;
            boolean result = this.hasPart.addAll(offsets);
            if (result) {
                recalculateDirectChildren();
            }
            return result;
        } else {
            return false;
        }
    }

    public boolean removeHasPart(HasPartOffset offsets) {
        boolean result = this.hasPart.remove(offsets);
        if (result) {
            recalculateDirectChildren();
        }
        return result;
    }

    @Override
    public void accept(PropositionDefinitionVisitor processor) {
        if (processor == null) {
            throw new IllegalArgumentException("processor cannot be null.");
        }
        processor.visit(this);
    }

    @Override
    public void acceptChecked(PropositionDefinitionCheckedVisitor processor)
            throws ProtempaException {
        if (processor == null) {
            throw new IllegalArgumentException("processor cannot be null.");
        }
        processor.visit(this);
    }

    /**
     * By definition, events are not concatenable.
     *
     * @return <code>false</code>.
     * @see org.protempa.PropositionDefinition#isConcatenable()
     */
    @Override
    public boolean isConcatenable() {
        return false;
    }

    /**
     * By definition, events can overlap.
     *
     * @return <code>false</code>.
     * @see org.protempa.PropositionDefinition#isSolid()
     */
    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    protected void recalculateDirectChildren() {
        String[] old = this.directChildren;
        Set<String> c = new HashSet<String>();
        if (this.hasPart != null) {
            for (HasPartOffset hpo : this.hasPart) {
                c.add(hpo.getEventDefinitionId());
            }
        }
        String[] inverseIsA = getInverseIsA();
        if (inverseIsA != null) {
            for (String propId : inverseIsA) {
                c.add(propId);
            }
        }
        this.directChildren = c.toArray(new String[c.size()]);
        this.changes.firePropertyChange(DIRECT_CHILDREN_PROPERTY, old,
                this.directChildren);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("hasPart", this.hasPart)
                .toString();
    }


}
