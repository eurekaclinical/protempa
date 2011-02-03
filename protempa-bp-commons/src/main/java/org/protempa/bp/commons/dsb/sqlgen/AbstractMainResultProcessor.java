package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.protempa.bp.commons.dsb.sqlgen.ColumnSpec.PropositionIdToSqlCode;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

abstract class AbstractMainResultProcessor<P extends Proposition>
        extends AbstractResultProcessor implements SQLGenResultProcessor {

    private Map<String, List<P>> results;
    

    final Map<String, List<P>> getResults() {
        return this.results;
    }

    final void setResults(Map<String, List<P>> results) {
        this.results = results;
    }

    final Map<UniqueIdentifier,P> createCache() {
        Map<UniqueIdentifier,P> result = new HashMap<UniqueIdentifier,P>();
        if (this.results != null) {
            for (List<P> props : this.results.values()) {
                for (P prop : props) {
                    result.put(prop.getUniqueIdentifier(), prop);
                }
            }
        }
        return result;
    }

    protected final String sqlCodeToPropositionId(ColumnSpec codeSpec,
            String code)
            throws SQLException {
        PropositionIdToSqlCode[] pidtosqlcodes =
                        codeSpec.getPropositionIdToSqlCodes();
        String propId = null;
        if (pidtosqlcodes.length > 0) {
            for (PropositionIdToSqlCode pidtosqlcode : pidtosqlcodes) {
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
