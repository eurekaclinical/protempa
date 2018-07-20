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
import org.drools.rule.Package;

/**
 *
 * @author Andrew Post
 */
class WorkingMemoryGlobals {

    static void addAll(Package rules) {
        rules.addGlobal(KEY_ID, String.class);
        rules.addGlobal(DERIVED_UNIQUE_ID_COUNTS, Map.class);
        rules.addGlobal(FORWARD_DERIVATIONS, Map.class);
        rules.addGlobal(BACKWARD_DERIVATIONS, Map.class);
    }
    private WorkingMemoryGlobals() {}
    
    static final String KEY_ID = "keyId";
    
    static final String DERIVED_UNIQUE_ID_COUNTS = "derivedUniqueIdCounts";
    
    static final String FORWARD_DERIVATIONS = "forwardDerivations";
    
    static final String BACKWARD_DERIVATIONS = "backwardDerivations";
}
