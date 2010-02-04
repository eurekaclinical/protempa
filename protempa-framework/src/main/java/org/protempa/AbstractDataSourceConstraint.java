package org.protempa;

import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

public abstract class AbstractDataSourceConstraint implements
		DataSourceConstraint {

	private String parameterId;

	private ValueComparator comparator;

	private Value value;

	private DataSourceConstraint and;

	public AbstractDataSourceConstraint() {
	}

	public void setComparator(ValueComparator comparator) {
		this.comparator = comparator;
	}

	public ValueComparator getComparator() {
		return comparator;
	}

	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}

	public String getParameterId() {
		return parameterId;
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public Value getValue() {
		return value;
	}

	public void setAnd(DataSourceConstraint and) {
		this.and = and;
	}

	public DataSourceConstraint getAnd() {
		return and;
	}
}
