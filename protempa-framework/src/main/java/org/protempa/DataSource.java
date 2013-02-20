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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protempa;

import java.util.Set;
import org.protempa.backend.DataSourceBackendUpdatedEvent;
import org.protempa.backend.dsb.DataSourceBackend;
import org.protempa.backend.dsb.filter.Filter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

/**
 *
 * @author Andrew Post
 */
public interface DataSource extends Source<DataSourceUpdatedEvent, 
        DataSourceBackend, DataSourceBackendUpdatedEvent> {

    /**
     * Returns an object for accessing the granularity of returned data from the
     * schema adaptor for this data source.
     *
     * @return a {@link GranularityFactory}, or <code>null</code> if the data
     * source could not be initialized or the schema adaptor returned a null
     * GranularityFactory.
     * @throws TerminologyAdaptorInitializationException if the terminology
     * adaptor could not be initialized.
     * @throws SchemaAdaptorInitializationException if the schema adaptor could
     * not be initialized.
     * @see SchemaAdaptor#getGranularityFactory()
     */
    GranularityFactory getGranularityFactory() throws DataSourceReadException;

    /**
     * Returns a string representing the type of keys in this data source (e.g.,
     * patient, case).
     *
     * @return a {@link String}, guaranteed not <code>null</code>.
     * @throws TerminologyAdaptorInitializationException if the terminology
     * adaptor could not be initialized.
     * @throws SchemaAdaptorInitializationException if the schema adaptor could
     * not be initialized.
     */
    String getKeyType() throws DataSourceReadException;

    /**
     * Returns a string representing the type of keys in this data source (e.g.,
     * patient, case) for display purposes.
     *
     * @return a {@link String}, guaranteed not <code>null</code>.
     * @throws TerminologyAdaptorInitializationException if the terminology
     * adaptor could not be initialized.
     * @throws SchemaAdaptorInitializationException if the schema adaptor could
     * not be initialized.
     */
    String getKeyTypeDisplayName() throws DataSourceReadException;

    /**
     * Returns a plural string representing the type of keys in this data source
     * (e.g., patient, case) for display purposes.
     *
     * @return a {@link String}, guaranteed not <code>null</code>.
     * @throws TerminologyAdaptorInitializationException if the terminology
     * adaptor could not be initialized.
     * @throws SchemaAdaptorInitializationException if the schema adaptor could
     * not be initialized.
     * @throws IllegalStateException if the schema adaptor and/or terminology
     * adaptor could not be initialized, or if the schema adaptor returned a
     * <code>null</code> key type plural display name.
     */
    String getKeyTypePluralDisplayName() throws DataSourceReadException;

    /**
     * Returns the length units of returned data from this data source.
     *
     * @return a {@link UnitFactory}, or <code>null</code> if the data source
     * could not be initialized or the schema adaptor returned a null
     * UnitFactory.
     * @throws DataSourceReadException if the data source could not be
     * initialized.
     */
    UnitFactory getUnitFactory() throws DataSourceReadException;

    DataStreamingEventIterator<Proposition> readPropositions(
            Set<String> keyIds, Set<String> propIds, Filter filters, 
            QuerySession qs) throws DataSourceReadException;
    
}
