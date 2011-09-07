package org.protempa;

import org.protempa.backend.BackendUpdatedEvent;

public interface Source<T extends BackendUpdatedEvent> 
        extends BackendListener<T>, Module {
    @Override
    void close();
}
