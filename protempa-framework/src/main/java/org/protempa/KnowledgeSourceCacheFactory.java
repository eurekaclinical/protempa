package org.protempa;

/*
 * #%L
 * Protempa Framework
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.arp.javautil.collections.Collections;
import org.protempa.valueset.ValueSet;

/**
 *
 * @author Andrew Post
 */
public final class KnowledgeSourceCacheFactory {

    public KnowledgeSourceCache getInstance(KnowledgeSource ks, Collection<PropositionDefinition> cache, boolean collectValueSets) throws KnowledgeSourceReadException {
        Map<String, PropositionDefinition> propDefCache = Collections.newHashMap(cache.size());
        for (PropositionDefinition pd : cache) {
            propDefCache.put(pd.getId(), pd);
        }
        Map<String, ValueSet> vsCache;
        if (collectValueSets) {
            vsCache = new HashMap<>();
            for (PropositionDefinition propDef : cache) {
                for (PropertyDefinition pd : propDef.getPropertyDefinitions()) {
                    String valueSetId = pd.getValueSetId();
                    if (valueSetId != null) {
                        vsCache.put(valueSetId, ks.readValueSet(valueSetId));
                    }
                }
            }
        } else {
            vsCache = null;
        }
        return new KnowledgeSourceCache(propDefCache, vsCache);
    }
}
