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
    private final String indexTablespace;
    private final String uniqueColumn;
    private final TableSpec replacedTable;
    private final SimpleColumnSpec[] stagedColumns;
    private final EntitySpec[] entitySpecs;

    /**
     * Creates a new staging spec for use in a relational database data source
     * backend.
     * 
     * @param stagingArea
     *            a {@link TableSpec} defining where to stage the data
     * @param indexTablespace
     *            the name of the tablespace where the indexes will be stored
     * @param uniqueColumn
     *            the unique key of the table
     * @param replacedTable
     *            a {@link TableSpec}s defining which table is to be replaced in
     *            queries
     * @param stagedColumns
     *            an array of {@link ColumnSpec}s specifying which columns from
     *            the table to stage
     * @param entitySpecs
     *            the {@link EntitySpec} the staged data will represent. This
     *            entity spec must map to the same proposition IDs as some other
     *            entity spec (constant, event, or primitive parameter) defined
     *            in the data source backend. Reference specs are not necessary.
     *            The only property specs needed are those that will constrain
     *            the data to be staged. Otherwise, the <tt>EntitySpec</tt>
     *            should be identical to the original.
     */
    public StagingSpec(TableSpec stagingArea, String indexTablespace,
            String uniqueColumn, TableSpec replacedTable,
            SimpleColumnSpec[] stagedColumns, EntitySpec[] entitySpecs) {
        this.stagingArea = stagingArea;
        this.indexTablespace = indexTablespace;
        this.uniqueColumn = uniqueColumn;
        this.replacedTable = replacedTable;
        this.stagedColumns = stagedColumns.clone();
        this.entitySpecs = entitySpecs;
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
     * Gets the tablespace where the indexes will live.
     * 
     * @return the index tablespace
     */
    public String getIndexTablespace() {
        return indexTablespace;
    }

    /**
     * Gets the unique column.
     * 
     * @return the unique column name
     */
    public String getUniqueColumn() {
        return uniqueColumn;
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
    public SimpleColumnSpec[] getStagedColumns() {
        return stagedColumns;
    }

    /**
     * Gets the entity spec
     * 
     * @return the {@link EntitySpec} being staged
     */
    public EntitySpec[] getEntitySpecs() {
        return entitySpecs;
    }

}
