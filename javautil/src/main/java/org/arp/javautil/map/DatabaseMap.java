package org.arp.javautil.map;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseException;

public class DatabaseMap<K, V> implements Map<K, V> {

    private final Database db;
    private final StoredMap storedMap;
    private int size;

    public DatabaseMap() throws DatabaseError {
        try {
        String dbName = UUID.randomUUID().toString();
        this.db = CacheDatabase.getDatabase(dbName);

        ClassCatalog catalog = CacheDatabase.getClassCatalog();
        EntryBinding binding = new SerialBinding(catalog, null);
        storedMap = new StoredMap(db, binding, binding, true);
        size = 0;
        } catch (DatabaseException dbe) {
            throw new DatabaseError(dbe);
        }
    }

    public void shutdown() throws DatabaseException {
        this.db.close();
    }

    @Override
    public void clear() {
        this.storedMap.clear();
        this.size = 0;
    }

    @Override
    public boolean containsKey(Object arg0) {
        return this.storedMap.containsKey(arg0);
    }

    @Override
    public boolean containsValue(Object arg0) {
        return this.storedMap.containsValue(arg0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return this.storedMap.entrySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(Object arg0) {
        return (V) this.storedMap.get(arg0);
    }

    @Override
    public boolean isEmpty() {
        return this.storedMap.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keySet() {
        return this.storedMap.keySet();
    }

    @Override
    public V put(K arg0, V arg1) {
        @SuppressWarnings("unchecked")
        V old = (V) this.storedMap.put(arg0, arg1);
        if (old == null) {
            this.size++;
        }
        return old;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> arg0) {
        this.storedMap.putAll(arg0);
    }

    @Override
    public V remove(Object arg0) {
        @SuppressWarnings("unchecked")
        V old = (V) this.storedMap.remove(arg0);
        if (old != null) {
            this.size--;
        }
        return old;
    }

    @Override
    public int size() {
        return this.size;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<V> values() {
        return (Collection<V>) this.storedMap.values();
    }

    class DatabaseMapEntry implements Entry<K, V> {
        private final StoredMap map;
        private final K key;

        public DatabaseMapEntry(StoredMap storedMap, K k) {
            this.map = storedMap;
            this.key = k;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @SuppressWarnings("unchecked")
        @Override
        public V getValue() {
            return (V) this.map.get(this.key);
        }

        @SuppressWarnings("unchecked")
        @Override
        public V setValue(V arg0) {
            return (V) this.map.put(this.key, arg0);
        }

    }
}
