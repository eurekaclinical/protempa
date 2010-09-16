package org.protempa;

import java.beans.PropertyChangeSupport;
import org.protempa.proposition.value.ValueType;

/**
 *
 * @author Andrew Post
 */
public final class PropertyDefinitionImpl implements PropertyDefinition {
    private static final long serialVersionUID = 5258018980150529695L;
    private static final ValueType DEFAULT_VALUE_TYPE = ValueType.NOMINALVALUE;

    private String name;
    private ValueType valueType;
    private PropertyChangeSupport changes;

    public PropertyDefinitionImpl() {
        this.changes = new PropertyChangeSupport(this);
        this.name = "";
        this.valueType = DEFAULT_VALUE_TYPE;
    }

    public void setName(String name) {
        if (name == null)
            name = "";
        String old = this.name;
        this.name = name;
        this.changes.firePropertyChange("name", old, this.name);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setValueType(ValueType valueType) {
        if (valueType == null)
            valueType = DEFAULT_VALUE_TYPE;
        ValueType old = this.valueType;
        this.valueType = valueType;
        this.changes.firePropertyChange("valueType", old, this.valueType);
    }

    @Override
    public ValueType getValueType() {
        return this.valueType;
    }
}
