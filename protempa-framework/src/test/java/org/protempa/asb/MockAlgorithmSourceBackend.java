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
package org.protempa.asb;

import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.asb.AbstractAlgorithmSourceBackend;
import org.protempa.*;
import org.protempa.backend.BackendInstanceSpec;

public final class MockAlgorithmSourceBackend
        extends AbstractAlgorithmSourceBackend {

    @Override
    public AbstractAlgorithm readAlgorithm(String id, Algorithms algorithms) {
        return null;
    }

    @Override
    public void readAlgorithms(Algorithms algorithms) {
    }

    @Override
    public void initialize(BackendInstanceSpec config)
            throws BackendInitializationException {
    }

    /**
     * Make public so that tests can call it.
     * @see AbstractAlgorithmSourceBackend
     */
    @Override
    public void fireAlgorithmSourceBackendUpdated() {
        super.fireAlgorithmSourceBackendUpdated();
    }

    @Override
    public String getDisplayName() {
        return "Mock Algorithm Source Backend";
    }
}
