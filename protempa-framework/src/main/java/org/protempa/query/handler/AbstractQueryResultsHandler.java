/*
 * #%L
 * Protempa Framework
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
package org.protempa.query.handler;

import org.apache.commons.lang.ArrayUtils;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.query.Query;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractQueryResultsHandler 
        implements QueryResultsHandler {

    @Override
    public void init(KnowledgeSource knowledgeSource, Query query)
            throws QueryResultsHandlerInitException {
    }

    @Override
    public void start() throws QueryResultsHandlerProcessingException {
    }

    @Override
    public void finish() throws QueryResultsHandlerProcessingException {
    }

    /**
     * Is a no-op. Override to provide validation functionality.
     * 
     * @throws QueryResultsHandlerValidationFailedException
     * @throws KnowledgeSourceReadException 
     */
    @Override
    public void validate() 
            throws QueryResultsHandlerValidationFailedException,
            KnowledgeSourceReadException {
    }
    
    /**
     * Returns an empty string array, because this method cannot return 
     * inferred proposition ids.
     * 
     * @return an empty {@link String} array. Guaranteed not <code>null</code>.
     */
    @Override
    public String[] getPropositionIdsNeeded() 
            throws KnowledgeSourceReadException {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    
}
