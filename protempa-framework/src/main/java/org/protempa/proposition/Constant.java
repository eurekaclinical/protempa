package org.protempa.proposition;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.protempa.ProtempaException;

/**
 * A parameter with no temporal component.
 * 
 * @author Andrew Post
 */
public final class Constant extends AbstractProposition
        implements Serializable {

    private static final long serialVersionUID = 7205801414947324421L;

    /**
     * Creates a constant with an identifier <code>String</code>.
     *
     * @param id
     *            an identifier <code>String</code>.
     */
    public Constant(String id) {
        super(id);
    }

    protected Constant() {}
    
    @Override
    public boolean isEqual(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Constant)) {
            return false;
        }

        Constant p = (Constant) o;
        return super.isEqual(p);
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
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .toString();
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        writeAbstractProposition(s);
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        readAbstractProposition(s);
    }
}
