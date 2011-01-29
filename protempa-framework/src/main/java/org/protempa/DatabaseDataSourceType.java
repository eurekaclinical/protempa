package org.protempa;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import org.apache.commons.lang.builder.ToStringBuilder;

public class DatabaseDataSourceType implements DataSourceType {

    private static final long serialVersionUID = 1408930133143229497L;
    
    private static Map<String, WeakReference<DatabaseDataSourceType>> cache
            = new WeakHashMap<String, WeakReference<DatabaseDataSourceType>>();

    public static DatabaseDataSourceType getInstance(String id) {
        WeakReference<DatabaseDataSourceType> wrResult = cache.get(id);
        if (wrResult != null) {
            return wrResult.get();
        } else {
            DatabaseDataSourceType result = new DatabaseDataSourceType(id);
            cache.put(id, new WeakReference<DatabaseDataSourceType>(result));
            return result;
        }
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
