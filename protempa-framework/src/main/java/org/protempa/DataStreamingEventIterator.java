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

import java.util.NoSuchElementException;

/**
 * An iterator-like interface for iterating over {@link DataStreamingEvent}s.
 * It throws checked exceptions and provides a {@link #close()} method for
 * cleaning up resources managed by the iterator. These iterators should
 * always be closed after use.
 * 
 * @author Andrew Post
 */
public interface DataStreamingEventIterator<E> {

    /**
     * Returns <code>true</code> if the iteration has more 
     * {@link DataStreamingEvent}s. (In other words, returns true if next 
     * would return a {@link DataStreamingEvent} rather than throwing an 
     * exception.)
     * 
     * @return true if the iterator has more {@link DataStreamingEvent}s.
     * @throws DataSourceReadException if an error occurred reading data from
     * the underlying data source.
     */
    boolean hasNext() throws DataSourceReadException;

    /**
     * Returns the next {@link DataStreamingEvent} in the iteration.
     * 
     * @return the next {@link DataStreamingEvent} in the iteration.
     * @throws DataSourceReadException if an error occurred reading data from
     * the underlying data source.
     * @throws NoSuchElementException if the iteration has no more 
     * {@link DataStreamingEvent}s.
     */
    DataStreamingEvent<E> next() throws DataSourceReadException;

    /**
     * Closes any IO or other resources opened by the iterator.
     * 
     * @throws DataSourceReadException if an error occurred reading data from
     * the underlying data source(s).
     */
    void close() throws DataSourceReadException;
}
