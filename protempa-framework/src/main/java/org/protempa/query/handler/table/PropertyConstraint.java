package org.protempa.query.handler.table;

import org.apache.commons.lang.StringUtils;
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
    private final V[] values;

    public PropertyConstraint(String propertyName,
            ValueComparator valueComparator, V... values) {
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName cannot be null");
        }
        if (valueComparator == null) {
            throw new IllegalArgumentException("valueComparator cannot be null");
        }
        this.propertyName = propertyName;
        this.valueComparator = valueComparator;
        this.values = values.clone();
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public V[] getValues() {
        return this.values;
    }

    public ValueComparator getValueComparator() {
        return this.valueComparator;
    }

    public String getFormatted() {
        StringBuilder vStr = new StringBuilder();
        if (this.values != null) {
            String[] formattedValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                formattedValues[i] = values[i].getFormatted();
            }
            vStr.append("[");
            vStr.append(StringUtils.join(formattedValues, ','));
            vStr.append("]");
        } else {
            vStr.append("null");
        }
        return this.propertyName + this.valueComparator.getComparatorString()
                + vStr;
    }
}
