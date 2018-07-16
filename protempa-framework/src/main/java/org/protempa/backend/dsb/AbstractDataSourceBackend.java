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

import java.util.Comparator;
import java.util.Set;
import org.protempa.BackendCloseException;
import org.protempa.DataSourceBackendSourceSystem;
import org.protempa.DataSourceReadException;
import org.protempa.DataSourceWriteException;
import org.protempa.KeySetSpec;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.backend.AbstractBackend;
import org.protempa.backend.DataSourceBackendFailedConfigurationValidationException;
import org.protempa.backend.DataSourceBackendFailedDataValidationException;
import org.protempa.backend.DataSourceBackendUpdatedEvent;
import org.protempa.proposition.value.AbsoluteTimeGranularityFactory;
import org.protempa.proposition.value.AbsoluteTimeUnitFactory;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;

/**
 * Convenience class for implementing a data source backend. It assumes data
 * that uses absolute time units by default, but that can be overridden using
 * {@link #setGranularityFactory(org.protempa.proposition.value.GranularityFactory) }
 * and {@link #setUnitFactory(org.protempa.proposition.value.UnitFactory) }. The
 * key type must be set using {@link #setKeyType(java.lang.String) } to 
 * the id of the proposition definition represented by keys in the data.
 *
 * @author Andrew Post
 */
public abstract class AbstractDataSourceBackend extends
        AbstractBackend<DataSourceBackendUpdatedEvent>
        implements DataSourceBackend {

    private static final AbsoluteTimeUnitFactory absTimeUnitFactory
            = new AbsoluteTimeUnitFactory();
    private static final AbsoluteTimeGranularityFactory absTimeGranularityFactory
            = new AbsoluteTimeGranularityFactory();

    private String keyType;
    private GranularityFactory granularityFactory;
    private UnitFactory unitFactory;
    private Comparator<Object> keyIdComparator;

    protected AbstractDataSourceBackend() {
        this.granularityFactory = absTimeGranularityFactory;
        this.unitFactory = absTimeUnitFactory;
    }

    @Override
    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    @Override
    public GranularityFactory getGranularityFactory() {
        return granularityFactory;
    }

    public void setGranularityFactory(GranularityFactory granularityFactory) {
        if (granularityFactory == null) {
            this.granularityFactory = absTimeGranularityFactory;
        } else {
            this.granularityFactory = granularityFactory;
        }
    }

    @Override
    public UnitFactory getUnitFactory() {
        return unitFactory;
    }

    public void setUnitFactory(UnitFactory unitFactory) {
        if (unitFactory == null) {
            this.unitFactory = absTimeUnitFactory;
        } else {
            this.unitFactory = unitFactory;
        }
    }

    public void setKeyIdComparator(Comparator<Object> keyIdComparator) {
        this.keyIdComparator = keyIdComparator;
    }

    @Override
    public Comparator<Object> getKeyIdComparator() {
        return this.keyIdComparator;
    }

    @Override
    public void close() throws BackendCloseException {
    }

    @Override
    public String getKeyTypeDisplayName() {
        return getKeyType();
    }

    @Override
    public String getKeyTypePluralDisplayName() {
        return getKeyTypeDisplayName() + "s";
    }

    @Override
    public KeySetSpec[] getSelectedKeySetSpecs() throws DataSourceReadException {
        return KeySetSpec.EMPTY_KEY_SET_SPEC_ARRAY;
    }

    @Override
    public void deleteAllKeys() throws DataSourceWriteException {
    }

    @Override
    public void writeKeys(Set<String> keyIds) throws DataSourceWriteException {
    }

    @Override
    public DataSourceBackendSourceSystem getSourceSystem() {
        return DataSourceBackendSourceSystem.getInstance(getId());
    }

    @Override
    public DataValidationEvent[] validateData(KnowledgeSource knowledgeSource) throws DataSourceBackendFailedDataValidationException, KnowledgeSourceReadException {
        return new DataValidationEvent[0];
    }
    
    @Override
    public void validateConfiguration(KnowledgeSource knowledgeSource) throws DataSourceBackendFailedConfigurationValidationException, KnowledgeSourceReadException {
    }

}
