package org.protempa.proposition.value;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import org.apache.commons.collections.map.ReferenceMap;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents a {@link String} value.
 * 
 * @author Andrew Post
 */
public final class NominalValue implements Value, Comparable<NominalValue>,
        Serializable {

    private static final long serialVersionUID = 440118249272295573L;
    
    private String val;
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
        if (val != null) {
            this.val = val;
        } else {
            this.val = "";
        }
    }
    
    @Override
    public NominalValue replace() {
        NominalValue result = cache.get(this.val);
        if (result != null) {
            return result;
        } else {
            return this;
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
    
    @Override
    public ValueType getType() {
        return ValueType.NOMINALVALUE;
    }

    /**
     * Compares this nominal value and another lexically. If the argument is
     * a {@link ListValue}, it checks this string for membership in the list.
     * 
     * @param o another {@link Value}.
     * @return If the provided value is a {@link NominalValue}, returns
     * {@link ValueComparator#GREATER_THAN},
     * {@link ValueComparator#LESS_THAN} or {@link ValueComparator#EQUAL_TO}
     * depending on whether this nominal value is lexically greater than,
     * less than or equal to the nominal value provided as argument. If the
     * provided value is a {@link ValueList}, returns 
     * {@link ValueComparator#IN} if this object is a member of the list, or
     * {@link ValueComparator#NOT_IN} if not. Otherwise, returns
     * {@link ValueComparator#UNKNOWN}.
     */
    @Override
    public ValueComparator compare(Value o) {
        if (o == null) {
            return ValueComparator.UNKNOWN;
        }
        switch (o.getType()) {
            case NOMINALVALUE:
                NominalValue other = (NominalValue) o;
                int comp = compareTo(other);
                return comp > 0 ? ValueComparator.GREATER_THAN
                        : (comp < 0 ? ValueComparator.LESS_THAN
                        : ValueComparator.EQUAL_TO);
            case VALUELIST:
                ValueList vl = (ValueList) o;
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
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.writeObject(this.val);
    }
    
    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        String tmpVal = (String) s.readObject();
        this.val = tmpVal != null ? tmpVal : "";
        if (tmpVal != null && tmpVal.length() < 20) {
            if (!cache.containsKey(tmpVal)) {
                cache.put(tmpVal, this);
            }
        }
    }

    @Override
    public int compareTo(NominalValue t) {
        return this.val.compareTo(t.val);
    }
}
