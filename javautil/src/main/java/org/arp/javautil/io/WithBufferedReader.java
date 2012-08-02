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

import java.io.*;

/**
 * Convenience class for reading from a
 * <code>BufferedReader</code>.
 *
 * @author Andrew Post
 */
public abstract class WithBufferedReader {

    private InputStreamReader inputStreamReader;

    public WithBufferedReader(InputStream inputStream) {
        this(new InputStreamReader(inputStream));
    }

    public WithBufferedReader(String file) throws FileNotFoundException {
        this(new File(file));
    }

    public WithBufferedReader(File file) throws FileNotFoundException {
        this(new FileReader(file));

    }

    public WithBufferedReader(InputStreamReader inputStreamReader) {
        this.inputStreamReader = inputStreamReader;

    }

    public abstract void read(BufferedReader reader);

    public void execute() throws IOException {
        BufferedReader r = new BufferedReader(this.inputStreamReader);
        try {
            read(r);
            r.close();
            r = null;
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException ignore) {
                }
            }
        }
    }
}
