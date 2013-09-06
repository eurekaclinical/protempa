package org.protempa.backend.dsb.relationaldb;

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

public final class InboundReferenceResultSetIterator implements
        DataStreamingEventIterator<UniqueIdPair> {

    private static final Logger LOGGER = Logger.getLogger
            (InboundReferenceResultSetIterator.class.getName());

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

    InboundReferenceResultSetIterator (String entityName) {
        this.entityName = entityName;
        this.referenceUniqueIds = new HashMap<DestructuredUniqueIdPair,
                Set<UniqueId>>();
        this.dataStreamingEventQueue = new
                LinkedList<DataStreamingEvent<UniqueIdPair>>();
    }

    public void resultSetComplete() {
        this.end = true;
        createDataStreamingEvent();
    }

    private boolean isDone() {
        return this.dataStreamingEventQueue.isEmpty() && this.end;
    }

    @Override
    public boolean hasNext() throws DataSourceReadException {
        return  !isDone();
    }

    @Override
    public DataStreamingEvent<UniqueIdPair> next() throws DataSourceReadException {
        if (isDone()) {
            throw new NoSuchElementException("dataStreamingEventQueue is " +
                    "empty");
        }

        if (!this.referenceUniqueIds.isEmpty()) {
            createDataStreamingEvent();
        }

        DataStreamingEvent<UniqueIdPair> result = this
                .dataStreamingEventQueue.remove();

        LOGGER.log(Level.INFO, "{0}: Current: {1}, Last Delivered: {2}",
                new Object[]{this.entityName, result.getKeyId(),
                        this.lastDelivered});

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

        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o instanceof DestructuredUniqueIdPair) {
                DestructuredUniqueIdPair other = (DestructuredUniqueIdPair) o;
                return referenceName.equals(other.referenceName) &&
                        proposition.equals(other.proposition);
            }

            return false;
        }

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
        handleKeyId(keyId);
        for (UniqueIdPair uniqueId : uniqueIds) {
            DestructuredUniqueIdPair lhs = new DestructuredUniqueIdPair
                    (uniqueId.getReferenceName(), uniqueId.getProposition());
            if (!this.referenceUniqueIds.containsKey(lhs)) {
                this.referenceUniqueIds.put(lhs, new HashSet<UniqueId>());
            }
            this.referenceUniqueIds.get(lhs).add(uniqueId.getReference());
        }
    }

    private void createDataStreamingEvent() {
        List<UniqueIdPair> uniqueIds = new ArrayList<UniqueIdPair>();
        for (Map.Entry<DestructuredUniqueIdPair, Set<UniqueId>> e : this
                .referenceUniqueIds.entrySet()) {
            for (UniqueId refId : e.getValue()) {
                uniqueIds.add(new UniqueIdPair(e.getKey().referenceName,
                        e.getKey().proposition, refId));
            }
        }
        this.dataStreamingEventQueue.offer(new DataStreamingEvent<UniqueIdPair>
                (this.keyId, uniqueIds));
        this.referenceUniqueIds = new HashMap<DestructuredUniqueIdPair,
                Set<UniqueId>>();
    }

    @Override
    public void close() throws DataSourceReadException {
        this.referenceUniqueIds.clear();
        this.referenceUniqueIds = null;
        if (!this.dataStreamingEventQueue.isEmpty()) {
            LOGGER.log(Level.WARNING, "Closing non-empty data streaming event" +
                    " queue. {0} elements remain.",
                    new Object[]{this.dataStreamingEventQueue.size()});
        }
        this.dataStreamingEventQueue.clear();
        this.dataStreamingEventQueue = null;
    }
}
