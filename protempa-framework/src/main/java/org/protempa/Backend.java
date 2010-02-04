package org.protempa;

import org.protempa.backend.BackendInstanceSpec;

/**
 * Common interface for PROTEMPA backends.
 * 
 * @author Andrew Post
 * 
 * @param <E>
 *            the {@link BackendUpdatedEvent} that this backend fires.
 * @param <S> 
 *            this backend's corresponding {@link Source}.
 */
public interface Backend<E extends BackendUpdatedEvent, S extends Source<E>> {

    /**
     * Initializes the backend from a {@link BackendInstanceSpec}. It is called
     * for you by {@link BackendInstanceSpec#getInstance()}, but if you
     * create a {@link Backend} object yourself, you must call this manually
     * before using it.
     *
     * @param source
     *            the backend's corresponding {@link Source} instance.
     * @param config
     *            the configuration {@link Properties} object.
     * @return <code>true</code> if initialization was successful,
     *         <code>false</code> if initialization failed.
     */
    void initialize(BackendInstanceSpec config)
            throws BackendInitializationException;

    /**
     * Releases any resources allocated by this backend.
     */
    void close();

    /**
     * Registers a listener that get called whenever a backend changes.
     *
     * @param listener
     *            a {@link BackendListener}.
     */
    void addBackendListener(BackendListener<E> listener);

    /**
     * Unregisters a listener so that changes to the backend are no longer sent.
     *
     * @param listener
     *            a {@link BackendListener}.
     */
    void removeBackendListener(BackendListener<E> listener);
}
