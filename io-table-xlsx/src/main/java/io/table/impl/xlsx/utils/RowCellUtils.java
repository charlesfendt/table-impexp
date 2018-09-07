package io.table.impl.xlsx.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Utility class for rows and cells.
 *
 * @author smarty
 *
 */
public final class RowCellUtils {

    /** Private constructor, utility class. */
    private RowCellUtils() {
        super();
    }

    /**
     * Convert a column index to a column name.
     *
     * @param column
     *            one-based column index.
     * @return Column name.
     */
    public static String colIndexToString(final int column) {
        final StringBuilder sb = new StringBuilder();
        int c = column - 1;
        while (c >= 0) {
            final int cur = c % 26;
            sb.append((char) ('A' + cur));
            c = (c - cur) / 26 - 1;
        }
        return sb.reverse().toString();
    }

    /**
     * Calculates the int column index from a cell matrix index, e.g. A1 or A.
     *
     * @param matrixIndex
     *            the matrix index of cell, e.g. A1
     * @return the int index
     */
    public static int stringToColIndex(final String matrixIndex) {
        final char[] charArray = matrixIndex.toCharArray();
        int colIndex = 0;
        int charCount = 0;
        for (int i = charArray.length - 1; i >= 0; i--) {
            final char c = charArray[i];
            if (c >= 65 && c <= 90) {
                colIndex += (c - 64) * Math.pow(26, charCount);
                charCount++;
            }
        }
        return colIndex;
    }

    /**
     * Getter for the microsoft format days.
     *
     * @param value
     *            the date to convert
     * @return the number of days since 1.1.1900 +2
     */
    public static double getDays(final Date value) {
        return value.getTime() / 86_400_000.0d + 25569.0d;
    }

    /**
     * Converts the microsoft days format to a date.
     *
     * @param value
     *            the microsoft days format as double
     * @return the date
     */
    public static Date getDateFromDays(final double value) {
        final long curVal = Math.round((value - 25569.0d) * 86_400_000.0d / 1000.d) * 1000L;
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(curVal);
        return cal.getTime();
    }
}
