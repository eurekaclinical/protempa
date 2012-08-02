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
package org.arp.javautil.datastore;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.arp.javautil.io.UniqueDirectoryCreator;

/**
 *
 * @author Andrew Post
 */
public class BdbUtil {
    private static final UniqueDirectoryCreator UNIQUE_DIRECTORY_CREATOR =
            new UniqueDirectoryCreator();
    /**
     * Creates a unique directory for housing a BDB environment, and returns 
     * its name.
     * 
     * @param prefix a prefix for the temporary directory's name. Cannot be
     * <code>null</code>.
     * 
     * @param suffix a suffix for the temporary directory's name.
     * @return the environment name to use.
     * 
     * @throws IOException if an error occurred in creating the temporary
     * directory.
     */
    public static String uniqueEnvironment(String prefix, String suffix, 
            File directory) throws IOException {
        File tmpDir = 
                UNIQUE_DIRECTORY_CREATOR.create(prefix, suffix, directory);
        String randomFilename = UUID.randomUUID().toString();
        File envNameAsFile = new File(tmpDir, randomFilename);
        return envNameAsFile.getAbsolutePath();
    }
}
