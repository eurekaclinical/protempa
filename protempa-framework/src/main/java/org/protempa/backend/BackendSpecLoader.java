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
        this.backendSpecs = new ArrayList<BackendSpec<B>>(backendSpecs);
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
