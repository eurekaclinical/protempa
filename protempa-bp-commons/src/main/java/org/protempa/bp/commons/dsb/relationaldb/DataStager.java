package org.protempa.bp.commons.dsb.relationaldb;

interface DataStager {

    /**
     * Builds staging areas for SQL tables.
     */
    public void stageTables();
    
    /**
     * Drops staging tables created by <tt>stageTables</tt>.
     */
    public void dropTables();
}
