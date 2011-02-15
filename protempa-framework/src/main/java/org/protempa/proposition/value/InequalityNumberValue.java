package org.protempa.proposition.value;

import java.math.BigDecimal;

/**
 * @author Andrew Post
 */
public final class InequalityNumberValue extends ValueImpl implements
        NumericalValue {

    private static final long serialVersionUID = 1485092589217545627L;
    private final NumberValue val;
    private final ValueComparator comp;
    private transient volatile int hashCode;

    /**
     * Creates a new <code>InequalityNumberValue</code> with the given
     * comparator and value.
     *
     * @param comparator
     *            a <code>ValueComparator</code>. If <code>null</code>,
     *            the default comparator is used (<code>ValueComparator.EQUAL_TO</code>).
     * @param val
     *            a <code>double</code>.
     */
    public InequalityNumberValue(ValueComparator comparator, double val) {
        this(comparator, BigDecimal.valueOf(val));
    }

    /**
     * Creates a new <code>InequalityNumberValue</code> with the given
     * comparator and value.
     *
     * @param comparator
     *            a <code>ValueComparator</code>. If <code>null</code>,
     *            the default comparator is used (<code>ValueComparator.EQUAL_TO</code>).
     * @param val
     *            a <code>BigDecimal</code>. If <code>null</code>, a value
     *            of <code>0</code> is used.
     */
    public InequalityNumberValue(ValueComparator comparator, BigDecimal val) {
        super(ValueType.INEQUALITYNUMBERVALUE);
        if (val != null) {
            this.val = NumberValue.getInstance(val);
        } else {
            this.val = NumberValue.getInstance(0);
        }
        if (comparator == null) {
            comp = ValueComparator.EQUAL_TO;
        } else {
            comp = comparator;
        }
    }

    @Override
    public Number getNumber() {
        return val.getNumber();
    }

    @Override
    public double doubleValue() {
        if (comp == ValueComparator.EQUAL_TO) {
            return val.doubleValue();
        } else if (comp == ValueComparator.LESS_THAN) {
            return val.doubleValue() - Double.MIN_VALUE;
        } else {
            return val.doubleValue() + Double.MIN_VALUE;
        }
    }

    public ValueComparator getInequality() {
        return comp;
    }

    @Override
    public String getFormatted() {
        return comp.getComparatorString() + " " + val.getFormatted();
    }

    @Override
    public String getRepr() {
        return reprType() + comp.getComparatorString() + " " + val.getRepr();
    }

    public ValueComparator getComparator() {
        return comp;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InequalityNumberValue)) {
            return false;
        }

        InequalityNumberValue i = (InequalityNumberValue) obj;
        return (val == i.val || val.equals(i.val)) && comp == i.comp;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            int result = 17;
            result = 37 * result + val.hashCode();
            result = 37 * result + comp.hashCode();
            hashCode = result;
        }
        return hashCode;
    }

    @Override
    protected ValueComparator compareNumberValue(NumberValue d2) {
        int comp = val.compareTo(d2);
        if (getComparator() == ValueComparator.EQUAL_TO) {
            return comp > 0 ? ValueComparator.GREATER_THAN
                    : (comp < 0 ? ValueComparator.LESS_THAN
                    : ValueComparator.EQUAL_TO);
        } else if (getComparator() == ValueComparator.LESS_THAN) {
            return comp <= 0 ? ValueComparator.LESS_THAN
                    : ValueComparator.UNKNOWN;
        } else {
            return comp >= 0 ? ValueComparator.GREATER_THAN
                    : ValueComparator.UNKNOWN;
        }
    }

    @Override
    protected ValueComparator compareInequalityNumberValue(
            InequalityNumberValue d2) {
        ValueComparator d1Comp = getComparator();
        ValueComparator d2Comp = d2.getComparator();
        int comp = Double.compare(val.doubleValue(), d2.doubleValue());

        if (d1Comp == d2Comp) {
            if (d1Comp == ValueComparator.EQUAL_TO) {
                return comp > 0 ? ValueComparator.GREATER_THAN
                        : (comp < 0 ? ValueComparator.LESS_THAN
                        : ValueComparator.EQUAL_TO);
            } else {
                return ValueComparator.UNKNOWN;
            }
        } else if (d1Comp == ValueComparator.GREATER_THAN
                && d2Comp == ValueComparator.LESS_THAN) {
            if (comp >= 0) {
                return ValueComparator.GREATER_THAN;
            } else {
                return ValueComparator.UNKNOWN;
            }
        } else {
            if (comp <= 0) {
                return ValueComparator.LESS_THAN;
            } else {
                return ValueComparator.UNKNOWN;
            }
        }
    }

    @Override
    public void accept(ValueVisitor valueVisitor) {
        if (valueVisitor == null) {
            throw new IllegalArgumentException("valueVisitor cannot be null");
        }
        valueVisitor.visit(this);
    }
}
