package org.protempa.bp.commons.dsb.relationaldb;

public final class StagingSpec {

    private final TableSpec stagingArea;
    private final TableSpec[] replacedTables;
    private final ColumnSpec[] stagedColumns;
    private final EntitySpec entitySpec;

    public StagingSpec(TableSpec stagingArea, TableSpec[] replacedTables, ColumnSpec[] stagedColumns,
            EntitySpec entitySpec) {
        this.stagingArea = stagingArea;
        this.replacedTables = replacedTables.clone();
        this.stagedColumns = stagedColumns.clone();
        this.entitySpec = entitySpec;
    }

    public TableSpec getStagingArea() {
        return stagingArea;
    }

    public TableSpec[] getReplacedTables() {
        return replacedTables;
    }
    
    public ColumnSpec[] getStagedColumns() {
        return stagedColumns;
    }

    public EntitySpec getEntitySpec() {
        return entitySpec;
    }

}
