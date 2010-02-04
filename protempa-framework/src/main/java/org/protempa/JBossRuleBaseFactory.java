package org.protempa;

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.rule.Package;
import org.drools.rule.Rule;

/**
 * Factory for creating Drools rule execution sets.
 * 
 * @author Andrew Post
 */
class JBossRuleBaseFactory {

    private final JBossRuleCreator ruleCreator;

    /**
     * Provides a knowledge source and an algorithm source.
     *
     * @param knowledgeSource
     *            the <code>KnowledgeSource</code> to use.
     * @param algorithmSource
     *            the <code>AlgorithmSource</code> to use.
     */
    JBossRuleBaseFactory(JBossRuleCreator ruleCreator) {
        assert ruleCreator != null : "ruleCreator cannot be null";

        this.ruleCreator = ruleCreator;
    }

    RuleBase newInstance() throws PropositionDefinitionInstantiationException {
        return newRuleBase(newRuleBaseConfiguration(), newPackage());
    }

    private Package newPackage() {
        org.drools.rule.Package rules =
                new org.drools.rule.Package("PROTEMPA Rules");
        for (Rule rule : this.ruleCreator.getRules()) {
            rules.addRule(rule);
        }
        return rules;
    }

    private static RuleBase newRuleBase(RuleBaseConfiguration config,
            Package rules)
            throws PropositionDefinitionInstantiationException {
        RuleBase ruleBase = RuleBaseFactory.newRuleBase(config);
        try {
            ruleBase.addPackage(rules);
        } catch (Exception e) {
            throw new PropositionDefinitionInstantiationException(
                    "Could not instantiate proposition definitions", e);
        }
        return ruleBase;
    }

    private RuleBaseConfiguration newRuleBaseConfiguration()
            throws PropositionDefinitionInstantiationException {
        RuleBaseConfiguration config = new RuleBaseConfiguration();
        config.setShadowProxy(false);
        try {
            config.setConflictResolver(new PROTEMPAConflictResolver(
                    this.ruleCreator.getKnowledgeSource(),
                    this.ruleCreator.getRuleToAbstractionDefinitionMap()));
        } catch (KnowledgeSourceReadException ex) {
            throw new PropositionDefinitionInstantiationException(
                    "Could not instantiate proposition definitions", ex);
        }
        config.setAssertBehaviour(AssertBehaviour.EQUALITY);
        return config;
    }
}