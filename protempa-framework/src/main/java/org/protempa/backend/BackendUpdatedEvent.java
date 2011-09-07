package org.protempa.backend;

import org.protempa.backend.Backend;
import java.util.EventObject;

/**
 * Backend updated events.
 * 
 * @author Andrew Post
 */
public abstract class BackendUpdatedEvent extends EventObject {

    private final Backend<?, ?> backend;

    BackendUpdatedEvent(Backend<?, ?> source) {
        super(source);
        this.backend = source;
    }

    /**
     * Returns the source {@link Backend} (the same as what
     * <code>getSource()</code> returns).
     * 
     * @return a {@link Backend}.
     */
    public Backend<?, ?> getBackend() {
        return this.backend;
    }
}
