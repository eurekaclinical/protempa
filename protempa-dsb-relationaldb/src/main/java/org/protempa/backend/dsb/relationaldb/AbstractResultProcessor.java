/*
 * #%L
 * Protempa Commons Backend Provider
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
package org.protempa.backend.dsb.relationaldb;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.protempa.proposition.DataSourceBackendId;
import org.protempa.proposition.UniqueId;

/**
 *
 * @author Andrew Post
 */
abstract class AbstractResultProcessor implements SQLGenResultProcessor {

    private String dataSourceBackendId;
    private EntitySpec entitySpec;
    private boolean casePresent;
    
    protected AbstractResultProcessor(EntitySpec entitySpec, 
            String dataSourceBackendId) {
        assert entitySpec != null : "entitySpec cannot be null";
        assert dataSourceBackendId != null : 
                "dataSourceBackendId cannot be null";
        this.entitySpec = entitySpec;
        this.dataSourceBackendId = dataSourceBackendId;
    }

    final String getDataSourceBackendId() {
        return dataSourceBackendId;
    }

    @Override
    public final EntitySpec getEntitySpec() {
        return this.entitySpec;
    }

    @Override
    public final boolean isCasePresent() {
        return this.casePresent;
    }

    @Override
    public final void setCasePresent(boolean casePresent) {
        this.casePresent = casePresent;
    }

    protected static int readUniqueIds(String[] uniqueIds, ResultSet resultSet,
            int i) throws SQLException {
        for (int m = 0; m < uniqueIds.length; m++) {
            uniqueIds[m] = resultSet.getString(i++);
        }
        return i;
    }

    protected final UniqueId generateUniqueId(String name,
            String[] uniqueIds) {
        return new UniqueId(
                DataSourceBackendId.getInstance(this.dataSourceBackendId),
                new SQLGenUniqueId(name, uniqueIds));
    }
}
