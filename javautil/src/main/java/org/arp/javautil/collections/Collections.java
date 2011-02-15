package org.arp.javautil.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Extra utilities for collections.
 * 
 * @author Andrew Post
 */
public class Collections {

    private Collections() {
    }

    /**
     * Puts a value into a map of key -> list of values. If the specified key
     * is new, it creates an {@link ArrayList} as its value.
     * @param map a {@link Map}.
     * @param key a key.
     * @param valueElt a value.
     */
    public static <K, V> void putList(Map<K, List<V>> map, K key, V valueElt) {
        if (map.containsKey(key)) {
            List<V> l = map.get(key);
            l.add(valueElt);
        } else {
            List<V> l = new ArrayList<V>();
            l.add(valueElt);
            map.put(key, l);
        }
    }

    /**
     * Puts a collection of values into a map of key -> list of values.
     * If the specified key is new, it creates an {@link ArrayList} as its
     * value.
     * @param map a {@link Map}.
     * @param key a key.
     * @param values a {@link Collection<? extends V>} of values.
     */
    public static <K, V> void putListMult(Map<K, List<V>> map, K key,
            Collection<? extends V> values) {
        for (V value : values) {
            putList(map, key, value);
        }
    }

    /**
     * Checks whether any of an arrys's elements are also in the provided set.
     *
     * @param aSet a {@link Set}.
     * @param arr an array.
     * @return <code>true</code> or <code>false</code>.
     */
    public static <K> boolean containsAny(Set<K> aSet, K[] arr) {
        for (K obj : arr) {
            if (aSet.contains(obj)) {
                return true;
            }
        }
        return false;
    }
}
