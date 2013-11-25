package org.protempa.backend.dsb.relationaldb;

/*
 * #%L
 * Protempa Relational Database Data Source Backend
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
import org.protempa.DataSourceReadException;
import org.protempa.DataStreamingEvent;
import org.protempa.DataStreamingEventIterator;
import org.protempa.UniqueIdPair;
import org.protempa.proposition.UniqueId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Iterates over the references. This does not entirely adhere to the
 * {@link java.util.Iterator} contract, because it relies on another class to
 * populate it while iteration is occurring.
 *
 * @author Michel Mansour
 */
final class InboundReferenceResultSetIterator implements
        DataStreamingEventIterator<UniqueIdPair> {

    private static final Logger LOGGER = Logger.getLogger(InboundReferenceResultSetIterator.class.getName());

    /*
     * For many-to-one references, we will get multiple instances of the same
     * reference back. This map ensures that we will only deliver one
     * instance of each reference.
     */
    private Map<DestructuredUniqueIdPair, Set<UniqueId>> referenceUniqueIds;
    private Queue<DataStreamingEvent<UniqueIdPair>> dataStreamingEventQueue;
    private String keyId;
    private boolean end = false;
    private final String entityName;
    private String lastDelivered;
    private boolean nextInvoked = false;
    private boolean addUniqueIdsInvoked = false;

    InboundReferenceResultSetIterator(String entityName) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Creating reference iterator for {0}", new Object[]{entityName});
        }
        this.entityName = entityName;
        this.referenceUniqueIds = new HashMap<>();
        this.dataStreamingEventQueue = new LinkedList<>();
    }

    void resultSetComplete() {
        this.end = true;
        createDataStreamingEvent();
    }

    private boolean isDone() {
        return this.dataStreamingEventQueue.isEmpty() && this.end;
    }

    @Override
    public boolean hasNext() throws DataSourceReadException {
        return !isDone();
    }

    @Override
    public DataStreamingEvent<UniqueIdPair> next() throws DataSourceReadException {
        if (LOGGER.isLoggable(Level.FINEST)) {
            if (!this.nextInvoked) {
                this.nextInvoked = true;
                LOGGER.log(Level.FINEST, "First invocation of next() for {0} reference iterator", this.entityName);
            }
        }

        if (isDone()) {
            throw new NoSuchElementException("dataStreamingEventQueue is "
                    + "empty");
        }
        if (this.keyId == null) {
            LOGGER.log(Level.SEVERE, "Fatal error in reference iterator {0}: keyId is null", this.entityName);
            LOGGER.log(Level.SEVERE, "Queue has data: {0}", this.dataStreamingEventQueue.isEmpty() ? "no" : "yes");
            LOGGER.log(Level.SEVERE, "Unique ids waiting: {0}", (this.referenceUniqueIds == null || this.referenceUniqueIds.isEmpty()) ? "no" : "yes");
        }

        DataStreamingEvent<UniqueIdPair> result;
        if (this.dataStreamingEventQueue.isEmpty()) {
            /*
             * next() might get called ahead of addUniqueIds(). In that
             * situation, the queue will be empty. If the queue is empty, then
             * send back a DataStreamingEvent with no UniqueIdPairs.
             */

            result = new DataStreamingEvent<>(this.keyId,
                    new ArrayList<UniqueIdPair>(0));
        } else {
            result = this.dataStreamingEventQueue.remove();
        }

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST,
                    "Iterating over references for {0}: Current: {1}, Last Delivered: {2}",
                    new Object[]{this.entityName, result.getKeyId(),
                this.lastDelivered});
        }

        this.lastDelivered = result.getKeyId();
        return result;
    }

    private static final class DestructuredUniqueIdPair {

        private final String referenceName;
        private final UniqueId proposition;

        DestructuredUniqueIdPair(String referenceName, UniqueId proposition) {
            this.referenceName = referenceName;
            this.proposition = proposition;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o instanceof DestructuredUniqueIdPair) {
                DestructuredUniqueIdPair other = (DestructuredUniqueIdPair) o;
                return referenceName.equals(other.referenceName)
                        && proposition.equals(other.proposition);
            }

            return false;
        }

        @Override
        public int hashCode() {
            int result = 17;

            int c = referenceName.hashCode();
            result = 31 * result + c;
            c = proposition.hashCode();
            result = 31 * result + c;

            return result;
        }
    }

    void handleKeyId(String keyId) {
        if (this.keyId != null && !this.keyId.equals(keyId)) {
            createDataStreamingEvent();
        }
        this.keyId = keyId;
    }

    void addUniqueIds(String keyId, UniqueIdPair[] uniqueIds) {
        if (LOGGER.isLoggable(Level.FINEST)) {
            if (!this.addUniqueIdsInvoked) {
                this.addUniqueIdsInvoked = true;
                LOGGER.log(Level.FINEST, "First invocation of addUniqueIds for {0}. keyId = {1}", new Object[]{this.entityName, keyId});
            }
        }
        if (keyId == null) {
            LOGGER.log(Level.SEVERE, "Adding unique ids for {0} with null keyId", this.entityName);
        }
        handleKeyId(keyId);
        if (uniqueIds != null) {
            for (UniqueIdPair uniqueId : uniqueIds) {
                if (uniqueId != null) {
                    DestructuredUniqueIdPair lhs = 
                            new DestructuredUniqueIdPair(
                            uniqueId.getReferenceName(), 
                            uniqueId.getProposition());
                    if (!this.referenceUniqueIds.containsKey(lhs)) {
                        this.referenceUniqueIds.put(lhs, 
                                new HashSet<UniqueId>());
                    }
                    this.referenceUniqueIds.get(lhs).add(
                            uniqueId.getReference());
                }
            }
        }
    }

    private void createDataStreamingEvent() {
        if (this.keyId != null) {
            List<UniqueIdPair> uniqueIds = new ArrayList<>();
            for (Map.Entry<DestructuredUniqueIdPair, Set<UniqueId>> e : 
                    this.referenceUniqueIds.entrySet()) {
                for (UniqueId refId : e.getValue()) {
                    uniqueIds.add(new UniqueIdPair(e.getKey().referenceName,
                            e.getKey().proposition, refId));
                }
            }
            this.dataStreamingEventQueue.offer(
                    new DataStreamingEvent<>(
                    this.keyId, uniqueIds));
        }
        this.referenceUniqueIds = 
                new HashMap<>();
    }

    @Override
    public void close() throws DataSourceReadException {
        this.referenceUniqueIds.clear();
        this.referenceUniqueIds = null;
        if (!this.dataStreamingEventQueue.isEmpty()) {
            LOGGER.log(Level.WARNING, "Closing non-empty data streaming event"
                    + " queue for entity {0}. {1} elements remain.",
                    new Object[]{this.entityName, this.dataStreamingEventQueue.size()});
        }
        this.dataStreamingEventQueue.clear();
        this.dataStreamingEventQueue = null;
    }
}
