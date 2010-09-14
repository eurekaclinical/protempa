package org.arp.javautil.string;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

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

    public static String getToString(Class cls, Map<String,Object> fields) {
        StringBuilder result = new StringBuilder();
        result.append(cls);
        result.append('{');
        for (Iterator<Map.Entry<String,Object>> itr =
                fields.entrySet().iterator(); itr.hasNext();) {
            Map.Entry<String,Object> me = itr.next();
            result.append(me.getKey());
            result.append('=');
            Object val = me.getValue();
            if (val instanceof Object[])
                result.append(Arrays.toString((Object[]) val));
            else
                result.append(me.getValue());
            if (itr.hasNext())
                result.append("; ");

        }
        result.append('}');
        return result.toString();
    }

}
