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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseConfiguration.AssertBehaviour;

abstract class AbstractExecutionStrategy implements ExecutionStrategy {

    /**
     * The {@link AbstractionFinder} using this execution strategy.
     */
    private final AbstractionFinder abstractionFinder;
    protected RuleBase ruleBase;

    /**
     * @param abstractionFinder
     *            the {@link AbstractionFinder} using this execution strategy
     */
    AbstractExecutionStrategy(AbstractionFinder abstractionFinder) {
        this.abstractionFinder = abstractionFinder;
    }

    protected final AbstractionFinder getAbstractionFinder() {
        return this.abstractionFinder;
    }

    @Override
    public void createRuleBase(Set<String> propIds,
            DerivationsBuilder listener, QuerySession qs)
            throws FinderException {
        ValidateAlgorithmCheckedVisitor visitor = new ValidateAlgorithmCheckedVisitor(
                this.abstractionFinder.getAlgorithmSource());
        JBossRuleCreator ruleCreator = new JBossRuleCreator(
                visitor.getAlgorithms(), listener);
        List<PropositionDefinition> propDefs = 
                new ArrayList<PropositionDefinition>(propIds.size());
        for (String propId : propIds) {
            PropositionDefinition propDef;
            try {
                propDef = this.abstractionFinder
                    .getKnowledgeSource().readPropositionDefinition(propId);
            } catch (KnowledgeSourceReadException ex) {
                throw new FinderException("Problem retrieving proposition definition " + propId + " from knowledge source");
            }
            if (propDef != null) {
                propDefs.add(propDef);
            } else {
                throw new FinderException("Invalid proposition id: " + propId);
            }
        }
        if (propIds != null) {
            Set<PropositionDefinition> result = new HashSet<PropositionDefinition>();
            aggregateDescendants(visitor, result, propDefs);
            try {
                ruleCreator.visit(result);
            } catch (ProtempaException ex) {
                throw new FinderException("Problem creating data processing rules", ex);
            }
        }
        try {
            this.ruleBase = new JBossRuleBaseFactory(ruleCreator,
                    createRuleBaseConfiguration(ruleCreator)).newInstance();
        } catch (PropositionDefinitionInstantiationException ex) {
            throw new FinderException("Problem creating data processing rules", ex);
        }
    }

    protected RuleBaseConfiguration createRuleBaseConfiguration(
            JBossRuleCreator ruleCreator)
            throws PropositionDefinitionInstantiationException {
        RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setShadowProxy(false);
        try {
            config.setConflictResolver(new PROTEMPAConflictResolver(
                    this.abstractionFinder.getKnowledgeSource(), ruleCreator
                            .getRuleToAbstractionDefinitionMap()));
        } catch (KnowledgeSourceReadException ex) {
            throw new PropositionDefinitionInstantiationException(
                    "Could not instantiate proposition definitions", ex);
        }
        config.setAssertBehaviour(AssertBehaviour.EQUALITY);
        return config;
    }
    
    /**
     * Collect all of the propositions for which we need to create rules.
     * 
     * @param algorithms
     *            an empty {@link Map} that will be populated with algorithms
     *            for each proposition definition for which a rule will be
     *            created.
     * @param result
     *            an empty {@link Set} that will be populated with the
     *            proposition definitions for which rules will be created.
     * @param propIds
     *            the proposition id {@link String}s to be found.
     * @throws org.protempa.ProtempaException
     *             if an error occurs reading the algorithm specified by a
     *             proposition definition.
     */
    private void aggregateDescendants(
            ValidateAlgorithmCheckedVisitor validatorVisitor,
            Set<PropositionDefinition> result,
            List<PropositionDefinition> propDefs) throws FinderException {
        HierarchicalProjectionChildrenVisitor dcVisitor = 
                new HierarchicalProjectionChildrenVisitor(
                this.abstractionFinder.getKnowledgeSource());
        for (PropositionDefinition propDef : propDefs) {
            try {
                propDef.acceptChecked(validatorVisitor);
                propDef.acceptChecked(dcVisitor);
                result.add(propDef);
                aggregateDescendants(validatorVisitor, result,
                        dcVisitor.getChildren());
                dcVisitor.clear();
            } catch (ProtempaException ex) {
                throw new FinderException("Problem reading from knowledge source", ex);
            }
        }
    }

    private class ValidateAlgorithmCheckedVisitor extends
            AbstractPropositionDefinitionCheckedVisitor {

        private final Map<LowLevelAbstractionDefinition, Algorithm> algorithms;
        private final AlgorithmSource algorithmSource;

        ValidateAlgorithmCheckedVisitor(AlgorithmSource algorithmSource) {
            this.algorithms = new HashMap<LowLevelAbstractionDefinition, Algorithm>();
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
        public void visit(PairDefinition pairAbstractionDefinition)
                throws ProtempaException {
        }

        @Override
        public void visit(ConstantDefinition def) throws ProtempaException {
        }
    }
}