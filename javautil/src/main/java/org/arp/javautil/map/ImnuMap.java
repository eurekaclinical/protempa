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
package org.arp.javautil.map;

import java.util.HashMap;

/**
 * ImnuMap is a {@link java.util.HashMap} that specifies a default value for
 * keys that are not in the map. The default value is specified through an
 * interface called DefaultValue that defines one method: <tt>defaultValue</tt>.
 * <tt>defaultValue</tt> accepts the key as an argument, so an implementing
 * class can decide what default value should be returned based on the supplied
 * key.
 * 
 * While there are many ways of providing a default value for missing keys, the
 * name of this class enshrines the fact that the design of this particular
 * implementation was suggested by Himanshu Rathod. Specifically, {IMNU} = {HIMANSHU} -
 * {HASH}. In other words, ImnuMap is the difference between Himanshu's
 * suggestion and a plain ol' HashMap.
 * 
 * @author Michel Mansour
 * 
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 */
public class ImnuMap<K, V> extends HashMap<K, V> {

    private static final long serialVersionUID = -2120654167887510727L;

    private final DefaultValue<V> dv;

    public ImnuMap(DefaultValue<V> defaultValue) {
        super();
        this.dv = defaultValue;
    }
    
    /**
     * Creates a new ImnuMap instance that returns null for all missing keys.
     */
    public ImnuMap() {
        this(new NullDefaultValue<V>());
    }

    public V get(Object key) {
        if (super.containsKey(key)) {
            return super.get(key);
        } else {
            return dv.defaultValue(key);
        }
    }

    public interface DefaultValue<V> {
        public V defaultValue(Object key);
    }

    /**
     * A built-in implementation of DefaultValue that returns <tt>null</tt> for
     * any missing key.
     */
    private static class NullDefaultValue<U> implements DefaultValue<U> {

        @Override
        public U defaultValue(Object key) {
            return null;
        }

    }
}
