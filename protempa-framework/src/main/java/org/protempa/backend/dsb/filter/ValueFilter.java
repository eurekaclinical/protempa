package org.protempa.backend.dsb.filter;

import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

public final class ValueFilter extends AbstractFilter {

    private final ValueComparator comparator;
    private final Value value;

    public ValueFilter(String[] propIds, ValueComparator comparator,
            Value value) {
        super(propIds);
        this.comparator = comparator;
        this.value = value;
    }

    public ValueComparator getComparator() {
        return comparator;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public void accept(FilterVisitor visitor) {
        visitor.visit(this);
    }
}
