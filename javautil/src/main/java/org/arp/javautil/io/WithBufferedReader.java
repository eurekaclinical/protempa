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
public abstract class WithBufferedReader {
	private InputStreamReader inputStreamReader;

	public WithBufferedReader(InputStream inputStream) {
		this(new InputStreamReader(inputStream));
	}

	public WithBufferedReader(String file) throws Exception {
		this(new File(file));
	}

	public WithBufferedReader(File file) throws Exception {
		this(new FileReader(file));

	}

	public WithBufferedReader(InputStreamReader inputStreamReader) {
		this.inputStreamReader = inputStreamReader;

	}

	public abstract void read(BufferedReader reader) throws Exception;

	public void execute() throws Exception {
		BufferedReader r = new BufferedReader(inputStreamReader);
		try {
			read(r);
		} finally {
			r.close();
		}
	}
}
