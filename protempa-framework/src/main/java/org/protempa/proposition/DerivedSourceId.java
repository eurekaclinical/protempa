package org.protempa.proposition;

import org.apache.commons.lang.builder.ToStringBuilder;

public final class DerivedSourceId extends SourceId {

    private static final int hashCode = 31;

    private static class DerivedSourceIdContainer {
        private static final DerivedSourceId derivedSourceId =
                new DerivedSourceId();
    }

    public static DerivedSourceId getInstance() {
        return DerivedSourceIdContainer.derivedSourceId;
    }


    private DerivedSourceId() {
    }

    @Override
    String getId() {
        return "DERIVED";
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
