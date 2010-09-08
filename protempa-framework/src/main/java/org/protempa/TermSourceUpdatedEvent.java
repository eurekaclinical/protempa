package org.protempa;

public final class TermSourceUpdatedEvent extends SourceUpdatedEvent {

    private static final long serialVersionUID = -5988214253013529423L;

    public TermSourceUpdatedEvent(TermSource termSource) {
        super(termSource);
    }

}
