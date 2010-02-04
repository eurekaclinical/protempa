package org.protempa;

import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;

public interface DataSourceConstraint {
	String getParameterId();

	ValueComparator getComparator();

	Value getValue();

	DataSourceConstraint getAnd();
}
