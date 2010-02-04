package org.protempa;

import java.util.HashSet;
import java.util.Map;

import org.drools.conflict.AbstractConflictResolver;
import org.drools.rule.Rule;
import org.drools.spi.Activation;


/**
 * 
 * This is Drool's default conflict resolver with the addition of a topological
 * sort of the rules corresponding to the abstraction hierarchy. We check:
 * <ol>
 * <li>Salience</li>
 * <li>Propagation number</li>
 * <li>Recency</li>
 * <li>Topological sort order</li>
 * <li>Load order</li>
 * </ol>
 * 
 * @author Andrew Post
 */
final class PROTEMPAConflictResolver extends AbstractConflictResolver {

	private static final long serialVersionUID = -2690384525731832692L;

	private final TopologicalSortComparator topSortComp;
	private final Map<Rule, AbstractionDefinition> ruleToAbstractionDefinition;

	PROTEMPAConflictResolver(KnowledgeSource knowledgeSource,
			Map<Rule, AbstractionDefinition> ruleToAbstractionDefinition) 
            throws KnowledgeSourceReadException {
        assert knowledgeSource != null : "knowledgeSource cannot be null";
		this.topSortComp = new TopologicalSortComparator(knowledgeSource,
                new HashSet<AbstractionDefinition>(
                ruleToAbstractionDefinition.values()));
		this.ruleToAbstractionDefinition = ruleToAbstractionDefinition;
	}

	public int compare(Activation a1, Activation a2) {
		final int s1 = a1.getSalience();
		final int s2 = a2.getSalience();
		if (s1 > s2) {
			return -1;
		} else if (s1 < s2) {
			return 1;
		}

		final long p1 = a1.getPropagationContext().getPropagationNumber();
		final long p2 = a2.getPropagationContext().getPropagationNumber();
		if (p1 != p2) {
			return (int) (p2 - p1);
		}

		final long r1 = a1.getTuple().getRecency();
		final long r2 = a2.getTuple().getRecency();
		if (r1 != r2) {
			return (int) (r2 - r1);
		}

		final Rule rule1 = a1.getRule();
		final Rule rule2 = a2.getRule();
		if (rule1 != rule2) {
			AbstractionDefinition def1 = this.ruleToAbstractionDefinition
					.get(rule1);
			AbstractionDefinition def2 = this.ruleToAbstractionDefinition
					.get(rule2);
			return this.topSortComp.compare(def1, def2);
		}

		final long l1 = rule1.getLoadOrder();
		final long l2 = rule2.getLoadOrder();
		return (int) (l2 - l1);
	}

}
