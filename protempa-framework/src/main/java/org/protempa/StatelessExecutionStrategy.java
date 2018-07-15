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
import org.apache.commons.collections4.iterators.IteratorChain;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.protempa.proposition.Proposition;

class StatelessExecutionStrategy extends AbstractExecutionStrategy {

    private StatelessSession statelessSession;
    private final DeletedWorkingMemoryEventListener workingMemoryEventListener;

    StatelessExecutionStrategy(AlgorithmSource algorithmSource) {
        super(algorithmSource);
        this.workingMemoryEventListener = new DeletedWorkingMemoryEventListener();
    }

    @Override
    public void initialize(Collection<PropositionDefinition> allNarrowerDescendants,
            DerivationsBuilder listener) throws ExecutionStrategyInitializationException {
        super.initialize(allNarrowerDescendants, listener);
        this.statelessSession = getRuleBase().newStatelessSession();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Proposition> execute(String keyId, List<?> objects) {
        this.statelessSession.setGlobal(WorkingMemoryGlobals.KEY_ID, keyId);
        this.statelessSession.addEventListener(this.workingMemoryEventListener);
        StatelessSessionResult result = this.statelessSession
                .executeWithResults(objects);
        this.statelessSession.removeEventListener(this.workingMemoryEventListener);
        return getWorkingMemoryIterator(result);
    }

    @Override
    public void closeCurrentWorkingMemory() {
    }

    @Override
    public void shutdown() {
    }
    
    private Iterator<Proposition> getWorkingMemoryIterator(StatelessSessionResult result) {
        return (Iterator<Proposition>) new IteratorChain(
                result.iterateObjects(),
                this.workingMemoryEventListener.getPropsToDelete().iterator());
    }

}
