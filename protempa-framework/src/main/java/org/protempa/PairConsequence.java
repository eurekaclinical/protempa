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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.interval.Relation;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.TemporalProposition;

public final class PairConsequence implements Consequence {

    private static final long serialVersionUID = -3641374073069516895L;
    private final DerivationsBuilder derivationsBuilder;
    private final TemporalExtendedPropositionDefinition[] leftAndRightHandSide;
    private final String defId;
    private final String propId;
    private final TemporalPatternOffset temporalOffset;
    private final Relation relation;
    private final boolean secondRequired;

    PairConsequence(PairDefinition def,
            DerivationsBuilder derivationsBuilder) {
        assert def != null : "def cannot be null";
        this.derivationsBuilder = derivationsBuilder;
        this.leftAndRightHandSide =
                new TemporalExtendedPropositionDefinition[]{
            def.getLeftHandProposition(),
            def.getRightHandProposition()};
        this.defId = def.getId();
        this.propId = def.getPropositionId();
        this.temporalOffset = def.getTemporalOffset();
        this.relation = def.getRelation();
        this.secondRequired = def.isSecondRequired();
    }

    @Override
    public void evaluate(KnowledgeHelper knowledgeHelper,
            WorkingMemory workingMemory) throws Exception {
        Logger logger = ProtempaUtil.logger();
        @SuppressWarnings("unchecked")
		List<TemporalProposition> pl =
                (List<TemporalProposition>) knowledgeHelper.get(
                knowledgeHelper.getDeclaration("result"));
        java.util.Collections.sort(pl, ProtempaUtil.TEMP_PROP_COMP);

        TemporalExtendedPropositionDefinition leftProp =
                this.leftAndRightHandSide[0];
        TemporalExtendedPropositionDefinition rightProp =
                this.leftAndRightHandSide[1];
        Segment<TemporalProposition> segment = null;
        Sequence<TemporalProposition> seq =
                new Sequence<TemporalProposition>(this.defId);

        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, 
                "Proposition list size: {0}; secondRequired: {1}", 
                    new Object[] {pl.size(), this.secondRequired});
        }
        
        int i = 0;
        for (int n = pl.size() - 1; i < n; i++) {
            TemporalProposition left = pl.get(i);
            TemporalProposition right = pl.get(i + 1);
            if (leftProp.getMatches(left) && rightProp.getMatches(right)) {
                if (this.relation.hasRelation(left.getInterval(),
                        right.getInterval())) {
                    seq.add(left);
                    seq.add(right);
                    if (segment == null) {
                        segment = new Segment<TemporalProposition>(seq);
                    } else {
                        segment.resetState(seq);
                    }
                    AbstractParameter result =
                            AbstractParameterFactory.getFromAbstraction(
                            this.propId,
                            segment,
                            seq,
                            null,
                            this.temporalOffset,
                            this.leftAndRightHandSide,
                            null);

                    knowledgeHelper.getWorkingMemory().insert(result);
                    derivationsBuilder.propositionAsserted(left, result);
                    derivationsBuilder.propositionAsserted(right, result);

                    seq.clear();

                    logger.log(Level.FINER, "Asserted derived proposition {0}",
                            result);
                }
            }
        }
        if (!this.secondRequired) {
            TemporalProposition left = pl.get(i);
            if (leftProp.getMatches(left)) {
                seq.add(left);
                if (segment == null) {
                    segment = new Segment<TemporalProposition>(seq);
                } else {
                    segment.resetState(seq);
                }
                AbstractParameter result =
                        AbstractParameterFactory.getFromAbstraction(
                        this.propId,
                        segment,
                        seq,
                        null,
                        this.temporalOffset,
                        this.leftAndRightHandSide,
                        null);
                knowledgeHelper.getWorkingMemory().insert(result);
                derivationsBuilder.propositionAsserted(left, result);
                seq.clear();
                logger.log(Level.FINER, "Asserted derived proposition {0}",
                        result);
            }
        }
    }
}
