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
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalProposition;

/**
 * Translates PROTEMPA proposition definitions into Drools (formerly called 
 * JBoss) rules.
 * 
 * A single instance of this class is created during execution of a PROTEMPA 
 * query by the {@link AbstractionFinder}. For proposition definitions that are 
 * relevant to a PROTEMPA query, the abstraction finder calls each 
 * proposition definition's {@link PropositionDefinition#accept(org.protempa.PropositionDefinitionVisitor) }
 * method with this instance. The abstraction finder then calls {@link #getRules()}
 * to retrieve the rules thus created. Turn on logging at the 
 * <code>FINER</code> level to see what rules get created during PROTEMPA
 * execution.
 * 
 * @author Andrew Post
 * 
 */
class JBossRuleCreator extends AbstractPropositionDefinitionCheckedVisitor {

    private static final ClassObjectType PRIM_PARAM_OT =
            new ClassObjectType(PrimitiveParameter.class);
    private static final ClassObjectType ARRAY_LIST_OT = new ClassObjectType(
            ArrayList.class);
    private static final ClassObjectType TEMP_PROP_OT = new ClassObjectType(
            TemporalProposition.class);
    private static final ClassObjectType PROP_OT = new ClassObjectType(
            Proposition.class);
    private static final SalienceInteger ONE_SALIENCE = new SalienceInteger(1);
    private static final SalienceInteger MINUS_TWO_SALIENCE =
            new SalienceInteger(-2);
    private final Map<LowLevelAbstractionDefinition, Algorithm> algorithms;
    private final List<Rule> rules;
    private final Map<Rule, AbstractionDefinition> ruleToAbstractionDefinition;
    private final DerivationsBuilder derivationsBuilder;
    private final Map<String, List<String>> inverseIsAPropIdMap;
    private int inverseIsAIndex = -1;
    private boolean getRulesCalled;

    JBossRuleCreator(Map<LowLevelAbstractionDefinition, Algorithm> algorithms,
            DerivationsBuilder derivationsBuilder) {
        this.algorithms = algorithms;
        this.rules = new ArrayList<Rule>();
        this.ruleToAbstractionDefinition =
                new HashMap<Rule, AbstractionDefinition>();
        this.derivationsBuilder = derivationsBuilder;
        this.inverseIsAPropIdMap = new HashMap<String, List<String>>();
    }

    /**
     * Gets a mapping from Drools rules to {@link AbstractionDefinition}s that
     * is created during the proposition definition translation process
     * described above. Rules that correspond to other types of 
     * {@link PropositionDefinition}s are not stored in this mapping.
     * 
     * @return the mapping as a {@link Map<Rule, AbstractionDefinition>}.
     */
    Map<Rule, AbstractionDefinition> getRuleToAbstractionDefinitionMap() {
        return this.ruleToAbstractionDefinition;
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
             * If there are no value definitions defined, we
             * might still have an inverseIsA relationship with another
             * low-level abstraction definition.
             */
            if (!def.getValueDefinitions().isEmpty()) {
                Rule rule = new Rule(def.getId());
                Pattern sourceP = new Pattern(2, 1, PRIM_PARAM_OT, "");
                sourceP.addConstraint(new PredicateConstraint(
                        new PropositionPredicateExpression(
                        def.getAbstractedFrom())));

                Pattern resultP = new Pattern(1, 1, ARRAY_LIST_OT, "result");
                resultP.setSource(new Collect(sourceP, new Pattern(1, 1,
                        ARRAY_LIST_OT, "result")));
                resultP.addConstraint(new PredicateConstraint(
                        new CollectionSizeExpression(1)));
                rule.addPattern(resultP);

                Algorithm algo = this.algorithms.get(def);

                rule.setConsequence(new LowLevelAbstractionConsequence(def,
                        algo, this.derivationsBuilder));
                rule.setSalience(ONE_SALIENCE);
                this.ruleToAbstractionDefinition.put(rule, def);
                rules.add(rule);
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
        private TemporalExtendedPropositionDefinition tepd;

        private GetMatchesPredicateExpression(
                TemporalExtendedPropositionDefinition tepd) {
            assert tepd != null : "tepd cannot be null";
            this.tepd = tepd;
        }

        @Override
        public boolean evaluate(Object arg0, Tuple arg1, Declaration[] arg2,
                Declaration[] arg3, WorkingMemory arg4, Object context)
                throws Exception {
            return this.tepd.getMatches((Proposition) arg0);
        }

        @Override
        public Object createContext() {
            return null;
        }
    }

    private static final class GetMatchesPredicateExpressionPair implements
            PredicateExpression {

        private static final long serialVersionUID = -6225160728904051528L;
        private TemporalExtendedPropositionDefinition leftHandSide;
        private TemporalExtendedPropositionDefinition rightHandSide;

        private GetMatchesPredicateExpressionPair(
                TemporalExtendedPropositionDefinition leftHandSide,
                TemporalExtendedPropositionDefinition rightHandSide) {
            assert leftHandSide != null : "leftHandSide cannot be null";
            assert rightHandSide != null : "rightHandSide cannot be null";
            this.leftHandSide = leftHandSide;
            this.rightHandSide = rightHandSide;
        }

        @Override
        public boolean evaluate(Object arg0, Tuple arg1, Declaration[] arg2,
                Declaration[] arg3, WorkingMemory arg4, Object context)
                throws Exception {
            return this.leftHandSide.getMatches((Proposition) arg0)
                    || (this.rightHandSide != null
                    && this.rightHandSide.getMatches((Proposition) arg0));
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
            Set<ExtendedPropositionDefinition> epdsC = def.getExtendedPropositionDefinitions();
            /*
             * If there are no extended proposition definitions defined, we
             * might still have an inverseIsA relationship with another
             * high-level abstraction definition.
             */
            if (!epdsC.isEmpty()) {
                Rule rule = new Rule(def.getId());
                rule.setSalience(ONE_SALIENCE);
                TemporalExtendedPropositionDefinition[] epds =
                        epdsC.toArray(new TemporalExtendedPropositionDefinition[epdsC.size()]);
                for (int i = 0; i < epds.length; i++) {
                    Pattern p = new Pattern(i, TEMP_PROP_OT);
                    Constraint c = new PredicateConstraint(
                            new GetMatchesPredicateExpression(epds[i]));
                    p.addConstraint(c);
                    rule.addPattern(p);
                }
                rule.addPattern(new EvalCondition(
                        new HighLevelAbstractionCondition(def, epds), null));
                rule.setConsequence(new HighLevelAbstractionConsequence(def, epds,
                        this.derivationsBuilder));
                this.ruleToAbstractionDefinition.put(rule, def);
                rules.add(rule);
            }
            addInverseIsARule(def);
            AbstractionCombiner.toRules(def, rules,
                        this.derivationsBuilder);
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
            Rule rule = new Rule("SLICE_" + def.getId());
            Pattern sourceP = new Pattern(2, 1, TEMP_PROP_OT, "");
            sourceP.addConstraint(new PredicateConstraint(
                    new PropositionPredicateExpression(
                    def.getAbstractedFrom())));

            Pattern resultP = new Pattern(1, 1, ARRAY_LIST_OT, "result");
            resultP.setSource(new Collect(sourceP, new Pattern(1, 1,
                    ARRAY_LIST_OT, "result")));
            resultP.addConstraint(new PredicateConstraint(
                    new CollectionSizeExpression(1)));
            rule.addPattern(resultP);
            rule.setConsequence(new SliceConsequence(def, this.derivationsBuilder));
            rule.setSalience(MINUS_TWO_SALIENCE);
            this.ruleToAbstractionDefinition.put(rule, def);
            rules.add(rule);
            addInverseIsARule(def);
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    /**
     * Translates a pair definition into rules.
     * 
     * @param def a {@link PairDefinition}. Cannot be <code>null</code>.
     * @throws KnowledgeSourceReadException if an error occurs accessing the
     * knowledge source during rule creation.
     */
    @Override
    public void visit(PairDefinition def) throws ProtempaException {
        ProtempaUtil.logger().log(Level.FINER, "Creating rule for {0}", def);
        this.getRulesCalled = false;
        try {

            /*
             * If there are no extended proposition definitions defined, we
             * might still have an inverseIsA relationship with another
             * pair abstraction definition.
             */
            if (def.getRelation() != null) {
                TemporalExtendedPropositionDefinition lhProp =
                        def.getLeftHandProposition();
                TemporalExtendedPropositionDefinition rhProp =
                        def.getRightHandProposition();
                boolean secondRequired = def.isSecondRequired();
                if (lhProp != null && (!secondRequired || rhProp != null)) {
                    Rule rule = new Rule("PAIR_" + def.getId());
                    Pattern sourceP = new Pattern(2, 1, TEMP_PROP_OT, "");

                    sourceP.addConstraint(new PredicateConstraint(
                            new GetMatchesPredicateExpressionPair(lhProp, rhProp)));

                    Pattern resultP = new Pattern(1, 1, ARRAY_LIST_OT, "result");
                    resultP.setSource(new Collect(sourceP, new Pattern(1, 1,
                            ARRAY_LIST_OT, "result")));
                    if (secondRequired) {
                        resultP.addConstraint(new PredicateConstraint(
                                new CollectionSizeExpression(2)));
                    } else {
                        resultP.addConstraint(new PredicateConstraint(
                                new CollectionSizeExpression(1)));
                    }
                    rule.addPattern(resultP);
                    rule.setConsequence(new PairConsequence(def, this.derivationsBuilder));
                    rule.setSalience(MINUS_TWO_SALIENCE);
                    this.ruleToAbstractionDefinition.put(rule, def);
                    rules.add(rule);
                } else {
                    assert lhProp != null : "lhProp should not be null in "
                            + def.getId();
                    assert rhProp != null : "rhProp should not be null in "
                            + def.getId();
                }
            }
            addInverseIsARule(def);
            AbstractionCombiner.toRules(def, rules,
                    this.derivationsBuilder);
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
            rule.setSalience(ONE_SALIENCE);
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