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

import java.util.List;
import org.protempa.proposition.Event;
import org.protempa.proposition.UniqueId;

/**
 *
 * @author Andrew Post
 */
final class EventRefResultProcessor extends RefResultProcessor<Event> {

    EventRefResultProcessor(RelationalDbDataSourceBackend backend, ResultCache<Event> results, 
            ReferenceSpec referenceSpec, EntitySpec entitySpec, 
            String dataSourceBackendId) {
        super(backend, results, referenceSpec, entitySpec, dataSourceBackendId);
    }

    @Override
    void addReferences(Event event, List<UniqueId> uids) {
        String referenceName = getReferenceSpec().getReferenceName();
        for (UniqueId uid : uids) {
            event.addReference(referenceName, uid);
        }
    }
}
