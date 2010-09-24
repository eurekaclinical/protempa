package org.protempa.proposition;


public class DerivedUniqueIdentifier implements LocalUniqueIdentifier {

    private static final long serialVersionUID = -7548400029812453768L;
    private String id;

    public DerivedUniqueIdentifier(String newId) {
        this.id = newId;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DerivedUniqueIdentifier other = (DerivedUniqueIdentifier) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "DerivedUniqueIdentifier [id=" + id + "]";
    }

    @Override
    public LocalUniqueIdentifier clone() {
        try {
            DerivedUniqueIdentifier clone = (DerivedUniqueIdentifier) super
                    .clone();
            // TODO:  Do we need to copy the ID?  Or is the cloned object 
            // a new object without a proper ID?
            // clone.id = this.id;
            return clone;
        } catch (CloneNotSupportedException cnse) {
            throw new AssertionError(cnse);
        }
    }

}
