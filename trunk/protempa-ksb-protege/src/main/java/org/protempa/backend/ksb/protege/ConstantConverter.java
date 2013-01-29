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

import edu.stanford.smi.protege.model.Instance;
import org.protempa.*;

class ConstantConverter implements PropositionConverter {

    @Override
    public ConstantDefinition convert(Instance protegeProposition,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException {
        ConstantDefinition result = new ConstantDefinition(
                protegeProposition.getName());
        ConnectionManager cm = backend.getConnectionManager();
        Util.setNames(protegeProposition, result, cm);
        Util.setInDataSource(protegeProposition, result, cm);
        Util.setProperties(protegeProposition, result, cm);
        Util.setTerms(protegeProposition, result, cm);
        Util.setInverseIsAs(protegeProposition, result, cm);
        Util.setReferences(protegeProposition, result, cm);
        result.setSourceId(DefaultSourceId.getInstance(
                backend.getDisplayName()));
        return result;
    }
    
    @Override
    public String getClsName() {
        return "Constant";
    }
}
