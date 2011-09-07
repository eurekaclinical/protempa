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
