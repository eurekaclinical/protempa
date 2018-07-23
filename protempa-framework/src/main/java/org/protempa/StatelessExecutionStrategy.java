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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.arp.javautil.collections.Iterators;

import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.protempa.proposition.Proposition;
import org.protempa.query.Query;

class StatelessExecutionStrategy extends AbstractExecutionStrategy {

    private StatelessSession statelessSession;
    private final DeletedWorkingMemoryEventListener workingMemoryEventListener;

    StatelessExecutionStrategy(AlgorithmSource algorithmSource, Query query) {
        super(algorithmSource, query);
        this.workingMemoryEventListener = new DeletedWorkingMemoryEventListener();
    }

    @Override
    public void initialize(Collection<? extends PropositionDefinition> cache) throws ExecutionStrategyInitializationException {
        super.initialize(cache);
        this.statelessSession = getRuleBase().newStatelessSession();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Proposition> execute(String keyId, Iterator<? extends Proposition> props) {
        this.statelessSession.setGlobal(WorkingMemoryGlobals.KEY_ID, keyId);
        this.statelessSession.addEventListener(this.workingMemoryEventListener);
        StatelessSessionResult result = this.statelessSession
                .executeWithResults(Iterators.asCollection(props));
        this.statelessSession.removeEventListener(this.workingMemoryEventListener);
        List<Proposition> propsToDelete = this.workingMemoryEventListener.getPropsToDelete();
        this.workingMemoryEventListener.clear();
        return (Iterator<Proposition>) new IteratorChain(result.iterateObjects(), propsToDelete.iterator());
    }

    @Override
    public void closeCurrentWorkingMemory() {
    }

    @Override
    public void shutdown() {
    }
    
    @Override
    protected JBossRuleCreator newRuleCreator() throws ExecutionStrategyInitializationException {
        ValidateAlgorithmCheckedVisitor visitor = new ValidateAlgorithmCheckedVisitor(
                getAlgorithmSource());
        Collection<PropositionDefinition> propDefs = getCache();
        try {
            visitor.visit(propDefs);
        } catch (ProtempaException ex) {
            throw new ExecutionStrategyInitializationException(ex);
        }
        JBossRuleCreator ruleCreator = new JBossRuleCreator(
                visitor.getAlgorithms(), getDerivationsBuilder(),
                propDefs, getQuery());
        try {
            ruleCreator.visit(propDefs);
        } catch (ProtempaException ex) {
            throw new ExecutionStrategyInitializationException(ex);
        }
        return ruleCreator;
    }
    
}
