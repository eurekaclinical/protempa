package org.protempa.bp.commons.dsb.sqlgen;

import java.io.Serializable;
import java.util.Map;
import org.protempa.bp.commons.dsb.PositionParser;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.ValueFactory;

/**
 * Defines a mapping from a proposition to a relational database's physical
 * schema.
 * 
 * @author Andrew Post
 */
public final class PropertySpec implements Serializable {

    private final String[] codes;
    private final EntitySpec entitySpec;
    private final ColumnSpec startTimeOrTimestampSpec;
    private final ColumnSpec finishTimeSpec;
    private final Map<String, ColumnSpec> propertyValueSpecs;
    private final Map<String, String> codeToPropIdMap;
    private final ColumnSpec codeSpec;
    private final ColumnSpec[] constraintSpecs;
    private final ValueFactory valueType;
    private final Granularity granularity;
    private final PositionParser positionParser;

    /**
     * Instantiates a proposition spec.
     * 
     * @param code
     * @param entitySpec
     * @param startTimeOrTimestampSpec
     * @param finishTimeSpec
     * @param propertyValueSpecs
     * @param constraintSpecs
     */
    public PropertySpec(String code, EntitySpec entitySpec,
            ColumnSpec startTimeOrTimestampSpec,
            ColumnSpec finishTimeSpec,
            Map<String, ColumnSpec> propertyValueSpecs,
            Map<String, String> codeToPropIdMap,
            ColumnSpec codeSpec,
            ColumnSpec[] constraintSpecs,
            ValueFactory valueType,
            Granularity granularity,
            PositionParser positionParser) {
        this(new String[] {code}, entitySpec, startTimeOrTimestampSpec,
                finishTimeSpec, propertyValueSpecs, codeToPropIdMap,
                codeSpec, constraintSpecs,
                valueType, granularity, positionParser);
    }

    /**
     * Instantiates a proposition spec.
     *
     * @param codes
     * @param entitySpec
     * @param startTimeOrTimestampSpec
     * @param finishTimeSpec
     * @param propertyValueSpecs
     * @param constraintSpecs
     */
    public PropertySpec(String[] codes, EntitySpec entitySpec,
            ColumnSpec startTimeOrTimestampSpec,
            ColumnSpec finishTimeSpec,
            Map<String, ColumnSpec> propertyValueSpecs,
            Map<String, String> codeToPropIdMap,
            ColumnSpec codeSpec,
            ColumnSpec[] constraintSpecs,
            ValueFactory valueType,
            Granularity granularity,
            PositionParser positionParser) {
        if (codes == null)
            throw new IllegalArgumentException("codes cannot be null");
        if (entitySpec == null)
            throw new IllegalArgumentException("entitySpec cannot be null");
        
        this.codes = new String[codes.length];
        System.arraycopy(codes, 0, this.codes, 0, codes.length);
        this.entitySpec = entitySpec;
        this.startTimeOrTimestampSpec = startTimeOrTimestampSpec;
        this.finishTimeSpec = finishTimeSpec;
        this.propertyValueSpecs = propertyValueSpecs;
        this.codeToPropIdMap = codeToPropIdMap;
        this.codeSpec = codeSpec;
        this.constraintSpecs = constraintSpecs;
        this.valueType = valueType;
        this.granularity = granularity;
        this.positionParser = positionParser;
    }

    /**
     * @return the code
     */
    public String[] getCodes() {
        return this.codes;
    }
    
    public EntitySpec getEntitySpec() {
        return this.entitySpec;
    }

    public ColumnSpec getStartTimeSpec() {
        return this.startTimeOrTimestampSpec;
    }

    public ColumnSpec getFinishTimeSpec() {
        return this.finishTimeSpec;
    }

    public Map<String, ColumnSpec> getPropertyValueSpecs() {
        return this.propertyValueSpecs;
    }

    public Map<String, String> getCodeToPropIdMap() {
        return this.codeToPropIdMap;
    }

    public ColumnSpec getCodeSpec() {
        return this.codeSpec;
    }

    public ColumnSpec[] getConstraintSpecs() {
        return this.constraintSpecs;
    }

    public ValueFactory getValueType() {
        return this.valueType;
    }

    public Granularity getGranularity() {
        return this.granularity;
    }

    public PositionParser getPositionParser() {
        return this.positionParser;
    }

}
