package io.table.impl.xlsx.utils;

/**
 * Utility class for rows and cells.
 *
 * @author smarty
 *
 */
public final class RowCellUtils {

    /** Private constructor, utility class. */
    private RowCellUtils() {
        // nothing to do
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
            c = ((c - cur) / 26) - 1;
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
            if ((c >= 65) && (c <= 90)) {
                colIndex += (c - 64) * Math.pow(26, charCount);
                charCount++;
            }
        }
        return colIndex;
    }
}
