package org.protempa.datastore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.arp.javautil.datastore.DataStore;
import org.arp.javautil.datastore.DataStoreFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.common.DroolsObjectInputStream;

/**
 * A data store mapping key IDs to Drools working memory objects. This
 * specialized class is necessary because of the way Drools and BerkeleyDB
 * handle object serialization. Drools expects to deserialize its objects in a
 * custom way that BerkeleyDB breaks. The solution is to convert the working
 * memory to a byte array and have BDB serialize that instead. The byte array
 * can then be deserialized into a Drools rule base, which can generate the
 * original working memory.
 * 
 * @author Michel Mansour
 * 
 */
final class DroolsWorkingMemoryStore implements
        DataStore<String, WorkingMemory> {

    private final DataStore<String, byte[]> store;

    /*
     * Drools rule base. Required to recreate the original working memory from a
     * byte array.
     */
    private final RuleBase ruleBase;

    DroolsWorkingMemoryStore(String dbName, RuleBase ruleBase) {
        store = DataStoreFactory.getPersistentStore(dbName);
        this.ruleBase = ruleBase;
    }

    @Override
    public void shutdown() {
        this.store.shutdown();
    }

    /**
     * Converts a byte array to the original Drools working memory it
     * represents. This is done using a Drools rule base, which can generate a
     * stateful session from a byte array.
     * 
     * @param barr
     * @return
     */
    private WorkingMemory readWorkingMemory(byte[] barr) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(barr);
            WorkingMemory wm = ruleBase.newStatefulSession(bais, false);
            bais.close();
            return wm;
        } catch (IOException ex) {
            throw new IOError(ex);
        } catch (ClassNotFoundException ex) {
            throw new IOError(ex);
        }
    }

    /**
     * Converts a Drools working memory into a byte array for storage by
     * BerkeleyDB. This is necessary because the BDB serializes objects
     * interferes with the custom way in which Drools expects to deserialize
     * them.
     */
    private byte[] writeWorkingMemory(WorkingMemory wm) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(wm);
            byte[] result = baos.toByteArray();
            oos.close();
            baos.close();
            return result;
        } catch (IOException ex) {
            throw new IOError(ex);
        }
    }

    @Override
    public WorkingMemory get(Object key) {
        if (key instanceof String) {
            return readWorkingMemory(this.store.get(key));
        } else {
            return null;
        }
    }

    @Override
    public WorkingMemory put(String key, WorkingMemory value) {
        this.store.put(key, writeWorkingMemory(value));
        return value;
    }

    @Override
    public int size() {
        return this.store.size();
    }

    @Override
    public boolean isEmpty() {
        return this.store.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.store.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.store.containsValue(value);
    }

    @Override
    public WorkingMemory remove(Object key) {
        WorkingMemory old = readWorkingMemory(this.store.remove(key));
        return old;
    }

    @Override
    public void putAll(Map<? extends String, ? extends WorkingMemory> m) {
        for (Entry<? extends String, ? extends WorkingMemory> e : m.entrySet()) {
            this.store.put(e.getKey(), writeWorkingMemory(e.getValue()));
        }
    }

    @Override
    public void clear() {
        this.store.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.store.keySet();
    }

    @Override
    public Collection<WorkingMemory> values() {
        Collection<WorkingMemory> values = new ArrayList<WorkingMemory>();
        for (byte[] barr : this.store.values()) {
            values.add(readWorkingMemory(barr));
        }

        return Collections.unmodifiableCollection(values);
    }

    @Override
    public Set<java.util.Map.Entry<String, WorkingMemory>> entrySet() {
        Set<java.util.Map.Entry<String, WorkingMemory>> entrySet = new HashSet<java.util.Map.Entry<String, WorkingMemory>>();

        for (Entry<String, byte[]> e : this.store.entrySet()) {
            String key = e.getKey();
            WorkingMemory value = readWorkingMemory(e.getValue());
            entrySet.add(new AbstractMap.SimpleEntry<String, WorkingMemory>(
                    key, value));
        }

        return entrySet;
    }

}
