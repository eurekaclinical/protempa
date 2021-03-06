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
package org.protempa;

/**
 * The event generated when a data source is updated.
 *
 * @author Andrew Post
 */
public final class DataSourceUpdatedEvent extends SourceUpdatedEvent {

    private static final long serialVersionUID = 9064890260294835226L;
    
    private final DataSource dataSource;

    /**
     * Constructs an event with the source
     * <code>DataSource</code> that generated the event.
     *
     * @param dataSource a <code>DataSource</code>.
     */
    public DataSourceUpdatedEvent(DataSource dataSource) {
        super(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public DataSource getProtempaSource() {
        return this.dataSource;
    }
}
