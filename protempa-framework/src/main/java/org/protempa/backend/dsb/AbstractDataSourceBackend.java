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
import org.protempa.backend.dsb.filter.Filter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.*;

import org.protempa.backend.AbstractBackend;
import org.protempa.backend.DataSourceBackendUpdatedEvent;
import org.protempa.proposition.Proposition;


/**
 * Convenience class for implementing a data source backend.
 * 
 * @author Andrew Post
 */
public abstract class AbstractDataSourceBackend extends
		AbstractBackend<DataSourceBackendUpdatedEvent, DataSource> 
                implements DataSourceBackend {

//    @Override
//    public Map<String, List<Proposition>> readPropositions(
//            Set<String> keyIds, Set<String> paramIds, Filter filters,
//            QuerySession qs)
//            throws DataSourceReadException {
//        return null;
//    }

    @Override
    public void close() {
    }

    @Override
    public String getKeyTypeDisplayName() {
        return getKeyType();
    }

    @Override
    public String getKeyTypePluralDisplayName() {
        return getKeyTypeDisplayName() + "s";
    }
}
