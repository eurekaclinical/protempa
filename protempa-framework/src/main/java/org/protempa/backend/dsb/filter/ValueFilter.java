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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime + ((comparator == null) ? 0 : comparator.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValueFilter other = (ValueFilter) obj;
		if (comparator != other.comparator)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
    
}
