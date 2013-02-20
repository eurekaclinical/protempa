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

import java.util.Collections;
import java.util.Set;

import org.drools.base.ClassObjectType;
import org.protempa.proposition.Event;

/**
 * Drools <code>ObjectType</code> for identifying <code>Event</code>s with
 * one of a set of event ids.
 * 
 * @author Andrew Post
 */
final class EventObjectType extends ClassObjectType {

    private static final long serialVersionUID = 3914175196588645508L;
    /**
     * The event id <code>String</code>s to check.
     */
    private final Set<String> eventIds;

    /**
     * Specifies the set of event ids.
     * 
     * @param eventIds
     *            a <code>Set</code> of event id <code>String</code>s. If
     *            <code>null</code> or empty, <code>matches()</code> does
     *            not evaluate arguments' event ids.
     */
    EventObjectType(Set<String> eventIds) {
        super(Event.class);
        this.eventIds = eventIds;
    }

    /**
     * Specifies the event id.
     * 
     * @param eventId
     *            an event id <code>String</code>. If <code>null</code> or
     *            empty, <code>matches()</code> does not evaluate arguments'
     *            event ids.
     */
    EventObjectType(String eventId) {
        super(Event.class);
        this.eventIds = eventId != null ? Collections.singleton(eventId) : null;
    }

    /**
     * Checks if the given argument is an <code>Event</code> with one of the
     * specified event ids.
     * 
     * @param arg0
     *            an <code>Event</code>.
     * @return <code>true</code> if the given argument is an
     *         <code>Event</code> with one of the specified event ids,
     *         <code>false</code> if the given argument is <code>null</code>,
     *         not an <code>Event</code>, or is an <code>Event</code> but
     *         has a different event id.
     * @see org.drools.spi.ObjectType#matches(java.lang.Object)
     */
    @Override
    public boolean matches(Object arg0) {
        if (!super.matches(arg0)) {
            return false;
        }
        Event event = (Event) arg0;
        return eventIds == null || eventIds.contains(event.getId());
    }
}
