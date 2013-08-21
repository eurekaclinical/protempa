package org.protempa.backend.dsb.relationaldb;

import org.protempa.DataSourceReadException;
import org.protempa.DataStreamingEvent;
import org.protempa.DataStreamingEventIterator;
import org.protempa.UniqueIdPair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class InboundReferenceResultSetIterator implements
        DataStreamingEventIterator<UniqueIdPair> {

    private static final Logger LOGGER = Logger.getLogger
            (InboundReferenceResultSetIterator.class.getName());

    private Set<UniqueIdPair> referenceUniqueIds;
    private Queue<DataStreamingEvent<UniqueIdPair>> dataStreamingEventQueue;

    InboundReferenceResultSetIterator () {
        this.referenceUniqueIds = new HashSet<UniqueIdPair>();
        this.dataStreamingEventQueue = new
                LinkedList<DataStreamingEvent<UniqueIdPair>>();
    }

    @Override
    public boolean hasNext() throws DataSourceReadException {
        return !this.dataStreamingEventQueue.isEmpty();
    }

    @Override
    public DataStreamingEvent<UniqueIdPair> next() throws DataSourceReadException {
        if (this.dataStreamingEventQueue.isEmpty()) {
            throw new NoSuchElementException("dataStreamingEventQueue is " +
                    "empty");
        }

        DataStreamingEvent<UniqueIdPair> result = this
                .dataStreamingEventQueue.remove();

        return result;
    }

    void addUniqueIds(UniqueIdPair[] uniqueIds) {
        for (UniqueIdPair uniqueId : uniqueIds) {
            this.referenceUniqueIds.add(uniqueId);
        }
    }

    void createDataStreamingEvent(String keyId) {
        this.dataStreamingEventQueue.offer(new DataStreamingEvent<UniqueIdPair>
                (keyId, new ArrayList<UniqueIdPair>(this.referenceUniqueIds)));
        this.referenceUniqueIds = new HashSet<UniqueIdPair>();
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
