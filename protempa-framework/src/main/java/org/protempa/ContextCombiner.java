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
import java.util.logging.Level;
import org.drools.base.ClassObjectType;
import org.drools.base.SalienceInteger;
import org.drools.rule.EvalCondition;
import org.drools.rule.InvalidRuleException;
import org.drools.rule.Pattern;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.Rule;
import org.drools.spi.Constraint;
import org.protempa.proposition.Context;

/**
 *
 * @author Andrew Post
 */
final class ContextCombiner implements TemporalPropositionCombiner<ContextDefinition> {

    private static final ClassObjectType CONTEXT_OBJECT_TYPE
            = new ClassObjectType(Context.class);

    @Override
    public void toRules(ContextDefinition d, List<Rule> rules,
            DerivationsBuilder derivationsBuilder) {
        try {
            Rule rule = new Rule("CONTEXT_COMBINER_" + d.getId());
            rule.setSalience(new SalienceInteger(3));
            Pattern p0 = new Pattern(0, CONTEXT_OBJECT_TYPE);
            Constraint c0 = new PredicateConstraint(
                    new PropositionPredicateExpression(d.getPropositionId()));
            p0.addConstraint(c0);
            Pattern p1 = new Pattern(1, CONTEXT_OBJECT_TYPE);
            Constraint c1 = new PredicateConstraint(
                    new PropositionPredicateExpression(d.getPropositionId()));
            p1.addConstraint(c1);
            rule.addPattern(p0);
            rule.addPattern(p1);
            rule.addPattern(new EvalCondition(
                    new ContextCombinerCondition(d), null));
            rule.setConsequence(new ContextCombinerConsequence(
                    derivationsBuilder));
            rules.add(rule);
        } catch (InvalidRuleException e) {
            ProtempaUtil.logger().log(Level.SEVERE,
                    "Could not create rules from " + d.toString() + ".", e);
        }
    }

}
