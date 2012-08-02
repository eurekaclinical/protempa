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
import java.util.Random;

/**
 * Creates temporary directories (i.e., that are deleted upon the exit of the
 * virtual machine). A shutdown hook deletes the directories and their contents.
 *
 * @author Andrew Post
 */
public class UniqueDirectoryCreator {

    protected static final Object tmpDirectoryLock = new Object();
    private int counter = -1;

    public File create(String prefix, String suffix, File directory)
            throws IOException {
        if (prefix == null) {
            throw new NullPointerException();
        }
        if (prefix.length() < 3) {
            throw new IllegalArgumentException("Prefix string too short");
        }
        if (directory == null) {
            throw new IllegalArgumentException("directory cannot be null");
        }
        return doGenerateFile(prefix, suffix, directory);
    }

    protected File doGenerateFile(String prefix, String suffix, File directory) throws IOException {
        String s = (suffix == null) ? "" : suffix;
        synchronized (tmpDirectoryLock) {
            File f;
            Random random = new Random();
            do {
                if (counter == -1) {
                    counter = random.nextInt() & 0xffff;
                }
                counter++;
                f = new File(directory, prefix + Integer.toString(counter) + s);
            } while (!f.mkdir());

            return f;
        }
    }
}
