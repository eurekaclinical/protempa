package org.protempa;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;

public final class DataSourceBackendId implements SourceId {

    private static final long serialVersionUID = -201656715932739725L;

    private static Map<String, DataSourceBackendId> cache =
            new HashMap<String, DataSourceBackendId>();

    private final String id;
    private int hashCode = -1;


    public static DataSourceBackendId getInstance(String id) {
        DataSourceBackendId result = cache.get(id);
        if (result == null) {
            result = new DataSourceBackendId(id);
            cache.put(id, result);
        }
        return result;
    }

    private DataSourceBackendId(String newId) {
        this.id = newId;
    }

    @Override
    public int hashCode() {
        if (hashCode >= 0) {
            return hashCode;
        } else {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            this.hashCode = result;
            return result;
        }
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
        DataSourceBackendId other = (DataSourceBackendId) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
