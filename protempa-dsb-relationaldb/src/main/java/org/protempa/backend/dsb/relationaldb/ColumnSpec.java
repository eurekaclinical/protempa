/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.backend.dsb.relationaldb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.arp.javautil.string.StringUtil;
import org.protempa.ProtempaUtil;

/**
 * Specifies part of a path through relational database tables via joins.
 * Instances of this class may be chained together to form a complete path.
 * 
 * @author Andrew Post
 */
public final class ColumnSpec implements Serializable {

    private static final long serialVersionUID = 2254623617064935923L;

    private static KnowledgeSourceIdToSqlCode[] EMPTY_KNOWLEDGE_SOURCE_ID_TO_SQL_CODE_ARRAY = new KnowledgeSourceIdToSqlCode[0];

    public static enum ColumnOp {

        UPPER
    }

    /**
     * SQL comparison operators.
     */
    public static enum Constraint {

        EQUAL_TO("="), LIKE("LIKE"), GREATER_THAN(">"), GREATER_THAN_OR_EQUAL_TO(
                ">="), LESS_THAN("<"), LESS_THAN_OR_EQUAL_TO("<="), NOT_EQUAL_TO(
                "<>");
        private String sqlOperator;

        private Constraint(String sqlOperator) {
            this.sqlOperator = sqlOperator;
        }

        /**
         * Gets the {@link String} operator.
         * 
         * @return
         */
        public String getSqlOperator() {
            return this.sqlOperator;
        }
    }

    /**
     * Represents a mapping from a proposition id to a value of the column
     * specified by a column spec. Pass an array of instances of this class into
     * a {@link ColumnSpec}'s constructor.
     */
    public static class KnowledgeSourceIdToSqlCode {

        private final String propositionId;
        private final Object sqlCode;

        /**
         * Instantiates a mapping between a proposition id and the value of the
         * column specified by a column spec.
         * 
         * @param propositionId
         *            a proposition id {@link String}. Cannot be
         *            <code>null</code>.
         * @param sqlCode
         *            a value {@link Object} in a column in a table in a
         *            database. Cannot be <code>null</code>.
         */
        public KnowledgeSourceIdToSqlCode(String propositionId, Object sqlCode) {
            if (propositionId == null) {
                throw new IllegalArgumentException(
                        "propositionId cannot be null");
            }
            if (sqlCode == null) {
                throw new IllegalArgumentException("sqlCode cannot be null");
            }
            this.propositionId = propositionId.intern();
            this.sqlCode = sqlCode;
        }

        /**
         * Returns the proposition id in the mapping.
         * 
         * @return a proposition id {@link String}. Guaranteed not
         *         <code>null</code>.
         */
        public String getPropositionId() {
            return this.propositionId;
        }

        /**
         * Returns the value {@link Object} in the mapping.
         * 
         * @return a code {@link Object} in a relational database. Guaranteed
         *         not <code>null</code>.
         */
        public Object getSqlCode() {
            return this.sqlCode;
        }

        /**
         * Compares the proposition id and value for equality.
         * 
         * @param obj
         *            another {@link Object}.
         * @return <code>true</code> if the proposition ids and values are
         *         equal, <code>false</code> otherwise.
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final KnowledgeSourceIdToSqlCode other = (KnowledgeSourceIdToSqlCode) obj;
            if (!this.propositionId.equals(other.propositionId)) {
                return false;
            }
            if (!this.sqlCode.equals(other.sqlCode)) {
                return false;
            }
            return true;
        }

        /**
         * Generates a hash from the proposition id and value.
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

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    private final String schema;
    private final String table;
    private final String column;
    private final JoinSpec joinSpec;
    private final Constraint constraint;
    private final KnowledgeSourceIdToSqlCode[] propIdToSqlCodes;
    private final Map<Object, String> propIdForSqlCode;
    private final ColumnOp columnOp;
    private final boolean propositionIdsComplete;

    /**
     * Instantiates part of a path using a schema, table and join.
     * 
     * @param schema
     *            a schema {@link String}, if the underlying database requires
     *            it (e.g., Oracle).
     * @param table
     *            a table {@link String}. Cannot be <code>null</code>.
     * @param joinSpec
     *            a {@link JoinSpec}.
     */
    public ColumnSpec(String schema, String table, JoinSpec joinSpec) {
        this(schema, table, null, joinSpec);
    }

    /**
     * Instantiates part of a path using a schema and table. This spec ends a
     * path as it has no join.
     * 
     * @param schema
     *            a schema {@link String}, if the underlying database requires
     *            it (e.g., Oracle).
     * @param table
     *            a table {@link String}. Cannot be <code>null</code>.
     */
    public ColumnSpec(String schema, String table) {
        this(schema, table, null, null);
    }

    /**
     * Specifies part of path in which the column has one or more constraints on
     * its value. This constructor may only used for the last part of a path.
     * 
     * @param schema
     *            a schema {@link String}, if the underlying database requires
     *            it (e.g., Oracle).
     * @param table
     *            a table {@link String}. Cannot be <code>null</code>.
     * @param column
     *            a column {@link String}. Cannot be <code>null</code> unless
     *            <code>constraint</code> is also <code>null</code>.
     * @param constraint
     *            a {@link Constraint}.
     * @param propIdToSqlCodes
     *            a {@link KnowledgeSourceIdToSqlCode[]}. An array of mappings
     *            from a proposition id to a value of the specified column. If
     *            <code>constraint</code> is not <code>null</code>, then this
     *            argument cannot be <code>null</code> either.
     * @param columnOp
     *            a {@link ColumnOp} (optional).
     * @param propositionIdsComplete
     *            whether the values in <code>propIdToSqlCodes</code> represent
     *            all of the unique values of the specified column. This serves
     *            as an optimization hint to the SQL generator to avoid the need
     *            for a long IN clause when the list of values in the clause is
     *            known to be almost the same as the list of unique values in
     *            the column.
     */
    public ColumnSpec(String schema, String table, String column,
            Constraint constraint,
            KnowledgeSourceIdToSqlCode[] propIdToSqlCodes, ColumnOp columnOp,
            boolean propositionIdsComplete) {
        this(schema, table, column, null, constraint, propIdToSqlCodes,
                columnOp, propositionIdsComplete);
    }

    /**
     * Specifies part of path in which the column has one or more constraints on
     * its value. This constructor may only used for the last part of a path.
     * 
     * @param schema
     *            a schema {@link String}, if the underlying database requires
     *            it (e.g., Oracle).
     * @param table
     *            a table {@link String}. Cannot be <code>null</code>.
     * @param column
     *            a column {@link String}. Cannot be <code>null</code> unless
     *            <code>constraint</code> is also <code>null</code>.
     * @param constraint
     *            a {@link Constraint}.
     * @param propIdToSqlCodes
     *            a {@link KnowledgeSourceIdToSqlCode[]}. An array of mappings
     *            from a proposition id to a value of the specified column. If
     *            <code>constraint</code> is not <code>null</code>, then this
     *            argument cannot be <code>null</code> either.
     */
    public ColumnSpec(String schema, String table, String column,
            Constraint constraint,
            KnowledgeSourceIdToSqlCode[] propIdToSqlCodes,
            boolean propositionIdsComplete) {
        this(schema, table, column, null, constraint, propIdToSqlCodes, null,
                propositionIdsComplete);
    }

    /**
     * Specifies part of path in which the column has one or more constraints on
     * its value. This constructor may only used for the last part of a path.
     * 
     * @param schema
     *            a schema {@link String}, if the underlying database requires
     *            it (e.g., Oracle).
     * @param table
     *            a table {@link String}. Cannot be <code>null</code>.
     * @param column
     *            a column {@link String}. Cannot be <code>null</code> unless
     *            <code>constraint</code> is also <code>null</code>.
     * @param constraint
     *            a {@link Constraint}.
     * @param propIdToSqlCodes
     *            a {@link KnowledgeSourceIdToSqlCode[]}. An array of mappings
     *            from a proposition id to a value of the specified column. If
     *            <code>constraint</code> is not <code>null</code>, then this
     *            argument cannot be <code>null</code> either.
     */
    public ColumnSpec(String schema, String table, String column,
            Constraint constraint, KnowledgeSourceIdToSqlCode[] propIdToSqlCodes) {
        this(schema, table, column, null, constraint, propIdToSqlCodes, null,
                false);
    }

    /**
     * Instantiates part of a path using a schema, table and column. This spec
     * ends a path as it has no join.
     * 
     * @param schema
     *            a schema {@link String}, if the underlying database requires
     *            it (e.g., Oracle).
     * @param table
     *            a table {@link String}. Cannot be <code>null</code>.
     * @param column
     *            a column {@link String}.
     */
    public ColumnSpec(String schema, String table, String column) {
        this(schema, table, column, null);
    }

    /**
     * Instantiates part of a path using a schema table, column and join
     * specification. The column will be ignored unless it is the first part of
     * a base path specification, in which its values are used as key ids.
     * 
     * @param schema
     *            a schema {@link String}.
     * @param table
     *            a table {@link String}. Cannot be <code>null</code>.
     * @param column
     *            a column {@link String}.
     * @param joinSpec
     *            a {@link JoinSpec}.
     */
    public ColumnSpec(String schema, String table, String column,
            JoinSpec joinSpec) {
        this(schema, table, column, joinSpec, null, null, null, false);
    }

    /**
     * Instantiates part of a path using a schema, table, column, join
     * specification, constraint and mappings from proposition ids to codes.
     * 
     * @param schema
     *            a schema {@link String}, if the underlying database requires
     *            it (e.g., Oracle).
     * @param table
     *            a table {@link String}. Cannot be <code>null</code>.
     * @param column
     *            a column {@link String}. Cannot be <code>null</code> unless
     *            <code>constraint</code> is also <code>null</code>.
     * @param joinSpec
     *            a {@link JoinSpec}.
     * @param constraint
     *            a {@link Constraint}.
     * @param propIdToSqlCodes
     *            a {@link KnowledgeSourceIdToSqlCode[]}. An array of mappings
     *            from a proposition id to a value of the specified column. If
     *            <code>constraint</code> is not <code>null</code>, then this
     *            argument cannot be <code>null</code> either.
     */
    private ColumnSpec(String schema, String table, String column,
            JoinSpec joinSpec, Constraint constraint,
            KnowledgeSourceIdToSqlCode[] propIdToSqlCodes, ColumnOp columnOp,
            boolean propositionIdsComplete) {
        if (table == null) {
            throw new IllegalArgumentException("table cannot be null");
        }
        this.schema = schema.intern();
        this.table = table.intern();
        this.column = column;
        this.joinSpec = joinSpec;
        this.constraint = constraint;
        if (this.constraint != null && this.column == null) {
            throw new IllegalArgumentException(
                    "A column must be specified if a constraint is specified.");
        }
        if (this.constraint != null && propIdToSqlCodes == null) {
            throw new IllegalArgumentException(
                    "propIdToSqlCodes must be specified if a constraint is specified.");
        }
        if (propIdToSqlCodes != null) {
            this.propIdToSqlCodes = propIdToSqlCodes.clone();
            ProtempaUtil.checkArrayForNullElement(this.propIdToSqlCodes,
                    "propIdToSqlCodes");
        } else {
            this.propIdToSqlCodes = EMPTY_KNOWLEDGE_SOURCE_ID_TO_SQL_CODE_ARRAY;
        }
        this.propIdForSqlCode = new HashMap<>();
        for (KnowledgeSourceIdToSqlCode k : this.propIdToSqlCodes) {
            propIdForSqlCode.put(k.sqlCode, k.propositionId);
        }

        if (this.joinSpec != null) {
            this.joinSpec.setPrevColumnSpec(this);
        }
        this.columnOp = columnOp;
        this.propositionIdsComplete = propositionIdsComplete;
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
     * @return a table name {@link String}. Guaranteed not <code>null</code>.
     */
    public String getTable() {
        return this.table;
    }

    /**
     * Gets the column name if specified.
     * 
     * @return a column name {@link String}.
     */
    public String getColumn() {
        return this.column;
    }

    /**
     * Returns a join specification from the this column spec's table to another
     * table.
     * 
     * @return a {@link JoinSpec}.
     */
    public JoinSpec getJoin() {
        return this.joinSpec;
    }

    /**
     * Returns a constraint on this column spec's column.
     * 
     * @return a {@link Constraint} or <code>null</code> if none is specified.
     */
    public Constraint getConstraint() {
        return this.constraint;
    }

    /**
     * Returns the proposition id corresponding to a specified value in this
     * column spec's column.
     * 
     * @param sqlCode
     *            the value.
     * @return a proposition id {@link String} or <code>null</code> if a value
     *         is specified that is not in this column spec's mappings from
     *         proposition ids to values ({@link #getPropositionIdToSqlCodes() }
     *         ).
     */
    String propositionIdFor(String sqlCode) {
        return this.propIdForSqlCode.get(sqlCode);
    }

    /**
     * Returns an array of mappings of proposition ids to values of the column
     * of the table of the database.
     * 
     * @return a {@link PropositionIdToSqlCode[]}.
     */
    public KnowledgeSourceIdToSqlCode[] getPropositionIdToSqlCodes() {
        return this.propIdToSqlCodes.clone();
    }

    /**
     * Returns the name of a SQL function to apply to values of this column
     * spec's column prior to attempting to map a value to a proposition id (see
     * {@link #getPropositionIdToSqlCodes() }).
     * 
     * @return a {@link ColumnOp}.
     */
    public ColumnOp getColumnOp() {
        return this.columnOp;
    }

    /**
     * Indicates whether the codes in the mappings of proposition ids to codes (
     * {@link #getPropositionIdToSqlCodes() }) represent all of the unique codes
     * in the database table.
     * 
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean isPropositionIdsComplete() {
        return this.propositionIdsComplete;
    }

    /**
     * Returns whether the given column specification has the same schema and
     * table as this one.
     * 
     * @param columnSpec
     *            a {@link ColumnSpec}.
     * @return <code>true</code> if the given column specification has the same
     *         schema and table as this one, <code>false</code> if not.
     */
    boolean isSameSchemaAndTable(ColumnSpec columnSpec) {
        return StringUtil.equals(columnSpec.schema, this.schema)
                && StringUtil.equals(columnSpec.table, this.table);
    }

    /**
     * Returns whether the given column specification has the same schema and
     * table as this one.
     * 
     * @param tableSpec
     *            a {@link TableSpec}.
     * @return <code>true</code> if the given table specification has the same
     *         schema and table as this one, <code>false</code> if not.
     */
    boolean isSameSchemaAndTable(TableSpec tableSpec) {
        return StringUtil.equals(tableSpec.getSchema(), this.schema)
                && StringUtil.equals(tableSpec.getTable(), this.table);
    }

    /**
     * Returns whether the specified column spec has a join {@link #getJoin() })
     * that uses the same columns as this one.
     * 
     * @param columnSpec
     *            the {@link ColumnSpec} to compare.
     * @return <code>true</code> or <code>false</code>.
     */
    boolean isSameJoin(ColumnSpec columnSpec) {
        JoinSpec otherJoinSpec = columnSpec.getJoin();
        if (this.joinSpec == null) {
            return otherJoinSpec == null;
        } else if (otherJoinSpec == null) {
            return false;
        } else {
            String joinSpecFromKey = this.joinSpec.getFromKey();
            String joinSpecToKey = this.joinSpec.getToKey();
            String otherJoinSpecFromKey = otherJoinSpec.getFromKey();
            String otherJoinSpecToKey = otherJoinSpec.getToKey();
            return joinSpecFromKey.equals(otherJoinSpecFromKey)
                    && joinSpecToKey.equals(otherJoinSpecToKey)
                    && this.joinSpec.getNextColumnSpec().isSameSchemaAndTable(
                            otherJoinSpec.getNextColumnSpec());
        }
    }
    
    /**
     * Gets the last column spec in the chain of column specs and joins.
     * 
     * @return a {@link ColumnSpec}
     */
    ColumnSpec getLastSpec() {
        if (this.joinSpec == null) {
            return this;
        } else {
            List<ColumnSpec> l = asList();
            return l.get(l.size() - 1);
        }
    }

    /**
     * Returns a list of column specs that are chained to this one via joins
     * (including this column spec). This is a convenience method for use with
     * collections.
     * 
     * @return a {@link List<ColumnSpec>}.
     */
    List<ColumnSpec> asList() {
        List<ColumnSpec> columnSpecs = new ArrayList<>();
        ColumnSpec spec = this;
        columnSpecs.add(spec);
        while (spec.getJoin() != null) {
            spec = spec.getJoin().getNextColumnSpec();
            columnSpecs.add(spec);
        }
        return columnSpecs;
    }

    @Override
    public String toString() {
//        return ToStringBuilder.reflectionToString(this);
        String result = "";
        for (ColumnSpec spec : asList()) {
            result += spec.getSchema() + "." + spec.getTable() + "." + spec.getColumn() + " -> ";
        }
        
        return result;
    }
}
