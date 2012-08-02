/*
 * #%L
 * Protempa Framework
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
package org.protempa;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.arp.javautil.datastore.DataStore;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.protempa.proposition.Proposition;

class StatefulExecutionStrategy extends AbstractExecutionStrategy {

    @Override
    public void initialize() {
    }

    StatefulExecutionStrategy(AbstractionFinder abstractionFinder) {
        super(abstractionFinder);
    }

    private StatefulSession applyRules(String keyId, List<?> objects) {
        StatefulSession workingMemory = ruleBase.newStatefulSession(false);
        ProtempaUtil.logger().log(Level.FINEST,
                "Adding {0} objects for key ID {1}",
                new Object[] { objects.size(), keyId });
        for (Object obj : objects) {
            workingMemory.insert(obj);
        }
        workingMemory.fireAllRules();
        int wmCount = 0;
        for (Iterator<?> itr = workingMemory.iterateObjects(); itr.hasNext();itr.next()) {
            wmCount++;
        }
        ProtempaUtil.logger().log(Level.FINEST,
                "Iterated over {0} objects", new Object[] { wmCount });
        return workingMemory;
    }

    @Override
    public Iterator<Proposition> execute(String keyId,
            Set<String> propositionIds, List<?> objects,
            DataStore<String, WorkingMemory> wmStore) {
        StatefulSession workingMemory = applyRules(keyId, objects);
        ProtempaUtil.logger().log(Level.FINEST,
                "Persisting working memory for key ID {0}", keyId);
        wmStore.put(keyId, workingMemory);
        workingMemory.dispose();
        ProtempaUtil.logger().log(Level.FINEST,
                "Persisted working memory for key ID {0}", keyId);

        return null;
    }

    @Override
    public void cleanup() {
    }
}