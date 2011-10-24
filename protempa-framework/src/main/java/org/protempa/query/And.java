package org.protempa.query;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A list of PROTEMPA ids that are chained together by boolean
 * <code>and</code>. An array of <code>And</code> instances is intended to
 * represent <code>And</code> instances chained together with boolean
 * <code>or</code>.
 *
 * @author Andrew Post
 */
public final class And<E> implements Serializable, Cloneable {
    private static final long serialVersionUID = -5176413026732863737L;
    private List<E> anded;
    private PropertyChangeSupport changes;

    public And() {
        this.anded = new ArrayList<E>();
        this.changes = new PropertyChangeSupport(this);
    }

    public And(E... anded) {
//        this.anded = anded.clone();
        this.anded = new ArrayList<E>();
        for (E e : anded) {
            this.anded.add(e);
        }
    }

    public void setAnded(List<E> anded) {
        if (anded == null)
            anded = new ArrayList<E>();
        List<E> old = this.anded;
        this.anded = new ArrayList<E>();
        for (E e : anded) {
            this.anded.add(e);
        }
        this.changes.firePropertyChange("anded", old, this.anded);
    }

    public List<E> getAnded() {
        return Collections.unmodifiableList(anded);
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
    public And<E> clone() {
        try {
            Object o = super.clone();
            @SuppressWarnings("unchecked") // needed because of type erasure in casts
			And<E> copy = (And<E>) o;
            copy.setAnded(this.anded);
            return copy;
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError("should not happen");
        }
    }
}
