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
package org.protempa.dest;

import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractQueryResultsHandler
        implements QueryResultsHandler {
    
    @Override
    public void validate() throws QueryResultsHandlerValidationFailedException {
    }

    @Override
    public String[] getPropositionIdsNeeded() throws QueryResultsHandlerProcessingException {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    @Override
    public void start() throws QueryResultsHandlerProcessingException {
    }

    @Override
    public void finish() throws QueryResultsHandlerProcessingException {
    }

    @Override
    public void close() throws QueryResultsHandlerCloseException {
    }
}
