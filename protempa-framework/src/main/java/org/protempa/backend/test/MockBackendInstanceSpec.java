package org.protempa.backend.test;

import java.util.ArrayList;
import java.util.List;

import org.protempa.Backend;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.BackendPropertySpec;
import org.protempa.backend.BackendSpec;
import org.protempa.backend.DefaultBackendPropertyValidator;

public final class MockBackendInstanceSpec<B extends Backend> {
    private BackendInstanceSpec<B> backendInstSpec;

    public BackendInstanceSpec<B> getBackendInstanceSpec() {
        return backendInstSpec;
    }

    public MockBackendInstanceSpec(List<BackendPropertySpec> propSpecs) {
        BackendSpec<B> backendSpec = new BackendSpec<B>(
                new MockBackendProvider(), "mockSpec", "Mock Spec",
                new ArrayList<BackendPropertySpec>());
        List<BackendPropertySpec> backendProps = new ArrayList<BackendPropertySpec>();
                
        backendInstSpec = new BackendInstanceSpec<B>(backendSpec, propSpecs);
    }
}
