package org.protempa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

        private String key;
        private List<P> value;

        public DataSourceResultMapEntry(String key, List<P> value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public List<P> getValue() {
            return this.value;
        }

        @Override
        public List<P> setValue(List<P> value) {
            throw new UnsupportedOperationException("This map is immutable");
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
        Set<Entry<String, List<P>>> result =
                new HashSet<Entry<String, List<P>>>();
        for (Map<String, List<P>> map : this.maps) {
            for (Map.Entry<String, List<P>> me : map.entrySet()) {
                DataSourceResultMapEntry newMe = new DataSourceResultMapEntry(
                        me.getKey(), me.getValue());
                result.add(newMe);
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
