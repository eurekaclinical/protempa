package org.protempa.bp.commons.dsb.relationaldb;

import java.sql.SQLException;

interface DataStager {

    /**
     * Builds staging areas for SQL tables.
     * 
     * @throws SQLException if an error occurs
     */
    public void stageTables() throws SQLException;
    
    /**
     * Drops staging tables created by <tt>stageTables</tt>.
     * 
     * @throws SQLException if an error occurs
     */
    public void cleanup() throws SQLException;
}
