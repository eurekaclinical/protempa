package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueFormat;
import org.protempa.proposition.value.ValueType;

abstract class AbstractMainResultProcessor<P extends Proposition> extends AbstractResultProcessor implements SQLGenResultProcessor {

    private ResultCache<P> results;

    final ResultCache<P> getResults() {
        return this.results;
    }

    final void setResults(ResultCache<P> resultCache) {
        assert resultCache != null : "resultCache cannot be null";
        this.results = resultCache;
    }

    protected static String sqlCodeToPropositionId(ColumnSpec codeSpec,
            String code) throws SQLException {
        return codeSpec.propositionIdFor(code);
    }

    protected static int extractPropertyValues(PropertySpec[] propertySpecs,
            ResultSet resultSet, int i, Value[] propertyValues)
            throws SQLException {
        for (int j = 0; j < propertySpecs.length; j++) {
            PropertySpec propertySpec = propertySpecs[j];
            ColumnSpec cs = propertySpec.getSpec();
            if (cs != null) {
                List<ColumnSpec> codeSpecL = cs.asList();
                cs = codeSpecL.get(codeSpecL.size() - 1);
            }
            ValueType valueType = propertySpec.getValueType();
            String valAsString = resultSet.getString(i++);
            String propId = cs.propositionIdFor(valAsString);
            if (propId != null) {
                valAsString = propId;
            }
            Value value = ValueFormat.parse(valAsString, valueType);
            propertyValues[j] = value;
        }
        return i;
    }
}
