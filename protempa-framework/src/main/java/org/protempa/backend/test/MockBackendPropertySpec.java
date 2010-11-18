package org.protempa.backend.test;

import org.protempa.backend.BackendPropertySpec;
import org.protempa.backend.DefaultBackendPropertyValidator;

public class MockBackendPropertySpec {
    private BackendPropertySpec propSpec;

    public BackendPropertySpec getBackendPropertySpec() {
        return propSpec;
    }

    public MockBackendPropertySpec(String name, Class type) {
        this.propSpec = new BackendPropertySpec(name, name, name, type,
                new DefaultBackendPropertyValidator());
    }
}
