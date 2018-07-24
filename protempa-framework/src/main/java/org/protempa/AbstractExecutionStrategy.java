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

import java.util.ArrayList;
import java.util.Collection;

import org.drools.RuleBase;
import org.protempa.query.Query;

abstract class AbstractExecutionStrategy implements ExecutionStrategy {

    private final AlgorithmSource algorithmSource;
    private final DerivationsBuilder derivationsBuilder;
    private Collection<PropositionDefinition> cache;
    private final Query query;
    private RuleBase ruleBase;

    /**
     * @param abstractionFinder the {@link AbstractionFinder} using this
     * execution strategy
     */
    AbstractExecutionStrategy(AlgorithmSource algorithmSource, Query query) {
        this.algorithmSource = algorithmSource;
        this.derivationsBuilder = new DerivationsBuilder();
        this.query = query;
    }

    public Query getQuery() {
        return query;
    }

    @Override
    public DerivationsBuilder getDerivationsBuilder() {
        return derivationsBuilder;
    }

    protected final AlgorithmSource getAlgorithmSource() {
        return this.algorithmSource;
    }

    protected Collection<PropositionDefinition> getCache() {
        return cache;
    }

    @Override
    public void initialize(Collection<? extends PropositionDefinition> cache)
            throws ExecutionStrategyInitializationException {
        if (cache == null) {
            throw new IllegalArgumentException("cache cannot be null");
        }
        this.cache = new ArrayList<>(cache);
        createRuleBase();
    }

    protected RuleBase getRuleBase() {
        return this.ruleBase;
    }

    private void createRuleBase() throws ExecutionStrategyInitializationException {
        JBossRuleCreator ruleCreator = newRuleCreator();
        try {
            this.ruleBase = new JBossRuleBaseFactory(ruleCreator).newInstance();
        } catch (RuleBaseInstantiationException ex) {
            throw new ExecutionStrategyInitializationException(ex);
        }
    }
    
    /**
     * Called by {@link #initialize(java.util.Collection) }.
     * @return a newly created rule creator instance.
     * @throws ExecutionStrategyInitializationException 
     */
    protected abstract JBossRuleCreator newRuleCreator() throws ExecutionStrategyInitializationException;
    
}
