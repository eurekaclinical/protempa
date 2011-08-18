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
