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
package org.arp.javautil.serviceloader;

import java.util.*;

/**
 * Functions like {@link java.util.ServiceLoader} except it returns
 * lists of {@link Class}es instead of an {@link java.lang.Iterable} of
 * instances.
 * 
 * @author Andrew Post
 */
public class SingletonServiceLoader {

    /**
     * Loads the classes of the providers of a service.
     *
     * @param service a service type.
     * @return a list of classes implementing the given service type, using
     * the current thread's context class loader.
     *
     * @throws ServiceConfigurationError if something goes wrong with loading
     * a service provider's class.
     */
    public static <S> S load(Class<S> service) {
        java.util.ServiceLoader<S> load = 
                java.util.ServiceLoader.load(service);
        return doLoad(load, service);
    }
    
    /**
     * Loads a service provider using {@link ServiceLoader}, assuming that 
     * there is only one instance of the given service provider.
     * 
     * @param service the class or interface that service providers implement 
     * or extend.
     * 
     * @throws ServiceConfigurationError if no classes were found or if 
     * multiple classes were found.
     */
    public static <S> S load(Class<S> service, ClassLoader loader) {
        java.util.ServiceLoader<S> load = 
                java.util.ServiceLoader.load(service, loader);
        return doLoad(load, service);
    }

    private static <S> S doLoad(ServiceLoader<S> load, Class<S> service) 
            throws ServiceConfigurationError {
        Iterator<S> itr = load.iterator();
        S result = null;
        if (!itr.hasNext()) {
            throw new ServiceConfigurationError("No " + service.getName() + 
                    " classes were found by service discovery");
        } else {
            result = itr.next();
            if (itr.hasNext()) {
                throw new ServiceConfigurationError("Multiple " 
                        + service.getName() + 
                    " classes were found by service discovery");
            }
        }
        return result;
    }
}
