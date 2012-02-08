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
package org.protempa.datastore;

import org.arp.javautil.datastore.DataStore;
import org.arp.javautil.datastore.DataStoreFactory;
import org.protempa.proposition.DataSourceBackendId;
import org.protempa.proposition.DerivedUniqueId;
import org.protempa.proposition.UniqueId;

final class BdbTest {
    public static void main(String[] args) throws Exception {
        DataStore<String, UniqueId> store = DataStoreFactory
                .newCacheStore();

        UniqueId uid = new UniqueId(
                DataSourceBackendId.getInstance("12345"),
                new DerivedUniqueId("foo"));

        System.out.println(uid.hashCode());
        store.put("bar", uid);
        UniqueId uid2 = store.get("bar");
        System.out.println(uid2.hashCode());
        
        store.shutdown();
    }
}
