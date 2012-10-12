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
package org.protempa;

import java.util.EventObject;
import org.protempa.backend.Backend;
import org.protempa.backend.BackendUpdatedEvent;

/**
 * Source updated events.
 *
 * @author Andrew Post
 *
 * @param <S>
 */
public abstract class SourceUpdatedEvent extends EventObject {
    
    private static final long serialVersionUID = 7105747666448825906L;

    <E extends BackendUpdatedEvent> SourceUpdatedEvent(Source<? extends SourceUpdatedEvent, ? extends Backend<E>, E> protempaSource) {
        super(protempaSource);
    }

    /**
     * Returns the source {@link Source} (the same as what
     * <code>getSource()</code> returns).
     *
     * @return a {@link Backend}.
     */
    public abstract<E extends BackendUpdatedEvent> Source<? extends SourceUpdatedEvent, ? extends Backend<E>, E> getProtempaSource();
}
