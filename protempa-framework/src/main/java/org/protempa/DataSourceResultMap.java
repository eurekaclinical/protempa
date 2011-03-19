package org.protempa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The map implementation that is returned by the query methods. It is
 * backed by maps that are returned by the configured data source backends.
 * The implementation is immutable. Write methods will throw
 * {@link UnsupportedOperationException}.
 *
 * @see #getEvents(java.util.Set, java.util.Set, org.protempa.dsb.filter.Filter, org.protempa.QuerySession)
 * @see #getConstantPropositions(java.util.Set, java.util.Set, org.protempa.dsb.filter.Filter, org.protempa.QuerySession)
 * @see #getPrimitiveParameters(java.util.Set, java.util.Set, org.protempa.dsb.filter.Filter, org.protempa.QuerySession)
 */
public class DataSourceResultMap<P> implements Map<String, List<P>> {

    public class DataSourceResultMapEntry implements Map.Entry<String, List<P>> {

        private volatile int hashCode = 0;
        private List<Map.Entry<String, List<P>>> entries =
                new ArrayList<Map.Entry<String, List<P>>>();

        public DataSourceResultMapEntry(Map.Entry<String, List<P>> entry) {
            this.entries.add(entry);
        }

        public void add(Map.Entry<String, List<P>> entry) {
            this.entries.add(entry);
            this.hashCode = 0;
        }

        @Override
        public String getKey() {
            return this.entries.get(0).getKey();
        }

        @Override
        public List<P> getValue() {
            if (this.entries.size() == 1) {
                return this.entries.get(0).getValue();
            } else {
                List<P> values = new ArrayList<P>();
                for (Map.Entry<String, List<P>> entry : this.entries) {
                    values.addAll(entry.getValue());
                }
                return values;
            }
        }

        @Override
        public List<P> setValue(List<P> value) {
            throw new UnsupportedOperationException("This map is immutable");
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Map.Entry<?, ?>)) {
                return false;
            }
            final Map.Entry<?, ?> other = (Map.Entry<?, ?>) obj;
            String key = getKey();
            Object otherKey = other.getKey();
            if (key != otherKey && (key == null || !key.equals(otherKey))) {
                return false;
            }
            List<P> value = getValue();
            Object otherValue = other.getValue();
            if (value != otherValue && (value == null || !value.equals(otherValue))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            if (this.hashCode == 0) {
                int hash = 3;
                String key = getKey();
                List<P> value = getValue();
                hash = 41 * hash + (key != null ? key.hashCode() : 0);
                hash = 41 * hash + (value != null ? value.hashCode() : 0);
                this.hashCode = hash;
            }
            return this.hashCode;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
    private List<Map<String, List<P>>> maps;

    public DataSourceResultMap(List<Map<String, List<P>>> maps) {
        this.maps = maps;
    }

    protected List<Map<String, List<P>>> getMaps() {
        return this.maps;
    }

    @Override
    public int size() {
        int size = 0;
        for (Map<String, List<P>> map : this.maps) {
            size += map.size();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    @SuppressWarnings("element-type-mismatch")
    public boolean containsKey(Object o) {
        boolean containsKey = false;
        for (Map<String, List<P>> map : this.maps) {
            if (map.containsKey(o)) {
                containsKey = true;
                break;
            }
        }
        return containsKey;
    }

    @Override
    @SuppressWarnings("element-type-mismatch")
    public boolean containsValue(Object o) {
        boolean containsValue = false;
        for (Map<String, List<P>> map : this.maps) {
            if (map.containsValue(o)) {
                containsValue = true;
                break;
            }
        }
        return containsValue;
    }

    @Override
    public List<P> get(Object o) {
        List<P> result = new ArrayList<P>();
        for (Map<String, List<P>> map : this.maps) {
            @SuppressWarnings("element-type-mismatch")
            List<P> r = map.get(o);
            if (r != null) {
                result.addAll(r);
            }
        }
        return result;
    }

    @Override
    public List<P> put(String k, List<P> v) {
        throw new UnsupportedOperationException("This map is immutable");
    }

    @Override
    public List<P> remove(Object o) {
        throw new UnsupportedOperationException("This map is immutable");
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<P>> map) {
        throw new UnsupportedOperationException("This map is immutable");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("This map is immutable");
    }

    @Override
    public Set<String> keySet() {
        Set<String> result = new HashSet<String>();
        for (Map<String, List<P>> map : this.maps) {
            result.addAll(map.keySet());
        }
        return result;
    }

    @Override
    public Set<Entry<String, List<P>>> entrySet() {
        Map<String, DataSourceResultMapEntry> keys =
                new HashMap<String, DataSourceResultMapEntry>();
        Set<Entry<String, List<P>>> result =
                new HashSet<Entry<String, List<P>>>();
        for (Map<String, List<P>> map : this.maps) {
            for (Map.Entry<String, List<P>> me : map.entrySet()) {
                DataSourceResultMapEntry existingMe = keys.get(me.getKey());
                DataSourceResultMapEntry newMe = new DataSourceResultMapEntry(
                        me);
                if (existingMe == null) {
                    result.add(newMe);
                    keys.put(me.getKey(), newMe);
                } else {
                    existingMe.add(me);
                }
            }
        }
        return result;
    }

    @Override
    public Collection<List<P>> values() {
        Collection<List<P>> result = new ArrayList<List<P>>();
        for (Map<String, List<P>> map : this.maps) {
            result.addAll(map.values());
        }
        return result;
    }
}
