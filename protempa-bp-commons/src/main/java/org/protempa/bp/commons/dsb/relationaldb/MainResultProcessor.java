package org.protempa.bp.commons.dsb.relationaldb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueType;

abstract class MainResultProcessor<P extends Proposition>
        extends AbstractResultProcessor {

    private ResultCache<P> results;
    private ColumnSpec[] lastColumnSpecs;
    private PropertySpec[] propertySpecs;
    
    protected MainResultProcessor(ResultCache<P> results,
            EntitySpec entitySpec, String dataSourceBackendId) {
        super(entitySpec, dataSourceBackendId);
        assert results != null : "resultCache cannot be null";
        this.results = results;
        this.propertySpecs = getEntitySpec().getPropertySpecs();
        this.lastColumnSpecs = new ColumnSpec[this.propertySpecs.length];
    }
    
    final ResultCache<P> getResults() {
        return this.results;
    }

    protected static String sqlCodeToPropositionId(ColumnSpec codeSpec,
            String code) throws SQLException {
        return codeSpec.propositionIdFor(code);
    }

    protected int extractPropertyValues(ResultSet resultSet, int i, 
            Value[] propertyValues, int[] colTypes) throws SQLException {
        for (int j = 0; j < this.propertySpecs.length; j++) {
            PropertySpec propertySpec = this.propertySpecs[j];
            ValueType valueType = propertySpec.getValueType();
            JDBCValueFormat valueFormat = propertySpec.getJDBCValueFormat();
            Value value;
            if (valueFormat != null) {
                value = valueFormat.toValue(resultSet, i, colTypes[i - 1]);
            } else {
                ColumnSpec columnSpec = this.lastColumnSpecs[j];
                if (columnSpec == null) {
                    ColumnSpec cs = propertySpec.getSpec();
                    List<ColumnSpec> codeSpecL = cs.asList();
                    columnSpec = codeSpecL.get(codeSpecL.size() - 1);
                    this.lastColumnSpecs[j] = columnSpec;
                }
                String valAsString = resultSet.getString(i);
                String propId = columnSpec.propositionIdFor(valAsString);
                if (propId != null) {
                    valAsString = propId;
                }
                value = valueType.parse(valAsString);
            }
            i++;
            propertyValues[j] = value;
        }
        return i;
    }
}
