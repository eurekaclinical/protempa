package org.protempa.dest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.arp.javautil.arrays.Arrays;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2014 Emory University
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

/**
 *
 * @author Andrew Post
 */
public final class DefaultStatistics implements Statistics {
    private final int numberOfKeys;
    private final Map<String, Integer> counts;
    private final Map<String, String> childrenToParents;

    public DefaultStatistics(int numberOfKeys, Map<String, Integer> counts, Map<String, String> childrenToParents) {
        this.numberOfKeys = numberOfKeys;
        if (counts != null) {
            this.counts = new HashMap<>(counts);
        } else {
            this.counts = Collections.emptyMap();
        }
        if (childrenToParents != null) {
            this.childrenToParents = new HashMap<>(childrenToParents);
        } else {
            this.childrenToParents = Collections.emptyMap();
        }
    }

    @Override
    public int getNumberOfKeys() {
        return numberOfKeys;
    }

    @Override
    public Map<String, String> getChildrenToParents() {
        return new HashMap<>(this.childrenToParents);
    }

    @Override
    public Map<String, String> getChildrenToParents(String[] propIds) {
        Map<String, String> result = getChildrenToParents();
        for (Map.Entry<String, String> me : result.entrySet()) {
            if (Arrays.contains(propIds, me.getKey())) {
                result.put(me.getKey(), me.getValue());
            }
        }
        return result;
    }
    
    @Override
    public Map<String, Integer> getCounts() {
        return new HashMap<>(this.counts);
    }

    @Override
    public Map<String, Integer> getCounts(String[] propIds) {
        Map<String, Integer> result = getCounts();
        for (Map.Entry<String, Integer> me : result.entrySet()) {
            if (Arrays.contains(propIds, me.getKey())) {
                result.put(me.getKey(), me.getValue());
            }
        }
        return result;
    }
    
}
