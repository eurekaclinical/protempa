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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.protempa.proposition.Proposition;

class StatelessExecutionStrategy extends AbstractExecutionStrategy {

    private StatelessSession statelessSession;

    StatelessExecutionStrategy(AlgorithmSource algorithmSource) {
        super(algorithmSource);
    }
    
    @Override
    public void initialize(Collection<PropositionDefinition> allNarrowerDescendants,
            DerivationsBuilder listener) throws ExecutionStrategyInitializationException {
        super.initialize(allNarrowerDescendants, listener);
        this.statelessSession = getRuleBase().newStatelessSession();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Proposition> execute(String keyId,
            Set<String> propositionIds, List<?> objects) {
        this.statelessSession.setGlobal(WorkingMemoryGlobals.KEY_ID, keyId);
        StatelessSessionResult result = this.statelessSession
                .executeWithResults(objects);
        return result.iterateObjects();
    }

    @Override
    public void closeCurrentWorkingMemory() {
    }
    
    @Override
    public void shutdown() {
    }

}
