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

import java.util.List;
import java.util.Map;

import org.protempa.proposition.interval.Relation;
import org.protempa.proposition.TemporalProposition;

/**
 * Finds instances of a high level abstraction definition in a set of
 * temporal propositions.
 * 
 * @author Andrew Post
 */
class HighLevelAbstractionFinder {

    private HighLevelAbstractionFinder() {
    }

    /**
     * Determines if a list of abstract parameters satisfies a complex
     * abstraction definition.
     *
     * @param def
     *            the {@link HighLevelAbstractionDefinition} that we're looking
     *            for.
     * @param potentialInstance
     *            a {@link List} of {@link TemporalProposition} objects
     *            containing one instance of each component abstraction
     *            definition in <code>def</code>.
     * @return <code>true</code> if <code>def</code> was found,
     *         <code>false</code> otherwise. It also returns <code>true</code>
     *         if no relations are defined.
     */
    static boolean find(
            Map<List<TemporalExtendedPropositionDefinition>, Relation> epdToRelation,
            List<List<TemporalExtendedPropositionDefinition>> epdPairs,
            Map<TemporalExtendedPropositionDefinition, TemporalProposition> potentialInstance) {

        /*
         * Loop through the abstraction definition pairs for each defined
         * relation. If a relation is not found, then set found to false.
         */
        for (List<TemporalExtendedPropositionDefinition> tepd : epdPairs) {
            if (!hasRelation(epdToRelation, potentialInstance, tepd)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param def
     *            the {@link HighLevelAbstractionDefinition} that we're looking
     *            for.
     * @param potentialInstance
     *            a {@link List} of {@link TemporalProposition} objects
     *            containing one instance of each component abstraction
     *            definition in <code>def</code>.
     * @param pair
     *            a pair of {@link TemporalExtendedPropositionDefinition}
     *            objects involved in a temporal relation.
     * @return <code>true</code> if <code>def</code> was found,
     *         <code>false</code> otherwise.
     */
    private static boolean hasRelation(
            Map<List<TemporalExtendedPropositionDefinition>, Relation> epdToRelation,
            Map<TemporalExtendedPropositionDefinition, TemporalProposition> potentialInstance,
            List<TemporalExtendedPropositionDefinition> pair) {
        TemporalProposition a1 = potentialInstance.get(pair.get(0));
        TemporalProposition a2 = potentialInstance.get(pair.get(1));
        return (epdToRelation.get(pair).hasRelation(a1.getInterval(), a2.getInterval()));

    }
}
