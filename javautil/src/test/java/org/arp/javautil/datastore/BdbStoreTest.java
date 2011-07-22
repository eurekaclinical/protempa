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
