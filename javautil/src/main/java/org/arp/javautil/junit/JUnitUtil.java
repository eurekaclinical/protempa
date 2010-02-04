package org.arp.javautil.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Utilities for JUnit testing.
 * 
 * @author Andrew Post
 */
public final class JUnitUtil {

	/**
	 * Constructor (private).
	 */
	private JUnitUtil() {

	}

	/**
	 * Serializes and deserializes the given object.
	 * 
	 * @param o
	 *            an <code>Object</code>.
	 * @return an <code>Object</code>.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object serializeAndDeserialize(Object o) throws IOException,
			ClassNotFoundException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bytes);
		try {
			out.writeObject(o);
		} finally {
			out.close();
		}

		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
				bytes.toByteArray()));
		try {
			Object result = in.readObject();
			return result;
		} finally {
			in.close();
		}
	}
}
