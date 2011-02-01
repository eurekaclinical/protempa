package org.arp.javautil.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

public class CacheMap<K, V> implements Map<K, V> {

    private final CacheManager cacheManager;
    private final Cache cache;
    private String id;

    public CacheMap() {
        this.id = UUID.randomUUID().toString();
        this.cacheManager = CacheManager.create();
        
        CacheConfiguration cacheConfig = new CacheConfiguration(id, 10);
        cacheConfig.setDiskStorePath("/tmp/cache");
        cacheConfig.setOverflowToDisk(true);
        cacheConfig.setDiskPersistent(false);
        cacheConfig.setEternal(true);
        
        cache = new Cache(cacheConfig);
        cacheManager.addCache(cache);
    }

    @Override
    public void clear() {
        this.cache.removeAll();
    }

    @Override
    public boolean containsKey(Object arg0) {
        return this.cache.isKeyInCache(arg0);
    }

    @Override
    public boolean containsValue(Object arg0) {
        return this.cache.isValueInCache(arg0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entrySet = new HashSet<Entry<K, V>>();
        List<K> keys = (List<K>) this.cache.getKeys();
        for (K key : keys) {
            V value = (V) this.cache.get(key);
            CacheMapEntry entry = new CacheMapEntry(key, value);
            entrySet.add(entry);
        }
        return entrySet;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(Object arg0) {
        return (V) this.cache.get(arg0).getValue();
    }

    @Override
    public boolean isEmpty() {
        return (this.cache.getSize() == 0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keySet() {
        return new HashSet<K>((List<K>) this.cache.getKeys());
    }

    @Override
    public V put(K arg0, V arg1) {
        this.cache.put(new Element(arg0, arg1));
        return arg1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void putAll(Map<? extends K, ? extends V> arg0) {
        Set<K> keys = (Set<K>) arg0.keySet();
        for (K key : keys) {
            this.cache.put(new Element(key, arg0.get(key)));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove(Object arg0) {
        V v = (V) this.cache.get(arg0).getValue();
        this.cache.remove(arg0);
        return v;
    }

    @Override
    public int size() {
        return this.cache.getSize();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<V> values() {
        List<K> keys = (List<K>) this.cache.getKeys();
        List<V> values = new ArrayList<V>();
        for (K key : keys) {
            values.add((V) this.cache.get(key));
        }
        return values;
    }

    @Override
    protected void finalize() throws Throwable {
        this.cache.dispose();
        this.cacheManager.removeCache(this.id);
        this.cacheManager.shutdown();
        super.finalize();
    }
    
    class CacheMapEntry implements Map.Entry<K, V> {
        K key;
        V value;
        
        CacheMapEntry (K k, V v) {
            this.key = k;
            this.value = v;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            this.value = value;
            return value;
        }
        
    }

    public static void main(String[] args) {
        Map<String, String> testMap = new CacheMap<String,String>();
        int i;
        for (i = 0; i < 100; i++) {
            testMap.put("Key " + i, "TEST VALUE " + i);
        }
        System.out.println("KEY 23: " + testMap.get("Key 23"));
    }
}
