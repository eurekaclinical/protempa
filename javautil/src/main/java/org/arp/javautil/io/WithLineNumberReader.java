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
 * <code>LineNumberReader</code>.
 *
 * @author Andrew Post
 */
public abstract class WithLineNumberReader {

    private InputStreamReader inputStreamReader;

    public WithLineNumberReader(InputStream inputStream) {
        this(new InputStreamReader(inputStream));
    }

    public WithLineNumberReader(String file) throws FileNotFoundException {
        this(new File(file));
    }

    public WithLineNumberReader(File file) throws FileNotFoundException {
        this(new FileReader(file));

    }

    public WithLineNumberReader(InputStreamReader inputStreamReader) {
        this.inputStreamReader = inputStreamReader;
    }

    public abstract void readLine(int lineNumber, String line);

    public void execute() throws IOException {
        LineNumberReader r = new LineNumberReader(this.inputStreamReader);
        try {
            String line;
            while ((line = r.readLine()) != null) {
                readLine(r.getLineNumber(), line);
            }
            r.close();
            r = null;
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException ignore) {
                }
            }
            this.inputStreamReader = null;
        }
    }
}
