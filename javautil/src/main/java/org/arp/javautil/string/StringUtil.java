package org.arp.javautil.string;

/**
 * @author Andrew Post
 */
public class StringUtil {

	/**
	 * 
	 */
	private StringUtil() {
		super();
	}

	/**
	 * Returns whether the given string is <code>null</code>, of length 0, or
	 * contains just whitespace.
	 * 
	 * @param str
	 *            a <code>String</code>.
	 * @return <code>true</code> if the string is <code>null</code>, of
	 *         length 0, or contains just whitespace; <code>false</code>
	 *         otherwise.
	 */
	public static boolean getEmptyOrNull(String str) {
		return str == null || str.trim().length() == 0;
	}

}
