package org.protempa.tsb;

import org.protempa.BackendCloseException;
import org.protempa.BackendListener;
import org.protempa.Term;
import org.protempa.TermSourceReadException;
import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.TermSourceBackendUpdatedEvent;
import org.protempa.backend.annotations.BackendInfo;
import org.protempa.backend.tsb.TermSourceBackend;

import java.util.List;
import java.util.Map;

/**
 *
 */
@BackendInfo(displayName = "Mock Term Source Backend")
public class MockTermSourceBackend implements TermSourceBackend {

    @Override
    public Term readTerm(String id) throws TermSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, Term> readTerms(String[] ids) throws TermSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getSubsumption(String id) throws TermSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void initialize(BackendInstanceSpec<?> config) throws BackendInitializationException {

    }

    @Override
    public String getDisplayName() {
        return "Mock Term Source Backend";
    }

    @Override
    public String getConfigurationsId() {
        return "";
    }

    @Override
    public void close() throws BackendCloseException {

    }

    @Override
    public void addBackendListener(BackendListener<TermSourceBackendUpdatedEvent> listener) {

    }

    @Override
    public void removeBackendListener(BackendListener<TermSourceBackendUpdatedEvent> listener) {

    }
}
