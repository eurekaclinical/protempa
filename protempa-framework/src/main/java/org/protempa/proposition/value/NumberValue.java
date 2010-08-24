package org.protempa.proposition.value;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * @author Andrew Post
 */
public final class NumberValue extends ValueImpl implements NumericalValue,
        Comparable<NumberValue> {

    private static final long serialVersionUID = 266750924747111671L;
    private static final NumberFormat REPR_FORMAT = NumberFormat.getInstance();

    static {
        REPR_FORMAT.setGroupingUsed(false);
    }
    private final BigDecimal num;
    private volatile int hashCode;

    public NumberValue(double num) {
        this(new BigDecimal(num));
    }

    public NumberValue(BigDecimal num) {
        super(ValueType.NUMBERVALUE);
        if (num == null) {
            this.num = new BigDecimal(0);
        } else {
            this.num = num;
        }
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = num.hashCode();
        }
        return this.hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NumberValue)) {
            return false;
        }

        NumberValue nv = (NumberValue) obj;

        return num == nv.num || num.equals(nv.num);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(NumberValue o) {
        return num.compareTo(o.num);
    }

    @Override
    public String getFormatted() {
        return num.toString();
    }

    /**
     * Returns the canonical string representing this value. Returns
     * "NUMBER_VALUE:the number formatted without grouping".
     *
     * @return a {@link String}.
     * @see org.protempa.proposition.value.Value#getRepr()
     */
    @Override
    public String getRepr() {
        return reprType() + REPR_FORMAT.format(num);
    }

    @Override
    public double doubleValue() {
        return num.doubleValue();
    }

    public long longValue() {
        return num.longValue();
    }

    @Override
    protected ValueComparator compareNumberValue(NumberValue d2) {
        int comp = compareTo(d2);
        return comp > 0 ? ValueComparator.GREATER_THAN
                : (comp < 0 ? ValueComparator.LESS_THAN
                : ValueComparator.EQUAL_TO);
    }

    @Override
    protected ValueComparator compareInequalityNumberValue(
            InequalityNumberValue d2) {
        int comp = num.compareTo((BigDecimal) d2.getNumber());
        if (d2.getComparator() == ValueComparator.EQUAL_TO) {
            return comp > 0 ? ValueComparator.GREATER_THAN
                    : (comp < 0 ? ValueComparator.LESS_THAN
                    : ValueComparator.EQUAL_TO);
        } else if (d2.getComparator() == ValueComparator.GREATER_THAN) {
            return comp <= 0 ? ValueComparator.LESS_THAN
                    : ValueComparator.UNKNOWN;
        } else {
            return comp >= 0 ? ValueComparator.GREATER_THAN
                    : ValueComparator.UNKNOWN;
        }
    }

    @Override
    public Number getNumber() {
        return num;
    }
}
