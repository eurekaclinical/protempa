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

import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.Segment;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.interval.Relation;

/**
 * Checks whether the union of two abstractions of the same type should be taken
 * based on their intervals.
 *
 * @author Andrew Post
 *
 */
final class HorizontalTemporalInference {
    private final Relation solidRelation;

    HorizontalTemporalInference() {
        this.solidRelation = new Relation(null, null, null, null,
            null, null, null, null,
            null, null, -1, null,
            null, null, null, null);
    }
    
    

    /**
     * Computes whether the union of two segments of temporal propositions
     * should be taken.
     *
     * We assume that
     * <code>tp1</code> is before or at the same time as
     * <code>tp2</code>, and that
     * <code>tp1</code> and
     * <code>tp2</code> are instances of
     * <code>propDef</code>.
     *
     * @param propDef a {@link PropositionDefinition}.
     * @param tp1 a {@link Segment<? extends TemporalProposition>}.
     * @param tp2 a {@link Segment<? extends TemporalProposition>}.
     * @return <code>true</code> if they should be combined, <code>false</code>
     * otherwise.
     */
    <T extends TemporalProposition> boolean execute(
            PropositionDefinition propDef, Segment<T> tp1, Segment<T> tp2) {
        if (tp1 == null || tp2 == null) {
            return false;
        }

        return executeInternal(propDef, tp1.getInterval(), tp2.getInterval());
    }

    /**
     * Computes whether the union of two segments of temporal propositions
     * should be taken.
     *
     * We assume that
     * <code>tp1</code> is before or at the same time as
     * <code>tp2</code>, and that
     * <code>tp1</code> and
     * <code>tp2</code> are instances of
     * <code>propDef</code>.
     *
     * @param propDef a {@link PropositionDefinition}.
     * @param tp1 a {@link TemporalProposition}.
     * @param tp2 a {@link TemporalProposition}.
     * @return <code>true</code> if they should be combined, <code>false</code>
     * otherwise.
     */
    <T extends TemporalProposition> boolean execute(
            PropositionDefinition propDef, T tp1, T tp2) {
        if (tp1 == null || tp2 == null) {
            return false;
        }

        return executeInternal(propDef, tp1.getInterval(), tp2.getInterval());
    }

    private boolean executeInternal(PropositionDefinition propDef, 
            Interval tp1Ival, Interval tp2Ival) {
        if (propDef.isConcatenable()) {
            if (Relation.MEETS.hasRelation(tp1Ival, tp2Ival)) {
                return true;
            }
        }

        if (propDef.isSolid()) {
            if (this.solidRelation.hasRelation(tp1Ival, tp2Ival)) {
                return true;
            }
        }
        return false;
    }
}
