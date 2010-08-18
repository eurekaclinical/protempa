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
public final class EntitySpec implements Serializable {

    private final String[] codes;
    private final boolean unique;
    private final ColumnSpec baseSpec;
    private final ColumnSpec startTimeOrTimestampSpec;
    private final ColumnSpec finishTimeSpec;
    private final PropertySpec[] propertySpecs;
    private final Map<String, String> codeToPropIdMap;
    private final ColumnSpec codeSpec;
    private final ColumnSpec[] constraintSpecs;
    private final ValueFactory valueType;
    private final Granularity granularity;
    private final PositionParser positionParser;

    /**
     * Instantiates a property spec.
     * 
     * @param code
     * @param entitySpec
     * @param startTimeOrTimestampSpec
     * @param finishTimeSpec
     * @param propertyValueSpecs
     * @param constraintSpecs
     */
    public EntitySpec(String code,
            boolean unique,
            ColumnSpec baseSpec,
            ColumnSpec startTimeOrTimestampSpec,
            ColumnSpec finishTimeSpec,
            PropertySpec[] propertySpecs,
            Map<String, String> codeToPropIdMap,
            ColumnSpec codeSpec,
            ColumnSpec[] constraintSpecs,
            ValueFactory valueType,
            Granularity granularity,
            PositionParser positionParser) {
        this(new String[] {code}, unique, baseSpec, startTimeOrTimestampSpec,
                finishTimeSpec, propertySpecs, codeToPropIdMap,
                codeSpec, constraintSpecs,
                valueType, granularity, positionParser);
    }

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
    public EntitySpec(String[] codes,
            boolean unique,
            ColumnSpec baseSpec,
            ColumnSpec startTimeOrTimestampSpec,
            ColumnSpec finishTimeSpec,
            PropertySpec[] propertySpecs,
            Map<String, String> codeToPropIdMap,
            ColumnSpec codeSpec,
            ColumnSpec[] constraintSpecs,
            ValueFactory valueType,
            Granularity granularity,
            PositionParser positionParser) {
        if (codes == null)
            throw new IllegalArgumentException("codes cannot be null");
        if (baseSpec == null)
            throw new IllegalArgumentException("baseSpec cannot be null");
        
        this.codes = new String[codes.length];
        this.unique = unique;
        System.arraycopy(codes, 0, this.codes, 0, codes.length);
        this.baseSpec = baseSpec;
        this.startTimeOrTimestampSpec = startTimeOrTimestampSpec;
        this.finishTimeSpec = finishTimeSpec;
        this.propertySpecs = propertySpecs;
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

    public boolean isUnique() {
        return this.unique;
    }

    public ColumnSpec getBaseSpec() {
        return this.baseSpec;
    }

    public ColumnSpec getStartTimeSpec() {
        return this.startTimeOrTimestampSpec;
    }

    public ColumnSpec getFinishTimeSpec() {
        return this.finishTimeSpec;
    }

    public PropertySpec[] getPropertySpecs() {
        return this.propertySpecs;
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
