/*
 * #%L
 * Protempa Protege Knowledge Source Backend
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
package org.protempa.backend.ksb.protege;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.protempa.DefaultSourceId;
import org.protempa.HighLevelAbstractionDefinition;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.TemporalExtendedPropositionDefinition;
import org.protempa.proposition.interval.Relation;
import org.protempa.proposition.value.ValueComparator;

import edu.stanford.smi.protege.model.Instance;
import java.util.Date;

/**
 * Converts and adds Protege complex abstraction instances to a PROTEMPA
 * knowledge base.
 * 
 * @author Andrew Post
 */
class HighLevelAbstractionConverter implements AbstractionConverter {

    static final Map<String, ValueComparator> STRING_TO_VAL_COMP_MAP = new HashMap<>();

    static {
        STRING_TO_VAL_COMP_MAP.put("eq", ValueComparator.EQUAL_TO);
        STRING_TO_VAL_COMP_MAP.put("gt", ValueComparator.GREATER_THAN);
        STRING_TO_VAL_COMP_MAP.put("gte",
                ValueComparator.GREATER_THAN_OR_EQUAL_TO);
        STRING_TO_VAL_COMP_MAP.put("lt", ValueComparator.LESS_THAN);
        STRING_TO_VAL_COMP_MAP.put("lte", ValueComparator.LESS_THAN_OR_EQUAL_TO);
        STRING_TO_VAL_COMP_MAP.put("in", ValueComparator.IN);
        STRING_TO_VAL_COMP_MAP.put("notin", ValueComparator.NOT_IN);
    }

    
    HighLevelAbstractionConverter() {
    }

    @Override
    public HighLevelAbstractionDefinition convert(Instance complexAbstractionInstance,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        HighLevelAbstractionDefinition result =
                new HighLevelAbstractionDefinition(
                complexAbstractionInstance.getName());
        ConnectionManager cm = backend.getConnectionManager();
        Util.setNames(complexAbstractionInstance, result, cm);
        Util.setInDataSource(complexAbstractionInstance, result, cm);
        Util.setProperties(complexAbstractionInstance, result, cm);
        Util.setTerms(complexAbstractionInstance, result, cm);
        Util.setGap(complexAbstractionInstance, result, backend, cm);
        Util.setSolid(complexAbstractionInstance, result, cm);
        Util.setConcatenable(complexAbstractionInstance, result, cm);
        Util.setReferences(complexAbstractionInstance, result, cm);
        result.setAccessed(new Date());
        Map<Instance, TemporalExtendedPropositionDefinition> extendedParameterCache =
                new HashMap<>();
        addComponentAbstractionDefinitions(complexAbstractionInstance, result,
                extendedParameterCache, backend);
        addRelationships(extendedParameterCache,
                complexAbstractionInstance, result, backend);
        Util.setInverseIsAs(complexAbstractionInstance, result, cm);
        result.setTemporalOffset(Util.temporalOffsets(
                complexAbstractionInstance, backend, extendedParameterCache));
        result.setSourceId(DefaultSourceId.getInstance(backend.getId()));
        return result;
    }

    /**
     * Adds all of the temporal relationships in the given Protege complex
     * abstraction instance (specified with the <code>withRelationships</code>
     * slot) to the given PROTEMPA complex abstraction definition.
     *
     * @param instance
     *            a Protege complex abstraction <code>Instance</code>.
     * @param cad
     *            a PROTEMPA <code>ComplexAbstractionDefinition</code> instance.
     */
    private static void addRelationships(
            Map<Instance, TemporalExtendedPropositionDefinition> extendedParameterCache,
            Instance instance, HighLevelAbstractionDefinition cad,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        ConnectionManager cm = backend.getConnectionManager();
        for (Iterator<?> itr = instance.getOwnSlotValues(
                instance.getKnowledgeBase().getSlot("withRelations")).iterator(); itr.hasNext();) {
            Instance relationInstance = (Instance) itr.next();

            Instance lhsExtendedParameter =
                    (Instance) cm.getOwnSlotValue(relationInstance,
                    cm.getSlot("lhs"));
            Instance rhsExtendedParameter =
                    (Instance) cm.getOwnSlotValue(relationInstance,
                    cm.getSlot("rhs"));

            Relation relation = Util.instanceToRelation(relationInstance,
                    cm, backend);

            TemporalExtendedPropositionDefinition lhsEP =
                    extendedParameterCache.get(lhsExtendedParameter);

            TemporalExtendedPropositionDefinition rhsEP =
                    extendedParameterCache.get(rhsExtendedParameter);

            cad.setRelation(lhsEP, rhsEP, relation);
        }
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
            Instance complexAbstractionInstance,
            HighLevelAbstractionDefinition cad,
            Map<Instance, TemporalExtendedPropositionDefinition> extendedParameterCache,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        Set<Object> extendedParameters = new HashSet<>();
        ConnectionManager cm = backend.getConnectionManager();
        for (Iterator<?> itr = cm.getOwnSlotValues(complexAbstractionInstance,
                cm.getSlot("withRelations")).iterator(); itr.hasNext();) {
            Instance relation = (Instance) itr.next();
            Object lhs = cm.getOwnSlotValue(relation, cm.getSlot("lhs"));
            if (lhs != null) {
                extendedParameters.add(lhs);
            }
            Object rhs = cm.getOwnSlotValue(relation, cm.getSlot("rhs"));
            if (rhs != null) {
                extendedParameters.add(rhs);
            }
        }

        for (Iterator<Object> itr = extendedParameters.iterator(); itr.hasNext();) {
            Instance extendedParameter = (Instance) itr.next();
            TemporalExtendedPropositionDefinition def =
                    Util.instanceToTemporalExtendedPropositionDefinition(
                    extendedParameter, backend);
            extendedParameterCache.put(extendedParameter, def);
            cad.add(def);
        }
    }

    @Override
    public String getClsName() {
        return "ComplexAbstraction";
    }
}
