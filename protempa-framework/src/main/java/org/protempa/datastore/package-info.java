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
/**
 * The package <code>org.protempa.datastore</code> contains classes for
 * accessing external temporary and permanent stores. This is the only type-safe
 * way of getting handles to the {@link org.arp.javautil.DataStore} interface, via
 * the {@link org.arp.javautil.datastore.DataStoreFactory} class. If a new type
 * of store is needed, an appropriate class should be created and should
 * implement {@link org.eurekaclinical.datastore.DataStoreCreator}, which
 * defines a method for building stores.
 * <p>
 * <code>getPermanentStore</code> returns a named store that will not be removed
 * once execution is finished. If a store with that name already exists, it may
 * be returned. Otherwise, a new store will be created.
 */
package org.protempa.datastore;
