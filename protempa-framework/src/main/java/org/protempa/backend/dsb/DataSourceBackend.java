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
package org.protempa.backend.dsb;

import org.protempa.DataSource;
import org.protempa.MultiplexingDataStreamingEventIterator;
import org.protempa.backend.dsb.filter.Filter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.*;
import org.protempa.backend.Backend;
import org.protempa.backend.DataSourceBackendFailedValidationException;
import org.protempa.backend.DataSourceBackendUpdatedEvent;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

/**
 * Interface for data source backends, which provide access to data sources
 * for PROTEMPA.
 * 
 * @author Andrew Post
 */
public interface DataSourceBackend extends
		Backend<DataSourceBackendUpdatedEvent, DataSource>{
    
    DataStreamingEventIterator<Proposition> readPropositions(
            Set<String> keyIds,
            Set<String> propIds, Filter filters, QuerySession qs) 
            throws DataSourceReadException;

    GranularityFactory getGranularityFactory();

    UnitFactory getUnitFactory();

    String getKeyType();

    String getKeyTypeDisplayName();

    String getKeyTypePluralDisplayName();

    void validate(KnowledgeSource knowledgeSource)
            throws DataSourceBackendFailedValidationException,
            KnowledgeSourceReadException;

}
