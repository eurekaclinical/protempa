package org.protempa;

public abstract class DataSourceType {

    DataSourceType() {}

    public abstract boolean isDerived();

    public abstract String getStringRepresentation();
}
