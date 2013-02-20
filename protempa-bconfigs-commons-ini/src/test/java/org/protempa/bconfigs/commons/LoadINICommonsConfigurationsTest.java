/*
 * #%L
 * Protempa Commons INI Backend Configurations
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
package org.protempa.bconfigs.commons;

import org.junit.After;
import org.junit.Test;
import org.protempa.backend.Configurations;
import org.protempa.bcp.commons.CommonsConfigurationsProvider;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Andrew Post
 */
public class LoadINICommonsConfigurationsTest {
    private CommonsConfigurationsProvider configurationsProvider;
    
    @Before
    public void setUp() {
        this.configurationsProvider = 
                new CommonsConfigurationsProvider();
        this.configurationsProvider.setConfigurationsClassLoader(
                INICommonsConfigurationsTest.class.getClassLoader());
    }

    @After
    public void tearDown() {
        this.configurationsProvider = null;
    }
    
    @Test
    public void testLoadFromConfigurationsProviderNotNull() {
        Configurations configurations =
                configurationsProvider.getConfigurations();
        assertNotNull(configurations);
    }
    
    @Test
    public void testLoadFromConfigurationsProviderRightClass() {
        Configurations configurations =
                this.configurationsProvider.getConfigurations();
        assertSame(INICommonsConfigurations.class, configurations.getClass());
    }
}
