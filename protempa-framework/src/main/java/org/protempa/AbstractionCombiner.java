package org.protempa;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.drools.WorkingMemory;
import org.drools.base.ClassObjectType;
import org.drools.base.SalienceInteger;
import org.drools.common.InternalFactHandle;
import org.drools.rule.Declaration;
import org.drools.rule.EvalCondition;
import org.drools.rule.InvalidRuleException;
import org.drools.rule.Pattern;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.Rule;
import org.drools.spi.Consequence;
import org.drools.spi.Constraint;
import org.drools.spi.EvalExpression;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.DerivedSourceId;
import org.protempa.proposition.DerivedUniqueId;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.UniqueId;

/**
 * Represents rules that combine two abstract parameters with 1) the same
 * abstraction definition, 2) values, and 3) are within the minimum and maximum
 * gap defined in the abstraction definition.
 * 
 * @author Andrew Post
 */
final class AbstractionCombiner {

    private static final ClassObjectType ABSTRACT_PARAMETER_OBJECT_TYPE = new ClassObjectType(
            AbstractParameter.class);

    private AbstractionCombiner() {
    }

    private static class AbstractionCombinerCondition implements EvalExpression {

        private static final long serialVersionUID = -3292416251502209461L;
        private final AbstractionDefinition abstractionDefinition;
        private final HorizontalTemporalInference hti = new HorizontalTemporalInference();

        public AbstractionCombinerCondition(
                AbstractionDefinition abstractionDefinition) {
            this.abstractionDefinition = abstractionDefinition;
        }

        @Override
        public boolean evaluate(Tuple arg0, Declaration[] arg1,
                WorkingMemory arg2, Object context) throws Exception {
            AbstractParameter a1 = (AbstractParameter) arg2.getObject(arg0
                    .get(0));
            AbstractParameter a2 = (AbstractParameter) arg2.getObject(arg0
                    .get(1));
            
            System.err.println("Gap function for " + a1 + "; " + a2 + "; " + abstractionDefinition
                            .getGapFunction().execute(a1, a2));
            
            return a1 != a2
                    && (a1.getValue() == a2.getValue() || (a1.getValue() != null && a1
                            .getValue().equals(a2.getValue())))
                    && (a1.getInterval().compareTo(a2.getInterval()) <= 0)
                    && (hti.execute(this.abstractionDefinition, a1, a2) || abstractionDefinition
                            .getGapFunction().execute(a1, a2));
        }

        @Override
        public Object createContext() {
            return null;
        }
    }

    private static class AbstractionCombinerConsequence implements Consequence {

        private static final long serialVersionUID = -7984448674528718012L;

        private final DerivationsBuilder derivationsBuilder;

        public AbstractionCombinerConsequence(
                DerivationsBuilder derivationsBuilder) {
            this.derivationsBuilder = derivationsBuilder;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void evaluate(KnowledgeHelper arg0, WorkingMemory arg1)
                throws Exception {
            InternalFactHandle a1f = arg0.getTuple().get(0);
            AbstractParameter a1 = (AbstractParameter) arg1.getObject(a1f);
            String a1Id = a1.getId();
            InternalFactHandle a2f = arg0.getTuple().get(1);
            AbstractParameter a2 = (AbstractParameter) arg1.getObject(a2f);

            Sequence<AbstractParameter> s = new Sequence<AbstractParameter>(
                    a1Id, 2);
            s.add(a1);
            s.add(a2);
            Segment<AbstractParameter> segment = new Segment<AbstractParameter>(
                    s);

            AbstractParameter result = new AbstractParameter(a1Id,
                    new UniqueId(DerivedSourceId.getInstance(),
                            new DerivedUniqueId(UUID.randomUUID().toString())));
            result.setDataSourceType(DerivedDataSourceType.getInstance());
            result.setInterval(segment.getInterval());
            result.setValue(a1.getValue());

            Logger logger = ProtempaUtil.logger();
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "Created {0} from {1} and {2}",
                        new Object[] { result, a1, a2 });
            }

            arg1.retract(a1f);
            arg1.retract(a2f);
            arg1.insert(result);
            // There should not be any forward derivations yet.
            // List<Proposition> a1PropForward =
            // this.derivationsBuilder.propositionRetractedForward(a1);
            List<Proposition> a1PropBackward = this.derivationsBuilder
                    .propositionRetractedBackward(a1);
            // There should not be any forward derivations yet.
            // List<Proposition> a2PropForward =
            // this.derivationsBuilder.propositionRetractedForward(a2);
            List<Proposition> a2PropBackward = this.derivationsBuilder
                    .propositionRetractedBackward(a2);
            for (Proposition prop : a1PropBackward) {
                this.derivationsBuilder.propositionReplaceForward(prop, a1,
                        result);
                this.derivationsBuilder.propositionAssertedBackward(prop,
                        result);
            }
            for (Proposition prop : a2PropBackward) {
                this.derivationsBuilder.propositionReplaceForward(prop, a2,
                        result);
                this.derivationsBuilder.propositionAssertedBackward(prop,
                        result);
            }

            logger.log(Level.FINER, "Asserted derived proposition {0}", result);
        }
    }

    static void toRules(AbstractionDefinition d, List<Rule> rules,
            DerivationsBuilder derivationsBuilder) {
        try {
            Rule rule = new Rule("ABSTRACTION_COMBINER_" + d.getId());
            rule.setSalience(new SalienceInteger(3));
            Pattern p0 = new Pattern(0, ABSTRACT_PARAMETER_OBJECT_TYPE);
            Constraint c0 = new PredicateConstraint(
                    new ParameterPredicateExpression(d.getId(), null));
            p0.addConstraint(c0);
            Pattern p1 = new Pattern(1, ABSTRACT_PARAMETER_OBJECT_TYPE);
            Constraint c1 = new PredicateConstraint(
                    new ParameterPredicateExpression(d.getId(), null));
            p1.addConstraint(c1);
            rule.addPattern(p0);
            rule.addPattern(p1);
            rule.addPattern(new EvalCondition(new AbstractionCombinerCondition(
                    d), null));
            rule.setConsequence(new AbstractionCombinerConsequence(
                    derivationsBuilder));
            rules.add(rule);
        } catch (InvalidRuleException e) {
            ProtempaUtil.logger().log(Level.SEVERE,
                    "Could not create rules from " + d.toString() + ".", e);
        }
    }
}
