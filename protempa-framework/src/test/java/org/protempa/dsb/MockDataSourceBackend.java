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
package org.protempa.dsb;

import org.protempa.MultiplexingDataStreamingEventIterator;
import java.util.Set;
import org.protempa.*;
import org.protempa.backend.dsb.AbstractDataSourceBackend;
import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.DataSourceBackendFailedValidationException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;


/**
 *
 * @author Andrew Post
 */
class MockDataSourceBackend extends AbstractDataSourceBackend {

    @Override
    public void initialize(BackendInstanceSpec config)
            throws BackendInitializationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GranularityFactory getGranularityFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UnitFactory getUnitFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getKeyType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getDisplayName() {
        return "Mock Data Source Backend";
    }

    @Override
    public void validate(KnowledgeSource knowledgeSource)
            throws DataSourceBackendFailedValidationException {
        
    }

    @Override
    public MultiplexingDataStreamingEventIterator readPropositions(Set<String> keyIds, Set<String> propIds, Filter filters, QuerySession qs) throws DataSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
