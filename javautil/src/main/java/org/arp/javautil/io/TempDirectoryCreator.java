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
package org.arp.javautil.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.io.FileUtils;

/**
 * Creates temporary directories (i.e., that are deleted upon the exit of the
 * virtual machine). A shutdown hook deletes the directories and their contents.
 *
 * @author Andrew Post
 */
public class TempDirectoryCreator extends UniqueDirectoryCreator {

    private final static List<File> TEMP_DIRS = new ArrayList<File>();

    static {
        Runtime.getRuntime().addShutdownHook(
                new Thread("TempDirectoryShutdownHook") {

                    @Override
                    public void run() {
                        for (File file : TEMP_DIRS) {
                            try {
                                FileUtils.deleteDirectory(file);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    public File create(String prefix, String suffix, File directory)
            throws IOException {
        if (directory == null) {
            directory = FileUtils.getTempDirectory();
        }
        File f = super.create(prefix, suffix, directory);
        TEMP_DIRS.add(f);
        return f;
    }
}
