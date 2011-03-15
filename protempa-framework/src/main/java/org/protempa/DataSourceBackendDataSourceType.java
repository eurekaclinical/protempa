package org.protempa;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;

public final class DataSourceBackendDataSourceType extends DataSourceType {
    
    private static Map<String, DataSourceBackendDataSourceType> cache =
            new HashMap<String, DataSourceBackendDataSourceType>();

    public static DataSourceBackendDataSourceType getInstance(String id) {
        DataSourceBackendDataSourceType result = cache.get(id);
        if (result == null) {
            result = new DataSourceBackendDataSourceType(id);
            cache.put(id, result);
        }
        return result;
    }
    
    private final String id;

    private DataSourceBackendDataSourceType(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public boolean isDerived() {
        return false;
    }

    @Override
    public String getStringRepresentation() {
        return ("Data source backend - " + this.id);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataSourceBackendDataSourceType other = (DataSourceBackendDataSourceType) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
