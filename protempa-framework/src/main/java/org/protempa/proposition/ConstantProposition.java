package org.protempa.proposition;

import java.beans.PropertyChangeListener;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.protempa.ProtempaException;

/**
 * A parameter with no temporal component.
 * 
 * @author Andrew Post
 */
public final class ConstantProposition extends AbstractProposition {

    private static final long serialVersionUID = 7205801414947324421L;

    /**
     * Creates a constant with an identifier <code>String</code>.
     *
     * @param id
     *            an identifier <code>String</code>.
     */
    public ConstantProposition(String id) {
        super(id);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        this.changes.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        this.changes.removePropertyChangeListener(l);
    }
    
    @Override
    public boolean isEqual(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConstantProposition)) {
            return false;
        }

        ConstantProposition p = (ConstantProposition) o;
        return super.isEqual(p);
    }

    public void accept(PropositionVisitor propositionVisitor) {
        throw new UnsupportedOperationException("Unimplemented");
    }

    public void acceptChecked(PropositionCheckedVisitor propositionCheckedVisitor) throws ProtempaException {
        propositionCheckedVisitor.visit(this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .toString();
    }
}
