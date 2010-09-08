package org.protempa;

public class DatabaseDataSourceType implements DataSourceType {

    private final String id;

    public DatabaseDataSourceType(String id) {
        this.id = id;
    }

    @Override
    public boolean isDerived() {
        return false;
    }

    @Override
    public String getStringRepresentation() {
        return ("Database - " + this.id);
    }

}
