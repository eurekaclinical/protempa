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
import org.arp.javautil.graph.WeightFactory;
import org.protempa.*;

class EventConverter implements TemporalPropositionConverter {

    @Override
    public EventDefinition convert(Instance protegeProposition,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        EventDefinition result = new EventDefinition(
                protegeProposition.getName());
        ConnectionManager cm = backend.getConnectionManager();
        Util.setNames(protegeProposition, result, cm);
        Util.setInDataSource(protegeProposition, result, cm);
        Util.setProperties(protegeProposition, result, cm);
        Util.setTerms(protegeProposition, result, cm);
        Util.setInverseIsAs(protegeProposition, result, cm);
        Util.setReferences(protegeProposition, result, cm);

        Collection<?> hasParts = cm.getOwnSlotValues(protegeProposition,
                cm.getSlot("hasPart"));
        Slot offsetEventSlot = cm.getSlot("offsetEvent");
        Slot offsetSide = cm.getSlot("offsetSide");
        for (Iterator<?> itr = hasParts.iterator(); itr.hasNext();) {
            Instance hasPartInstance = (Instance) itr.next();
            Integer cst = Util.parseTimeConstraint(
                    hasPartInstance, "offset", cm);
        }
        result.setSourceId(
                DefaultSourceId.getInstance(backend.getId()));
        return result;
    }

    @Override
    public String getClsName() {
        return "Event";
    }
}
