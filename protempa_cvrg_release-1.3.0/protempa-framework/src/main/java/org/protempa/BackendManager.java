/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa;

import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.BackendUpdatedEvent;
import org.protempa.backend.Backend;
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

//	private S source;

	BackendManager(S source, B[] backends) {
//		this.source = source;
		this.backendsToAdd = backends;
	}

	List<B> getBackends() {
		return this.backends;
	}

	/**
	 * Connect to the knowledge source backend.
	 */
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
