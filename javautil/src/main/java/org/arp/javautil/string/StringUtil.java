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

    public static boolean equals(String str1, String str2) {
        return str1 != null ? str1.equals(str2) : str2 == null;
    }

}
