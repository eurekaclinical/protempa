package org.arp.javautil.string;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author Andrew Post
 */
public class StringUtil {

    private static final char QUOTE = '"';
    private static final char[] SEARCH_CHARS = new char[]{
        QUOTE, CharUtils.CR, CharUtils.LF};

    private StringUtil() {
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

    public static List<String> escapeDelimitedColumns(
            List<String> columnValues, char delimiter) {
        List<String> result = new ArrayList<String>(columnValues.size());
        for (String value : columnValues) {
            result.add(StringUtil.escapeDelimitedColumn(value, delimiter));
        }
        return result;
    }

    public static void escapeDelimitedColumnsInPlace(
            List<String> columnValues, char delimiter) {
        for (int i = 0, n = columnValues.size(); i < n; i++) {
            String value = columnValues.get(i);
            columnValues.set(i,
                    StringUtil.escapeDelimitedColumn(value, delimiter));
        }
    }

    public static String[] escapeDelimitedColumns(String[] columnValues,
            char delimiter) {
        String[] result = new String[columnValues.length];
        for (int i = 0; i < columnValues.length; i++) {
            String columnValue = columnValues[i];
            result[i] = StringUtil.escapeDelimitedColumn(columnValue,
                    delimiter);
        }
        return result;
    }

    public static void escapeDelimitedColumnsInPlace(String[] columnValues,
            char delimiter) {
        for (int i = 0; i < columnValues.length; i++) {
            String columnValue = columnValues[i];
            columnValues[i] = StringUtil.escapeDelimitedColumn(columnValue,
                    delimiter);
        }
    }

    public static void escapeAndWriteDelimitedColumns(String[] columnValues,
            char delimiter, Writer writer) throws IOException {
        for (int i = 0; i < columnValues.length; i++) {
            String columnValue = columnValues[i];
            escapeDelimitedColumn(columnValue, delimiter, writer);
            if (i < columnValues.length - 1) {
                writer.write(delimiter);
            }
        }
    }

    public static void escapeAndWriteDelimitedColumns(
            List<String> columnValues,
            char delimiter, Writer writer) throws IOException {
        for (int i = 0, n = columnValues.size(); i < n; i++) {
            String columnValue = columnValues.get(i);
            escapeDelimitedColumn(columnValue, delimiter, writer);
            if (i < n - 1) {
                writer.write(delimiter);
            }
        }
    }

    /**
     * Escapes a column in a delimited file and writes it directly to a
     * {@link Writer}. This is somewhat more efficient than
     * {@link #escapeDelimitedColumn(java.lang.String, char)} because it does
     * less temporary object creation. The performance difference will become
     * more apparent when writing large delimited files.
     * 
     * @param str a column {@link String}.
     * @param delimiter the file's delimiter character.
     * @param writer the {@link Writer} to which to write the escaped column.
     * @throws IOException if an error writing to <code>writer</code> occurs.
     */
    public static void escapeDelimitedColumn(String str, char delimiter,
            Writer writer) throws IOException {
        if (str == null) {
            throw new IllegalArgumentException("str cannot be null");
        }
        if (StringUtils.containsNone(str, SEARCH_CHARS)
                && str.indexOf(delimiter) < 0) {
            writer.write(str);
        } else {
            writer.write(QUOTE);
            for (int j = 0, n = str.length(); j < n; j++) {
                char c = str.charAt(j);
                if (c == QUOTE) {
                    writer.write(QUOTE);
                }
                writer.write(c);
            }
            writer.write(QUOTE);
        }
    }

    /**
     * Escapes a column in a delimited file.
     *
     * @param str a column {@link String}.
     * @param delimiter the file's delimiter character.
     * @return the escaped column {@link String}.
     */
    public static String escapeDelimitedColumn(String str, char delimiter) {
        if (str == null) {
            throw new IllegalArgumentException("str cannot be null");
        }
        if (StringUtils.containsNone(str, SEARCH_CHARS)
                && str.indexOf(delimiter) < 0) {
            return str;
        } else {
            StringBuilder writer = new StringBuilder();
            writer.append(QUOTE);
            for (int j = 0, n = str.length(); j < n; j++) {
                char c = str.charAt(j);
                if (c == QUOTE) {
                    writer.append(QUOTE);
                }
                writer.append(c);
            }
            writer.append(QUOTE);
            return writer.toString();
        }
    }
}
