/*
 * #%L
 * JavaUtil
 * %%
 * Copyright (C) 2012 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
