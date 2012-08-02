/*
 * #%L
 * Protempa Commons Backend Provider
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
package org.protempa.backend;

import org.protempa.backend.annotations.BackendProperty;
import org.protempa.backend.dsb.AbstractDataSourceBackend;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractCommonsDataSourceBackend
        extends AbstractDataSourceBackend  {

    private String dataSourceBackendId;

    @Override
    public void initialize(BackendInstanceSpec config)
        throws DataSourceBackendInitializationException {
        CommonsBackend.initialize(this, config);
        if (this.dataSourceBackendId == null)
            throw new DataSourceBackendInitializationException(
                    "dataSourceBackendId is not set");
    }

    @Override
    public final String getDisplayName() {
        return CommonsBackend.backendInfo(this).displayName();
    }

    protected final String nameForErrors() {
        return CommonsBackend.nameForErrors(this);
    }

    @BackendProperty
    public final void setDataSourceBackendId(String id) {
        this.dataSourceBackendId = id;
    }

    public final String getDataSourceBackendId () {
        return this.dataSourceBackendId;
    }
}
