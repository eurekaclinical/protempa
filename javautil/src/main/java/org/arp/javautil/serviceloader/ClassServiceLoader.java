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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ServiceConfigurationError;

/**
 * Functions like {@link java.util.ServiceLoader} except it returns
 * lists of {@link Class}es instead of an {@link java.lang.Iterable} of
 * instances.
 * 
 * @author Andrew Post
 */
public class ClassServiceLoader {

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
    public static <S> List<Class<? extends S>> load(Class<S> service) {
        return load(service, null);
    }

    /**
     * Loads the classes of the providers of a service.
     *
     * @param service a service type. If <code>null</code>, a
     * {@link NullPointerException} will be thrown.
     * @param loader a class loader. If <code>null</code>, the current thread's
     * context class loader will be used.
     * @return a list of classes implementing the given service type and
     * class loader.
     *
     * @throws ServiceConfigurationError if something goes wrong with loading
     * a service provider's class.
     */
    public static <S> List<Class<? extends S>> load(Class<S> service,
            ClassLoader loader) {
        if (loader == null) {
            loader = Thread.currentThread().getContextClassLoader();
        }
        List<Class<? extends S>> services = new ArrayList<Class<? extends S>>();
        try {
            Enumeration<URL> e = loader.getResources("META-INF/services/"
                    + service.getName());

            while (e.hasMoreElements()) {
                URL url = e.nextElement();
                InputStream is = url.openStream();
                String name = null;
                try {
                    BufferedReader r = new BufferedReader(
                            new InputStreamReader(is, "UTF-8"));
                    while (true) {
                        String line = r.readLine();
                        if (line == null) {
                            break;
                        }
                        int comment = line.indexOf('#');
                        if (comment >= 0) {
                            line = line.substring(0, comment);
                        }
                        name = line.trim();
                        if (name.length() == 0) {
                            continue;
                        }
                        Class<?> clz = Class.forName(name, true, loader);
                        Class<? extends S> impl = clz.asSubclass(service);

                        services.add(impl);
                    }
                } catch (ClassNotFoundException cnfe) {
                    throw new ServiceConfigurationError("Could not load class for service provider " + name, cnfe);
                } finally {
                    is.close();
                }
            }
        } catch (IOException ioe) {
            /**
             * {@link java.util.ServiceLoader} swallows this exception, so
             * will we.
             */
        }
        return services;
    }
}
