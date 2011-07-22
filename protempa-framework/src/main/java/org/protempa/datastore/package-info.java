/**
 * The package <code>org.protempa.store</code> contains classes for accessing
 * external temporary and permanent stores. This is the only type-safe way of
 * getting handles to the {@link org.arp.javautil.Store} interface via the
 * {@link org.arp.javautil.datastore.DataStoreFactory} class. If a new type of store is
 * needed, an appropriate class should be added to this package. The class
 * should implement {@link org.protempa.datastore.ProtempaDataStoreCreator}, which
 * defines two methods for building stores.
 * <p>
 * <code>getPermanentStore</code> returns a named store that will not be removed
 * once execution is finished. If a store with that name already exists, it may
 * be returned. Otherwise, a new store will be created.
 * <p>
 * <code>newCacheStore</code> returns a temporary store that will be destroyed
 * once execution is complete. Caches are not named, and the method is
 * guaranteed to return a new store each time it is called, so it is the
 * caller's responsibility to keep a reference to the store.
 */
package org.protempa.datastore;