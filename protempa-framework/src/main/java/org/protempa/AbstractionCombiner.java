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
final class AbstractionCombiner implements TemporalPropositionCombiner<AbstractionDefinition> {

    private static final ClassObjectType ABSTRACT_PARAMETER_OBJECT_TYPE = new ClassObjectType(
            AbstractParameter.class);

    AbstractionCombiner() {
    }

    public void toRules(AbstractionDefinition d, List<Rule> rules,
            DerivationsBuilder derivationsBuilder) {
        try {
            Rule rule = new Rule("ABSTRACTION_COMBINER_" + d.getId());
            rule.setSalience(new SalienceInteger(3));
            Pattern p0 = new Pattern(0, ABSTRACT_PARAMETER_OBJECT_TYPE);
            Constraint c0 = new PredicateConstraint(
                    new ParameterPredicateExpression(d.getPropositionId(), null));
            p0.addConstraint(c0);
            Pattern p1 = new Pattern(1, ABSTRACT_PARAMETER_OBJECT_TYPE);
            Constraint c1 = new PredicateConstraint(
                    new ParameterPredicateExpression(d.getPropositionId(), null));
            p1.addConstraint(c1);
            rule.addPattern(p0);
            rule.addPattern(p1);
            rule.addPattern(new EvalCondition(
                    new AbstractionCombinerCondition(d), null));
            rule.setConsequence(new AbstractionCombinerConsequence(
                    derivationsBuilder));
            rules.add(rule);
        } catch (InvalidRuleException e) {
            ProtempaUtil.logger().log(Level.SEVERE,
                    "Could not create rules from " + d.toString() + ".", e);
        }
    }
}
