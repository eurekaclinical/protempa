/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
package org.protempa;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eurekaclinical.datastore.DataStore;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.protempa.proposition.Proposition;

class StatefulExecutionStrategy extends AbstractExecutionStrategy {

    private StatefulSession workingMemory;

    StatefulExecutionStrategy(AlgorithmSource algorithmSource) {
        super(algorithmSource);
    }
    
    @Override
    public void initialize() {
        this.workingMemory = ruleBase.newStatefulSession(false);
    }

    @Override
    public Iterator<Proposition> execute(String keyId,
            Set<String> propositionIds, List<?> objects,
            DataStore<String, WorkingMemory> wmStore) {
        Logger logger = ProtempaUtil.logger();
        this.workingMemory.setGlobal(WorkingMemoryGlobals.KEY_ID, keyId);
        for (Object obj : objects) {
            this.workingMemory.insert(obj);
        }
        this.workingMemory.fireAllRules();
        logger.log(Level.FINEST,
                "Persisting working memory for key ID {0}", keyId);
        wmStore.put(keyId, this.workingMemory);
        logger.log(Level.FINEST,
                "Persisted working memory for key ID {0}", keyId);

        return null;
    }

    @Override
    public void cleanup() {
        this.workingMemory.dispose();
    }
}
