package org.protempa.bp.commons.dsb.relationaldb;

public final class SimpleColumnSpec {
    private final ColumnSpec spec;

    public SimpleColumnSpec(String schema, String table, String column) {
        this.spec = new ColumnSpec(schema, table, column);
    }

    public static SimpleColumnSpec withSchemaTableAndColumn(String schema,
            String table, String column) {
        return new SimpleColumnSpec(schema, table, column);
    }
    
    public String getSchema() {
        return spec.getSchema();
    }
    
    public String getTable() {
        return spec.getTable();
    }
    
    public String getColumn() {
        return spec.getColumn();
    }
    
    public ColumnSpec toColumnSpec() {
        return spec;
    }
}
