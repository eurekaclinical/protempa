/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.proposition.value;

import org.apache.commons.collections4.map.ReferenceMap;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Represents a number, either integral or floating point, with unbounded
 * upper and lower limit (subject to available memory). Significant digits are
 * preserved.
 * 
 * @author Andrew Post
 */
public final class NumberValue implements NumericalValue,
        Comparable<NumberValue>, Serializable {

    private static final long serialVersionUID = 266750924747111671L;

    private static final Map<BigDecimal, NumberValue> cache =
            new ReferenceMap<>();
    private BigDecimal num;
    private transient volatile int hashCode;
    
    /**
     * Parses a number from a string. The parser expects a string of the format
     * described in the javadoc for {@link BigDecimal}'s string constructor.
     * 
     * @param str a string representing a number.
     * @return a {@link NumberValue}, or <code>null</code> if no number was 
     * found in the string.
     */
    public static NumberValue parse(String str) {
        return (NumberValue) ValueType.NUMBERVALUE.parse(str);
    }

    /**
     * Gets an instance of {@link NumberValue} representing a 
     * <code>double</code>.
     * 
     * @param num a double.
     * @return a {@link NumberValue}. Guaranteed not <code>null</code>.
     */
    public static NumberValue getInstance(double num) {
        return getInstance(BigDecimal.valueOf(num));
    }

    public static NumberValue getInstance(long num) {
        return getInstance(BigDecimal.valueOf(num));
    }

    public static NumberValue getInstance(BigDecimal num) {
        NumberValue result;
        if (num != null) {
            result = cache.get(num);
            if (result == null) {
                result = new NumberValue(num);
                cache.put(num, result);
            }
        } else {
            result = getInstance(BigDecimal.ZERO);
        }
        return result;
    }

    public NumberValue(long num) {
        this(BigDecimal.valueOf(num));
    }

    public NumberValue(double num) {
        this(BigDecimal.valueOf(num));
    }

    public NumberValue(BigDecimal num) {
        init(num);
    }

    private void init(BigDecimal num) {
        if (num == null) {
            this.num = BigDecimal.ZERO;
        } else {
            this.num = num;
        }
    }

    @Override
    public NumberValue replace() {
        NumberValue result = cache.get(this.num);
        if (result != null) {
            return result;
        } else {
            return this;
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
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NumberValue other = (NumberValue) obj;
        if (this.num != other.num && !this.num.equals(other.num)) {
            return false;
        }
        return true;
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

    @Override
    public ValueType getType() {
        return ValueType.NUMBERVALUE;
    }

    @Override
    public double doubleValue() {
        return num.doubleValue();
    }

    public long longValue() {
        return num.longValue();
    }

    @Override
    public BigDecimal getBigDecimal() {
        return num;
    }

    /**
     * Compares this value and another numerically, or checks this number value
     * for membership in a value list.
     * 
     * @param o a {@link Value}.
     * @return If the provided value is a {@link NumericalValue}, returns
     * {@link ValueComparator#GREATER_THAN},
     * {@link ValueComparator#LESS_THAN} or {@link ValueComparator#EQUAL_TO}
     * depending on whether this value is numerically greater than,
     * less than or equal to the value provided as argument. If the provided
     * value is a {@link ValueList}, returns 
     * {@link ValueComparator#IN} if this object is a member of the list, or
     * {@link ValueComparator#NOT_IN} if not. Otherwise, returns
     * {@link ValueComparator#UNKNOWN}.
     */
    @Override
    public ValueComparator compare(Value o) {
        if (o == null) {
            return ValueComparator.NOT_EQUAL_TO;
        }
        switch (o.getType()) {
            case NUMBERVALUE:
                NumberValue other = (NumberValue) o;
                int comp = compareTo(other);
                return comp > 0 ? ValueComparator.GREATER_THAN
                        : (comp < 0 ? ValueComparator.LESS_THAN
                        : ValueComparator.EQUAL_TO);
            case INEQUALITYNUMBERVALUE:
                InequalityNumberValue other2 = (InequalityNumberValue) o;
                int comp2 = num.compareTo((BigDecimal) other2.getNumber());
                switch (other2.getComparator()) {
                    case EQUAL_TO:
                        return comp2 > 0 ? ValueComparator.GREATER_THAN
                            : (comp2 < 0 ? ValueComparator.LESS_THAN
                            : ValueComparator.EQUAL_TO);
                    case GREATER_THAN:
                        return comp2 <= 0 ? ValueComparator.LESS_THAN
                            : ValueComparator.UNKNOWN;
                    default:
                        return comp2 >= 0 ? ValueComparator.GREATER_THAN
                            : ValueComparator.UNKNOWN;
                }
            case VALUELIST:
                ValueList<?> vl = (ValueList<?>) o;
                return equals(vl) ? ValueComparator.EQUAL_TO 
                        : ValueComparator.NOT_EQUAL_TO;
            default:
                return ValueComparator.NOT_EQUAL_TO;
}
    }

    @Override
    public Number getNumber() {
        return num;
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
        s.writeObject(this.num);
    }

    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        BigDecimal tmpNum = (BigDecimal) s.readObject();
        init(tmpNum);
        if (!cache.containsKey(tmpNum)) {
            cache.put(tmpNum, this);
        }
    }

    @Override
    public NumberValue getNumberValue() {
        return this;
    }
}
