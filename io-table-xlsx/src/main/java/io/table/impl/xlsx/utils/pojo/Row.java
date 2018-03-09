package io.table.impl.xlsx.utils.pojo;

import java.util.HashMap;
import java.util.Map;

/** Pojo class representing a row. */
public final class Row {
    private int index;
    private int columnCount = 0;

    private final Map<String, Value> values = new HashMap<>();

    public int getIndex() {
        return this.index;
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public Map<String, Value> getValues() {
        return this.values;
    }

    public void setIndex(final int index) {
        this.index = index;
    }

    public void setColumnCount(final int columnCount) {
        this.columnCount = columnCount;
    }

}
