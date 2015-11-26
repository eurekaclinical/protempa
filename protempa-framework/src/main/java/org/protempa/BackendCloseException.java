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

/**
 * Abstract class for exceptions that are thrown when an error occurs calling
 * the data source.
 *
 * @author Andrew Post
 *
 */
public final class BackendCloseException extends ProtempaException {

    public BackendCloseException() {
    }

    public BackendCloseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BackendCloseException(String message) {
        super(message);
    }

    public BackendCloseException(Throwable cause) {
        super(cause);
    }
}
