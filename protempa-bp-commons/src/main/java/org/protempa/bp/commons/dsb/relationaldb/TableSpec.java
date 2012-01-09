package org.protempa.bp.commons.dsb.relationaldb;

/**
 * Wrapper for {@link ColumnSpec} that only allows tables.
 * 
 * @author Michel Mansour
 * 
 */
public final class TableSpec {
    private final ColumnSpec spec;

    public TableSpec(String schema, String table) {
        this.spec = new ColumnSpec(schema, table);
    }

    public String getSchema() {
        return spec.getSchema();
    }

    public String getTable() {
        return spec.getTable();
    }

    public boolean equals(Object o) {
        if (o instanceof TableSpec) {
            TableSpec other = (TableSpec) o;
            return other.getSchema().equals(getSchema())
                    && other.getTable().equals(getTable());
        }
        return false;
    }
}
