package org.protempa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.arp.javautil.collections.CompositeList;

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
    private final List<Map<String, List<P>>> maps;

    public DataSourceResultMap(List<Map<String, List<P>>> maps) {
        if (maps != null) {
           this.maps = new ArrayList<Map<String, List<P>>>(maps);
        } else {
            this.maps = Collections.emptyList();
        }
    }

    @Override
    public int size() {
        Set<String> keys = new HashSet<String>();
        for (Map<String, List<P>> map : this.maps) {
            keys.addAll(map.keySet());
        }
        return keys.size();
    }

    @Override
    public boolean isEmpty() {
        boolean result = true;
        for (Map<String, List<P>> map : this.maps) {
            result = map.isEmpty();
            if (!result) {
                break;
            }
        }
        return result;
    }

    @Override
    public boolean containsKey(Object o) {
        boolean result = false;
        for (Map<String, List<P>> map : this.maps) {
            result = map.containsKey(o);
            if (result) {
                break;
            }
        }
        return result;
    }

    @Override
    public boolean containsValue(Object o) {
        boolean result = false;
        for (Map<String, List<P>> map : this.maps) {
            result = map.containsValue(o);
            if (result) {
                break;
            }
        }
        return result;
    }
    
    

    @Override
    public List<P> get(Object o) {
        Collection<List<P>> lists = null;
        for (Map<String, List<P>> map : this.maps) {
            List<P> list = map.get(o);
            if (list != null) {
                if (lists == null) {
                    lists = new ArrayList<List<P>>(this.maps.size());
                }
                lists.add(list);
            }
        }
        if (lists == null) {
            return null;
        } else {
            return Collections.unmodifiableList(new CompositeList<P>(lists));
        }
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
        return Collections.unmodifiableSet(result);
    }

    @Override
    public Set<Entry<String, List<P>>> entrySet() {
        Map<String, List<P>> result = new HashMap<String, List<P>>();
        if (!this.maps.isEmpty()) {
            int n = this.maps.size();
            for (int i = 0; i < n; i++) {
                for (Map.Entry<String, List<P>> me : this.maps.get(i).entrySet()) {
                    String key = me.getKey();
                    CompositeList<P> vals = (CompositeList<P>) result.get(key);
                    if (vals == null) {
                        vals = new CompositeList<P>();
                        result.put(key, vals);
                    }
                    vals.addList(me.getValue());
                }
            }
        }
        return Collections.unmodifiableSet(result.entrySet());
    }

    @Override
    public Collection<List<P>> values() {
        Map<String, List<P>> result = new HashMap<String, List<P>>();
        if (!this.maps.isEmpty()) {
            int n = this.maps.size();
            for (int i = 0; i < n; i++) {
                for (Map.Entry<String, List<P>> me : this.maps.get(i).entrySet()) {
                    String key = me.getKey();
                    CompositeList<P> vals = (CompositeList<P>) result.get(key);
                    if (vals == null) {
                        vals = new CompositeList<P>();
                        result.put(key, vals);
                    }
                    vals.addList(me.getValue());
                }
            }
        }
        return Collections.unmodifiableCollection(result.values());
    }
}
