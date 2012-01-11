package org.protempa.bp.commons.dsb.relationaldb;

/**
 * Wrapper for {@link ColumnSpec} that only allows a schema and table.
 * 
 * @author Michel Mansour
 * 
 */
public final class TableSpec {
    private final ColumnSpec spec;

    private TableSpec(String schema, String table) {
        this.spec = new ColumnSpec(schema, table);
    }

    private TableSpec(ColumnSpec spec) {
        this.spec = spec;
    }

    /**
     * Creates and returns a new {@link TableSpec} with the given schema and
     * table names.
     * 
     * @param schema
     *            the schema name to use
     * @param table
     *            the table name to use
     * @return a new {@link TableSpec} with the given schema and table names
     */
    public static TableSpec withSchemaAndTable(String schema, String table) {
        return new TableSpec(schema, table);
    }

    /**
     * Creates and returns a new {@link TableSpec} from the given
     * {@link ColumnSpec}. The <tt>TableSpec</tt> will have the same schema and
     * table name as the <tt>ColumnSpec</tt>.
     * 
     * @param columnSpec
     *            the {@link ColumnSpec} the new {@link TableSpec} will be based
     *            upon
     * @return a new {@link TableSpec} with the same schema and table as the
     *         given {@link ColumnSpec}.
     */
    public static TableSpec fromColumnSpec(ColumnSpec columnSpec) {
        return new TableSpec(columnSpec);
    }

    /**
     * Gets the schema
     * 
     * @return the schema name
     */
    public String getSchema() {
        return spec.getSchema();
    }

    /**
     * Gets the table
     * 
     * @return the table name
     */
    public String getTable() {
        return spec.getTable();
    }

    public boolean equals(Object o) {
        if (null == o) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof TableSpec) {
            TableSpec other = (TableSpec) o;
            return other.getSchema().equals(getSchema())
                    && other.getTable().equals(getTable());
        }
        return false;
    }

    public int hashCode() {
        int result = 17;

        result = 31 * result + spec.getSchema().hashCode();
        result = 31 * result + spec.getTable().hashCode();

        return result;
    }

    public String toString() {
        return spec.getSchema() + "." + spec.getTable();
    }
}
