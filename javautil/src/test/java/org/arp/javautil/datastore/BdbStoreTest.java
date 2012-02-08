/*
 * #%L
 * JavaUtil
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
package org.arp.javautil.datastore;


final class BdbStoreTest {
    public static void main(String[] args) throws Exception {
        DataStore<String, String> store = DataStoreFactory.newCacheStore();
//        Store<String, String> store = StoreFactory.getPermanentStore("foo");
//        Store<String, String> bar = StoreFactory.getPermanentStore("bar");

        if (store.containsKey("foo")) {
            System.out.println("Found 'foo': " + store.get("foo"));
        } else {
            System.out.println("Couldn't find 'foo'...adding 'foo'");
            store.put("foo", "FOO");
        }
        
//        if (bar.containsKey("bar")) {
//            System.out.println("Found 'bar': " + bar.get("bar"));
//        } else {
//            bar.put("bar", "BAR");
//        }
        
        store.shutdown();
    }
}
