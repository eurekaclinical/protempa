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
 * Thrown if an error occurred during validation that prevented its
 * completion.
 * 
 * @author Andrew Post
 */
public class DataSourceValidationIncompleteException
        extends DataSourceException {
    private static final long serialVersionUID = -3628452535225862601L;

    /**
     * Instantiates the exception with another nested {@link Throwable}.
     *
     * @param cause a {@link Throwable}.
     */
    public DataSourceValidationIncompleteException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates the exception with an error message.
     *
     * @param message a {@link String}.
     */
    public DataSourceValidationIncompleteException(String message) {
        super(message);
    }

    /**
     * Instantiates the exception with an error message and a nested
     * {@link Throwable}.
     *
     * @param message an error message {@link String}.
     * @param cause a {@link Throwable}.
     */
    public DataSourceValidationIncompleteException(String message,
            Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates the exception with no information.
     */
    public DataSourceValidationIncompleteException() {
    }



}
