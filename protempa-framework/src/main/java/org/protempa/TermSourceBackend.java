package org.protempa;

public interface TermSourceBackend extends
        Backend<TermSourceBackendUpdatedEvent, TermSource> {
    Term readTerm(String id, Terminology terminology);
}
