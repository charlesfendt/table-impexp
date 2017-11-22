/**
 *
 */
package io.table.impl.xlsx.utils;

/**
 * Utility class for String escape.
 *
 * @author fendtc
 */
public final class StringEscapeUtils {

    /**
     * Hidden constructor for the utility class.
     */
    private StringEscapeUtils() {
        super();
    }

    /**
     * Method to escape string.
     *
     * @param str
     *            The string to convert.
     * @return The converted String compliant with XML format.
     */
    public static String escapeString(final String str) {
        final StringBuilder buff = new StringBuilder();
        if (str != null) {
            for (final char ch : str.toCharArray()) {
                switch (ch) {
                    case '<':
                        buff.append("&lt;");
                        break;
                    case '>':
                        buff.append("&gt;");
                        break;
                    case '&':
                        buff.append("&amp;");
                        break;
                    case '\'':
                        buff.append("&apos;");
                        break;
                    case '"':
                        buff.append("&quot;");
                        break;
                    default:
                        if (ch > 0x7e) {
                            buff.append("&#x").append(Integer.toHexString(ch)).append(';');
                        } else {
                            buff.append(ch);
                        }
                        break;
                }
            }
        }
        return buff.toString();
    }
}
