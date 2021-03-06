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

import edu.stanford.smi.protege.model.Instance;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.TemporalPropositionDefinition;
import org.protempa.backend.ksb.KnowledgeSourceBackend;

/**
 *
 * @author Andrew Post
 */
interface TemporalPropositionConverter extends PropositionConverter {
    /**
     * Convert and add the given Protege parameter instance to the given
     * PROTEMPA knowledge base, if it hasn't already been added to the PROTEMPA
     * knowledge base.
     *
     * @param protegeProposition
     *            the Protege proposition {@link Instance}.
     * @param backend
     *            the Protege {@link KnowledgeSourceBackend}.
     */
    TemporalPropositionDefinition convert(Instance protegeProposition,
            ProtegeKnowledgeSourceBackend backend)
            throws KnowledgeSourceReadException;
}
