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
    private transient PropertyChangeSupport changes;

    public And() {
        this.anded = new ArrayList<>();
        this.changes = new PropertyChangeSupport(this);
    }
    
    private Object readResolve() {
        changes = new PropertyChangeSupport(this);
        return this;
      }

    public And(E... anded) {
//        this.anded = anded.clone();
        this.anded = new ArrayList<>();
        for (E e : anded) {
            this.anded.add(e);
        }
    }

    public void setAnded(List<E> anded) {
        if (anded == null)
            anded = new ArrayList<>();
        List<E> old = this.anded;
        this.anded = new ArrayList<>();
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

	@Override
	public int hashCode() {
		return (anded == null) ? 0 : anded.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		And<?> other = (And<?>) obj;
		if (anded == null) {
			if (other.anded != null)
				return false;
		} else if (!anded.equals(other.anded))
			return false;
		return true;
	}
}
