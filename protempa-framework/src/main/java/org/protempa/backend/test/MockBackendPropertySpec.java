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
package org.protempa.backend.test;

import org.protempa.backend.BackendPropertySpec;
import org.protempa.backend.DefaultBackendPropertyValidator;

public class MockBackendPropertySpec {
    private BackendPropertySpec propSpec;

    public BackendPropertySpec getBackendPropertySpec() {
        return propSpec;
    }

    public MockBackendPropertySpec(String name, Class<?> type) {
        this.propSpec = new BackendPropertySpec(name, name, name, type,
                new DefaultBackendPropertyValidator());
    }
}
