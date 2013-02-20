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

import java.util.EventListener;

/**
 * Listener interface for when the source changes.
 * 
 * @author Andrew Post
 */
public interface SourceListener<E extends SourceUpdatedEvent>
        extends EventListener {

    /**
     * The method that gets called when the source is updated.
     *
     * @param event
     *            a {@link SourceUpdatedEvent}.
     */
    void sourceUpdated(E event);

    /**
     * The method that gets called when the source is closed unexpectedly, like
     * when a connection to a server is lost.
     *
     * @param e a {@link SourceClosedUnexpectedlyEvent}.
     */
    void closedUnexpectedly(SourceClosedUnexpectedlyEvent e);
}
