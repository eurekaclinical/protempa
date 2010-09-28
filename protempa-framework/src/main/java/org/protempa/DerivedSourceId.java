package org.protempa;

public class DerivedSourceId implements SourceId {

    private static final int hashCode = 31;

    DerivedSourceId() {
    }

    @Override
    public int hashCode() {
        return hashCode;
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
        return true;
    }

    @Override
    public String toString() {
        return "DerivedSourceId []";
    }

}
