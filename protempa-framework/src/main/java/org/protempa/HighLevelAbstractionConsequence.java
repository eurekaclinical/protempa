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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.ProviderBasedUniqueIdFactory;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.UniqueIdFactory;

/**
 * @author Andrew Post
 */
class HighLevelAbstractionConsequence implements Consequence {

    private static final long serialVersionUID = -833609244124008166L;
    private final HighLevelAbstractionDefinition cad;
    private final int columns;
    private final ExtendedPropositionDefinition[] epds;
    private final DerivationsBuilder derivationsBuilder;

    /**
     *
     * @param def a {@link HighLevelAbstractionDefinition}, cannot be
     * <code>null</code>.
     * @param columns the number of parameters, must be greater than zero.
     */
    HighLevelAbstractionConsequence(HighLevelAbstractionDefinition def,
            ExtendedPropositionDefinition[] epds,
            DerivationsBuilder derivationsBuilder) {
        assert def != null : "def cannot be null";
        assert epds != null : "epds cannot be null";
        int col = epds.length;
        assert col > 0 : "columns must be > 0, was " + col;
        this.cad = def;
        this.columns = col;
        this.epds = epds;
        this.derivationsBuilder = derivationsBuilder;
    }

    @Override
    public void evaluate(KnowledgeHelper arg0, WorkingMemory arg1)
            throws Exception {
        Logger logger = ProtempaUtil.logger();
        List<Proposition> ps = parameters(arg0.getTuple(), arg1);
        List<TemporalProposition> tps = extractTemporalPropositions(ps);
        List<TemporalExtendedPropositionDefinition> tepdsL
                = extractTemporalExtendedPropositionDefinitions();
        TemporalExtendedPropositionDefinition[] tepds
                = tepdsL.toArray(
                        new TemporalExtendedPropositionDefinition[tepdsL.size()]);
        Segment<TemporalProposition> segment
                = new Segment<>(
                        new Sequence<>(cad.getPropositionId(), tps));
        TemporalPatternOffset temporalOffset = cad.getTemporalOffset();
        JBossRulesDerivedLocalUniqueIdValuesProvider provider = new JBossRulesDerivedLocalUniqueIdValuesProvider(arg1, cad.getPropositionId());
        UniqueIdFactory factory = new ProviderBasedUniqueIdFactory(provider);
        AbstractParameter result
                = AbstractParameterFactory.getFromAbstraction(cad.getPropositionId(), 
                        factory.getInstance(),
                        segment, tps, null, temporalOffset, tepds, null);
        arg0.getWorkingMemory().insert(result);
        for (Proposition proposition : segment) {
            this.derivationsBuilder.propositionAsserted(proposition, result);
        }
        logger.log(Level.FINER, "Asserted derived proposition {0}", result);
    }

    private List<Proposition> parameters(Tuple arg0,
            WorkingMemory arg1) {
        List<Proposition> sequences
                = new ArrayList<>(columns);
        for (int i = 0; i < columns; i++) {
            sequences.add((Proposition) arg1.getObject(arg0.get(i)));
        }
        return sequences;
    }

    private List<TemporalProposition> extractTemporalPropositions(
            List<Proposition> ps) {
        List<TemporalProposition> tps = new ArrayList<>();
        for (Proposition prop : ps) {
            if (prop instanceof TemporalProposition) {
                tps.add((TemporalProposition) prop);
            }
        }
        return tps;
    }

    private List<TemporalExtendedPropositionDefinition> extractTemporalExtendedPropositionDefinitions() {
        List<TemporalExtendedPropositionDefinition> tepdsL
                = new ArrayList<>();
        for (ExtendedPropositionDefinition epd : epds) {
            if (epd instanceof TemporalExtendedPropositionDefinition) {
                tepdsL.add((TemporalExtendedPropositionDefinition) epd);
            }
        }
        return tepdsL;
    }
}
