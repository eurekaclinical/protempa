package org.protempa.backend;

import org.protempa.backend.tsb.TermSourceBackend;

public class TermSourceBackendUpdatedEvent extends BackendUpdatedEvent {

    private static final long serialVersionUID = 1541779603882476882L;

    public TermSourceBackendUpdatedEvent(TermSourceBackend termSourceBackend) {
        super(termSourceBackend);
    }

}
