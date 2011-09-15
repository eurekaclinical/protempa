package org.protempa.bp.commons.dsb.relationaldb;

interface SelectClause extends SqlClause {
    void setCaseClause(CaseClause caseClause);
}