package org.protempa;

public class DerivedDataSourceType implements DataSourceType {

    @Override
    public boolean isDerived() {
        return true;
    }

    @Override
    public String getStringRepresentation() {
        return "Derived";
    }

}
