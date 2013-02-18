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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.arp.javautil.collections.Collections;

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
 * the
 * <code>FINER</code> level to see what rules get created during PROTEMPA
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
    private static final ClassObjectType ABSTRACT_PARAM_OT =
            new ClassObjectType(AbstractParameter.class);
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
    private final Map<String, List<String>> inverseIsAPropIdMap;
    private int inverseIsAIndex = -1;
    private boolean getRulesCalled;

    JBossRuleCreator(Map<LowLevelAbstractionDefinition, Algorithm> algorithms,
            DerivationsBuilder derivationsBuilder) {
        this.algorithms = algorithms;
        this.rules = new ArrayList<Rule>();
        this.ruleToAbstractionDefinition =
                new HashMap<Rule, TemporalPropositionDefinition>();
        this.derivationsBuilder = derivationsBuilder;
        this.inverseIsAPropIdMap = new HashMap<String, List<String>>();
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
                            new GetMatchesPredicateExpression(tepds[i])));
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
                addInverseIsARule(def);
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
        if (def.getId().startsWith("USER:")) {
            System.err.println("Creating rule for " + def);
        }
        ProtempaUtil.logger().log(Level.FINER, "Creating rule for {0}", def);
        this.getRulesCalled = false;
        try {
            /*
             * If there are no value definitions defined, we might still have an
             * inverseIsA relationship with another low-level abstraction
             * definition.
             */
            if (!def.getValueDefinitions().isEmpty()) {
                if (def.getId().startsWith("USER:")) {
                    System.err.println("In here");
                }
                Rule rule = new Rule(def.getId());
                Pattern sourceP = new Pattern(2, 1, PRIM_PARAM_OT, "");
                sourceP.addConstraint(new PredicateConstraint(
                        new PropositionPredicateExpression(def
                        .getAbstractedFrom())));
                Pattern resultP = new Pattern(1, 1, ARRAY_LIST_OT, "result");
                resultP.setSource(new Collect(sourceP, new Pattern(1, 1,
                        ARRAY_LIST_OT, "result")));
                resultP.addConstraint(new PredicateConstraint(
                        new CollectionSizeExpression(1)));
                rule.addPattern(resultP);

                String contextId = def.getContextId();
                if (def.getId().startsWith("USER:")) {
                    System.err.println("Context is " + (contextId == null ? "is null!" : "not null: " + contextId));
                }
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
            addInverseIsARule(def);
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    @Override
    public void visit(CompoundLowLevelAbstractionDefinition def) {
        ProtempaUtil.logger().log(Level.FINER, "Creating rule for {0}", def);
        this.getRulesCalled = false;
        try {
            if (!def.getLowLevelAbstractionIds().isEmpty()) {
                Rule rule = new Rule(def.getId());
                Pattern sourceP = new Pattern(2, 1, ABSTRACT_PARAM_OT, "");
                sourceP.addConstraint(new PredicateConstraint(
                        new AbstractParameterPredicateExpression(def
                        .getAbstractedFrom(), def.getContextId())));
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
            addInverseIsARule(def);
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    private static final class GetMatchesPredicateExpression implements
            PredicateExpression {

        private static final long serialVersionUID = -6225160728904051528L;
        private ExtendedPropositionDefinition epd;

        private GetMatchesPredicateExpression(ExtendedPropositionDefinition epd) {
            assert epd != null : "epd cannot be null";
            this.epd = epd;
        }

        @Override
        public boolean evaluate(Object arg0, Tuple arg1, Declaration[] arg2,
                Declaration[] arg3, WorkingMemory arg4, Object context)
                throws Exception {
            return epd.getMatches((Proposition) arg0);
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
    public void visit(HighLevelAbstractionDefinition def) {
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
                for (int i = 0; i < epds.length; i++) {
                    Pattern p = new Pattern(i, PROP_OT);
                    Constraint c = new PredicateConstraint(
                            new GetMatchesPredicateExpression(epds[i]));
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
            addInverseIsARule(def);
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
    public void visit(SliceDefinition def) {
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
                    Constraint c =
                            new PredicateConstraint(
                            new GetMatchesPredicateExpression(epds[i]));
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
            addInverseIsARule(def);
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

                sourceP.addConstraint(new PredicateConstraint(
                        new GetMatchesPredicateExpression(lhs)));
                SubsequentTemporalExtendedPropositionDefinition[] relatedTemporalExtendedPropositionDefinitions = def.getSubsequentTemporalExtendedPropositionDefinitions();
                for (int i = 0; i < relatedTemporalExtendedPropositionDefinitions.length; i++) {
                    SubsequentTemporalExtendedPropositionDefinition rtepd = 
                            relatedTemporalExtendedPropositionDefinitions[i];
                    Constraint c = new PredicateConstraint(
                            new GetMatchesPredicateExpression(
                            rtepd.getRelatedTemporalExtendedPropositionDefinition()));
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
            
            addInverseIsARule(def);
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
        try {
            addInverseIsARule(def);
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }

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
        try {
            addInverseIsARule(def);
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
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
        try {
            addInverseIsARule(def);
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    /**
     * Returns a newly-created an array of rules that were generated.
     *
     * @return a {@link Rule[]}. Guaranteed not <code>null</code>.
     */
    Rule[] getRules() {
        if (!getRulesCalled) {
            constructInverseIsARule();
        }
        this.getRulesCalled = true;
        return this.rules.toArray(new Rule[this.rules.size()]);
    }

    /**
     * Populates a map of isA relationships between proposition ids. When
     * {@link #getRules()} is called, this mapping is used to create a single
     * Drools rule for deriving such propositions.
     *
     * @param def a {@link PropositionDefinition}.
     */
    private void addInverseIsARule(PropositionDefinition def) {
        String[] inverseIsA = def.getInverseIsA();
        for (String propId : inverseIsA) {
            Collections.putList(this.inverseIsAPropIdMap, propId, def.getId());
        }
    }

    /**
     * Constructs a Drools rule for deriving propositions based on inverseIsA
     * relationships. There is one rule that computes all such derived
     * propositions, which performs much much better than creating a separate
     * rule for each inverseIsA relationship in the knowledge source.
     */
    private void constructInverseIsARule() {
        Logger logger = ProtempaUtil.logger();
        if (this.inverseIsAIndex > -1) {
            this.rules.remove(this.inverseIsAIndex);
            this.inverseIsAIndex = -1;
        }
        if (!this.inverseIsAPropIdMap.isEmpty()) {
            if (logger.isLoggable(Level.FINER)) {
                logCreatingInverseIsA(logger);
            }
            Constraint c = new PredicateConstraint(
                    new PropositionPredicateExpression(
                    this.inverseIsAPropIdMap.keySet()));
            Pattern p = new Pattern(0, PROP_OT);
            p.addConstraint(c);
            Rule rule = new Rule("INVERSEISA_GLOBAL");
            rule.addPattern(p);
            rule.setConsequence(new InverseIsAConsequence(
                    this.inverseIsAPropIdMap, this.derivationsBuilder));
            rule.setSalience(ZERO_SALIENCE);
            this.inverseIsAIndex = rules.size();
            rules.add(rule);
        } else {
            logger.log(Level.FINER, "No inverseIsA rule created");
        }
    }

    private void logCreatingInverseIsA(Logger logger) {
        Set<String> propIds = new HashSet<String>();
        for (List<String> pIds : this.inverseIsAPropIdMap.values()) {
            propIds.addAll(pIds);
        }
        logger.log(Level.FINER, "Creating inverseIsA rule for {0}",
                StringUtils.join(propIds, ", "));
    }
}
