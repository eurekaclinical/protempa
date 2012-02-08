/*
 * #%L
 * Protempa Framework
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
package org.protempa.xml;

import java.io.IOException;
import java.io.Writer;

/**
 * Wrap a given writer, replacing all tab characters written to the writer with
 * the equivalent XML character references. "\t" becomes &#9;
 * 
 * @author mgrand
 */
class CharacterToReferenceWriter extends Writer {
	private Writer underlyingWriter;

	public CharacterToReferenceWriter(Writer underlyingWriter) {
		super();
		this.underlyingWriter = underlyingWriter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Writer#close()
	 */
	@Override
	public void close() throws IOException {
		underlyingWriter.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Writer#flush()
	 */
	@Override
	public void flush() throws IOException {
		underlyingWriter.flush();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Writer#write(char[], int, int)
	 */
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		for (int i = off; i < (off+len); i++) {
			if (cbuf[i] == '\t') {
				underlyingWriter.write("&#9;");
			} else {
				underlyingWriter.write(cbuf[i]);
			}
		}
	}

}
