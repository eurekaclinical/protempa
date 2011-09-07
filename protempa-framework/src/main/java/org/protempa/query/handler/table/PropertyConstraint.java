package org.protempa.query.handler.table;

import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

/**
 * 
 * @author mmansou
 * @param <V>
 *            The type of values the constraint applies to. Must be a subtype of
 *            {@link org.protempa.proposition.value.Value}
 */
public final class PropertyConstraint<V extends Value> {

    private final String propertyName;
    private final ValueComparator valueComparator;
    private final V value;

    /**
     * Constructs a property constraint with a property name, comparator and
     * one or more values.
     * 
     * @param propertyName the name of the property.
     * @param valueComparator the comparator.
     * @param values the values to compare against.
     */
    public PropertyConstraint(String propertyName,
            ValueComparator valueComparator, V value) {
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName cannot be null");
        }
        if (valueComparator == null) {
            throw new IllegalArgumentException(
                    "valueComparator cannot be null");
        }
        this.propertyName = propertyName;
        this.valueComparator = valueComparator;
        this.value = value;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public V getValue() {
        return this.value;
    }

    public ValueComparator getValueComparator() {
        return this.valueComparator;
    }

    public String getFormatted() {
        StringBuilder vStr = new StringBuilder();
        if (this.value != null) {
            vStr.append(this.value.getFormatted());
        } else {
            vStr.append("null");
        }
        return this.propertyName + this.valueComparator.getComparatorString()
                + vStr;
    }
}
