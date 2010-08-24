package org.protempa.dsb.filter;

import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

public final class ValueFilter
        extends AbstractFilter {

    private ValueComparator comparator;
    private Value value;

    public ValueFilter(String[] propIds) {
        super(propIds);
    }

    public void setComparator(ValueComparator comparator) {
        this.comparator = comparator;
    }

    public ValueComparator getComparator() {
        return comparator;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public void accept(FilterVisitor visitor) {
        visitor.visit(this);
    }
}
