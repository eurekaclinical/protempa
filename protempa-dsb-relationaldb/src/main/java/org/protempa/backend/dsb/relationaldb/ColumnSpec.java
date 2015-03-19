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
import java.util.List;
import org.arp.javautil.string.StringUtil;
import org.protempa.backend.dsb.relationaldb.mappings.Mappings;

/**
 * Specifies part of a path through relational database tables via joins.
 * Instances of this class may be chained together to form a complete path.
 * 
 * @author Andrew Post
 */
public class ColumnSpec implements Serializable, IColumnSpec {

    private static final long serialVersionUID = 2254623617064935923L;

    private final String schema;
    private final String table;
    private final String column;
    private final JoinSpec joinSpec;
    private final Operator constraint;
    private final Mappings mappings;
    private final ColumnOp columnOp;
    private final boolean propositionIdsComplete;
    private final String expr;
    
    public ColumnSpec(String expr) {
        this.expr = expr;
        this.schema = null;
        this.table = null;
        this.column = null;
        this.joinSpec = null;
        this.constraint = null;
        this.mappings = null;
        this.columnOp = null;
        this.propositionIdsComplete = false;
    }
    
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
     *            a {@link Operator}.
     * @param mappings
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
            Operator constraint,
            Mappings mappings, ColumnOp columnOp,
            boolean propositionIdsComplete) {
        this(schema, table, column, null, constraint, mappings,
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
     *            a {@link Operator}.
     * @param mappings
     *            a {@link KnowledgeSourceIdToSqlCode[]}. An array of mappings
     *            from a proposition id to a value of the specified column. If
     *            <code>constraint</code> is not <code>null</code>, then this
     *            argument cannot be <code>null</code> either.
     */
    public ColumnSpec(String schema, String table, String column,
            Operator constraint,
            Mappings mappings,
            boolean propositionIdsComplete) {
        this(schema, table, column, null, constraint, mappings, null,
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
     *            a {@link Operator}.
     * @param mappings
     *            a {@link KnowledgeSourceIdToSqlCode[]}. An array of mappings
     *            from a proposition id to a value of the specified column. If
     *            <code>constraint</code> is not <code>null</code>, then this
     *            argument cannot be <code>null</code> either.
     */
    public ColumnSpec(String schema, String table, String column,
            Operator constraint, Mappings mappings) {
        this(schema, table, column, null, constraint, mappings, null,
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
     *            a {@link Operator}.
     * @param mappings
     *            a {@link KnowledgeSourceIdToSqlCode[]}. An array of mappings
     *            from a proposition id to a value of the specified column. If
     *            <code>constraint</code> is not <code>null</code>, then this
     *            argument cannot be <code>null</code> either.
     */
    private ColumnSpec(String schema, String table, String column,
            JoinSpec joinSpec, Operator constraint,
            Mappings mappings, ColumnOp columnOp,
            boolean propositionIdsComplete) {
        if (table == null) {
            throw new IllegalArgumentException("table cannot be null");
        }
        if (schema != null) {
            this.schema = schema.intern();
        } else {
            this.schema = null;
        }
        this.table = table.intern();
        this.column = column;
        this.joinSpec = joinSpec;
        this.constraint = constraint;
        if (this.constraint != null && this.column == null) {
            throw new IllegalArgumentException(
                    "A column must be specified if a constraint is specified.");
        }
        if (this.constraint != null && mappings == null) {
            throw new IllegalArgumentException(
                    "mappings must be specified if a constraint is specified.");
        }
        
        this.mappings = mappings;

        if (this.joinSpec != null) {
            this.joinSpec.setPrevColumnSpec(this);
        }
        this.columnOp = columnOp;
        this.propositionIdsComplete = propositionIdsComplete;
        this.expr = null;
    }
    
    public String getExpr() {
        return this.expr;
    }

    /**
     * Gets the schema name.
     * 
     * @return a schema name {@link String}.
     */
    @Override
    public String getSchema() {
        return this.schema;
    }

    /**
     * Gets the table name.
     * 
     * @return a table name {@link String}. Guaranteed not <code>null</code>.
     */
    @Override
    public String getTable() {
        return this.table;
    }

    /**
     * Gets the column name if specified.
     * 
     * @return a column name {@link String}.
     */
    @Override
    public String getColumn() {
        return this.column;
    }

    /**
     * Returns a join specification from the this column spec's table to another
     * table.
     * 
     * @return a {@link JoinSpec}.
     */
    @Override
    public JoinSpec getJoin() {
        return this.joinSpec;
    }

    /**
     * Returns a constraint on this column spec's column.
     * 
     * @return a {@link Operator} or <code>null</code> if none is specified.
     */
    @Override
    public Operator getConstraint() {
        return this.constraint;
    }

    /**
     * Returns the proposition id corresponding to a specified value in this
     * column spec's column.
     * 
     * @param source
     *            the value.
     * @return a proposition id {@link String} or <code>null</code> if a value
     *         is specified that is not in this column spec's mappings from
     *         proposition ids to values ({@link #getMappings() }
     *         ).
     */
    String getTarget(Object source) {
        if (this.mappings != null) {
            return this.mappings.getTarget(source);
        } else {
            return null;
        }
    }

    /**
     * Returns an array of mappings of proposition ids to values of the column
     * of the table of the database.
     * 
     * @return a {@link PropositionIdToSqlCode[]}.
     */
    @Override
    public Mappings getMappings() {
        return this.mappings;
    }

    /**
     * Returns the name of a SQL function to apply to values of this column
     * spec's column prior to attempting to map a value to a proposition id (see
     * {@link #getMappings() }).
     * 
     * @return a {@link ColumnOp}.
     */
    @Override
    public ColumnOp getColumnOp() {
        return this.columnOp;
    }

    /**
     * Indicates whether the codes in the mappings of proposition ids to codes (
     * {@link #getMappings() }) represent all of the unique codes
     * in the database table.
     * 
     * @return <code>true</code> or <code>false</code>.
     */
    @Override
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
    public boolean isSameSchemaAndTable(TableSpec tableSpec) {
        return StringUtil.equals(tableSpec.getSchema(), this.schema)
                && StringUtil.equals(tableSpec.getTable(), this.table);
    }
    
    /**
     * Gets the last column spec in the chain of column specs and joins.
     * 
     * @return a {@link ColumnSpec}
     */
    public ColumnSpec getLastSpec() {
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
