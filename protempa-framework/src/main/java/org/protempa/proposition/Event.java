package org.protempa.proposition;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.protempa.ProtempaException;

/**
 * An external volitional action or process, such as the administration of a
 * drug (as opposed to a measurable datum, such as temperature). Events cannot
 * be abstracted from other data.
 * 
 * @author Andrew Post
 */
public final class Event extends TemporalProposition implements Serializable {

    private static final long serialVersionUID = -47155268578773061L;

    /**
     * Creates an event with an id and no attribute id.
     * 
     * @param id
     *            an identification <code>String</code> for this event.
     * @param uniqueId
     *            a <code>UniqueId</code> that uniquely identifies this event.
     */
    public Event(String id, UniqueId uniqueId) {
        super(id, uniqueId);
    }

    @Override
    public void setInterval(Interval interval) {
        super.setInterval(interval);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.protempa.proposition.TemporalProposition#isEqual(java.lang.Object)
     */
    @Override
    public boolean isEqual(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }

        Event a = (Event) o;
        return super.isEqual(a);
    }

    @Override
    public void accept(PropositionVisitor propositionVisitor) {
        propositionVisitor.visit(this);
    }

    @Override
    public void acceptChecked(
            PropositionCheckedVisitor propositionCheckedVisitor)
            throws ProtempaException {
        propositionCheckedVisitor.visit(this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString())
                .toString();
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        writeAbstractProposition(s);
        writeTemporalProposition(s);
    }

    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        readAbstractProposition(s);
        readTemporalProposition(s);
    }
}
