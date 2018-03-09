package io.table.impl.xlsx.utils.pojo;

import io.table.impl.xlsx.TableReaderXlsxImpl.EnumDataType;

/**
 * Pojo for a value.
 *
 * @author smarty
 *
 */
public final class Value {
    /** The data type of the value. */
    private EnumDataType dataType;
    /** The val as object. */
    private Object val;
    /** The current cell value index, e.g. A1. */
    private String cell;

    /**
     * Getter.
     *
     * @return data type of the value
     */
    public EnumDataType getDataType() {
        return this.dataType;
    }

    /**
     * Getter
     *
     * @return the current val as object
     */
    public Object getVal() {
        return this.val;
    }

    /**
     * Getter.
     *
     * @return the current cell value index, e.g. A1
     */
    public String getCell() {
        return this.cell;
    }

    /**
     * Setter.
     * 
     * @param dataType
     *            data type of the value
     */
    public void setDataType(final EnumDataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Setter.
     * 
     * @param val
     *            val as object
     */
    public void setVal(final Object val) {
        this.val = val;
    }

    /**
     * Setter.
     * 
     * @param cell
     *            the current cell value index, e.g. A1
     */
    public void setCell(final String cell) {
        this.cell = cell;
    }

}
