package org.protempa;

/*-
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2018 Emory University
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

import java.util.List;
import java.util.logging.Level;
import org.drools.base.ClassObjectType;
import org.drools.base.SalienceInteger;
import org.drools.rule.InvalidRuleException;
import org.drools.rule.Pattern;
import org.drools.rule.PredicateConstraint;
import org.drools.rule.Rule;
import org.drools.spi.Constraint;
import org.protempa.proposition.Proposition;

/**
 *
 * @author Andrew Post
 */
final class DeletedProposition {
    
    private static final ClassObjectType PROPOSITION_OBJECT_TYPE = 
            new ClassObjectType(Proposition.class);

    public void toRules(List<Rule> rules, DerivationsBuilder derivationsBuilder) {
        try {
            Rule rule = new Rule("DELETE_PROPOSITION");
            rule.setSalience(new SalienceInteger(10));
            Pattern p0 = new Pattern(0, PROPOSITION_OBJECT_TYPE);
            Constraint c0 = new PredicateConstraint(new DeletedPropositionExpression());
            p0.addConstraint(c0);
            rule.addPattern(p0);
            rule.setConsequence(new DeletedPropositionConsequence(derivationsBuilder));
            rules.add(rule);
        } catch (InvalidRuleException e) {
            ProtempaUtil.logger().log(Level.SEVERE, "Could not create rules.", e);
        }
    }
    
}
