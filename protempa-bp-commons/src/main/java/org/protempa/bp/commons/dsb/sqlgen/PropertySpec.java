package org.protempa.bp.commons.dsb.sqlgen;

import java.io.Serializable;
import java.util.Map;
import org.protempa.proposition.value.ValueFactory;

/**
 *
 * @author Andrew Post
 */
public final class PropertySpec implements Serializable {
    private final String name;
    private final Map<String, String> codeToPropIdMap;
    private final ColumnSpec codeSpec;
    private final ValueFactory valueType;

    /**
     * Instantiates a property spec.
     *
     * @param codes
     * @param entitySpec
     * @param startTimeOrTimestampSpec
     * @param finishTimeSpec
     * @param propertyValueSpecs
     * @param constraintSpecs
     */
    public PropertySpec(String name,
            Map<String, String> codeToPropIdMap,
            ColumnSpec codeSpec,
            ValueFactory valueType) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        this.name = name;
        this.codeToPropIdMap = codeToPropIdMap;
        this.codeSpec = codeSpec;
        this.valueType = valueType;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, String> getCodeToPropIdMap() {
        return this.codeToPropIdMap;
    }

    public ColumnSpec getCodeSpec() {
        return this.codeSpec;
    }

    public ValueFactory getValueType() {
        return this.valueType;
    }
}
