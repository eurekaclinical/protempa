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
import java.util.HashMap;
import java.util.Map;

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseConfiguration.AssertBehaviour;

abstract class AbstractExecutionStrategy implements ExecutionStrategy {

    private final AlgorithmSource algorithmSource;
    protected RuleBase ruleBase;

    /**
     * @param abstractionFinder
     *            the {@link AbstractionFinder} using this execution strategy
     */
    AbstractExecutionStrategy(AlgorithmSource algorithmSource) {
        this.algorithmSource = algorithmSource;
    }

    protected final AlgorithmSource getAlgorithmSource() {
        return this.algorithmSource;
    }

    @Override
    public void createRuleBase(Collection<PropositionDefinition> allNarrowerDescendants,
            DerivationsBuilder listener, QuerySession qs)
            throws CreateRuleBaseException {
        ValidateAlgorithmCheckedVisitor visitor = new ValidateAlgorithmCheckedVisitor(
                this.algorithmSource);
        JBossRuleCreator ruleCreator = new JBossRuleCreator(
                visitor.getAlgorithms(), listener, allNarrowerDescendants);
        if (allNarrowerDescendants != null) {
            try {
                for (PropositionDefinition pd : allNarrowerDescendants) {
                    pd.acceptChecked(visitor);
                }
                ruleCreator.visit(allNarrowerDescendants);
            } catch (ProtempaException ex) {
                throw new CreateRuleBaseException(ex);
            }
        }
        try {
            this.ruleBase = new JBossRuleBaseFactory(ruleCreator,
                    createRuleBaseConfiguration(ruleCreator, allNarrowerDescendants)).newInstance();
        } catch (RuleBaseInstantiationException ex) {
            throw new CreateRuleBaseException(ex);
        }
    }

    @Override
    public RuleBase getRuleBase() {
        return this.ruleBase;
    }

    protected RuleBaseConfiguration createRuleBaseConfiguration(
            JBossRuleCreator ruleCreator, 
            Collection<PropositionDefinition> allNarrowerDescendants)
            throws RuleBaseInstantiationException {
        RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setShadowProxy(false);
        try {
            config.setConflictResolver(new PROTEMPAConflictResolver(
                    allNarrowerDescendants, ruleCreator.getRuleToTPDMap()));
        } catch (CycleDetectedException ex) {
            throw new RuleBaseInstantiationException(
                    "Problem creating data processing rules", ex);
        }
        config.setAssertBehaviour(AssertBehaviour.EQUALITY);
        return config;
    }

    private class ValidateAlgorithmCheckedVisitor extends
            AbstractPropositionDefinitionCheckedVisitor {

        private final Map<LowLevelAbstractionDefinition, Algorithm> algorithms;
        private final AlgorithmSource algorithmSource;

        ValidateAlgorithmCheckedVisitor(AlgorithmSource algorithmSource) {
            this.algorithms = new HashMap<>();
            this.algorithmSource = algorithmSource;
        }

        Map<LowLevelAbstractionDefinition, Algorithm> getAlgorithms() {
            return this.algorithms;
        }

        @Override
        public void visit(
                LowLevelAbstractionDefinition lowLevelAbstractionDefinition)
                throws ProtempaException {
            String algorithmId = lowLevelAbstractionDefinition.getAlgorithmId();
            Algorithm algorithm = algorithmSource.readAlgorithm(algorithmId);
            if (algorithm == null && algorithmId != null) {
                throw new NoSuchAlgorithmException(
                        "Low level abstraction definition "
                                + lowLevelAbstractionDefinition.getId()
                                + " wants the algorithm " + algorithmId
                                + ", but no such algorithm is available.");
            }
            this.algorithms.put(lowLevelAbstractionDefinition, algorithm);

        }

        @Override
        public void visit(
                CompoundLowLevelAbstractionDefinition extendedLowLevelAbstractionDefinition)
                throws ProtempaException {
        }

        @Override
        public void visit(EventDefinition eventDefinition)
                throws ProtempaException {
        }

        @Override
        public void visit(
                HighLevelAbstractionDefinition highLevelAbstractionDefinition)
                throws ProtempaException {
        }

        @Override
        public void visit(
                PrimitiveParameterDefinition primitiveParameterDefinition)
                throws ProtempaException {
        }

        @Override
        public void visit(SliceDefinition sliceAbstractionDefinition)
                throws ProtempaException {
        }

        @Override
        public void visit(SequentialTemporalPatternDefinition pairAbstractionDefinition)
                throws ProtempaException {
        }

        @Override
        public void visit(ConstantDefinition def) throws ProtempaException {
        }

        @Override
        public void visit(ContextDefinition def) throws ProtempaException {
        }
        
        
    }
}
