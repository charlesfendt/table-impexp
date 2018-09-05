package io.table.impl.xlsx.utils.pojo;

/**
 * Pojo for a excel sheet.
 *
 * @author smarty
 *
 */
public class Sheet {

    /** Id. */
    private final String rId;
    /** Type. */
    private final String sheetId;
    /** Target. */
    private final String name;

    /**
     * Constructor.
     *
     * @param rId
     *            id
     * @param name
     *            name
     * @param sheetId
     *            sheetId
     */
    public Sheet(final String rId, final String name, final String sheetId) {
        super();
        this.rId = rId;
        this.sheetId = sheetId;
        this.name = name;
    }

    /**
     * Getter.
     *
     * @return id
     */
    public String getrId() {
        return this.rId;
    }

    /**
     * Getter.
     *
     * @return type
     */
    public String getSheetId() {
        return this.sheetId;
    }

    /**
     * Getter.
     *
     * @return target
     */
    public String getName() {
        return this.name;
    }
}
