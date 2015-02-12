package org.protempa.backend;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2015 Emory University
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

import org.protempa.DefaultSourceIdFactory;
import org.protempa.SourceId;

/**
 *
 * @author Andrew Post
 */
public class BackendSourceIdFactory {
    private final Backend backend;
    private final DefaultSourceIdFactory sourceIdFactory;

    public BackendSourceIdFactory(Backend backend) {
        if (backend == null) {
            throw new IllegalArgumentException("backend cannot be null");
        }
        this.backend = backend;
        this.sourceIdFactory = new DefaultSourceIdFactory();
    }

    public SourceId getInstance() {
        return this.sourceIdFactory.getInstance(this.backend.getId());
    }
}
