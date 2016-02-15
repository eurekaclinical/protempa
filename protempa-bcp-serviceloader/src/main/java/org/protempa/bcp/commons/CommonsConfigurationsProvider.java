/*
 * #%L
 * Protempa Commons Backend Configurations Provider
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
package org.protempa.bcp.commons;

import java.util.ServiceConfigurationError;
import org.arp.javautil.serviceloader.SingletonServiceLoader;
import org.protempa.backend.Configurations;
import org.protempa.backend.ConfigurationsProvider;

/**
 * Uses JavaUtil's {@link SingletonServiceLoader} to load 
 * {@link Configurations}. By default, it configures 
 * {@link SingletonServiceLoader} to use the current thread's context class 
 * loader. Use {@link #setConfigurationsClassLoader} to specify a different 
 * class loader.
 * 
 * @author Andrew Post
 */
public class CommonsConfigurationsProvider implements ConfigurationsProvider {

    private ClassLoader configurationsClassLoader;
    private boolean classLoaderSpecified;

    @Override
    public Configurations getConfigurations() {
        Configurations result;
        if (this.classLoaderSpecified) {
            result =
                    SingletonServiceLoader.load(Configurations.class,
                    this.configurationsClassLoader);
        } else {
            result = SingletonServiceLoader.load(Configurations.class);
        }
        if (result == null) {
            throw new ServiceConfigurationError(
                    "No Configurations classes found!");
        }
        return result;
    }

    public void setConfigurationsClassLoader(ClassLoader loader) {
        this.configurationsClassLoader = loader;
        this.classLoaderSpecified = true;
    }

    public ClassLoader getConfigurationsClassLoader() {
        return this.configurationsClassLoader;
    }
    
    public boolean isConfigurationsClassLoaderSpecified() {
        return this.classLoaderSpecified;
    }
}
