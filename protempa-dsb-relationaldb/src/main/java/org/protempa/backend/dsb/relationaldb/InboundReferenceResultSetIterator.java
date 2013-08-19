package org.protempa.backend.dsb.relationaldb;

import org.protempa.DataSourceReadException;
import org.protempa.DataStreamingEvent;
import org.protempa.DataStreamingEventIterator;
import org.protempa.UniqueIdPair;
import org.protempa.proposition.Proposition;

import java.util.ArrayList;
import java.util.List;

public final class InboundReferenceResultSetIterator implements
        DataStreamingEventIterator<UniqueIdPair> {

    private String keyId;
    private List<UniqueIdPair> referenceUniqueIds;
    private DataStreamingEvent<UniqueIdPair> dataStreamingEvent;

    private final PropositionResultSetIterator<? extends Proposition> propItr;

    InboundReferenceResultSetIterator
            (PropositionResultSetIterator<? extends Proposition> propItr) {
        this.referenceUniqueIds = new ArrayList<UniqueIdPair>();
        this.propItr = propItr;
        this.dataStreamingEvent = null;
    }

    @Override
    public boolean hasNext() throws DataSourceReadException {
        return this.propItr.hasNext();
    }

    @Override
    public DataStreamingEvent<UniqueIdPair> next() throws DataSourceReadException {
        if (keyId == null) {
            throw new IllegalStateException("attempting to access references " +
                    "before key is complete");
        }
        this.keyId = null;
        this.referenceUniqueIds = new ArrayList<UniqueIdPair>();

        return this.dataStreamingEvent;
    }

    void addUniqueIds(UniqueIdPair[] uniqueIds) {
        for (UniqueIdPair uniqueId : uniqueIds) {
            this.referenceUniqueIds.add(uniqueId);
        }
    }

    void createDataStreamingEvent() {
        this.dataStreamingEvent = new DataStreamingEvent<UniqueIdPair>(this
                .keyId, this.referenceUniqueIds);
    }

    void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    @Override
    public void close() throws DataSourceReadException {
        this.keyId = null;
        this.referenceUniqueIds.clear();
        this.referenceUniqueIds = null;
    }
}
