package org.arp.javautil.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Convenience class for reading from a <code>BufferedReader</code>.
 * 
 * @author Andrew Post
 */
public abstract class WithBufferedReaderByLine {
	private InputStreamReader inputStreamReader;

	public WithBufferedReaderByLine(InputStream inputStream) {
		this(new InputStreamReader(inputStream));
	}

	public WithBufferedReaderByLine(String file) throws Exception {
		this(new File(file));
	}

	public WithBufferedReaderByLine(File file) throws Exception {
		this(new FileReader(file));
	}

	public WithBufferedReaderByLine(InputStreamReader inputStreamReader) {
		this.inputStreamReader = inputStreamReader;
	}

	public abstract void readLine(String line) throws Exception;

	public void execute() throws Exception {
		BufferedReader r = new BufferedReader(inputStreamReader);
		try {
			String line = null;
			while ((line = r.readLine()) != null) {
				readLine(line);
			}
		} finally {
			r.close();
		}
	}
}
