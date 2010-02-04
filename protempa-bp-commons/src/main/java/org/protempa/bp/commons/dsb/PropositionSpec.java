package org.protempa.bp.commons.dsb;

public class PropositionSpec {

    private String code;
    private TableSpec table;

    public PropositionSpec() {
    }

    public PropositionSpec(String code, TableSpec table) {
        this.code = code;
        this.table = table;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code
     *            the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the table
     */
    public TableSpec getTable() {
        return table;
    }

    /**
     * @param table
     *            the table to set
     */
    public void setTable(TableSpec table) {
        this.table = table;
    }
}
