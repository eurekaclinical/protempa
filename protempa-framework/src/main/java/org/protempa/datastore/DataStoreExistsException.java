package org.protempa.datastore;

/*-
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2018 Emory University
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

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew
 */
public class DataStoreExistsException extends ProtempaException {

    public DataStoreExistsException() {
    }

    public DataStoreExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataStoreExistsException(String message) {
        super(message);
    }

    public DataStoreExistsException(Throwable cause) {
        super(cause);
    }
    
}
