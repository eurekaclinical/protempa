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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.base.SalienceInteger;
import org.drools.rule.Collect;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.InvalidRuleException;
import org.drools.rule.Pattern;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.Rule;
import org.drools.spi.Constraint;
import org.drools.spi.PredicateExpression;
import org.drools.spi.Tuple;
import org.protempa.SequentialTemporalPatternDefinition.SubsequentTemporalExtendedPropositionDefinition;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Context;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalProposition;

/**
 * Translates PROTEMPA proposition definitions into Drools (formerly called
 * JBoss) rules.
 *
 * A single instance of this class is created during execution of a PROTEMPA
 * query by the {@link AbstractionFinder}. For proposition definitions that are
 * relevant to a PROTEMPA query, the abstraction finder calls each proposition
 * definition's null {@link PropositionDefinition#accept(org.protempa.PropositionDefinitionVisitor)
 * }
 * method with this instance. The abstraction finder then calls
 * {@link #getRules()} to retrieve the rules thus created. Turn on logging at
 * the <code>FINER</code> level to see what rules get created during PROTEMPA
 * execution.
 *
 * @author Andrew Post
 *
 */
class JBossRuleCreator extends AbstractPropositionDefinitionCheckedVisitor {

    private static final ClassObjectType PRIM_PARAM_OT = new ClassObjectType(
            PrimitiveParameter.class);
    private static final ClassObjectType ARRAY_LIST_OT = new ClassObjectType(
            ArrayList.class);
    private static final ClassObjectType TEMP_PROP_OT = new ClassObjectType(
            TemporalProposition.class);
    private static final ClassObjectType ABSTRACT_PARAM_OT
            = new ClassObjectType(AbstractParameter.class);
    private static final ClassObjectType PROP_OT = new ClassObjectType(
            Proposition.class);
    private static final ClassObjectType CONTEXT_OT = new ClassObjectType(
            Context.class);
    private static final SalienceInteger TWO_SALIENCE = new SalienceInteger(2);
    private static final SalienceInteger ONE_SALIENCE = new SalienceInteger(1);
    private static final SalienceInteger ZERO_SALIENCE = new SalienceInteger(0);
    private static final SalienceInteger MINUS_TWO_SALIENCE = new SalienceInteger(
            -2);
    private static final SalienceInteger MINUS_THREE_SALIENCE = new SalienceInteger(
            -3);
    private static final SalienceInteger MINUS_FOUR_SALIENCE = new SalienceInteger(
            -4);
    private final Map<LowLevelAbstractionDefinition, Algorithm> algorithms;
    private final List<Rule> rules;
    private final Map<Rule, TemporalPropositionDefinition> ruleToAbstractionDefinition;
    private final DerivationsBuilder derivationsBuilder;
    private int inverseIsAIndex = -1;
    private boolean getRulesCalled;
    private final KnowledgeSource knowledgeSource;

    JBossRuleCreator(Map<LowLevelAbstractionDefinition, Algorithm> algorithms,
            DerivationsBuilder derivationsBuilder, KnowledgeSource knowledgeSource) {
        assert knowledgeSource != null : "knowledgeSource cannot be null";
        this.algorithms = algorithms;
        this.rules = new ArrayList<>();
        this.ruleToAbstractionDefinition
                = new HashMap<>();
        this.derivationsBuilder = derivationsBuilder;
        this.knowledgeSource = knowledgeSource;
    }

    /**
     * Gets a mapping from Drools rules to
     * {@link TemporalPropositionDefinition}s that is created during the
     * proposition definition translation process described above. Rules that
     * correspond to other types of {@link PropositionDefinition}s are not
     * stored in this mapping.
     *
     * @return the mapping as a
     * {@link Map<Rule, TemporalPropositionDefinition>}.
     */
    Map<Rule, TemporalPropositionDefinition> getRuleToTPDMap() {
        return this.ruleToAbstractionDefinition;
    }

    @Override
    public void visit(ContextDefinition def)
            throws KnowledgeSourceReadException {
        ProtempaUtil.logger().log(Level.FINER, "Creating rule for {0}", def);
        this.getRulesCalled = false;
        try {
            boolean ruleCreated = false;
            TemporalExtendedPropositionDefinition[] tepds = def.getInducedBy();
            if (tepds.length > 0) {
                Rule inducedByRule = new Rule(def.getId() + "_INDUCED_BY");
                for (int i = 0; i < tepds.length; i++) {
                    Pattern sourceP = new Pattern(i, TEMP_PROP_OT);
                    sourceP.addConstraint(new PredicateConstraint(
                            new GetMatchesPredicateExpression(tepds[i], this.knowledgeSource)));
                    inducedByRule.addPattern(sourceP);
                }
                inducedByRule.setConsequence(
                        new ContextDefinitionInducedByConsequence(def,
                                this.derivationsBuilder));
                inducedByRule.setSalience(MINUS_THREE_SALIENCE);
                this.rules.add(inducedByRule);
                this.ruleToAbstractionDefinition.put(inducedByRule, def);
                ruleCreated = true;
            }

            if (ruleCreated) {
                new ContextCombiner()
                        .toRules(def, rules, this.derivationsBuilder);
            }
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    /**
     * Translates a low-level abstraction definition into rules.
     *
     * @param def a {@link LowLevelAbstractionDefinition}. Cannot be
     * <code>null</code>.
     * @throws KnowledgeSourceReadException if an error occurs accessing the
     * knowledge source during rule creation.
     */
    @Override
    public void visit(LowLevelAbstractionDefinition def)
            throws KnowledgeSourceReadException {
        ProtempaUtil.logger().log(Level.FINER, "Creating rule for {0}", def);
        this.getRulesCalled = false;
        try {
            /*
             * If there are no value definitions defined, we might still have an
             * inverseIsA relationship with another low-level abstraction
             * definition.
             */
            if (!def.getValueDefinitions().isEmpty()) {
                Rule rule = new Rule(def.getId());
                Pattern sourceP = new Pattern(2, 1, PRIM_PARAM_OT, "");
                Set<String> abstractedFrom = def.getAbstractedFrom();
                String[] abstractedFromArr = 
                        abstractedFrom.toArray(new String[abstractedFrom.size()]);
                Set<String> subtrees = this.knowledgeSource.collectSubtrees(abstractedFromArr);
                sourceP.addConstraint(new PredicateConstraint(
                        new PropositionPredicateExpression(subtrees)));
                Pattern resultP = new Pattern(1, 1, ARRAY_LIST_OT, "result");
                resultP.setSource(new Collect(sourceP, new Pattern(1, 1,
                        ARRAY_LIST_OT, "result")));
                resultP.addConstraint(new PredicateConstraint(
                        new CollectionSizeExpression(1)));
                rule.addPattern(resultP);

                String contextId = def.getContextId();
                if (contextId != null) {
                    Pattern sourceP2 = new Pattern(4, 1, CONTEXT_OT, "context");
                    sourceP2.addConstraint(new PredicateConstraint(
                            new PropositionPredicateExpression(contextId)));
                    Pattern resultP2 = new Pattern(3, 1, ARRAY_LIST_OT, "result2");
                    resultP2.setSource(new Collect(sourceP2, new Pattern(3, 1, ARRAY_LIST_OT, "result")));
                    resultP2.addConstraint(new PredicateConstraint(
                            new CollectionSizeExpression(1)));
                    rule.addPattern(resultP2);
                }

                Algorithm algo = this.algorithms.get(def);

                rule.setConsequence(new LowLevelAbstractionConsequence(def,
                        algo, this.derivationsBuilder));
                rule.setSalience(TWO_SALIENCE);
                this.ruleToAbstractionDefinition.put(rule, def);
                rules.add(rule);
            }
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    @Override
    public void visit(CompoundLowLevelAbstractionDefinition def) throws KnowledgeSourceReadException {
        ProtempaUtil.logger().log(Level.FINER, "Creating rule for {0}", def);
        this.getRulesCalled = false;
        try {
            if (!def.getLowLevelAbstractionIds().isEmpty()) {
                Rule rule = new Rule(def.getId());
                Pattern sourceP = new Pattern(2, 1, ABSTRACT_PARAM_OT, "");
                Set<String> abstractedFrom = def.getAbstractedFrom();
                String[] abstractedFromArr = 
                        abstractedFrom.toArray(new String[abstractedFrom.size()]);
                Set<String> subtrees = this.knowledgeSource.collectSubtrees(abstractedFromArr);
                sourceP.addConstraint(new PredicateConstraint(
                        new AbstractParameterPredicateExpression(subtrees, def.getContextId())));
                Pattern resultP = new Pattern(1, 1, ARRAY_LIST_OT, "result");
                resultP.setSource(new Collect(sourceP,
                        new Pattern(1, 1, ARRAY_LIST_OT, "result")));
                resultP.addConstraint(
                        new PredicateConstraint(
                                new CollectionSizeExpression(1)));
                rule.addPattern(resultP);
                rule.setConsequence(
                        new CompoundLowLevelAbstractionConsequence(def,
                                this.derivationsBuilder));
                rule.setSalience(ONE_SALIENCE);
                this.ruleToAbstractionDefinition.put(rule, def);
                rules.add(rule);
                new AbstractionCombiner()
                        .toRules(def, rules, this.derivationsBuilder);
            }
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    private static final class GetMatchesPredicateExpression implements
            PredicateExpression {

        private static final long serialVersionUID = -6225160728904051528L;
        private final ExtendedPropositionDefinition epd;
        private final Set<String> subtrees;

        private GetMatchesPredicateExpression(ExtendedPropositionDefinition epd, KnowledgeSource knowledgeSource) throws KnowledgeSourceReadException {
            assert epd != null : "epd cannot be null";
            this.epd = epd;
            this.subtrees = knowledgeSource.collectSubtrees(epd.getPropositionId());
        }

        Set<String> getSubtrees() {
            return subtrees;
        }

        @Override
        public boolean evaluate(Object arg0, Tuple arg1, Declaration[] arg2,
                Declaration[] arg3, WorkingMemory arg4, Object context)
                throws Exception {
            return epd.getMatches((Proposition) arg0, subtrees);
        }

        @Override
        public Object createContext() {
            return null;
        }
    }

    /**
     * Translates a high-level abstraction definition into rules.
     *
     * @param def a {@link HighLevelAbstractionDefinition}. Cannot be
     * <code>null</code>.
     * @throws KnowledgeSourceReadException if an error occurs accessing the
     * knowledge source during rule creation.
     */
    @Override
    public void visit(HighLevelAbstractionDefinition def) throws KnowledgeSourceReadException {
        ProtempaUtil.logger().log(Level.FINER, "Creating rule for {0}", def);
        this.getRulesCalled = false;
        try {
            Set<ExtendedPropositionDefinition> epdsC = def
                    .getExtendedPropositionDefinitions();

            /*
             * If there are no extended proposition definitions defined, we
             * might still have an inverseIsA relationship with another
             * high-level abstraction definition.
             */
            if (!epdsC.isEmpty()) {
                Rule rule = new Rule(def.getId());
                rule.setSalience(TWO_SALIENCE);
                ExtendedPropositionDefinition[] epds = epdsC
                        .toArray(new ExtendedPropositionDefinition[epdsC.size()]);
                Set<String> propIds = new HashSet<>();
                for (int i = 0; i < epds.length; i++) {
                    Pattern p = new Pattern(i, PROP_OT);
                    GetMatchesPredicateExpression matchesPredicateExpression = new GetMatchesPredicateExpression(epds[i], this.knowledgeSource);
                    propIds.addAll(matchesPredicateExpression.getSubtrees());
                    Constraint c = new PredicateConstraint(
                            matchesPredicateExpression);
                    p.addConstraint(c);
                    rule.addPattern(p);
                }
                rule.addPattern(new EvalCondition(
                        new HighLevelAbstractionCondition(def, epds), null));
                rule.setConsequence(new HighLevelAbstractionConsequence(def,
                        epds, this.derivationsBuilder));
                this.ruleToAbstractionDefinition.put(rule, def);
                rules.add(rule);
                new AbstractionCombiner()
                        .toRules(def, rules, this.derivationsBuilder);
            }
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    /**
     * Translates a slice definition into rules.
     *
     * @param def a {@link SliceDefinition}. Cannot be <code>null</code>.
     * @throws KnowledgeSourceReadException if an error occurs accessing the
     * knowledge source during rule creation.
     */
    @Override
    public void visit(SliceDefinition def) throws KnowledgeSourceReadException {
        ProtempaUtil.logger().log(Level.FINER, "Creating rule for {0}", def);
        this.getRulesCalled = false;
        try {
            Set<TemporalExtendedPropositionDefinition> epdsC = def
                    .getTemporalExtendedPropositionDefinitions();
            if (!epdsC.isEmpty()) {
                TemporalExtendedPropositionDefinition[] epds = epdsC
                        .toArray(new TemporalExtendedPropositionDefinition[epdsC.size()]);
                Rule rule = new Rule("SLICE_" + def.getId());

                Pattern sourceP = new Pattern(2, 1, TEMP_PROP_OT, "");
                for (int i = 0; i < epds.length; i++) {
                    GetMatchesPredicateExpression matchesPredicateExpression = new GetMatchesPredicateExpression(epds[i], this.knowledgeSource);
                    Constraint c = new PredicateConstraint(
                            matchesPredicateExpression);
                    sourceP.addConstraint(c);
                }

                Pattern resultP = new Pattern(1, 1, ARRAY_LIST_OT, "result");
                resultP.setSource(new Collect(sourceP, new Pattern(1, 1,
                        ARRAY_LIST_OT, "result")));
                int len;
                int minInd = def.getMinIndex();
                int maxInd = def.getMaxIndex();
                if (maxInd < 0) {
                    len = Math.abs(minInd);
                } else {
                    len = maxInd;
                }
                resultP.addConstraint(new PredicateConstraint(
                        new CollectionSizeExpression(len)));
                rule.addPattern(resultP);
                rule.setConsequence(new SliceConsequence(def,
                        this.derivationsBuilder));
                rule.setSalience(MINUS_TWO_SALIENCE);
                this.ruleToAbstractionDefinition.put(rule, def);
                rules.add(rule);
            }
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    /**
     * Translates a sequential temporal pattern definition into rules.
     *
     * @param def a {@link PairDefinition}. Cannot be <code>null</code>.
     * @throws KnowledgeSourceReadException if an error occurs accessing the
     * knowledge source during rule creation.
     */
    @Override
    public void visit(SequentialTemporalPatternDefinition def) throws ProtempaException {
        ProtempaUtil.logger().log(Level.FINER, "Creating rule for {0}", def);
        this.getRulesCalled = false;
        try {
            TemporalExtendedPropositionDefinition lhs = def.getFirstTemporalExtendedPropositionDefinition();
            if (lhs != null) {
                Rule rule = new Rule("SEQ_TP_" + def.getId());
                Pattern sourceP = new Pattern(2, TEMP_PROP_OT);
                GetMatchesPredicateExpression matchesPredicateExpression = new GetMatchesPredicateExpression(lhs, this.knowledgeSource);
                sourceP.addConstraint(new PredicateConstraint(
                        matchesPredicateExpression));
                SubsequentTemporalExtendedPropositionDefinition[] relatedTemporalExtendedPropositionDefinitions = def.getSubsequentTemporalExtendedPropositionDefinitions();
                for (int i = 0; i < relatedTemporalExtendedPropositionDefinitions.length; i++) {
                    SubsequentTemporalExtendedPropositionDefinition rtepd
                            = relatedTemporalExtendedPropositionDefinitions[i];
                    GetMatchesPredicateExpression matchesPredicateExpression1 = new GetMatchesPredicateExpression(
                            rtepd.getRelatedTemporalExtendedPropositionDefinition(), this.knowledgeSource);
                    Constraint c = new PredicateConstraint(
                            matchesPredicateExpression1);
                    sourceP.addConstraint(c);
                }

                Pattern resultP = new Pattern(1, 1, ARRAY_LIST_OT, "result");
                resultP.setSource(new Collect(sourceP, new Pattern(1, 1,
                        ARRAY_LIST_OT, "result")));
                resultP.addConstraint(new PredicateConstraint(
                        new CollectionSizeExpression(1)));
                rule.addPattern(resultP);
                rule.setConsequence(new SequentialTemporalPatternConsequence(def,
                        this.derivationsBuilder));
                rule.setSalience(MINUS_TWO_SALIENCE);
                this.ruleToAbstractionDefinition.put(rule, def);
                rules.add(rule);
                new AbstractionCombiner()
                        .toRules(def, rules, this.derivationsBuilder);
            }
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    /**
     * Translates an event definition into rules.
     *
     * @param def an {@link EventDefinition}. Cannot be <code>null</code>.
     * @throws KnowledgeSourceReadException if an error occurs accessing the
     * knowledge source during rule creation.
     */
    @Override
    public void visit(EventDefinition def) {
        this.getRulesCalled = false;

    }

    /**
     * Translates a constant definition into rules.
     *
     * @param def a {@link ConstantDefinition}. Cannot be <code>null</code>.
     * @throws KnowledgeSourceReadException if an error occurs accessing the
     * knowledge source during rule creation.
     */
    @Override
    public void visit(ConstantDefinition def) {
        this.getRulesCalled = false;
    }

    /**
     * Translates a primitive parameter definition into rules.
     *
     * @param def a {@link PrimitiveParameterDefinition}. Cannot be
     * <code>null</code>.
     * @throws KnowledgeSourceReadException if an error occurs accessing the
     * knowledge source during rule creation.
     */
    @Override
    public void visit(PrimitiveParameterDefinition def) {
        this.getRulesCalled = false;
    }

    /**
     * Returns a newly-created an array of rules that were generated.
     *
     * @return a {@link Rule[]}. Guaranteed not <code>null</code>.
     */
    Rule[] getRules() {
        this.getRulesCalled = true;
        return this.rules.toArray(new Rule[this.rules.size()]);
    }

}
