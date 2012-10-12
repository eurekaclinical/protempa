/*
 * #%L
 * Protempa Protege Knowledge Source Backend
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
package org.protempa.backend.ksb.protege;

import org.protempa.PropositionDefinitionCache;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PairDefinition;
import org.protempa.TemporalExtendedPropositionDefinition;
import org.protempa.proposition.interval.Relation;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import java.util.HashMap;
import java.util.Map;
import org.protempa.*;

public class PairAbstractionConverter implements AbstractionConverter {

    @Override
    public PairDefinition convert(Instance protegeProposition,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        PairDefinition result = new PairDefinition(
                protegeProposition.getName());
        ConnectionManager cm = backend.getConnectionManager();
        Util.setNames(protegeProposition, result, cm);
        Util.setInDataSource(protegeProposition, result, cm);
        Util.setProperties(protegeProposition, result, cm);
        Util.setTerms(protegeProposition, result, cm);
        Util.setSolid(protegeProposition, result, cm);
        Util.setConcatenable(protegeProposition, result, cm);
        Util.setGap(protegeProposition, result, backend, cm);
        Util.setReferences(protegeProposition, result, cm);
        Map<Instance, TemporalExtendedPropositionDefinition> extendedParameterCache =
                new HashMap<Instance, TemporalExtendedPropositionDefinition>();
        addComponentAbstractionDefinitions(protegeProposition, result,
                extendedParameterCache, backend);
        setRelation(protegeProposition, result, backend, cm);
        setRequireSecond(protegeProposition, result, cm);
        Util.setInverseIsAs(protegeProposition, result, cm);
        result.setTemporalOffset(Util.temporalOffsets(protegeProposition, 
                backend, extendedParameterCache));
        result.setSourceId(
                DefaultSourceId.getInstance(backend.getDisplayName()));
        return result;
    }
    
    @Override
    public String getClsName() {
        return "PairAbstraction";
    }

    private void setRelation(
            Instance protegeProposition, PairDefinition pd,
            ProtegeKnowledgeSourceBackend backend, ConnectionManager cm)
            throws KnowledgeSourceReadException {
        Slot slot = cm.getSlot("withRelation");
        Instance relationInstance = (Instance) cm.getOwnSlotValue(
                protegeProposition, slot);

        if (relationInstance != null) {
            Relation relation = Util.instanceToRelation(relationInstance,
                    cm, backend);
            pd.setRelation(relation);
        }
    }
    
    private void setRequireSecond(
            Instance protegeProposition, PairDefinition pd,
            ConnectionManager cm) 
            throws KnowledgeSourceReadException {
        Slot slot = cm.getSlot("requireSecond");
        Boolean requireSecond = 
                (Boolean) cm.getOwnSlotValue(protegeProposition, slot);
        assert requireSecond != null : "requireSecond cannot be null!";
        pd.setSecondRequired(requireSecond);
    }

    /**
     * Adds all of the abstraction definitions for which the given complex
     * abstraction instance defines a temporal relation to the given complex
     * abstraction definition.
     * 
     * @param complexAbstractionInstance
     *            a Protege complex abstraction <code>Instance</code>.
     * @param cad
     *            a PROTEMPA <code>HighLevelAbstractionDefinition</code>
     *            instance.
     */
    private static void addComponentAbstractionDefinitions(
            Instance pairAbstractionInstance, PairDefinition pd,
            Map<Instance, TemporalExtendedPropositionDefinition> extendedParameterCache,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        ConnectionManager cm = backend.getConnectionManager();
        Instance relation = (Instance) cm.getOwnSlotValue(
                pairAbstractionInstance, cm.getSlot("withRelation"));
        if (relation != null) {
            Instance lhs = (Instance) cm.getOwnSlotValue(relation,
                    cm.getSlot("lhs"));
            assert lhs != null : "lhs cannot be null";
            Instance rhs = (Instance) cm.getOwnSlotValue(relation,
                    cm.getSlot("rhs"));
            assert rhs != null : "rhs cannot be null";
            TemporalExtendedPropositionDefinition lhsDefinition =
                    Util.instanceToTemporalExtendedPropositionDefinition(lhs, 
                    backend);
            extendedParameterCache.put(lhs, lhsDefinition);
            TemporalExtendedPropositionDefinition rhsDefinition =
                    Util.instanceToTemporalExtendedPropositionDefinition(rhs, 
                    backend);
            extendedParameterCache.put(rhs, rhsDefinition);
            pd.setLeftHandProposition(lhsDefinition);
            pd.setRightHandProposition(rhsDefinition);
        }
    }
}
