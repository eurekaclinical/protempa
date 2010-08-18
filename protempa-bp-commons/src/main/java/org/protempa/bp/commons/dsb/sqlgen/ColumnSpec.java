package org.protempa.bp.commons.dsb.sqlgen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.arp.javautil.string.StringUtil;

/**
 * Specifies a column in a relational database table.
 *
 * @author Andrew Post
 */
public final class ColumnSpec implements Serializable {
    public static enum Constraint {
        EQUAL_TO, LIKE
    }

    public static class ConstraintValue {
        private String code;
        private Object value;

        public ConstraintValue(String code, Object value) {
            this.code = code;
            this.value = value;
        }

        public String getCode() {
            return this.code;
        }

        public Object getValue() {
            return this.value;
        }
    }

    private final String schema;
    private final String table;
    private final String column;
    private final JoinSpec joinSpec;
    private Constraint constraint;
    private ConstraintValue[] constraintValues;

    public ColumnSpec(String schema, String table, JoinSpec joinSpec) {
        this(schema, table, null, joinSpec);
    }

    public ColumnSpec(String schema, String table) {
        this(schema, table, null, null);
    }

    public ColumnSpec(String schema, String table, String column,
            Constraint constraint, ConstraintValue... constraintValue) {
        this(schema, table, column, null);
        this.constraint = constraint;
        this.constraintValues = constraintValue;
    }

    public ColumnSpec(String schema, String table, String column) {
        this(schema, table, column, null);
    }

    public ColumnSpec(String schema, String table,
            String column, JoinSpec joinSpec) {
        if (table == null)
            throw new IllegalArgumentException("table cannot be null");
        this.schema = schema;
        this.table = table;
        this.column = column;
        this.joinSpec = joinSpec;
        if (this.joinSpec != null)
            this.joinSpec.setPrevColumnSpec(this);
    }

    /**
     * Gets the schema name.
     * @return a schema name {@link String}.
     */
    public String getSchema() {
        return this.schema;
    }

    /**
     * Gets the table name.
     * @return a table name {@link String}.
     */
    public String getTable() {
        return this.table;
    }

    /**
     * Gets the column name.
     * @return a column name {@link String}.
     */
    public String getColumn() {
        return this.column;
    }
    
    public JoinSpec getJoin() {
        return this.joinSpec;
    }

    public Constraint getConstraint() {
        return this.constraint;
    }

    public ConstraintValue[] getConstraintValues() {
        return this.constraintValues;
    }

    public boolean isSameSchemaAndTable(ColumnSpec columnSpec) {
        return StringUtil.equals(columnSpec.getSchema(),this.getSchema())
                   && StringUtil.equals(columnSpec.getTable(),this.getTable());
    }

    /**
     * Returns a list of column specs that are chained to this one via joins
     * (including this column spec).
     * This is a convenience method for use with collections.
     *
     * @return a {@link List<ColumnSpec>}.
     */
    List<ColumnSpec> asList() {
        List<ColumnSpec> columnSpecs = new ArrayList<ColumnSpec>();
        ColumnSpec spec = this;
        columnSpecs.add(this);
        while (spec.getJoin() != null) {
            spec = spec.getJoin().getNextColumnSpec();
            columnSpecs.add(spec);
        }
        return columnSpecs;
    }

    @Override
    public String toString() {
        return "ColumnSpec{" + "schema=" +
                schema + "; table=" + table + "; column=" + column + "}";
    }




}
