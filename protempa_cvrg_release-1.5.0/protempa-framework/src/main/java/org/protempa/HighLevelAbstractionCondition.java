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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.arp.javautil.arrays.Arrays;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.EvalExpression;
import org.drools.spi.Tuple;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.interval.Relation;
import org.protempa.proposition.TemporalProposition;

/**
 * High level abstraction definition condition.
 *
 * @author Andrew Post
 */
class HighLevelAbstractionCondition implements EvalExpression {

    private static final long serialVersionUID = -4946151589366639279L;
    private final HighLevelAbstractionDefinition def;
    private final ExtendedPropositionDefinition[] epds;
    private int parameterMapCapacity;
    private List<List<TemporalExtendedPropositionDefinition>> epdPairs;
    private Map<List<TemporalExtendedPropositionDefinition>, Relation> epdToRelation;

    HighLevelAbstractionCondition(HighLevelAbstractionDefinition def,
            ExtendedPropositionDefinition[] epds) {
        this.def = def;
        this.epds = epds;
        this.parameterMapCapacity = this.epds.length * 4 / 3 + 1;
        this.epdPairs = new ArrayList<List<TemporalExtendedPropositionDefinition>>(
                def.getTemporalExtendedPropositionDefinitionPairs());
        this.epdToRelation = new HashMap<List<TemporalExtendedPropositionDefinition>, Relation>(
                this.parameterMapCapacity);
        for (List<TemporalExtendedPropositionDefinition> pair : this.epdPairs) {
            this.epdToRelation.put(pair, this.def.getRelation(pair));
        }
    }

    @Override
    public boolean evaluate(Tuple arg0, Declaration[] arg1, WorkingMemory arg2,
            Object context) throws Exception {

        /*
         * For constructing a map of extended proposition definition to actual
         * temporal proposition.
         */
        Map<TemporalExtendedPropositionDefinition, TemporalProposition> propositionMap =
                new HashMap<TemporalExtendedPropositionDefinition, TemporalProposition>(this.parameterMapCapacity);
        /*
         * To check for duplicate inputs. We'll only have a few temporal
         * propositions, so using a set for tps probably would be slower.
         */
        Proposition[] ps = new Proposition[this.epds.length];

        /*
         * Populate the map and remove duplicates.
         */
        for (int i = 0; i < this.epds.length; i++) {
            Proposition p = (Proposition) arg2.getObject(arg0.get(i));
            if (Arrays.contains(ps, p)) // remove duplicates
            {
                return false;
            }
            ps[i] = p;
            if (epds[i] instanceof TemporalExtendedPropositionDefinition) {
                propositionMap.put((TemporalExtendedPropositionDefinition) epds[i], (TemporalProposition) p);
            }
        }

        /*
         * Check for the presence of the specified temporal relations.
         */
        return HighLevelAbstractionFinder.find(this.epdToRelation,
                this.epdPairs, propositionMap);
    }

    @Override
    public Object createContext() {
        return null;
    }
}
