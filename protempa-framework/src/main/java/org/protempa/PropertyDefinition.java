package org.protempa;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.proposition.value.ValueType;

/**
 *
 * @author Andrew Post
 */
public final class PropertyDefinition implements Serializable {
    private static final long serialVersionUID = 5258018980150529695L;

    private String name;
    private ValueType valueType;

    public PropertyDefinition(String name, ValueType valueType) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        if (valueType == null)
            throw new IllegalArgumentException("valueType cannot be null");
        this.name = name;
        this.valueType = valueType;
    }

    public String getName() {
        return this.name;
    }
    
    public ValueType getValueType() {
        return this.valueType;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
