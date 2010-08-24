package org.protempa.bp.commons.dsb.sqlgen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.arp.javautil.string.StringUtil;

/**
 * Specifies a column in a relational database table.
 *
 * @author Andrew Post
 */
public final class ColumnSpec implements Serializable {

    public static enum Constraint {

        EQUAL_TO("="),
        LIKE("LIKE"),
        GREATER_THAN(">"),
        GREATER_THAN_OR_EQUAL_TO(">="),
        LESS_THAN("<"),
        LESS_THAN_OR_EQUAL_TO("<="),
        NOT_EQUAL_TO("<>");
        private String sqlOperator;

        private Constraint(String sqlOperator) {
            this.sqlOperator = sqlOperator;
        }

        public String getSqlOperator() {
            return this.sqlOperator;
        }
    }

    public static class PropositionIdToSqlCode {

        private String propositionId;
        private Object value;

        public PropositionIdToSqlCode(String propositionId, Object value) {
            this.propositionId = propositionId;
            this.value = value;
        }

        public String getPropositionId() {
            return this.propositionId;
        }

        public Object getSqlCode() {
            return this.value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PropositionIdToSqlCode other = (PropositionIdToSqlCode) obj;
            if ((this.propositionId == null) ? (other.propositionId != null) :
                !this.propositionId.equals(other.propositionId)) {
                return false;
            }
            if (this.value != other.value && (this.value == null ||
                    !this.value.equals(other.value))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + (this.propositionId != null ?
                this.propositionId.hashCode() : 0);
            hash = 17 * hash + (this.value != null ?
                this.value.hashCode() : 0);
            return hash;
        }


    }
    private final String schema;
    private final String table;
    private final String column;
    private final JoinSpec joinSpec;
    private Constraint constraint;
    private PropositionIdToSqlCode[] propIdToSqlCodes;

    public ColumnSpec(String schema, String table, JoinSpec joinSpec) {
        this(schema, table, null, joinSpec);
    }

    public ColumnSpec(String schema, String table) {
        this(schema, table, null, null);
    }

    public ColumnSpec(String schema, String table, String column,
            Constraint constraint,
            PropositionIdToSqlCode... propIdToSqlCodes) {
        this(schema, table, column, null);
        this.constraint = constraint;
        this.propIdToSqlCodes = propIdToSqlCodes;
    }

    public ColumnSpec(String schema, String table, String column) {
        this(schema, table, column, null);
    }

    public ColumnSpec(String schema, String table,
            String column, JoinSpec joinSpec) {
        if (table == null) {
            throw new IllegalArgumentException("table cannot be null");
        }
        this.schema = schema;
        this.table = table;
        this.column = column;
        this.joinSpec = joinSpec;
        if (this.joinSpec != null) {
            this.joinSpec.setPrevColumnSpec(this);
        }
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

    public PropositionIdToSqlCode[] getPropositionIdToSqlCodes() {
        return this.propIdToSqlCodes;
    }

    public boolean isSameSchemaAndTable(ColumnSpec columnSpec) {
        return StringUtil.equals(columnSpec.getSchema(), this.getSchema())
                && StringUtil.equals(columnSpec.getTable(), this.getTable());
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
        return "ColumnSpec{" + "schema="
                + schema + "; table=" + table + "; column=" + column + "}";
    }
}
