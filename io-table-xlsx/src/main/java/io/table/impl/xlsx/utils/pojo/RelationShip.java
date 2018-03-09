package io.table.impl.xlsx.utils.pojo;

/**
 * Relationship pojo.
 *
 * @author smarty
 *
 */
public class RelationShip {

    /** Id. */
    private final String rId;
    /** Type. */
    private final String type;
    /** Target. */
    private final String target;

    /**
     * Constructor.
     *
     * @param rId
     *            id
     * @param type
     *            type
     * @param target
     *            target
     */
    public RelationShip(final String rId, final String type, final String target) {
        super();
        this.rId = rId;
        this.type = type;
        this.target = target;
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
    public String getType() {
        return this.type;
    }

    /**
     * Getter.
     *
     * @return target
     */
    public String getTarget() {
        return this.target;
    }

}
