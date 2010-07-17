package org.protempa.bp.commons.dsb.sqlgen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
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

    private final boolean unique;
    private final String schema;
    private final String table;
    private final String column;
    private final JoinSpec joinSpec;
    private Constraint constraint;
    private ConstraintValue[] constraintValues;

    public ColumnSpec(String schema, String table, JoinSpec joinSpec) {
        this(false, schema, table, null, joinSpec);
    }

    public ColumnSpec(String schema, String table, String column,
            JoinSpec joinSpec) {
        this(false, schema, table, column, joinSpec);
    }

    public ColumnSpec(String schema, String table) {
        this(false, schema, table, null);
    }

    public ColumnSpec(String schema, String table, String column) {
        this(false, schema, table, column);
    }

    public ColumnSpec(String schema, String table, String column,
            Constraint constraint, ConstraintValue... constraintValue) {
        this(false, schema, table, column);
        this.constraint = constraint;
        this.constraintValues = constraintValue;
    }

    public ColumnSpec(boolean unique, String schema, String table,
            String column) {
        this(unique, schema, table, column, null);
    }

    public ColumnSpec(boolean unique, String schema, String table,
            String column, JoinSpec joinSpec) {
        if (table == null)
            throw new IllegalArgumentException("table cannot be null");
        this.unique = unique;
        this.schema = schema;
        this.table = table;
        this.column = column;
        this.joinSpec = joinSpec;
    }

    public boolean isUnique() {
        return this.unique;
    }

    public String getSchema() {
        return this.schema;
    }

    public String getTable() {
        return this.table;
    }

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
