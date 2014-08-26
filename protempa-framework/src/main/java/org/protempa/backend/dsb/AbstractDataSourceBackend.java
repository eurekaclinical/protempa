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


import java.util.Set;
import org.protempa.BackendCloseException;
import org.protempa.DataSourceWriteException;
import org.protempa.backend.AbstractBackend;
import org.protempa.backend.DataSourceBackendUpdatedEvent;


/**
 * Convenience class for implementing a data source backend.
 * 
 * @author Andrew Post
 */
public abstract class AbstractDataSourceBackend extends
		AbstractBackend<DataSourceBackendUpdatedEvent> 
                implements DataSourceBackend {
    
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
    
}
