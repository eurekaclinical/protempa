package org.protempa.backend.dsb.relationaldb;

/**
 * SQL comparison operators.
 * 
 * @author Andrew Post
 */
public enum Operator {
    EQUAL_TO("="), LIKE("LIKE"), GREATER_THAN(">"), GREATER_THAN_OR_EQUAL_TO(">="), LESS_THAN("<"), LESS_THAN_OR_EQUAL_TO("<="), NOT_EQUAL_TO("<>");
    private String sqlOperator;

    private Operator(String sqlOperator) {
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
