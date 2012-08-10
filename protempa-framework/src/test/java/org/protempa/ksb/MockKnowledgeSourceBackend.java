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
package org.protempa.ksb;

import org.apache.commons.lang.ArrayUtils;
import org.drools.util.StringUtils;
import org.protempa.AbstractionDefinition;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.ksb.AbstractKnowledgeSourceBackend;

public final class MockKnowledgeSourceBackend
        extends AbstractKnowledgeSourceBackend {

    @Override
    public void initialize(BackendInstanceSpec config)
            throws BackendInitializationException {
    }

    /**
     * Make public so that tests can call it.
     * @see AbstractKnowledgeSourceBackend
     */
    @Override
    public void fireKnowledgeSourceBackendUpdated() {
        super.fireKnowledgeSourceBackendUpdated();
    }

    @Override
    public String getDisplayName() {
        return "Mock Knowledge Source Backend";
    }

    @Override
    public PropositionDefinition readPropositionDefinition(String id) throws KnowledgeSourceReadException {
        return null;
    }

    @Override
    public AbstractionDefinition readAbstractionDefinition(String id) throws KnowledgeSourceReadException {
        return null;
    }

    @Override
    public String[] readAbstractedInto(String propId) {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    @Override
    public String[] readIsA(String propId) {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }
}
