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
package org.protempa;

import org.protempa.backend.DataSourceBackendFailedValidationException;
import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.DataSourceBackendUpdatedEvent;
import org.protempa.backend.dsb.DataSourceBackend;
import java.util.ArrayList;
import org.protempa.backend.dsb.filter.Filter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.backend.BackendNewInstanceException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

/**
 * Read-only access to a database. Data source backends are specified in the
 * constructor that implement the actual database connection(s).
 *
 * @author Andrew Post
 * @see DataSourceBackend
 */
public final class DataSource extends AbstractSource<DataSourceUpdatedEvent, DataSourceBackendUpdatedEvent> {

    private final BackendManager<DataSourceBackendUpdatedEvent, DataSource, DataSourceBackend> backendManager;

    public DataSource(DataSourceBackend[] backends) {
        super(backends);
        this.backendManager =
                new BackendManager<DataSourceBackendUpdatedEvent, DataSource, DataSourceBackend>(this, backends);

    }

    /**
     * Returns a string representing the type of keys in this data source (e.g.,
     * patient, case).
     *
     * @return a {@link String}, guaranteed not
     * <code>null</code>.
     * @throws TerminologyAdaptorInitializationException if the terminology
     * adaptor could not be initialized.
     * @throws SchemaAdaptorInitializationException if the schema adaptor could
     * not be initialized.
     */
    public String getKeyType() throws DataSourceReadException {
        initializeIfNeeded();
        String result = null;
        DataSourceBackend b = null;
        for (DataSourceBackend backend : this.backendManager.getBackends()) {
            result = backend.getKeyType();
            b = backend;
            break;
        }
        assert result != null : "no key type found in " + b.getClass() + "!";
        return result;
    }

    /**
     * Returns a string representing the type of keys in this data source (e.g.,
     * patient, case) for display purposes.
     *
     * @return a {@link String}, guaranteed not
     * <code>null</code>.
     * @throws TerminologyAdaptorInitializationException if the terminology
     * adaptor could not be initialized.
     * @throws SchemaAdaptorInitializationException if the schema adaptor could
     * not be initialized.
     */
    public String getKeyTypeDisplayName()
            throws DataSourceReadException {
        initializeIfNeeded();
        String result = null;
        DataSourceBackend b = null;
        for (DataSourceBackend backend : this.backendManager.getBackends()) {
            result = backend.getKeyTypeDisplayName();
            b = backend;
            break;
        }
        assert result != null : "no key type display name found in "
                + b.getClass() + "!";
        return result;
    }

    /**
     * Returns a plural string representing the type of keys in this data source
     * (e.g., patient, case) for display purposes.
     *
     * @return a {@link String}, guaranteed not
     * <code>null</code>.
     * @throws TerminologyAdaptorInitializationException if the terminology
     * adaptor could not be initialized.
     * @throws SchemaAdaptorInitializationException if the schema adaptor could
     * not be initialized.
     * @throws IllegalStateException if the schema adaptor and/or terminology
     * adaptor could not be initialized, or if the schema adaptor returned a
     * <code>null</code> key type plural display name.
     */
    public String getKeyTypePluralDisplayName()
            throws DataSourceReadException {
        initializeIfNeeded();
        String result = null;
        DataSourceBackend b = null;
        for (DataSourceBackend backend : this.backendManager.getBackends()) {
            result = backend.getKeyTypePluralDisplayName();
            b = backend;
            break;
        }
        assert result != null : "no key type plural display name found in "
                + b.getClass() + "!";
        return result;
    }

    /**
     * Returns an object for accessing the granularity of returned data from the
     * schema adaptor for this data source.
     *
     * @return a {@link GranularityFactory}, or
     * <code>null</code> if the data source could not be initialized or the
     * schema adaptor returned a null GranularityFactory.
     * @throws TerminologyAdaptorInitializationException if the terminology
     * adaptor could not be initialized.
     * @throws SchemaAdaptorInitializationException if the schema adaptor could
     * not be initialized.
     * @see SchemaAdaptor#getGranularityFactory()
     */
    public GranularityFactory getGranularityFactory()
            throws DataSourceReadException {
        initializeIfNeeded();
        GranularityFactory result = null;
        DataSourceBackend b = null;
        for (DataSourceBackend backend : this.backendManager.getBackends()) {
            result = backend.getGranularityFactory();
            b = backend;
            break;
        }
        assert result != null : "no granularity factory returned from "
                + b.getClass() + "!";
        return result;
    }

    /**
     * Returns the length units of returned data from this data source.
     *
     * @return a {@link UnitFactory}, or
     * <code>null</code> if the data source could not be initialized or the
     * schema adaptor returned a null UnitFactory.
     * @throws DataSourceReadException if the data source could not be
     * initialized.
     */
    public UnitFactory getUnitFactory() throws DataSourceReadException {
        initializeIfNeeded();
        UnitFactory result = null;
        DataSourceBackend b = null;
        for (DataSourceBackend backend : this.backendManager.getBackends()) {
            result = backend.getUnitFactory();
            b = backend;
            break;
        }
        assert result != null : "no unit factory returned from "
                + b.getClass() + "!";
        return result;
    }

    public DataStreamingEventIterator<Proposition> readPropositions(
            Set<String> keyIds, Set<String> propIds,
            Filter filters, QuerySession qs)
            throws DataSourceReadException {
        Set<String> notNullKeyIds = handleKeyIdSetArgument(keyIds);
        Set<String> notNullPropIds = handlePropIdSetArgument(propIds);

        initializeIfNeeded();
        List<DataSourceBackend> backends = this.backendManager.getBackends();
        List<DataStreamingEventIterator<Proposition>> itrs =
                new ArrayList<DataStreamingEventIterator<Proposition>>(backends.size());
        for (DataSourceBackend backend : backends) {
            itrs.add(backend.readPropositions(notNullKeyIds,
                    notNullPropIds, filters, qs));
        }
        return new MultiplexingDataStreamingEventIterator(itrs, 
                new PropositionDataStreamerProcessor());
    }

    @Override
    public void backendUpdated(DataSourceBackendUpdatedEvent evt) {
        clear();
        fireDataSourceUpdated();
    }

    /**
     * Determines whether the data source's data element mappings are consistent
     * with the data elements' definitions.
     *
     * @param knowledgeSource a {@link KnowledgeSource}.
     * @throws DataSourceFailedValidationException if the data element mappings
     * are not consistent with the data elements' definitions.
     * @throws DataSourceValidationException if an error occurred during
     * validation.
     */
    void validate(KnowledgeSource knowledgeSource)
            throws DataSourceFailedValidationException,
            DataSourceValidationIncompleteException {
        try {
            initializeIfNeeded();
            for (DataSourceBackend backend : this.backendManager.getBackends()) {
                try {
                    backend.validate(knowledgeSource);
                } catch (DataSourceBackendFailedValidationException ex) {
                    throw new DataSourceFailedValidationException(
                            "Data source failed validation", ex);
                } catch (KnowledgeSourceReadException ex) {
                    throw new DataSourceValidationIncompleteException(
                            "An error occurred during validation", ex);
                }
            }
        } catch (DataSourceReadException ex) {
            throw new DataSourceValidationIncompleteException(
                    "An error occurred during validation", ex);
        }
    }

    @Override
    public void close() {
        clear();
        this.backendManager.close();
        super.close();
    }

    @Override
    public void clear() {
    }

    private void initializeIfNeeded() throws DataSourceReadException {
        if (isClosed()) {
            throw new IllegalStateException("Data source already closed!");
        }
        try {
            this.backendManager.initializeIfNeeded();
        } catch (BackendNewInstanceException iae) {
            throw new DataSourceReadException(iae);
        } catch (BackendInitializationException ex) {
            throw new DataSourceReadException(ex);
        }
    }

    /**
     * Notifies registered listeners that the data source has been updated.
     *
     * @see DataSourceUpdatedEvent
     * @see SourceListener
     */
    private void fireDataSourceUpdated() {
        fireSourceUpdated(new DataSourceUpdatedEvent(this));
    }

    private static Set<String> handleKeyIdSetArgument(Set<String> keyIds) {
        if (keyIds == null) {
            return new HashSet<String>();
        } else {
            return new HashSet<String>(keyIds);
        }
    }

    private static Set<String> handlePropIdSetArgument(Set<String> propIds) {
        if (propIds != null) {
            return new HashSet<String>(propIds);
        } else {
            throw new IllegalArgumentException("propIds cannot be null");
        }
    }
}
