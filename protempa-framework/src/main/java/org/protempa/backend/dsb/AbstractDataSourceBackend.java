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
import org.protempa.DataSourceWriteException;
import org.protempa.backend.AbstractBackend;
import org.protempa.backend.DataSourceBackendUpdatedEvent;
import org.protempa.proposition.value.AbsoluteTimeGranularityFactory;
import org.protempa.proposition.value.AbsoluteTimeUnitFactory;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;


/**
 * Convenience class for implementing a data source backend.
 * 
 * @author Andrew Post
 */
public abstract class AbstractDataSourceBackend extends
		AbstractBackend<DataSourceBackendUpdatedEvent> 
                implements DataSourceBackend {
    private static AbsoluteTimeUnitFactory absTimeUnitFactory
            = new AbsoluteTimeUnitFactory();
    private static AbsoluteTimeGranularityFactory absTimeGranularityFactory
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
    public void failureOccurred(Throwable throwable) {
        
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
    
}
