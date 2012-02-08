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
 *
 * @author Andrew Post
 */
public class TempDirectory {
    private static final Object tmpDirectoryLock = new Object();

    private static int counter = -1; /* Protected by tmpDirectoryLock */
    private static String tmpdir;

    private static File generateFile(String prefix, String suffix, File dir)
        throws IOException {
        if (counter == -1) {
            counter = new Random().nextInt() & 0xffff;
        }
        counter++;
        return new File(dir, prefix + Integer.toString(counter) + suffix);
    }

   public static File create(String prefix, String suffix,
                                      File directory)
        throws IOException {
        if (prefix == null) throw new NullPointerException();
        if (prefix.length() < 3)
            throw new IllegalArgumentException("Prefix string too short");
        String s = (suffix == null) ? ".tmp" : suffix;
        synchronized (tmpDirectoryLock) {
            if (directory == null) {
                String tmpDir = getTempDir();
                directory = new File(tmpDir);
            }
            File f;
            do {
                f = generateFile(prefix, s, directory);
            } while (!f.mkdir());
            return f;
        }
    }


    public static File create(String prefix, String suffix)
        throws IOException {
        return create(prefix, suffix, null);
    }

    private static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }
}
