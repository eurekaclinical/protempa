package org.protempa.backend.dsb.relationaldb;

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
     * {@link #getPropositionIdToSqlCodes() }).
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
    KnowledgeSourceIdToSqlCode[] getPropositionIdToSqlCodes();

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
     * {@link #getPropositionIdToSqlCodes() }) represent all of the unique codes
     * in the database table.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    boolean isPropositionIdsComplete();

}
