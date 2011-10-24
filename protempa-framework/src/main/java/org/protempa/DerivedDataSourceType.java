package org.protempa;

import org.apache.commons.lang.builder.ToStringBuilder;

public final class DerivedDataSourceType extends DataSourceType {
    
    private static class DerivedDataSourceTypeContainer {
        private static DerivedDataSourceType INSTANCE = 
                new DerivedDataSourceType();
    }

    public static DerivedDataSourceType getInstance() {
        return DerivedDataSourceTypeContainer.INSTANCE;
    }

    private DerivedDataSourceType() {

    }

    @Override
    public boolean isDerived() {
        return true;
    }

    @Override
    public String getStringRepresentation() {
        return "Derived";
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
