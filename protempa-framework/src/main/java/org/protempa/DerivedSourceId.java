package org.protempa;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DerivedSourceId implements SourceId {

    private static final long serialVersionUID = -7137216308980692834L;

    private static final int hashCode = 31;

    private static class DerivedSourceIdContainer {
        private static final DerivedSourceId derivedSourceId =
                new DerivedSourceId();
    }

    static DerivedSourceId getInstance() {
        return DerivedSourceIdContainer.derivedSourceId;
    }


    private DerivedSourceId() {
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
        return ToStringBuilder.reflectionToString(this);
    }

}
