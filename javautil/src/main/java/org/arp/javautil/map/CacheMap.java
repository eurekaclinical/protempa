package org.arp.javautil.map;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;

/**
 * Implements EHCache-backed {@link Map}. Because this map is backed by a
 * cache, some aspects of {@link Map}'s contract for modifiable maps cannot be
 * implemented. Thus, users of this implementation that expose instances of it
 * as a {@link Map} should wrap it in
 * {@link java.util.Collections#unmodifiableMap(java.util.Map)} so that callers
 * cannot modify it, and the map should not be modified after it is so wrapped.
 *
 * @author Himanshu Rathod
 * @author Andrew Post
 */
public class CacheMap<K, V> implements Map<K, V> {

    private final CacheManager cacheManager;
    private final Cache cache;
    private String id;

    private class ShutdownRunnable implements Runnable {

        private String id;

        public ShutdownRunnable(String id) {
            this.id = new String(id);
        }

        @Override
        public void run() {
            Logger logger = MapUtil.logger();
            synchronized (cacheManager) {
                if (cacheManager.getStatus() == Status.STATUS_ALIVE) {
                    logger.log(Level.FINE,
                            "cacheManager shutting down cache {0}",
                            new String(id));
                    cacheManager.shutdown();
                }
            }
        }
    }

    public CacheMap() {
        Logger logger = MapUtil.logger();
        this.id = UUID.randomUUID().toString();

        this.cacheManager = new CacheManager();


        Runtime.getRuntime().addShutdownHook(
                new Thread(new ShutdownRunnable(this.id)));

        if (logger.isLoggable(Level.FINE)) {
            File file = new File(this.cacheManager.getDiskStorePath(),
                    this.id);
            String path = file.getAbsolutePath();
            logger.log(Level.FINE, "Creating cache {0} in {1}",
                    new Object[]{this.id, path});

        }

        this.cacheManager.addCache(this.id);
        this.cache = cacheManager.getCache(this.id);
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
            Element elt = this.cache.get(key);
            V value = (V) elt.getValue();
            CacheMapEntry entry = new CacheMapEntry(key, value);
            entrySet.add(entry);
        }
        return entrySet;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(Object arg0) {
        Element old = this.cache.get(arg0);
        if (old != null) {
            return (V) old.getValue();
        } else {
            return null;
        }
    }

    @Override
    public boolean isEmpty() {
        return (this.cache.getSize() == 0);
    }

    /**
     * The returned set is NOT backed by the map.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keySet() {
        return new HashSet<K>((List<K>) this.cache.getKeys());
    }

    /**
     * Note that if <code>arg1</code>'s state changes, the map will not reflect
     * the change unless this method is called again with the changed object.
     * 
     * @param arg0 a key.
     * @param arg1 a value.
     * @return
     */
    @Override
    public V put(K arg0, V arg1) {
        Element old = this.cache.get(arg0);
        this.cache.put(new Element(arg0, arg1));
        if (old != null) {
            return (V) old.getValue();
        } else {
            return null;
        }
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
        Element element = this.cache.get(arg0);
        this.cache.remove(arg0);
        if (element != null) {
            return (V) element.getValue();
        } else {
            return null;
        }
    }

    @Override
    public int size() {
        return this.cache.getSize();
    }

    /**
     * The returned values collection is NOT backed by the map.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection<V> values() {
        List<K> keys = (List<K>) this.cache.getKeys();
        List<V> values = new ArrayList<V>();
        for (K key : keys) {
            Element elt = this.cache.get(key);
            values.add((V) elt.getValue());
        }
        return values;
    }

    @Override
    protected void finalize() throws Throwable {
        Logger logger = MapUtil.logger();
        synchronized (this.cacheManager) {
            logger.log(Level.FINE, "cacheManager removing cache {0}", this.id);
            if (cacheManager.getStatus() == Status.STATUS_ALIVE) {
                cacheManager.shutdown();
            }
        }
        super.finalize();
    }

    class CacheMapEntry implements Map.Entry<K, V> {

        K key;
        V value;

        CacheMapEntry(K k, V v) {
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
        CacheMap<String, String> testMap = new CacheMap<String, String>();
        int i;
        for (i = 0; i < 100000; i++) {
            testMap.put("Key " + i, "TEST VALUE " + i);
        }
        System.out.println("KEY 23: " + testMap.get("Key 23"));
    }
}
