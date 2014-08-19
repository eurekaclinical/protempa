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
package org.protempa.backend.dsb;

import java.util.List;
import java.util.Set;
import org.protempa.*;
import org.protempa.backend.Backend;
import org.protempa.backend.DataSourceBackendFailedConfigurationValidationException;
import org.protempa.backend.DataSourceBackendFailedDataValidationException;
import org.protempa.backend.DataSourceBackendUpdatedEvent;
import org.protempa.backend.dsb.filter.Filter;
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
		Backend<DataSourceBackendUpdatedEvent>{
    
    DataStreamingEventIterator<Proposition> readPropositions(
            Set<String> keyIds,
            Set<String> propIds, Filter filters, QuerySession qs) 
            throws DataSourceReadException;

    GranularityFactory getGranularityFactory();

    UnitFactory getUnitFactory();

    String getKeyType();

    String getKeyTypeDisplayName();

    String getKeyTypePluralDisplayName();
    
    DataValidationEvent[] validateData(KnowledgeSource knowledgeSource) 
            throws DataSourceBackendFailedDataValidationException,
            KnowledgeSourceReadException;

    void validateConfiguration(KnowledgeSource knowledgeSource)
            throws DataSourceBackendFailedConfigurationValidationException,
            KnowledgeSourceReadException;
    
    void failureOccurred(Throwable throwable);
    
    void deleteAllKeys() throws DataSourceWriteException;

    void writeKeys(List<Proposition> propositions) throws DataSourceWriteException;
}
