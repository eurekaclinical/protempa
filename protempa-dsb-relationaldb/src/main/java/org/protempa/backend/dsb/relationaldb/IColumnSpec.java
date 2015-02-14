package org.protempa.backend.dsb.relationaldb;

import org.protempa.backend.dsb.relationaldb.mappings.Mappings;

/*
 * #%L
 * Protempa Relational Database Data Source Backend
 * %%
 * Copyright (C) 2012 - 2014 Emory University
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

/**
 *
 * @author Andrew Post
 */
public interface IColumnSpec {

    /**
     * Gets the column name if specified.
     *
     * @return a column name {@link String}.
     */
    String getColumn();

    /**
     * Returns the name of a SQL function to apply to values of this column
     * spec's column prior to attempting to map a value to a proposition id (see
     * {@link #getMappings() }).
     *
     * @return a {@link ColumnOp}.
     */
    ColumnOp getColumnOp();

    /**
     * Returns a constraint on this column spec's column.
     *
     * @return a {@link Operator} or <code>null</code> if none is specified.
     */
    Operator getConstraint();

    /**
     * Returns a join specification from the this column spec's table to another
     * table.
     *
     * @return a {@link JoinSpec}.
     */
    JoinSpec getJoin();

    /**
     * Returns an array of mappings of proposition ids to values of the column
     * of the table of the database.
     *
     * @return a {@link PropositionIdToSqlCode[]}.
     */
    Mappings getMappings();

    /**
     * Gets the schema name.
     *
     * @return a schema name {@link String}.
     */
    String getSchema();

    /**
     * Gets the table name.
     *
     * @return a table name {@link String}. Guaranteed not <code>null</code>.
     */
    String getTable();

    /**
     * Indicates whether the codes in the mappings of proposition ids to codes (
     * {@link #getMappings() }) represent all of the unique codes
     * in the database table.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    boolean isPropositionIdsComplete();

}
