package org.arp.javautil.collections;

import java.util.Collection;

/**
 * Extra utilities for collections.
 * 
 * @author Andrew Post
 */
public class Collections {

	private Collections() {

	}

	/**
	 * Return a string which is the concatenation of the strings in the
	 * collection (or the string representations of each object in the
	 * collection).
	 * 
	 * @param c
	 *            the <code>Collection</code>. If null, an empty
	 *            <code>String</code> will be returned.
	 * @param separator
	 *            the <code>String</code> separator between elements. If
	 *            <code>null</code>, <code>""</code> is used.
	 * @return a <code>String</code>.
	 */
	public static String join(Collection c, String separator) {
		if (c == null) {
			return "";
		} else {
			return Iterators.join(c.iterator(), separator);
		}
	}
}
