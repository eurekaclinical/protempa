/*
 * #%L
 * JavaUtil
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
package org.arp.javautil.io;

/**
 * Adds support for automatically retrying an operation whose failure is 
 * expected under defined circumstances. This enables fault tolerance for
 * data access operations.
 *
 * @param <E> the type of error code or exception that occurs when the
 * operation fails.
 * 
 * @author Andrew Post
 */
public interface Retryable<E> {

    /**
     * Attempts to execute an operation that may fail under defined
     * circumstances.
     *
     * @return an error code or exception, or <code>null</code> if the attempt 
     * is successful. Exceptions are returned not thrown so that the type
     * of error can be specified using generics (<code>throw E</code> causes
     * a compile-time error).
     */
    E attempt();

    /**
     * Performs some operation between attempts (e.g., wait for a few seconds).
     * This will not be called after the last attempt. Use the 
     * {@link #attempt() } method for resource cleanup.
     */
    void recover();
    
}
