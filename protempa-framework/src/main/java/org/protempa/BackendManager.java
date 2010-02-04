package org.protempa;

import java.util.ArrayList;
import java.util.List;
import org.protempa.backend.BackendNewInstanceException;

/**
 * Manages the lifecycle of and access to multiple backends for the algorithm,
 * knowledge, and data source.
 * 
 * @author Andrew Post
 * 
 */
class BackendManager<E extends BackendUpdatedEvent, S extends Source<E>,
        B extends Backend<E, S>> {
	/**
	 * An array of knowledge source backends
	 */
	private List<B> backends;

	private B[] backendsToAdd;

	private S source;

	BackendManager(S source, B[] backends) {
		this.source = source;
		this.backendsToAdd = backends;
	}

	List<B> getBackends() {
		return this.backends;
	}

	/**
	 * Connect to the knowledge source backend.
	 */
	@SuppressWarnings("unchecked")
	void initializeIfNeeded() throws BackendInitializationException,
            BackendNewInstanceException {
		List<B> ksb = null;
		if (this.backends == null && this.backendsToAdd != null) {
			ksb = new ArrayList<B>(this.backendsToAdd.length);
			for (B b : this.backendsToAdd) {
				if (b != null) {
					ksb.add(b);
				}
			}

		}

		if (ksb != null)
			this.backends = ksb;
	}

    /**
     * Closes the backends (by calling {@link Backend.close()}).
     */
	void close() {
		if (this.backends != null) {
			for (B backend : this.backends) {
				backend.close();
			}
			this.backends = null;
		}
	}
}
