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

import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Instance;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.SliceDefinition;

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

        Boolean mergedInterval = (Boolean) cm.getOwnSlotValue(protegeParameter,
                cm.getSlot("mergedInterval"));
        ad.setMergedInterval(mergedInterval);

        Collection<?> abstractedFromInstances = cm.getOwnSlotValues(
                protegeParameter, cm.getSlot("abstractedFrom"));
        for (Iterator<?> itr = abstractedFromInstances.iterator(); itr
                .hasNext();) {
            ad.addAbstractedFrom(((Instance) itr.next()).getName());
        }
        return ad;
    }

    @Override
    public String getClsName() {
        return "SliceAbstraction";
    }
}
