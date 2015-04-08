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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.drools.WorkingMemory;
import org.protempa.proposition.LocalUniqueIdValuesProvider;

/**
 *
 * @author Andrew Post
 */
class JBossRulesDerivedLocalUniqueIdValuesProvider implements LocalUniqueIdValuesProvider, Serializable {
    private static final long serialVersionUID = 1;
    
    private final String propId;
    private int instanceNum;
    private final String keyId;
    private final WorkingMemory workingMemory;

    JBossRulesDerivedLocalUniqueIdValuesProvider(WorkingMemory workingMemory, String propId) {
        this.propId = propId;
        this.keyId = (String) workingMemory.getGlobal(WorkingMemoryGlobals.KEY_ID);
        this.workingMemory = workingMemory;
    }

    @Override
    public void incr() {
        Map<String, Integer> counts = (Map<String, Integer>) workingMemory.getGlobal(WorkingMemoryGlobals.DERIVED_UNIQUE_ID_COUNTS);
        if (counts == null) {
            counts = new HashMap<>();
            workingMemory.setGlobal(WorkingMemoryGlobals.DERIVED_UNIQUE_ID_COUNTS, counts);
        }
        Integer count = counts.get(propId);
        if (count == null) {
            count = 0;
        } else {
            count++;
        }
        counts.put(propId, count);
        this.instanceNum = count;
    }

    @Override
    public String getId() {
        return this.keyId + "^" + this.propId + "^" + this.instanceNum;
    }

    @Override
    public int getNumericalId() {
        return this.instanceNum;
    }

}
