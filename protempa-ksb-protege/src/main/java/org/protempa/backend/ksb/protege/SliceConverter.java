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

import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.SliceDefinition;
import org.protempa.TemporalExtendedPropositionDefinition;

final class SliceConverter implements AbstractionConverter {

    SliceConverter() {
    }

    @Override
    public SliceDefinition convert(Instance protegeParameter,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {

        SliceDefinition ad = new SliceDefinition(protegeParameter.getName());
        ConnectionManager cm = backend.getConnectionManager();
        Util.setNames(protegeParameter, ad, cm);
        Util.setInDataSource(protegeParameter, ad, cm);
        Util.setInverseIsAs(protegeParameter, ad, cm);
        Util.setProperties(protegeParameter, ad, cm);
        Util.setTerms(protegeParameter, ad, cm);
        Integer maxIndexInt = (Integer) cm.getOwnSlotValue(protegeParameter,
                cm.getSlot("maxIndex"));
        if (maxIndexInt != null) {
            ad.setMaxIndex(maxIndexInt.intValue());
        }
        Integer minIndexInt = (Integer) cm.getOwnSlotValue(protegeParameter,
                cm.getSlot("minIndex"));
        if (maxIndexInt != null) {
            ad.setMinIndex(minIndexInt.intValue());
        }

        Slot mergedIntervalSlot = cm.getSlot("mergedInterval");
        if (mergedIntervalSlot != null) {
            Boolean mergedInterval = 
                    (Boolean) cm.getOwnSlotValue(protegeParameter,
                    mergedIntervalSlot);
            ad.setMergedInterval(mergedInterval);
        } else {
            Util.logger().log(Level.WARNING, 
                    "Ontology in {0} does not have mergedInterval slot, skipping for slice definition {1}", 
                    new Object[]{backend.getDisplayName(), ad.getId()});
        }

        Collection<?> epInstances = cm.getOwnSlotValues(
                protegeParameter, cm.getSlot("extendedParameters"));
        Map<Instance, TemporalExtendedPropositionDefinition> extendedParameterCache =
                new HashMap<Instance, TemporalExtendedPropositionDefinition>();
        for (Iterator<?> itr = epInstances.iterator(); itr
                .hasNext();) {
            Instance extendedParameter = (Instance) itr.next();
            TemporalExtendedPropositionDefinition tepd =
                    Util.instanceToTemporalExtendedPropositionDefinition(
                    extendedParameter, backend);
            extendedParameterCache.put(extendedParameter, tepd);
            ad.add(tepd);
        }
        return ad;
    }

    @Override
    public String getClsName() {
        return "SliceAbstraction";
    }
}
