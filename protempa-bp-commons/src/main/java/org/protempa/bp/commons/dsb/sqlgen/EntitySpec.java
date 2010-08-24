package org.protempa.bp.commons.dsb.sqlgen;

import java.io.Serializable;
import java.util.Map;
import org.protempa.ProtempaUtil;
import org.protempa.bp.commons.dsb.PositionParser;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.ValueType;

/**
 * Defines a mapping from a proposition to a relational database's physical
 * schema.
 * 
 * @author Andrew Post
 */
public final class EntitySpec implements Serializable {
    private final String name;
    private final String description;
    private final String[] propositionIds;
    private final boolean unique;
    private final ColumnSpec baseSpec;
    private final ColumnSpec uniqueIdSpec;
    private final ColumnSpec startTimeOrTimestampSpec;
    private final ColumnSpec finishTimeSpec;
    private final PropertySpec[] propertySpecs;
    private final Map<String, String> codeToPropIdMap;
    private final ColumnSpec codeSpec;
    private final ColumnSpec[] constraintSpecs;
    private final ValueType valueType;
    private final Granularity granularity;
    private final PositionParser positionParser;

    /**
     * Creates an entity spec instance.
     * 
     * @param name a {@link String}. Cannot be <code>null</code>.
     * @param description a {@link String}. The constructor replaces a
     * <code>null</code> argument with a {@link String} of length zero.
     * @param propositionIds a {@link String[]} containing proposition ids.
     * Cannot be <code>null</code>, length zero or contain <code>null</code>
     * values.
     * @param unique <code>true</code> if every row in the database table
     * specified by the <code>baseSpec</code> argument contains a unique
     * instance of this entity, <code>false</code> otherwise.
     * @param baseSpec
     * @param uniqueIdSpec
     * @param startTimeOrTimestampSpec
     * @param finishTimeSpec
     * @param propertySpecs
     * @param codeToPropIdMap
     * @param codeSpec
     * @param constraintSpecs
     * @param valueType
     * @param granularity
     * @param positionParser
     */
    public EntitySpec(String name,
            String description,
            String[] propositionIds,
            boolean unique,
            ColumnSpec baseSpec,
            ColumnSpec uniqueIdSpec,
            ColumnSpec startTimeOrTimestampSpec,
            ColumnSpec finishTimeSpec,
            PropertySpec[] propertySpecs,
            Map<String, String> codeToPropIdMap,
            ColumnSpec codeSpec,
            ColumnSpec[] constraintSpecs,
            ValueType valueType,
            Granularity granularity,
            PositionParser positionParser) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        ProtempaUtil.checkArray(propositionIds, "propositionIds");
        if (baseSpec == null)
            throw new IllegalArgumentException("baseSpec cannot be null");
        this.name = name;
        if (description == null)
            description = "";
        this.description = description;
        this.propositionIds = new String[propositionIds.length];
        this.unique = unique;
        System.arraycopy(propositionIds, 0, this.propositionIds, 0,
                propositionIds.length);
        this.baseSpec = baseSpec;
        this.uniqueIdSpec = uniqueIdSpec;
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
     * Gets this entity spec's name.
     *
     * @return a {@link String}.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns a textual description of this entity spec.
     *
     * @return a {@link String}. Cannot be <code>null</code>.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return the code
     */
    public String[] getPropositionIds() {
        return this.propositionIds;
    }

    public boolean isUnique() {
        return this.unique;
    }

    public ColumnSpec getBaseSpec() {
        return this.baseSpec;
    }

    public ColumnSpec getUniqueIdSpec() {
        return this.uniqueIdSpec;
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

    public ValueType getValueType() {
        return this.valueType;
    }

    public Granularity getGranularity() {
        return this.granularity;
    }

    public PositionParser getPositionParser() {
        return this.positionParser;
    }
}
