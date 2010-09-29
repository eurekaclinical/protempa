package org.protempa;

import java.util.EventObject;

/**
 *
 * @author Andrew Post
 */
public final class SourceClosedUnexpectedlyEvent extends EventObject {
    private static final long serialVersionUID = 7088929112407759901L;
    private Source protempaSource;

    /**
     * Initializes the event with the {@link Source} that closed unexpectedly.
     *
     * @param protempaSource a {@link Source}.
     */
    public SourceClosedUnexpectedlyEvent(Source protempaSource) {
        super(protempaSource);
        this.protempaSource = protempaSource;
    }

    /**
     * Returns the {@link Source} that closed unexpectedly. The
     * {@link #getSource()} method returns the same thing but requires a cast.
     *
     * @return a {@link Source}.
     */
    public Source getProtempaSource() {
        return this.protempaSource;
    }
}
