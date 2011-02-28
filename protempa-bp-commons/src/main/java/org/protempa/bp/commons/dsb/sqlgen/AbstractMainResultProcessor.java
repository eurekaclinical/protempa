package org.protempa.bp.commons.dsb.sqlgen;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueFactory;
import org.protempa.proposition.value.ValueType;

abstract class AbstractMainResultProcessor<P extends Proposition> extends
        AbstractResultProcessor implements SQLGenResultProcessor {

    private ResultCache<P> results;

    final ResultCache<P> getResults() {
        return this.results;
    }

    final void setResults(ResultCache<P> resultCache) {
        assert resultCache != null : "resultCache cannot be null";
        this.results = resultCache;
    }

    protected final String sqlCodeToPropositionId(ColumnSpec codeSpec,
            String code) throws SQLException {
        return codeSpec.propositionIdFor(code);
    }

    protected final int extractPropertyValues(PropertySpec[] propertySpecs,
            ResultSet resultSet, int i, Value[] propertyValues)
            throws SQLException {
        for (int j = 0; j < propertySpecs.length; j++) {
            PropertySpec propertySpec = propertySpecs[j];
            ValueType valueType = propertySpec.getValueType();
            Value value = 
                    ValueFactory.get(valueType).parseValue(
                    resultSet.getString(i++));
            propertyValues[j] = value;
        }
        return i;
    }
}
