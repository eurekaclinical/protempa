package org.protempa;

public interface Source<T extends BackendUpdatedEvent> 
        extends BackendListener<T>, Module {
    @Override
    void close();
}
