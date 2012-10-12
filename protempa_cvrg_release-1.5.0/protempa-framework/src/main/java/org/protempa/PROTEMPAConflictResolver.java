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

import java.util.Map;

import org.drools.conflict.AbstractConflictResolver;
import org.drools.rule.Rule;
import org.drools.spi.Activation;

/**
 * 
 * This is Drool's default conflict resolver with the addition of a topological
 * sort of the rules corresponding to {@link AbstractionDefinition} hierarchy. 
 * 
 * We check (in order):
 * <ol>
 * <li>Salience</li>
 * <li>Propagation number</li>
 * <li>Recency</li>
 * <li>Topological sort order (for {@link AbstractionDefinition}s only)</li>
 * <li>Load order</li>
 * </ol>
 * 
 * @author Andrew Post
 */
final class PROTEMPAConflictResolver extends AbstractConflictResolver {

    private static final long serialVersionUID = -2690384525731832692L;
    private final TopologicalSortComparator topSortComp;
    private final Map<Rule, AbstractionDefinition> ruleToAbstractionDefinition;

    /**
     * Creates a conflict resolver instance.
     * 
     * @param knowledgeSource a {@link KnowledgeSource}. Used for creating
     * a topological sort order for all abstraction definitions. Cannot be 
     * <code>null</code>.
     * @param ruleToAbstractionDefinition a mapping from {@link Rule}s to
     * {@link AbstractionDefinition}s. All abstraction definitions and rules
     * should be in this map, or the behavior of the conflict resolver will be
     * undefined. Cannot be <code>null</code> (throws a 
     * {@link NullPointerException}.
     * @throws KnowledgeSourceReadException if an error occurs reading from
     * the knowledge source.
     */
    PROTEMPAConflictResolver(KnowledgeSource knowledgeSource,
            Map<Rule, AbstractionDefinition> ruleToAbstractionDefinition)
            throws KnowledgeSourceReadException {
        assert knowledgeSource != null : "knowledgeSource cannot be null";
        this.topSortComp = new TopologicalSortComparator(knowledgeSource,
                ruleToAbstractionDefinition.values());
        this.ruleToAbstractionDefinition = ruleToAbstractionDefinition;
    }

    /**
     * Compares two activations for order. Sequentially tries salience-, 
     * propagation-, recency-, topological- and
     * load order-based conflict resolution. The first of these conflict
     * resolution attempts that successfully finds a non-equal ordering of
     * the two activations immediately returns the ordering found.
     * 
     * @param a1 an {@link Activation}.
     * @param a2 another {@link Activation}.
     * @return a negative integer, zero, or a positive integer as this object 
     * is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compare(Activation a1, Activation a2) {
        //Salience-based conflict resolution.
        final int s1 = a1.getSalience();
        final int s2 = a2.getSalience();
        if (s1 > s2) {
            return -1;
        } else if (s1 < s2) {
            return 1;
        }

        //Propagation-based conflict resolution.
        final long p1 = a1.getPropagationContext().getPropagationNumber();
        final long p2 = a2.getPropagationContext().getPropagationNumber();
        if (p1 != p2) {
            return (int) (p2 - p1);
        }

        //Recency-based conflict resolution.
        final long r1 = a1.getTuple().getRecency();
        final long r2 = a2.getTuple().getRecency();
        if (r1 != r2) {
            return (int) (r2 - r1);
        }

        //Topological-sort-based conflict resolution.
        final Rule rule1 = a1.getRule();
        final Rule rule2 = a2.getRule();
        if (rule1 != rule2) {
            AbstractionDefinition def1 =
                    this.ruleToAbstractionDefinition.get(rule1);
            AbstractionDefinition def2 =
                    this.ruleToAbstractionDefinition.get(rule2);
            /*
             * If def1 is null, then rule1 does not correspond to an
             * abstraction definition. If def2 is null, then rule2 does not
             * correspond to an abstraction definition. In either case, 
             * topological sort-based conflict resolution does not apply, so 
             * skip to the next conflict resolution strategy.
             */
            if (def1 != null && def2 != null) {
                return this.topSortComp.compare(def1, def2);
            }
        }

        //Load order-based conflict resolution.
        final long l1 = rule1.getLoadOrder();
        final long l2 = rule2.getLoadOrder();
        return (int) (l2 - l1);
    }
}
