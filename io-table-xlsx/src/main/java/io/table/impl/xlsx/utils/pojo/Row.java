package io.table.impl.xlsx.utils.pojo;

import java.util.HashMap;
import java.util.Map;

/** Pojo class representing a row. */
public final class Row {

    /** The current row index. */
    private int index;
    /** The sum of coulmn count, meaning the index of the last available column. */
    private int columnCount = 0;
    /** All values of a row, key is cell value index, e.g. A1. */
    private final Map<String, Value> values = new HashMap<>();

    /**
     * Getter for the index, the row number.
     *
     * @return the index, the row number
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * The sum of coulmn count, meaning the index of the last available column.
     *
     * @return The sum of coulmn count, meaning the index of the last available column
     */
    public int getColumnCount() {
        return this.columnCount;
    }

    /**
     * Getter for all values.
     *
     * @return all values of a row, key is cell value index, e.g. A1
     */
    public Map<String, Value> getValues() {
        return this.values;
    }

    /**
     * Setter for the index.
     * 
     * @param index
     *            the index
     */
    public void setIndex(final int index) {
        this.index = index;
    }

    /**
     * Setter for column count.
     * 
     * @param columnCount
     *            column count
     */
    public void setColumnCount(final int columnCount) {
        this.columnCount = columnCount;
    }

}
