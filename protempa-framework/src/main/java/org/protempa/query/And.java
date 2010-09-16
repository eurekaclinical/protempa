package org.protempa.query;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

/**
 * A list of PROTEMPA ids that are chained together by boolean
 * <code>and</code>. An array of <code>And</code> instances is intended to
 * represent <code>And</code> instances chained together with boolean
 * <code>or</code>.
 *
 * @author Andrew Post
 */
public final class And implements Serializable, Cloneable {
    private static final long serialVersionUID = -5176413026732863737L;
    private String[] anded;
    private PropertyChangeSupport changes;

    public And() {
        this.anded = new String[0];
        this.changes = new PropertyChangeSupport(this);
    }

    public And(String... anded) {
        this.anded = anded.clone();
    }

    public void setAnded(String[] anded) {
        if (anded == null)
            anded = new String[0];
        String[] old = this.anded;
        this.anded = anded.clone();
        this.changes.firePropertyChange("anded", old, this.anded);
    }

    public String[] getAnded() {
        return this.anded.clone();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.changes.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.changes.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        this.changes.addPropertyChangeListener(propertyName, listener);
    }
    
    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        this.changes.removePropertyChangeListener(propertyName, listener);
    }

    @Override
    public And clone() {
        try {
            Object o = super.clone();
            And copy = (And) o;
            copy.setAnded(this.anded);
            return copy;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError("should not happen");
        }
    }
}
