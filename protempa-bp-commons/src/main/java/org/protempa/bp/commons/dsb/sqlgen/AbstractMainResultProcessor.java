package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.SQLException;

import org.protempa.bp.commons.dsb.sqlgen.ColumnSpec.KnowledgeSourceIdToSqlCode;
import org.protempa.proposition.Proposition;

abstract class AbstractMainResultProcessor<P extends Proposition> extends
        AbstractResultProcessor implements SQLGenResultProcessor {

    ResultCache<P> results;

    final ResultCache<P> getResults() {
        return this.results;
    }

    final void setResults(ResultCache<P> resultCache) {
        this.results = resultCache;
    }

    protected final String sqlCodeToPropositionId(ColumnSpec codeSpec,
            String code) throws SQLException {
        KnowledgeSourceIdToSqlCode[] pidtosqlcodes = codeSpec
                .getPropositionIdToSqlCodes();
        String propId = null;
        if (pidtosqlcodes.length > 0) {
            for (KnowledgeSourceIdToSqlCode pidtosqlcode : pidtosqlcodes) {
                if (pidtosqlcode.getSqlCode().equals(code)) {
                    propId = pidtosqlcode.getPropositionId();
                    break;
                }
            }
            if (propId == null) {
                throw new SQLException("Unexpected SQL code: " + code);
            }
        } else {
            propId = code;
        }
        return propId;
    }
}
