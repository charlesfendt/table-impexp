package io.table.impl.xlsx.utils.pojo;

import io.table.impl.xlsx.TableReaderXlsxImpl.EnumDataType;

public final class Value {
    private EnumDataType dataType;
    private Object val;
    private String cell;

    public EnumDataType getDataType() {
        return this.dataType;
    }

    public Object getVal() {
        return this.val;
    }

    public String getCell() {
        return this.cell;
    }

    public void setDataType(final EnumDataType dataType) {
        this.dataType = dataType;
    }

    public void setVal(final Object val) {
        this.val = val;
    }

    public void setCell(final String cell) {
        this.cell = cell;
    }

}
