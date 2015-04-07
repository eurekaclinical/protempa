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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arp.javautil.arrays.Arrays;
import org.drools.FactException;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.SequentialTemporalPatternDefinition.SubsequentTemporalExtendedPropositionDefinition;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.ProviderBasedUniqueIdFactory;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.UniqueIdFactory;
import org.protempa.proposition.interval.Relation;

/**
 * @author Andrew Post
 */
class SequentialTemporalPatternConsequence implements Consequence {

    private static final long serialVersionUID = -833609244124008166L;
    private final SequentialTemporalPatternDefinition def;
    private final TemporalExtendedPropositionDefinition[] epds;
    private final DerivationsBuilder derivationsBuilder;
    private int parameterMapCapacity;
    private List<List<TemporalExtendedPropositionDefinition>> epdPairs;
    private Map<List<TemporalExtendedPropositionDefinition>, Relation> epdToRelation;

    /**
     *
     * @param def a {@link HighLevelAbstractionDefinition}, cannot be
     * <code>null</code>.
     * @param columns the number of parameters, must be greater than zero.
     */
    SequentialTemporalPatternConsequence(SequentialTemporalPatternDefinition def,
            DerivationsBuilder derivationsBuilder) {
        assert def != null : "def cannot be null";
        this.def = def;
        SubsequentTemporalExtendedPropositionDefinition[] relatedTemporalExtendedPropositionDefinitions = 
                def.getSubsequentTemporalExtendedPropositionDefinitions();
        TemporalExtendedPropositionDefinition[] epds = 
                new TemporalExtendedPropositionDefinition[relatedTemporalExtendedPropositionDefinitions.length + 1];
        assert epds != null : "epds cannot be null";
        assert epds.length > 0 : "epds must be > 0";
        epds[0] = def.getFirstTemporalExtendedPropositionDefinition();
        for (int i = 1; i < epds.length; i++) {
            epds[i] = 
                    relatedTemporalExtendedPropositionDefinitions[i - 1].getRelatedTemporalExtendedPropositionDefinition();
        }
        this.epds = epds;
        this.derivationsBuilder = derivationsBuilder;
        this.parameterMapCapacity = this.epds.length * 4 / 3 + 1;
        this.epdPairs = 
                new ArrayList<>();
        this.epdToRelation = 
                new HashMap<>(
                this.parameterMapCapacity);
        TemporalExtendedPropositionDefinition lhs = 
                def.getFirstTemporalExtendedPropositionDefinition();
        assert lhs != null : 
                "mainTemporalExtendedPropositionDefinition cannot be null";
        for (SubsequentTemporalExtendedPropositionDefinition rhsr :
                def.getSubsequentTemporalExtendedPropositionDefinitions()) {
            TemporalExtendedPropositionDefinition rhs = 
                    rhsr.getRelatedTemporalExtendedPropositionDefinition();
            List<TemporalExtendedPropositionDefinition> asList =
                    Arrays.asList(
                    new TemporalExtendedPropositionDefinition[]{
                        lhs,
                        rhs
                    });
            this.epdPairs.add(asList);
            this.epdToRelation.put(asList, rhsr.getRelation());
            lhs = rhs;
        }
    }

    @Override
    public void evaluate(KnowledgeHelper knowledgeHelper, WorkingMemory arg1)
            throws Exception {
        @SuppressWarnings("unchecked")
        List<TemporalProposition> tps = 
                (List<TemporalProposition>) knowledgeHelper
                .get(knowledgeHelper.getDeclaration("result"));
        Collections.sort(tps, ProtempaUtil.TEMP_PROP_COMP);

        int i = 0;
        Map<TemporalExtendedPropositionDefinition, TemporalProposition> propositionMap =
                    new HashMap<>(this.parameterMapCapacity);
        
        JBossRulesDerivedLocalUniqueIdValuesProvider provider = new JBossRulesDerivedLocalUniqueIdValuesProvider(arg1, def.getPropositionId());
        UniqueIdFactory factory = new ProviderBasedUniqueIdFactory(provider);
        
        TOP_LEVEL:
        for (int l = this.epds.length, n = tps.size() - l + 1; i < n; i++) {
            List<TemporalProposition> subList = tps.subList(i, i + l);

            /*
             * For constructing a map of extended proposition definition to actual
             * temporal proposition.
             */
            
            /*
             * Populate the map.
             */
            for (int j = 0; j < l; j++) {
                TemporalProposition p = subList.get(j);
                if (!p.getId().equals(epds[j].getPropositionId())) {
                    continue TOP_LEVEL;
                }
                propositionMap.put(epds[j], p);
            }

            /*
             * Check for the presence of the specified temporal relations.
             */
            if (HighLevelAbstractionFinder.find(this.epdToRelation,
                    this.epdPairs, propositionMap)) {
                assertProposition(subList, knowledgeHelper, factory.getInstance());
            }
        }
        
        if (this.def.isAllowPartialMatches()) {
            List<TemporalProposition> subList = tps.subList(i, tps.size());
            propositionMap.clear();
            for (int j = 0, n = subList.size(); j < n; j++) {
                propositionMap.put(epds[j], subList.get(j));
            }
            for (int j = 0, n = this.epdPairs.size(); j < n; j++) {
                List<TemporalExtendedPropositionDefinition> pair = 
                        this.epdPairs.get(j);
                TemporalProposition a1 = propositionMap.get(pair.get(0));
                TemporalProposition a2 = propositionMap.get(pair.get(1));
                if (a1 != null && a2 != null) {
                    if (!this.epdToRelation.get(pair).hasRelation(
                            a1.getInterval(), a2.getInterval())) {
                        return;
                    }
                }
            }
            assertProposition(subList, knowledgeHelper, factory.getInstance());
        }
    }

    private void assertProposition(List<TemporalProposition> subList, 
            KnowledgeHelper knowledgeHelper, UniqueId uniqueId) throws FactException {
        Logger logger = ProtempaUtil.logger();
        Segment<TemporalProposition> segment =
                        new Segment<>(
                        new Sequence<>(
                        def.getPropositionId(), subList));
        TemporalPatternOffset temporalOffset = def.getTemporalOffset();
        AbstractParameter result =
                AbstractParameterFactory.getFromAbstraction(
                def.getPropositionId(), uniqueId,
                segment, subList, null, temporalOffset, epds, null);
        knowledgeHelper.getWorkingMemory().insert(result);
        for (Proposition proposition : segment) {
            this.derivationsBuilder.propositionAsserted(proposition, result);
        }
        logger.log(Level.FINER, "Asserted derived proposition {0}", result);
    }
}
