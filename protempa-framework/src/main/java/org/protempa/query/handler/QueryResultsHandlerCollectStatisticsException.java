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
 * Thrown if an error occurs during collection of data statistics.
 * 
 * @author Andrew Post
 */
public class QueryResultsHandlerCollectStatisticsException extends ProtempaException {

    public QueryResultsHandlerCollectStatisticsException() {
    }

    public QueryResultsHandlerCollectStatisticsException(String message, Throwable cause) {
        super(message, cause);
    }

    public QueryResultsHandlerCollectStatisticsException(String message) {
        super(message);
    }

    public QueryResultsHandlerCollectStatisticsException(Throwable cause) {
        super(cause);
    }
    
}
