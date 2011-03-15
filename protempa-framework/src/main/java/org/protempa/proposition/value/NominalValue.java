package org.protempa.proposition.value;

import java.util.Map;
import org.apache.commons.collections.map.ReferenceMap;

/**
 * A {@link String} value.
 * 
 * @author Andrew Post
 */
public final class NominalValue extends ValueImpl {

    private static final long serialVersionUID = 440118249272295573L;
    private final String val;
    private transient volatile int hashCode;

    @SuppressWarnings("unchecked")
    private static final Map<String, NominalValue> cache = new ReferenceMap();

    /**
     * Creates a new nominal value. If <code>val</code>'s length is less
     * than 20 characters, the created {@link NominalValue} object will be
     * cached and reused.
     * 
     * @param val a {@link String}. If <code>null</code>, {@link NominalValue}
     * with <code>""</code> will be created.
     * @return a {@link NominalValue}.
     */
    public static NominalValue getInstance(String val) {
        if (val != null && val.length() < 20) {
            NominalValue result = cache.get(val);
            if (result == null) {
                val = val.intern();
                result = new NominalValue(val);
                cache.put(val, result);
            }
            return result;
        } else {
            return new NominalValue(val);
        }
    }

    /**
     * Creates a new nominal value with the given value. The recommended way to
     * create nominal values is with {@link #getInstance(java.lang.String) }.
     *
     * @param val
     *            a <code>String</code>. If <code>null</code>, the default
     *            string is used (<code>""</code>).
     */
    public NominalValue(String val) {
        super(ValueType.NOMINALVALUE);
        if (val != null) {
            this.val = val;
        } else {
            this.val = "";
        }
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = val.hashCode();
        }
        return this.hashCode;
    }

    /**
     * Compares this nominal value and another for string equality.
     *
     * @param obj another object.
     * @return <code>true</code> if the two nominal values' strings are equal,
     * <code>false</code> if not.
     */
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
        final NominalValue other = (NominalValue) obj;
        return this.val.equals(other.val);
    }

    /**
     * Gets the value as a Java {@link String}.
     *
     * @return a {@link String}.
     */
    public String getString() {
        return val;
    }

    /**
     * Returns the same thing as {@link #getString}.
     */
    @Override
    public String getFormatted() {
        return val;
    }

    /**
     * Returns the canonical string representing this value. Returns
     * "NOMINAL_VALUE:the string as-is".
     *
     * @return a {@link String}.
     *
     * @see org.protempa.proposition.value.Value#getRepr()
     */
    @Override
    public String getRepr() {
        return reprType() + val;
    }

    /**
     * Compares this nominal value and another lexically.
     * 
     * @param d2 another {@link NominalValue}.
     * @return {@link ValueComparator.GREATER_THAN},
     * {@link ValueComparator.LESS_THAN} or {@link ValueComparator.EQUAL_TO}
     * depending on whether this nominal value is lexically greater than,
     * less than or equal to the nominal value provided as argument.
     */
    @Override
    protected ValueComparator compareNominalValue(NominalValue d2) {
        int comp = val.compareTo(d2.val);
        return comp > 0 ? ValueComparator.GREATER_THAN
                : (comp < 0 ? ValueComparator.LESS_THAN
                : ValueComparator.EQUAL_TO);
    }

    @Override
    public void accept(ValueVisitor valueVisitor) {
        if (valueVisitor == null) {
            throw new IllegalArgumentException("valueVisitor cannot be null");
        }
        valueVisitor.visit(this);
    }
}
