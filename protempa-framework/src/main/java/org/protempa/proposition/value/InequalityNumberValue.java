package org.protempa.proposition.value;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents a test result that is outside of the reportable range of the test
 * and thus is reported prefixed with a greater than or less than sign. They
 * are comparable to actual {@link NumberValue}s.
 * 
 * @author Andrew Post
 */
public final class InequalityNumberValue implements
        NumericalValue, Serializable {

    private static final long serialVersionUID = 1485092589217545627L;
    private NumberValue val;
    private ValueComparator comp;
    private transient volatile int hashCode;
    
    /**
     * Parses a number with an inequality from a string, e.g., "> 100". 
     * 
     * @param str a string representing a number prefixed with an inequality.
     * @return a {@link InequalityNumberValue}, or <code>null</code> if no 
     * such number was found.
     */
    public static InequalityNumberValue parse(String str) {
        return 
            (InequalityNumberValue) ValueType.INEQUALITYNUMBERVALUE.parse(str);
    }

    /**
     * Creates a new {@link InequalityNumberValue} with the given
     * comparator and value.
     *
     * @param comparator
     *            a {@link ValueComparator}. If <code>null</code>,
     *            the default comparator is used 
     *            ({@link ValueComparator#EQUAL_TO}).
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
     *            a {@link ValueComparator}. If <code>null</code>,
     *            the default comparator is used ({@link ValueComparator#EQUAL_TO}).
     * @param val
     *            a {@link BigDecimal}. If <code>null</code>, a value
     *            of <code>0</code> is used.
     */
    public InequalityNumberValue(ValueComparator comparator, BigDecimal val) {
        init(comparator, val);
    }
    
    private void init(ValueComparator comparator, BigDecimal val) {
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
    public InequalityNumberValue replace() {
        return this;
    }

    @Override
    public Number getNumber() {
        return val.getNumber();
    }
    
    @Override
    public BigDecimal getBigDecimal() {
        return val.getBigDecimal();
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
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public ValueComparator getComparator() {
        return comp;
    }

    @Override
    public ValueType getType() {
        return ValueType.INEQUALITYNUMBERVALUE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
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

    /**
     * Compares this value and another numerically, or checks this value
     * for membership in a value list.
     * 
     * @param o a {@link Value}.
     * @return If the provided value is a {@link NumericalValue}, returns 
     * {@link ValueComparator#GREATER_THAN},
     * {@link ValueComparator#LESS_THAN} or {@link ValueComparator#EQUAL_TO}
     * depending on whether this value is numerically greater than,
     * less than or equal to the value provided as argument. IF the provided
     * value is a {@link ValueList}, returns {@link ValueComparator#IN} if this
     * object is a member of the value list, or {@link ValueComparator#NOT_IN}
     * if this object is not a member. Otherwise, returns
     * {@link ValueComparator#UNKNOWN}.
     */
    @Override
    public ValueComparator compare(Value d2) {
        if (d2 == null) {
            return ValueComparator.UNKNOWN;
        }
        switch (d2.getType()) {
            case NUMBERVALUE:
                NumberValue otherVal = (NumberValue) d2;
                int valComp = val.compareTo(otherVal);
                switch (this.comp) {
                    case EQUAL_TO:
                        return valComp > 0 ? ValueComparator.GREATER_THAN
                            : (valComp < 0 ? ValueComparator.LESS_THAN
                            : ValueComparator.EQUAL_TO);
                    case LESS_THAN:
                        return valComp <= 0 ? ValueComparator.LESS_THAN
                            : ValueComparator.UNKNOWN;
                    default:
                        return valComp >= 0 ? ValueComparator.GREATER_THAN
                            : ValueComparator.UNKNOWN;
                }
            case INEQUALITYNUMBERVALUE:
                InequalityNumberValue other = (InequalityNumberValue) d2;
                ValueComparator d2Comp = other.comp;
                int valComp2 = this.val.compareTo(other.val);

                if (this.comp == d2Comp) {
                    if (this.comp == ValueComparator.EQUAL_TO) {
                        return valComp2 > 0 ? ValueComparator.GREATER_THAN
                                : (valComp2 < 0 ? ValueComparator.LESS_THAN
                                : ValueComparator.EQUAL_TO);
                    } else {
                        return ValueComparator.UNKNOWN;
                    }
                } else if (this.comp == ValueComparator.GREATER_THAN
                        && d2Comp == ValueComparator.LESS_THAN) {
                    if (valComp2 >= 0) {
                        return ValueComparator.GREATER_THAN;
                    } else {
                        return ValueComparator.UNKNOWN;
                    }
                } else {
                    if (valComp2 <= 0) {
                        return ValueComparator.LESS_THAN;
                    } else {
                        return ValueComparator.UNKNOWN;
                    }
                }
            case VALUELIST:
                ValueList vl = (ValueList) d2;
                return vl.contains(this) ? ValueComparator.IN : ValueComparator.NOT_IN;
            default:
                return ValueComparator.UNKNOWN;
        }
    }

    @Override
    public void accept(ValueVisitor valueVisitor) {
        if (valueVisitor == null) {
            throw new IllegalArgumentException("valueVisitor cannot be null");
        }
        valueVisitor.visit(this);
    }
    
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.writeObject(this.val.getBigDecimal());
        s.writeObject(this.comp);
    }
    
    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        BigDecimal tmpVal = (BigDecimal) s.readObject();
        ValueComparator tmpComp = (ValueComparator) s.readObject();
        init(tmpComp, tmpVal);
    }
}
