package org.protempa.backend.dsb.relationaldb.mappings;

/*
 * #%L
 * Protempa Relational Database Data Source Backend
 * %%
 * Copyright (C) 2012 - 2015 Emory University
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
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arp.javautil.arrays.Arrays;

/**
 *
 * @author Andrew Post
 */
public class DefaultMappings implements Mappings {

    private static final Logger LOGGER = Logger.getLogger(DefaultMappings.class.toString());
    private static final String DEFAULT_DESCRIPTION_MISSING_TARGET = "DescriptionMissing";
    
    private final Map<Object, String> cache;
    
    public DefaultMappings(Map<Object, String> mappings) {
        if (mappings == null) {
            throw new IllegalArgumentException("mappings cannot be null");
        }
        this.cache = mappings;
    }
    
    @Override
    public Object[] readSources() {
        Set<Object> keySet = this.cache.keySet();
        return keySet.toArray(new Object[keySet.size()]);
    }
    
    @Override
    public String[] readTargets() throws IOException {
        Collection<String> targetColl = this.cache.values();
        Set<String> targets = new HashSet<>();
        for (String target : targetColl) {
            targets.add(target);
        }
        return targets.toArray(new String[targets.size()]);
    }
    
    @Override
    public String getTarget(Object source) {
        String result = this.cache.get(source);
        if (result == null) {
            result = this.cache.get("*");
            if (result == null) {
                result = DEFAULT_DESCRIPTION_MISSING_TARGET;
            }
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "No mapping for source {0}; this value will be loaded as {1}", new Object[]{source, result});
            }
        }
        return result;
    }
    
    @Override
    public int size() {
        return this.cache.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.cache.isEmpty();
    }
    
    @Override
    public Mappings subMappingsBySources(Object[] sources) {
        Map<Object, String> newMappings = new HashMap<>();
        for (Object source : sources) {
            newMappings.put(source, this.cache.get(source));
        }
        return new DefaultMappings(newMappings);
    }
    
    @Override
    public Mappings subMappingsByTargets(String[] targets) {
        Map<Object, String> newMappings = new HashMap<>();
        Set<String> targetsSet = Arrays.asSet(targets);
        for (Map.Entry<Object, String> me : this.cache.entrySet()) {
            if (targetsSet.contains(me.getValue())) {
                newMappings.put(me.getKey(), me.getValue());
            }
        }
        return new DefaultMappings(newMappings);
    }
    
    @Override
    public void close() {
        this.cache.clear();
    }
}
