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

import java.util.Map;
import org.protempa.valueset.ValueSet;

/**
 *
 * @author Andrew Post
 */
public final class KnowledgeSourceCache {

    private final PropositionDefinitionCache cache;
    private final Map<String, ValueSet> valueSetCache;

    public KnowledgeSourceCache(PropositionDefinitionCache propDefCache, Map<String, ValueSet> valueSetCache) {
        if (propDefCache == null) {
            throw new IllegalArgumentException("propDefCache != null");
        }
        this.cache = propDefCache;
        this.valueSetCache = valueSetCache;
    }

    public PropositionDefinition get(String propId) {
        return this.cache.get(propId);
    }

    public ValueSet getValueSet(String valueSetId) {
        if (this.valueSetCache != null) {
            return this.valueSetCache.get(valueSetId);
        } else {
            return null;
        }
    }
}
