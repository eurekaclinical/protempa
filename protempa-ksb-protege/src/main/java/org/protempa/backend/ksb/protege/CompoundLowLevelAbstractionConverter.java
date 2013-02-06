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

import java.util.Iterator;

import org.protempa.CompoundLowLevelAbstractionDefinition;
import org.protempa.ValueClassification;
import org.protempa.CompoundLowLevelAbstractionDefinition.ValueDefinitionMatchOperator;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.MinMaxGapFunction;
import org.protempa.proposition.value.Unit;

import edu.stanford.smi.protege.model.Instance;

public final class CompoundLowLevelAbstractionConverter implements
        AbstractionConverter {

    @Override
    public String getClsName() {
        return "CompoundSimpleAbstraction";
    }

    @Override
    public CompoundLowLevelAbstractionDefinition convert(
            Instance protegeProposition, ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        CompoundLowLevelAbstractionDefinition d = new CompoundLowLevelAbstractionDefinition(
                protegeProposition.getName());
        ConnectionManager cm = backend.getConnectionManager();
        Util.setNames(protegeProposition, d, cm);
        Util.setInDataSource(protegeProposition, d, cm);
        Util.setInverseIsAs(protegeProposition, d, cm);
        Util.setProperties(protegeProposition, d, cm);
        Util.setTerms(protegeProposition, d, cm);
        Util.setReferences(protegeProposition, d, cm);
        Util.setGap(protegeProposition, d, backend, cm);
        setGapBetweenValues(protegeProposition, d, backend);

        Integer minValues = (Integer) cm.getOwnSlotValue(protegeProposition,
                cm.getSlot("minValues"));
        if (minValues != null) {
            d.setMinimumNumberOfValues(minValues);
        }

        String valDefMatchOp = cm.getOwnSlotValue(protegeProposition,
                cm.getSlot("matchOperator")).toString();
        if (!(valDefMatchOp.equalsIgnoreCase("any") || valDefMatchOp
                .equalsIgnoreCase("all"))) {
            throw new IllegalArgumentException(
                    "valueDefinitionMatchOperator can only be any or all");
        } else {
            d.setValueDefinitionMatchOperator(ValueDefinitionMatchOperator
                    .valueOf(valDefMatchOp.toUpperCase()));
        }

        for (Iterator<?> itr = cm.getOwnSlotValues(protegeProposition,
                cm.getSlot("compoundValueClassifications")).iterator(); itr
                .hasNext();) {
            addValueClassification(d, (Instance) itr.next(), cm);
        }
        return d;
    }

    /**
     * @param instance
     * @param d
     */
    private static void setGapBetweenValues(Instance abstractParameter,
            CompoundLowLevelAbstractionDefinition d,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        ConnectionManager cm = backend.getConnectionManager();
        Integer minGapValues = (Integer) Util.parseTimeConstraint(
                abstractParameter, "minGapValues", cm);
        Unit minGapValuesUnit = (Unit) Util.parseUnitsConstraint(
                abstractParameter, "minGapValuesUnits", backend, cm);
        Integer maxGapValues = (Integer) Util.parseTimeConstraint(
                abstractParameter, "maxGapValues", cm);
        Unit maxGapValuesUnit = Util.parseUnitsConstraint(abstractParameter,
                "maxGapValuesUnits", backend, cm);
        d.setGapFunctionBetweenValues(new MinMaxGapFunction(minGapValues,
                minGapValuesUnit, maxGapValues, maxGapValuesUnit));
    }

    private static void addValueClassification(
            CompoundLowLevelAbstractionDefinition def,
            Instance compoundValueClasificationInstance, ConnectionManager cm)
            throws KnowledgeSourceReadException {
        String valName = compoundValueClasificationInstance.getName();
        for (Iterator<?> itr = cm.getOwnSlotValues(
                compoundValueClasificationInstance,
                cm.getSlot("simpleValueClassifications")).iterator(); itr
                .hasNext();) {
            Instance simpleVal = (Instance) itr.next();
            String llaId = ((Instance) cm.getOwnSlotValue(simpleVal,
                    cm.getSlot("simpleAbstraction"))).getName();
            Instance paramVal = (Instance) cm.getOwnSlotValue(simpleVal,
                    cm.getSlot("parameterValue"));
            String llaValName = (String) cm.getOwnSlotValue(paramVal,
                    cm.getSlot("displayName"));
            def.addValueClassification(new ValueClassification(valName, llaId,
                    llaValName));
        }
    }
}
