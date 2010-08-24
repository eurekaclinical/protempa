package org.protempa.bp.commons.dsb.sqlgen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.arp.javautil.string.StringUtil;
import org.protempa.ProtempaUtil;

/**
 * Specifies part of a path through relational database tables via joins.
 * Instances of this class may be chained together to form a complete path.
 *
 * @author Andrew Post
 */
public final class ColumnSpec implements Serializable {

    /**
     * SQL comparison operators.
     */
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

        /**
         * Gets the {@link String} operator.
         * @return
         */
        public String getSqlOperator() {
            return this.sqlOperator;
        }
    }

    /**
     * A container for a one-to-one mapping from a proposition id to a
     * database code.
     */
    public static class PropositionIdToSqlCode {

        private String propositionId;
        private Object sqlCode;

        /**
         * Instantiates a mapping between a proposition id and a code in a
         * relational database.
         * 
         * @param propositionId a proposition id {@link String}.
         * @param sqlCode a code {@link Object} in a relational database.
         */
        public PropositionIdToSqlCode(String propositionId, Object sqlCode) {
            if (propositionId == null)
                throw new IllegalArgumentException(
                        "propositionId cannot be null");
            if (sqlCode == null)
                throw new IllegalArgumentException("sqlCode cannot be null");
            this.propositionId = propositionId;
            this.sqlCode = sqlCode;
        }

        /**
         * Returns the proposition id in the mapping.
         *
         * @return a proposition id {@link String}.
         */
        public String getPropositionId() {
            return this.propositionId;
        }

        /**
         * Returns the code {@link String} in the mapping.
         *
         * @return a code {@link Object} in a relational database.
         */
        public Object getSqlCode() {
            return this.sqlCode;
        }

        /**
         * Compares the proposition id and code for equality.
         *
         * @param obj another {@link Object}.
         * @return <code>true</code> if the proposition ids and codes are
         * equal, <code>false</code> otherwise.
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PropositionIdToSqlCode other = (PropositionIdToSqlCode) obj;
            if (!this.propositionId.equals(other.propositionId)) {
                return false;
            }
            if (!this.sqlCode.equals(other.sqlCode)) {
                return false;
            }
            return true;
        }

        /**
         * Generates a hash from the proposition id and code.
         *
         * @return an <code>int</code> hash.
         */
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + this.propositionId.hashCode();
            hash = 17 * hash + this.sqlCode.hashCode();
            return hash;
        }


    }
    
    private final String schema;
    private final String table;
    private final String column;
    private final JoinSpec joinSpec;
    private final Constraint constraint;
    private final PropositionIdToSqlCode[] propIdToSqlCodes;

    /**
     * Instantiates part of a path using a schema, table and join.
     * 
     * @param schema a schema {@link String}, if the underlying database
     * requires it (e.g., Oracle).
     * @param table a table {@link String}. Cannot be <code>null</code>.
     * @param joinSpec a {@link JoinSpec}.
     */
    public ColumnSpec(String schema, String table, JoinSpec joinSpec) {
        this(schema, table, null, joinSpec);
    }

    /**
     * Instantiates part of a path using a schema and table. This spec
     * ends a path as it has no join.
     *
     * @param schema a schema {@link String}, if the underlying database
     * requires it (e.g., Oracle).
     * @param table a table {@link String}. Cannot be <code>null</code>.
     */
    public ColumnSpec(String schema, String table) {
        this(schema, table, null, null);
    }

    /**
     * Specifies part of path in which the column has one or more constraints
     * on its value. This constructor may only used for the last part of a
     * path.
     * 
     * @param schema a schema {@link String}, if the underlying database
     * requires it (e.g., Oracle).
     * @param table a table {@link String}. Cannot be <code>null</code>.
     * @param column a column {@link String}. Cannot be <code>null</code>
     * unless <code>constraint</code> is also <code>null</code>.
     * @param constraint a {@link Constraint}.
     * @param propIdToSqlCodes a {@link PropositionIdToSqlCode...}. If
     * <code>constraint</code> is not <code>null</code>, then this argument
     * cannot be <code>null</code> either.
     */
    public ColumnSpec(String schema, String table, String column,
            Constraint constraint,
            PropositionIdToSqlCode... propIdToSqlCodes) {
        this(schema, table, column, null, constraint, propIdToSqlCodes);
    }

    /**
     * Instantiates part of a path using a schema, table and column. This spec
     * ends a path as it has no join.
     *
     * @param schema a schema {@link String}, if the underlying database
     * requires it (e.g., Oracle).
     * @param table a table {@link String}. Cannot be <code>null</code>.
     * @param column a column {@link String}.
     */
    public ColumnSpec(String schema, String table, String column) {
        this(schema, table, column, null);
    }

    /**
     * Instantiates part of a path using a schema table, column and join
     * specification. The column will be ignored unless it is the first
     * part of a base path specification, in which its values are used as
     * key ids.
     *
     * @param schema a schema {@link String}.
     * @param table a table {@link String}. Cannot be <code>null</code>.
     * @param column a column {@link String}.
     * @param joinSpec a {@link JoinSpec}.
     */
    public ColumnSpec(String schema, String table,
            String column, JoinSpec joinSpec) {
        this(schema, table, column, joinSpec, null);
    }

    /**
     * Instantiates part of a path using a schema, table, column, join
     * specification, constraint and mappings from proposition ids to codes.
     *
     * @param schema a schema {@link String}.
     * @param table a table {@link String}. Cannot be <code>null</code>.
     * @param column a column {@link String}.
     * @param joinSpec a {@link JoinSpec}.
     * @param constraint a {@link Constraint}.
     * @param propIdToSqlCodes a {@lnk PropositionIdToSqlCode[]}.
     */
    private ColumnSpec(String schema, String table,
            String column, JoinSpec joinSpec, Constraint constraint,
            PropositionIdToSqlCode... propIdToSqlCodes) {
        if (table == null) {
            throw new IllegalArgumentException("table cannot be null");
        }
        this.schema = schema;
        this.table = table;
        this.column = column;
        this.joinSpec = joinSpec;
        this.constraint = constraint;
        if (this.constraint != null && this.column == null)
            throw new IllegalArgumentException(
                "A column must be specified if a constraint is specified.");
        if (this.constraint != null && propIdToSqlCodes == null)
          throw new IllegalArgumentException(
           "propIdToSqlCodes must be specified if a constraint is specified.");
        if (propIdToSqlCodes != null) {
            this.propIdToSqlCodes = new PropositionIdToSqlCode[
                    propIdToSqlCodes.length];
            System.arraycopy(propIdToSqlCodes, 0, this.propIdToSqlCodes, 0,
                    propIdToSqlCodes.length);
            ProtempaUtil.checkArrayForNullElement(this.propIdToSqlCodes,
                    "propIdToSqlCodes");
        } else {
            this.propIdToSqlCodes = new PropositionIdToSqlCode[0];
        }
        if (this.joinSpec != null) {
            this.joinSpec.setPrevColumnSpec(this);
        }
    }

    /**
     * Gets the schema name.
     *
     * @return a schema name {@link String}.
     */
    public String getSchema() {
        return this.schema;
    }

    /**
     * Gets the table name.
     *
     * @return a table name {@link String}.
     */
    public String getTable() {
        return this.table;
    }

    /**
     * Gets the column name if specified in this instance.
     *
     * @return a column name {@link String}.
     */
    public String getColumn() {
        return this.column;
    }

    /**
     * Returns a join specification, if this instance has one.
     *
     * @return a {@link JoinSpec}.
     */
    public JoinSpec getJoin() {
        return this.joinSpec;
    }

    /**
     * Returns a constraint, if this instance has one.
     *
     * @return a {@link Constraint}.
     */
    public Constraint getConstraint() {
        return this.constraint;
    }

    /**
     * Returns an array of mappings of proposition ids to codes in the
     * underlying database, if this instance has one.
     *
     * @return a {@link PropositionIdToSqlCode[]}.
     */
    public PropositionIdToSqlCode[] getPropositionIdToSqlCodes() {
        return this.propIdToSqlCodes;
    }

    /**
     * Returns whether the given column specification has the same schema
     * and table as this one.
     *
     * @param columnSpec a {@link ColumnSpec}.
     * @return <code>true</code> if the given column specification has the
     * same schema and table as this one, <code>false</code> if not.
     */
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
