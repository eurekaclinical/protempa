/*
 * #%L
 * Protempa Framework
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
package org.protempa.backend;

import org.protempa.BackendListener;
import org.protempa.Source;
import org.protempa.backend.BackendInstanceSpec;

/**
 * Common interface for PROTEMPA backends.
 * 
 * @author Andrew Post
 * 
 * @param <E>
 *            the {@link BackendUpdatedEvent} that this backend fires.
 * @param <S> 
 *            this backend's corresponding {@link Source}.
 */
public interface Backend<E extends BackendUpdatedEvent, S extends Source<E>> {

    /**
     * Initializes the backend from a {@link BackendInstanceSpec}. It is called
     * for you by {@link BackendInstanceSpec#getInstance()}, but if you
     * create a {@link Backend} object yourself, you must call this manually
     * before using it.
     *
     * @param source
     *            the backend's corresponding {@link Source} instance.
     * @param config
     *            the configuration {@link Properties} object.
     * @return <code>true</code> if initialization was successful,
     *         <code>false</code> if initialization failed.
     */
    void initialize(BackendInstanceSpec<?> config)
            throws BackendInitializationException;

    /**
     * Returns the name of this backend for display in user interfaces.
     * @return a {@link String}.
     */
    String getDisplayName();

    /**
     * Releases any resources allocated by this backend.
     */
    void close();

    /**
     * Registers a listener that get called whenever a backend changes.
     *
     * @param listener
     *            a {@link BackendListener}.
     */
    void addBackendListener(BackendListener<E> listener);

    /**
     * Unregisters a listener so that changes to the backend are no longer sent.
     *
     * @param listener
     *            a {@link BackendListener}.
     */
    void removeBackendListener(BackendListener<E> listener);
}