/*
 * #%L
 * Protempa Commons Backend Provider
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
package org.protempa.bp.commons.dsb.relationaldb;

/**
 * Thrown when no SQL generator was found that is compatible with the database
 * and available drivers.
 * 
 * @author Andrew Post
 */
public class NoCompatibleSQLGeneratorException extends Exception {

    /**
     * Constructs a new exception with null as its detail message.
     */
    public NoCompatibleSQLGeneratorException() {
    }

    /**
     * Constructs a new exception with the specified cause and a detail message 
     * of (cause==null ? null : cause.toString()) (which typically contains the 
     * class and detail message of cause).
     * 
     * @param cause the cause. May be <code>null</code> if the cause is
     * nonexistent or unknown.
     */
    public NoCompatibleSQLGeneratorException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * 
     * @param message the detail message.
     * @param cause the cause. May be <code>null</code> if the cause is
     * nonexistent or unknown.
     */
    public NoCompatibleSQLGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message the detail message.
     */
    public NoCompatibleSQLGeneratorException(String message) {
        super(message);
    }

    

}
