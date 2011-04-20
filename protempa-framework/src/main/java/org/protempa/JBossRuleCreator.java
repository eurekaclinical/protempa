package org.protempa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

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
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalProposition;

/**
 * Visitor that translates PROTEMPA proposition definitions into JBoss rules.
 * 
 * @author Andrew Post
 * 
 */
class JBossRuleCreator extends AbstractPropositionDefinitionCheckedVisitor {

//    private static final ClassObjectType SEQUENCE_OBJECT_TYPE = new ClassObjectType(
//            Sequence.class);
    private static final ClassObjectType EVENT_OT = new ClassObjectType(
            Event.class);
    private static final ClassObjectType CONSTANT_OT = new ClassObjectType(
            Constant.class);
    private static final ClassObjectType PRIM_PARAM_OT =
            new ClassObjectType(PrimitiveParameter.class);
    private static final ClassObjectType ARRAY_LIST_OT = new ClassObjectType(
            ArrayList.class);
    private static final ClassObjectType TEMP_PROP_OT = new ClassObjectType(
            TemporalProposition.class);
    private static final ClassObjectType ABSTRACT_PARAM_OT =
            new ClassObjectType(AbstractParameter.class);
    private static final SalienceInteger ONE_SALIENCE = new SalienceInteger(1);
    private static final SalienceInteger MINUS_TWO_SALIENCE =
            new SalienceInteger(-2);

    private final Map<LowLevelAbstractionDefinition, Algorithm> algorithms;
    private final List<Rule> rules;
    private final Map<Rule, AbstractionDefinition> ruleToAbstractionDefinition;
    private final DerivationsBuilder derivationsBuilder;

    JBossRuleCreator(Map<LowLevelAbstractionDefinition, Algorithm> algorithms,
                DerivationsBuilder derivationsBuilder) {
        this.algorithms = algorithms;
        this.rules = new ArrayList<Rule>();
        this.ruleToAbstractionDefinition =
                new HashMap<Rule, AbstractionDefinition>();
        this.derivationsBuilder = derivationsBuilder;
    }

    Map<Rule, AbstractionDefinition> getRuleToAbstractionDefinitionMap() {
        return this.ruleToAbstractionDefinition;
    }

    @Override
    public void visit(LowLevelAbstractionDefinition def)
            throws KnowledgeSourceReadException {
        ProtempaUtil.logger().log(Level.FINER, "Creating rule for {0}", def);
        try {
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
//            Pattern p = new Pattern(0, SEQUENCE_OBJECT_TYPE);
//
//            Set<String> propIds = def.getAbstractedFrom();
//            Constraint c = new PredicateConstraint(
//                    new SequencePredicateExpression(propIds));
//            p.addConstraint(c);
//            rule.addPattern(p);
            Algorithm algo = this.algorithms.get(def);

            rule.setConsequence(new LowLevelAbstractionConsequence(def, algo,
                    this.derivationsBuilder));
            rule.setSalience(ONE_SALIENCE);
            this.ruleToAbstractionDefinition.put(rule, def);
            rules.add(rule);
            addInverseIsARule(def, ABSTRACT_PARAM_OT);
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
            this.tepd = tepd;
        }

        @Override
        public boolean evaluate(Object arg0, Tuple arg1, Declaration[] arg2,
                Declaration[] arg3, WorkingMemory arg4, Object context)
                throws Exception {
            return this.tepd == null
                    || this.tepd.getMatches((Proposition) arg0);
        }

        @Override
        public Object createContext() {
            return null;
        }
    }

    private static final class GetMatchesPredicateExpressionPair implements
            PredicateExpression {

        private static final long serialVersionUID = -6225160728904051528L;
        private TemporalExtendedPropositionDefinition tepd;
        private TemporalExtendedPropositionDefinition rightHandSide;

        private GetMatchesPredicateExpressionPair(
                TemporalExtendedPropositionDefinition leftHandSide,
                TemporalExtendedPropositionDefinition rightHandSide) {
            this.tepd = leftHandSide;
            this.rightHandSide = rightHandSide;
        }

        @Override
        public boolean evaluate(Object arg0, Tuple arg1, Declaration[] arg2,
                Declaration[] arg3, WorkingMemory arg4, Object context)
                throws Exception {
            return this.tepd.getMatches((Proposition) arg0)
                    || this.rightHandSide.getMatches((Proposition) arg0);
        }

        @Override
        public Object createContext() {
            return null;
        }
    }

    @Override
    public void visit(HighLevelAbstractionDefinition def) {
        ProtempaUtil.logger().log(Level.FINER, "Creating rule for {0}", def);
        try {
            Rule rule = new Rule(def.getId());
            rule.setSalience(ONE_SALIENCE);
            Set<ExtendedPropositionDefinition> epdsC = def
                    .getExtendedPropositionDefinitions();
            TemporalExtendedPropositionDefinition[] epds = epdsC
                    .toArray(new TemporalExtendedPropositionDefinition[epdsC
                            .size()]);
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
            AbstractionCombiner.toRules(def, rules);
            addInverseIsARule(def, ABSTRACT_PARAM_OT);
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    @Override
    public void visit(SliceDefinition def) {
        ProtempaUtil.logger().log(Level.FINER, "Creating rule for {0}", def);
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
            addInverseIsARule(def, ABSTRACT_PARAM_OT);
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    @Override
    public void visit(PairDefinition def) throws ProtempaException {
        ProtempaUtil.logger().log(Level.FINER, "Creating rule for {0}", def);
        try {
            Rule rule = new Rule("PAIR_" + def.getId());
            Pattern sourceP = new Pattern(2, 1, TEMP_PROP_OT, "");
            sourceP.addConstraint(new PredicateConstraint(
                    new PropositionPredicateExpression(
                    def.getAbstractedFrom())));
            TemporalExtendedPropositionDefinition lhProp =
                    def.getLeftHandProposition();
            assert lhProp != null : "lhProp should not be null";
            TemporalExtendedPropositionDefinition rhProp =
                    def.getRightHandProposition();
            assert rhProp != null : "rhProp should not be null";
            sourceP.addConstraint(new PredicateConstraint(
                    new GetMatchesPredicateExpressionPair(lhProp, rhProp)));

            Pattern resultP = new Pattern(1, 1, ARRAY_LIST_OT, "result");
            resultP.setSource(new Collect(sourceP, new Pattern(1, 1,
                    ARRAY_LIST_OT, "result")));
            resultP.addConstraint(new PredicateConstraint(
                    new CollectionSizeExpression(2)));
            rule.addPattern(resultP);
            rule.setConsequence(new PairConsequence(def, this.derivationsBuilder));
            rule.setSalience(MINUS_TWO_SALIENCE);
            this.ruleToAbstractionDefinition.put(rule, def);
            rules.add(rule);
            addInverseIsARule(def, ABSTRACT_PARAM_OT);
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    @Override
    public void visit(EventDefinition def) {
        try {
            addInverseIsARule(def, EVENT_OT);
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }

    }

    @Override
    public void visit(ConstantDefinition def) {
        try {
            addInverseIsARule(def, CONSTANT_OT);
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    @Override
    public void visit(PrimitiveParameterDefinition def) {
        try {
            addInverseIsARule(def, PRIM_PARAM_OT);
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    /**
     * Get the rules that were created.
     * 
     * @return a {@link List}.
     */
    List<Rule> getRules() {
        return this.rules;
    }

    private void addInverseIsARule(PropositionDefinition def,
            ClassObjectType objectType) {
        String[] inverseIsA = def.getInverseIsA();
        if (inverseIsA.length > 0) {
            String propId = def.getId();
            ProtempaUtil.logger().log(Level.FINER,
                    "Creating inverseIsA rule for {0}", def);
            Constraint c = new PredicateConstraint(
                    new PropositionPredicateExpression(inverseIsA));
            Pattern p = new Pattern(0, objectType);
            p.addConstraint(c);
            Rule rule = new Rule("INVERSEISA_" + propId);
            rule.addPattern(p);
            rule.setConsequence(new InverseIsAConsequence(propId,
                    this.derivationsBuilder));
            rule.setSalience(ONE_SALIENCE);
            rules.add(rule);
        }
    }
}
