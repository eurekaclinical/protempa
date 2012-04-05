/*
 * #%L
 * Protempa Commons Backend Configurations Provider
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
package org.protempa.bcp.commons;

import org.apache.commons.discovery.tools.DiscoverSingleton;
import org.protempa.backend.Configurations;
import org.protempa.backend.ConfigurationsProvider;

/**
 *
 * @author Andrew Post
 */
public class CommonsConfigurationsProvider implements ConfigurationsProvider {

    public Configurations getConfigurations() {
        Configurations result = (Configurations)
                DiscoverSingleton.find(Configurations.class);
        if (result == null)
            throw new AssertionError("No Configurations classes found!");
        return result;
    }

    

}
