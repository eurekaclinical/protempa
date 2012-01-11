package org.protempa.bp.commons.dsb.relationaldb;

/**
 * Represents a spec for staging an entity spec. It requires {@link TableSpec}s
 * to describe which table to stage and where to stage it, and
 * {@link ColumnSpec}s indicating which columns in the table to stage. An
 * {@link EntitySpec} describes the relationships this spec has to other
 * <tt>EntitySpec</tt>s, so that queries will be properly generated. The
 * <tt>EntitySpec</tt> must have the same proposition IDs as some other
 * <tt>EntitySpec</tt> (constant, event, or primitive parameter) defined in the
 * data source backend.
 */
public final class StagingSpec {

    private final TableSpec stagingArea;
    private final TableSpec replacedTable;
    private final ColumnSpec[] stagedColumns;
    private final EntitySpec entitySpec;

    /**
     * Creates a new staging spec for use in a relational database data source
     * backend.
     * 
     * @param stagingArea
     *            a {@link TableSpec} defining where to stage the data
     * @param replacedTable
     *            a {@link TableSpec} defining which table is to be replaced in
     *            queries
     * @param stagedColumns
     *            an array of {@link ColumnSpec}s specifying which columns from
     *            the table to stage
     * @param entitySpec
     *            the {@link EntitySpec} the staged data will represent. This
     *            entity spec must map to the same proposition IDs as some other
     *            entity spec (constant, event, or primitive parameter) defined
     *            in the data source backend. Reference specs are not necessary.
     *            The only property specs needed are those that will constrain
     *            the data to be staged. Otherwise, the <tt>EntitySpec</tt>
     *            should be identical to the original.
     */
    public StagingSpec(TableSpec stagingArea, TableSpec replacedTable,
            ColumnSpec[] stagedColumns, EntitySpec entitySpec) {
        this.stagingArea = stagingArea;
        this.replacedTable = replacedTable;
        this.stagedColumns = stagedColumns.clone();
        this.entitySpec = entitySpec;
    }

    /**
     * Gets the staging area.
     * 
     * @return a {@link TableSpec} describing the staging area
     */
    public TableSpec getStagingArea() {
        return stagingArea;
    }

    /**
     * Gets the replaced table
     * 
     * @return a {@link TableSpec} describing the table being replaced
     */
    public TableSpec getReplacedTable() {
        return replacedTable;
    }

    /**
     * Gets the staged columns
     * 
     * @return the array of {@link ColumnSpec}s which are the columns being
     *         staged
     */
    public ColumnSpec[] getStagedColumns() {
        return stagedColumns;
    }

    /**
     * Gets the entity spec
     * 
     * @return the {@link EntitySpec} being staged
     */
    public EntitySpec getEntitySpec() {
        return entitySpec;
    }

}
