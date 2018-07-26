package org.protempa;

/*-
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2018 Emory University
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * A cache containing all of the proposition definitions currently being
 * processed.
 * 
 * @author Andrew Post
 */
public class PropositionDefinitionCache {
    
    private final Map<String, PropositionDefinition> cache;
    
    
    public PropositionDefinitionCache(Collection<? extends PropositionDefinition> propDefs) {
        this.cache = new HashMap<>();
        for (PropositionDefinition pd : propDefs) {
            this.cache.put(pd.getId(), pd);
        }
    }
    
    /**
     * Gets an immutable collection of the proposition definitions.
     * 
     * @return a collection of proposition definitions.
     */
    public Collection<PropositionDefinition> getAll() {
        return Collections.unmodifiableCollection(this.cache.values());
    }
    
    public PropositionDefinition get(String id) {
        return this.cache.get(id);
    }
    
    public Set<String> collectPropIdDescendantsUsingInverseIsA(String... propIds) throws QueryException {
        Set<String> result = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        org.arp.javautil.arrays.Arrays.addAll(queue, propIds);
        String propId;
        while ((propId = queue.poll()) != null) {
            if (result.add(propId)) {
                PropositionDefinition pd = cache.get(propId);
                if (pd != null) {
                    String[] children = pd.getInverseIsA();
                    org.arp.javautil.arrays.Arrays.addAll(queue, children);
                }
            }
        }
        return result;
    }

}
