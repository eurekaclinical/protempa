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
package org.protempa;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * Thrown when there is an error retrieving data from the data source. To be
 * raised from {@link SchemaAdaptor}s.
 *
 * @author Andrew Post
 *
 */
public class DataSourceReadException extends DataSourceException {

    private static final long serialVersionUID = -1607783133183868272L;

    public DataSourceReadException() {
        super();
    }

    public DataSourceReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataSourceReadException(String message) {
        super(message);
    }

    public DataSourceReadException(Throwable cause) {
        super(cause);
    }

    public DataSourceReadException(List<DataSourceReadException> causes) {
        super(exceptionsToString(causes));
    }
    
    private static String exceptionsToString(List<DataSourceReadException> causes) {
        StringBuilder stackTrace = new StringBuilder();
        for (DataSourceReadException ex : causes) {
            stackTrace.append(ExceptionUtils.getStackTrace(ex));
        }
        
        return stackTrace.toString();
    }
}
