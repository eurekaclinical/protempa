package org.protempa;

import org.apache.commons.lang.builder.ToStringBuilder;

public class DerivedDataSourceType implements DataSourceType {
    private static final long serialVersionUID = 8407624453239522038L;
    
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
