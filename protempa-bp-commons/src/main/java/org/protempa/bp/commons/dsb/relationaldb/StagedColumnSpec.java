package org.protempa.bp.commons.dsb.relationaldb;

public final class StagedColumnSpec {
    private final ColumnSpec spec;
    private final String asName;
    private final String[] forEntitySpecs;

    public StagedColumnSpec(String schema, String table, String column,
            String asName, String[] forEntitySpecs) {
        if (asName != null
                && (forEntitySpecs == null || forEntitySpecs.length < 1)) {
            throw new IllegalArgumentException(
                    "The substitute name for a column must apply to at least one entity spec. 'forEntitySpecs' is null or has fewer than 1 element.");
        }
        this.spec = new ColumnSpec(schema, table, column);
        this.asName = asName;
        this.forEntitySpecs = forEntitySpecs;
    }

    public StagedColumnSpec(String schema, String table, String column) {
        this(schema, table, column, null, null);
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

    public String getAsName() {
        return asName;
    }

    public String[] getForEntitySpecs() {
        if (null == forEntitySpecs) {
            return null;
        }
        return forEntitySpecs.clone();
    }

    public ColumnSpec toColumnSpec() {
        return spec;
    }
}
