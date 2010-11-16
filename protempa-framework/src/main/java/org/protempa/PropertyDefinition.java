package org.protempa;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.proposition.value.ValueType;

/**
 * Defines a property of a proposition definition.
 *
 * @author Andrew Post
 */
public final class PropertyDefinition implements Serializable {
    private static final long serialVersionUID = 5258018980150529695L;

    private final String name;
    private final ValueType valueType;
    private final String valueSetId;

    /**
     * Initializes the property definition with a name, a value type and a
     * <code>null</code> value set.
     * 
     * @param name a name {@link String}. Cannot be <code>null</code>.
     * @param valueType a {@link ValueType}. Cannot be <code>null</code>.
     */
    public PropertyDefinition(String name, ValueType valueType) {
        this(name, valueType, null);
    }

    /**
     * Initializes the property definition with a name, a value type and a
     * value set.
     * 
     * @param name a name {@link String}. Cannot be <code>null</code>.
     * @param valueType a {@link ValueType}. Cannot be <code>null</code>.
     * @param valueSet a {@link EnumeratedValueSet} that is compatible with
     * the given <code>valueType</code>.
     *
     * @see ValueType#isCompatible(ValueSet) 
     */
    public PropertyDefinition(String name, ValueType valueType,
            String valueSetId) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        if (valueType == null)
            throw new IllegalArgumentException("valueType cannot be null");
        this.name = name;
        this.valueType = valueType;
        this.valueSetId = valueSetId;
    }

    /**
     * Returns the property's name.
     *
     * @return a {@link String}.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the property's value type.
     *
     * @return a {@link ValueType}.
     */
    public ValueType getValueType() {
        return this.valueType;
    }

    /**
     * Returns the property's value set (for nominal and ordinal values).
     *
     * @return the {@link EnumeratedValueSet}, or <code>null</code> if none is
     * defined.
     */
    public String getValueSetId() {
        return this.valueSetId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
