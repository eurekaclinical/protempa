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

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public class QueryResultsHandlerInitException extends ProtempaException {

    public QueryResultsHandlerInitException() {
    }

    public QueryResultsHandlerInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryResultsHandlerInitException(String message) {
        super(message);
    }

    public QueryResultsHandlerInitException(Throwable cause) {
        super(cause);
    }
    
}
