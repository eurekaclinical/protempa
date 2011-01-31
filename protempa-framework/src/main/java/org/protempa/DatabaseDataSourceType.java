package org.protempa;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;

public class DatabaseDataSourceType implements DataSourceType {

    private static final long serialVersionUID = 1408930133143229497L;
    
    private static Map<String, DatabaseDataSourceType> cache =
            new HashMap<String, DatabaseDataSourceType>();

    public static DatabaseDataSourceType getInstance(String id) {
        DatabaseDataSourceType result = cache.get(id);
        if (result == null) {
            result = new DatabaseDataSourceType(id);
            cache.put(id, result);
        }
        return result;
    }
    
    private final String id;

    private DatabaseDataSourceType(String id) {
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
