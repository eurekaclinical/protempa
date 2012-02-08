/**
 * 
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
public class CharacterToReferenceWriter extends Writer {
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
