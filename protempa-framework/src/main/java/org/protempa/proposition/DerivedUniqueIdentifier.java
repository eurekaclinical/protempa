package org.protempa.proposition;

import org.apache.commons.lang.builder.ToStringBuilder;

public final class DerivedUniqueIdentifier implements LocalUniqueIdentifier {

    private static final long serialVersionUID = -7548400029812453768L;
    private final String id;
    private transient volatile int hashCode;

    public DerivedUniqueIdentifier(String newId) {
        if (newId == null) {
            throw new IllegalArgumentException("newId cannot be null");
        }
        this.id = newId;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DerivedUniqueIdentifier other = (DerivedUniqueIdentifier) obj;
        if (!this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int hash = 3;
            hash = 53 * hash + this.id.hashCode();
            this.hashCode = hash;
        }
        return this.hashCode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public LocalUniqueIdentifier clone() {
        try {
            DerivedUniqueIdentifier clone = (DerivedUniqueIdentifier) super.clone();
            // TODO:  Do we need to copy the ID?  Or is the cloned object 
            // a new object without a proper ID?
            // clone.id = this.id;
            return clone;
        } catch (CloneNotSupportedException cnse) {
            throw new AssertionError(cnse);
        }
    }
}
