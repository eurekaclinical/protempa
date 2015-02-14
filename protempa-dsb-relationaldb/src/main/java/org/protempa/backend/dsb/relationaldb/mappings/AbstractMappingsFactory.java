package org.protempa.backend.dsb.relationaldb.mappings;

/*
 * #%L
 * Protempa Relational Database Data Source Backend
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractMappingsFactory implements MappingsFactory {
    private final List<Mappings> allMappings;

    public AbstractMappingsFactory() {
        this.allMappings = new ArrayList<>();
    }
    
    protected void addMappings(Mappings mappings) {
        this.allMappings.add(mappings);
    }

    @Override
    public void closeAll() throws IOException {
        for (Mappings m : this.allMappings) {
            m.close();
        }
    }
    
    
}
