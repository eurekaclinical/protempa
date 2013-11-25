/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
package org.protempa.backend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Loads 
 * @author Andrew Post
 */
public final class BackendSpecLoader<B extends Backend>
        implements Iterable<BackendSpec<B>> {
    private final List<BackendSpec<B>> backendSpecs;

    public BackendSpecLoader(List<BackendSpec<B>> backendSpecs) {
        this.backendSpecs = new ArrayList<>(backendSpecs);
    }

    public BackendSpec<B> loadSpec(String id)
            throws BackendSpecNotFoundException {
        for (BackendSpec<B> backendSpec : backendSpecs) {
            if (id.equals(backendSpec.getId())) {
                return backendSpec;
            }
        }
        throw new BackendSpecNotFoundException(id);
    }

    public boolean hasSpec(String id) {
        for (BackendSpec<B> backendSpec : backendSpecs) {
            if (id.equals(backendSpec.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<BackendSpec<B>> iterator() {
        return backendSpecs.iterator();
    }
}
