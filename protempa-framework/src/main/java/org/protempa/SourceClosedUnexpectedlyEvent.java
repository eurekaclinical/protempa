package org.protempa;

import java.util.EventObject;

import org.protempa.backend.BackendUpdatedEvent;

/**
 *
 * @author Andrew Post
 */
public final class SourceClosedUnexpectedlyEvent<T extends BackendUpdatedEvent> extends EventObject {
    private static final long serialVersionUID = 7088929112407759901L;
    private Source<T> protempaSource;

    /**
     * Initializes the event with the {@link Source} that closed unexpectedly.
     *
     * @param protempaSource a {@link Source}.
     */
    public SourceClosedUnexpectedlyEvent(Source<T> protempaSource) {
        super(protempaSource);
        this.protempaSource = protempaSource;
    }

    /**
     * Returns the {@link Source} that closed unexpectedly. The
     * {@link #getSource()} method returns the same thing but requires a cast.
     *
     * @return a {@link Source}.
     */
    public Source<T> getProtempaSource() {
        return this.protempaSource;
    }
}
