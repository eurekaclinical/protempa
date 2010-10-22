package org.protempa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import org.protempa.proposition.Constant;
import org.protempa.proposition.Event;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.TemporalProposition;

/**
 * Visitor that translates PROTEMPA proposition definitions into JBoss rules.
 * 
 * @author Andrew Post
 * 
 */
class JBossRuleCreator extends AbstractPropositionDefinitionCheckedVisitor {

    private final Map<LowLevelAbstractionDefinition, Algorithm> algorithms;
    private final KnowledgeSource knowledgeSource;
    private final List<Rule> rules;
    private final Map<Rule, AbstractionDefinition> ruleToAbstractionDefinition;
    private final ClassObjectType SEQUENCE_OBJECT_TYPE = new ClassObjectType(
            Sequence.class);
    private final ClassObjectType PROPOSITION_OBJECT_TYPE = new ClassObjectType(
            Proposition.class);
    private final ClassObjectType EVENT_OBJECT_TYPE = new ClassObjectType(
            Event.class);
    private final ClassObjectType CONSTANT_OBJECT_TYPE = new ClassObjectType(
            Constant.class);
    private static final ClassObjectType ARRAY_LIST_OT = new ClassObjectType(
            ArrayList.class);
    private static final ClassObjectType TEMP_PROP_OT = new ClassObjectType(
            TemporalProposition.class);
    private final Map<Proposition, List<Proposition>> derivations;

    JBossRuleCreator(Map<LowLevelAbstractionDefinition, Algorithm> algorithms,
            KnowledgeSource knowledgeSource,
            Map<Proposition, List<Proposition>> derivations) {
        this.algorithms = algorithms;
        this.knowledgeSource = knowledgeSource;
        this.rules = new ArrayList<Rule>();
        this.ruleToAbstractionDefinition = new HashMap<Rule, AbstractionDefinition>();
        this.derivations = derivations;
    }

    /**
     * @return the knowledgeSource
     */
    KnowledgeSource getKnowledgeSource() {
        return this.knowledgeSource;
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
            Pattern p = new Pattern(0, SEQUENCE_OBJECT_TYPE);

            Set<String> propIds = this.knowledgeSource
                    .primitiveParameterIds(def.getId());
            Constraint c = new PredicateConstraint(
                    new SequencePredicateExpression(propIds));
            p.addConstraint(c);
            rule.addPattern(p);
            Algorithm algo = this.algorithms.get(def);

            rule.setConsequence(new LowLevelAbstractionConsequence(def, algo,
                    this.derivations));
            rule.setSalience(new SalienceInteger(1));
            this.ruleToAbstractionDefinition.put(rule, def);
            rules.add(rule);
            addInverseIsARule(def.getId(), def.getInverseIsA(),
                    SEQUENCE_OBJECT_TYPE);
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    private static final class GetMatchesPredicateExpression implements
            PredicateExpression {

        private static final long serialVersionUID = -6225160728904051528L;
        private TemporalExtendedPropositionDefinition tepd;
        private TemporalExtendedPropositionDefinition rightHandSide;

        private GetMatchesPredicateExpression(
                TemporalExtendedPropositionDefinition tepd) {
            this.tepd = tepd;
        }

        private GetMatchesPredicateExpression(
                TemporalExtendedPropositionDefinition leftHandSide,
                TemporalExtendedPropositionDefinition rightHandSide) {
            this.tepd = leftHandSide;
            this.rightHandSide = rightHandSide;
        }

        @Override
        public boolean evaluate(Object arg0, Tuple arg1, Declaration[] arg2,
                Declaration[] arg3, WorkingMemory arg4, Object context)
                throws Exception {
            return this.tepd == null
                    || this.tepd.getMatches((Proposition) arg0)
                    || this.rightHandSide == null
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
            rule.setSalience(new SalienceInteger(1));
            Set<ExtendedPropositionDefinition> epdsC = def
                    .getExtendedPropositionDefinitions();
            TemporalExtendedPropositionDefinition[] epds = (TemporalExtendedPropositionDefinition[]) epdsC
                    .toArray(new TemporalExtendedPropositionDefinition[epdsC
                            .size()]);
            for (int i = 0; i < epds.length; i++) {
                Pattern p = new Pattern(i, PROPOSITION_OBJECT_TYPE);
                Constraint c = new PredicateConstraint(
                        new GetMatchesPredicateExpression(epds[i]));
                p.addConstraint(c);
                rule.addPattern(p);
            }
            rule.addPattern(new EvalCondition(
                    new HighLevelAbstractionCondition(def, epds), null));
            rule.setConsequence(new HighLevelAbstractionConsequence(def, epds,
                    this.derivations));
            this.ruleToAbstractionDefinition.put(rule, def);
            rules.add(rule);
            AbstractionCombiner.toRules(knowledgeSource, def, rules);
            addInverseIsARule(def.getId(), def.getInverseIsA(), TEMP_PROP_OT);
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
                    new PropositionPredicateExpression(def.getAbstractedFrom())));

            Pattern resultP = new Pattern(1, 1, ARRAY_LIST_OT, "result");
            resultP.setSource(new Collect(sourceP, new Pattern(1, 1,
                    ARRAY_LIST_OT, "result")));
            resultP.addConstraint(new PredicateConstraint(
                    new CollectionSizeExpression(1)));
            rule.addPattern(resultP);
            rule.setConsequence(new SliceConsequence(def, this.derivations));
            rule.setSalience(new SalienceInteger(-2));
            this.ruleToAbstractionDefinition.put(rule, def);
            rules.add(rule);
            addInverseIsARule(def.getId(), def.getInverseIsA(), TEMP_PROP_OT);
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
                    new PropositionPredicateExpression(def.getAbstractedFrom())));
            sourceP.addConstraint(new PredicateConstraint(
                    new GetMatchesPredicateExpression(def.getLeftHandProposition(),
                            def.getRightHandProposition())));

            Pattern resultP = new Pattern(1, 1, ARRAY_LIST_OT, "result");
            resultP.setSource(new Collect(sourceP, new Pattern(1, 1,
                    ARRAY_LIST_OT, "result")));
            resultP.addConstraint(new PredicateConstraint(
                    new CollectionSizeExpression(2)));
            rule.addPattern(resultP);
            rule.setConsequence(new PairConsequence(def, this.derivations));
            rule.setSalience(new SalienceInteger(-2));
            this.ruleToAbstractionDefinition.put(rule, def);
            rules.add(rule);
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    @Override
    public void visit(EventDefinition def) {
        ProtempaUtil.logger().log(Level.FINER, "Creating rule for {0}", def);
        try {
            addInverseIsARule(def.getId(), def.getInverseIsA(),
                    EVENT_OBJECT_TYPE);
        } catch (InvalidRuleException e) {
            throw new AssertionError(e.getClass().getName() + ": "
                    + e.getMessage());
        }

    }

    @Override
    public void visit(ConstantDefinition def) {
        ProtempaUtil.logger().log(Level.FINER, "Creating rule for {0}", def);
        try {
            addInverseIsARule(def.getId(), def.getInverseIsA(),
                    CONSTANT_OBJECT_TYPE);
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

    private void addInverseIsARule(String propId, String[] inverseIsA,
            ClassObjectType objectType) {
        Rule rule = new Rule("INVERSEISA_" + propId);
        Pattern p = new Pattern(0, objectType);
        Set<String> propIdSet = new HashSet<String>();
        if (inverseIsA != null) {
            for (String pId : inverseIsA) {
                propIdSet.add(pId);
            }
        }
        if (!propIdSet.isEmpty()) {
            Constraint c = new PredicateConstraint(
                    new PropositionPredicateExpression(propIdSet));
            p.addConstraint(c);
            rule.addPattern(p);
            rule.setConsequence(new InverseIsAConsequence(propId,
                    this.derivations));
            rule.setSalience(new SalienceInteger(1));
            rules.add(rule);
        }
    }
}
