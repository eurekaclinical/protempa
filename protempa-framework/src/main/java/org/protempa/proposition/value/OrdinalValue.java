package org.protempa.proposition.value;

import java.util.ArrayList;
import java.util.List;

/**
 * Ordinal string values.
 * 
 * @author Andrew Post
 */
public final class OrdinalValue extends ValueImpl implements OrderedValue {

    private static final long serialVersionUID = -1605459658420554439L;
    private final String val;
    private final List<String> allowedValues;
    private volatile int hashCode;

    /**
     * Creates an ordinal value of a type with allowed values.
     *
     * @param value
     *            a {@link String}.
     * @param sortedAllowedValues
     *            the allowed values {@link List<String}.
     */
    OrdinalValue(String value, List<String> sortedAllowedValues) {
        super(ValueType.ORDINALVALUE);
        this.val = value;
        this.allowedValues = new ArrayList<String>(sortedAllowedValues);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.protempa.proposition.value.Value#getFormatted()
     */
    @Override
    public String getFormatted() {
        return val;
    }

    /**
     * Returns the value.
     *
     * @return a {@link String}.
     */
    public String getValue() {
        return val;
    }

    /**
     * Returns the canonical string representing this value. Returns
     * "ORDINAL_VALUE:string to [comma-separated allowed values]".
     *
     * @return a {@link String}.
     *
     * @see org.protempa.proposition.value.Value#getRepr()
     */
    @Override
    public String getRepr() {
        return reprType() + val + " of " + this.allowedValues;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.protempa.proposition.value.ValueImpl#compareOrdinalValue(org.protempa.proposition.value.OrdinalValue)
     */
    @Override
    protected ValueComparator compareOrdinalValue(OrdinalValue ordVal) {
        if (allowedValues == null
                || val == null
                || ordVal.allowedValues == null
                || ordVal.val == null
                || !(allowedValues == ordVal.allowedValues || allowedValues.equals(ordVal.allowedValues))) {
            return ValueComparator.UNKNOWN;
        }

        int c = allowedValues.indexOf(val) - allowedValues.indexOf(ordVal.val);
        if (c == 0) {
            return ValueComparator.EQUAL_TO;
        } else if (c > 0) {
            return ValueComparator.GREATER_THAN;
        } else {
            return ValueComparator.LESS_THAN;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (hashCode == 0) {
            int result = 17;
            if (val != null) {
                result = result * 37 + val.hashCode();
            }
            if (allowedValues != null) {
                result = result * 37 + allowedValues.hashCode();
            }
            hashCode = result;
        }
        return hashCode;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NominalValue)) {
            return false;
        }

        OrdinalValue v = (OrdinalValue) obj;

        return (val == v.val || (val != null && val.equals(v.val)))
                || allowedValues == v.allowedValues
                || allowedValues.equals(v.allowedValues);
    }

    @Override
    public void accept(ValueVisitor valueVisitor) {
        if (valueVisitor == null) {
            throw new IllegalArgumentException("valueVisitor cannot be null");
        }
        valueVisitor.visit(this);
    }
}
