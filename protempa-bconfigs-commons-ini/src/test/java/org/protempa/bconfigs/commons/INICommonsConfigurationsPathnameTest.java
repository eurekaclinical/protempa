/*
 * #%L
 * Protempa Commons INI Backend Configurations
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
package org.protempa.bconfigs.commons;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.arp.javautil.io.TempDirectoryCreator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Andrew Post
 */
public class INICommonsConfigurationsPathnameTest {

    @Test
    public void testDefault() {
        assertEquals(INICommonsConfigurations.DEFAULT_DIRECTORY,
                new INICommonsConfigurations().getDirectory());
    }

    @Test
    public void testSystemProperty() {
        String tempDirPath = FileUtils.getTempDirectoryPath();
        String dirSysProp = INICommonsConfigurations.DIRECTORY_SYSTEM_PROPERTY;
        System.setProperty(dirSysProp, tempDirPath);
        INICommonsConfigurations configs = new INICommonsConfigurations();
        assertEquals(new File(tempDirPath), configs.getDirectory());
    }
    
    @Test
    public void testManuallySpecified() throws IOException {
        TempDirectoryCreator tmpDirCreator = new TempDirectoryCreator();
        File dir = tmpDirCreator.create("foo", "bar", null);
        INICommonsConfigurations configs = new INICommonsConfigurations(dir);
        assertEquals(dir, configs.getDirectory());
    }
}
