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

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.rule.Package;
import org.drools.rule.Rule;

/**
 * Factory for creating Drools rule execution sets.
 * 
 * @author Andrew Post
 */
class JBossRuleBaseFactory {

    private final JBossRuleCreator ruleCreator;
    private final RuleBaseConfiguration ruleBaseConfiguration;

    /**
     * Provides a knowledge source and an algorithm source.
     *
     * @param knowledgeSource
     *            the <code>KnowledgeSource</code> to use.
     * @param algorithmSource
     *            the <code>AlgorithmSource</code> to use.
     */
    JBossRuleBaseFactory(JBossRuleCreator ruleCreator, RuleBaseConfiguration ruleBaseConfiguration) {
        assert ruleCreator != null : "ruleCreator cannot be null";

        this.ruleCreator = ruleCreator;
        this.ruleBaseConfiguration = ruleBaseConfiguration;
    }

    RuleBase newInstance() throws RuleBaseInstantiationException {
        return newRuleBase(this.ruleBaseConfiguration, newPackage());
    }

    private Package newPackage() {
        org.drools.rule.Package rules =
                new org.drools.rule.Package(ProtempaUtil.DROOLS_PACKAGE_NAME);
        for (Rule rule : this.ruleCreator.getRules()) {
            rules.addRule(rule);
        }
        return rules;
    }

    private static RuleBase newRuleBase(RuleBaseConfiguration config,
            Package rules)
            throws RuleBaseInstantiationException {
        WorkingMemoryGlobals.addAll(rules);
        
        RuleBase ruleBase = RuleBaseFactory.newRuleBase(config);
        try {
            ruleBase.addPackage(rules);
        } catch (Exception e) {
            throw new RuleBaseInstantiationException(
                    "Could not instantiate proposition definitions", e);
        }
        return ruleBase;
    }
}
