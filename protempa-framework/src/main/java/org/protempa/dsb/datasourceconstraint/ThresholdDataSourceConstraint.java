package org.protempa.dsb.datasourceconstraint;

import org.protempa.proposition.value.ValueComparator;

public final class ThresholdDataSourceConstraint
        extends AbstractValueDataSourceConstraint {

    private ValueComparator comparator;

    public ThresholdDataSourceConstraint(String propId) {
        super(propId);
    }

    public void setComparator(ValueComparator comparator) {
        this.comparator = comparator;
    }

    public ValueComparator getComparator() {
        return comparator;
    }

    public void accept(AbstractDataSourceConstraintVisitor visitor) {
        visitor.visit(this);
    }
}
