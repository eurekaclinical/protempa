package org.protempa.query.handler.table;

import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

public final class ColumnSpecConstraint {

    private final String propertyName;
    private final ValueComparator valueComparator;
    private final Value value;

    public ColumnSpecConstraint(String propertyName, ValueComparator valueComparator, Value value) {
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName cannot be null");
        }
        if (valueComparator == null) {
            throw new IllegalArgumentException("valueComparator cannot be null");
        }
        this.propertyName = propertyName;
        this.valueComparator = valueComparator;
        this.value = value;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public Value getValue() {
        return this.value;
    }

    public ValueComparator getValueComparator() {
        return this.valueComparator;
    }

    public String getFormatted() {
        String vStr = this.value != null ? this.value.getFormatted() : "null";
        return this.propertyName + this.valueComparator.getComparatorString() + vStr;
    }
}
