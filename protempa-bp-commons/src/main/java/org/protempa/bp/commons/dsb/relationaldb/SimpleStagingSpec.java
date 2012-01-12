package org.protempa.bp.commons.dsb.relationaldb;

final class SimpleStagingSpec {

    private final StagingSpec spec;

    public SimpleStagingSpec(TableSpec stagingArea, String uniqueColumn,
            TableSpec replacedTable, SimpleColumnSpec[] stagedColumns,
            EntitySpec entitySpec) {
        this.spec = new StagingSpec(stagingArea, uniqueColumn, replacedTable,
                stagedColumns, new EntitySpec[] { entitySpec });
    }
    
    public TableSpec getStagingArea() {
        return spec.getStagingArea();
    }
    
    public String getUniqueColumn() {
        return spec.getUniqueColumn();
    }
    
    public TableSpec getReplacedTable() {
        return spec.getReplacedTable();
    }
    
    public SimpleColumnSpec[] getStagedColumns() {
        return spec.getStagedColumns();
    }
    
    public EntitySpec getEntitySpec() {
        return spec.getEntitySpecs()[0];
    }
}
