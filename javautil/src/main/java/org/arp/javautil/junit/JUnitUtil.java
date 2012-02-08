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
