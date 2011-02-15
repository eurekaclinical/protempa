package org.arp.javautil.graph;

import java.util.Map;
import org.apache.commons.collections.map.ReferenceMap;

/**
 *
 * @author Andrew Post
 */
public class WeightFactory {

    /**
     * Special value representing positive infinity.
     */
    public static final Weight POS_INFINITY = new Weight(true);
    /**
     * Special value representing negative infinity.
     */
    public static final Weight NEG_INFINITY = new Weight(false);
    /**
     * Special value representing the smallest possible positive value.
     */
    public static final Weight POS_EPSILON = new Weight(1L);
    /**
     * Special value representing the smallest possible negative value.
     */
    public static final Weight NEG_EPSILON = new Weight(-1L);
    /**
     * Special value representing zero.
     */
    public static final Weight ZERO = new Weight(0L);
    
    private static final Map cache = new ReferenceMap();

    public Weight getInstance() {
        return getInstance(null);
    }

    public Weight getInstance(long val) {
        return getInstance((Number) val);
    }

    public Weight getInstance(Number val) {
        Weight weight = (Weight) cache.get(val);
        if (weight == null) {
            weight = new Weight(val);
            cache.put(val, weight);
        }
        return weight;
    }
}
