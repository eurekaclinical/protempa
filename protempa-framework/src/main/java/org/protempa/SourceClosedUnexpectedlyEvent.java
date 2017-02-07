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

import java.util.EventObject;
import org.protempa.backend.Backend;

import org.protempa.backend.BackendUpdatedEvent;

/**
 *
 * @author Andrew Post
 */
public final class SourceClosedUnexpectedlyEvent<S extends SourceUpdatedEvent, B extends Backend<T>, T extends BackendUpdatedEvent> extends EventObject {
    private static final long serialVersionUID = 7088929112407759901L;
    private Source<S, B, T> protempaSource;

    /**
     * Initializes the event with the {@link Source} that closed unexpectedly.
     *
     * @param protempaSource a {@link Source}.
     */
    public SourceClosedUnexpectedlyEvent(Source<S, B, T> protempaSource) {
        super(protempaSource);
        this.protempaSource = protempaSource;
    }

    /**
     * Returns the {@link Source} that closed unexpectedly. The
     * {@link #getSource()} method returns the same thing but requires a cast.
     *
     * @return a {@link Source}.
     */
    public Source<S, B, T> getProtempaSource() {
        return this.protempaSource;
    }
}
