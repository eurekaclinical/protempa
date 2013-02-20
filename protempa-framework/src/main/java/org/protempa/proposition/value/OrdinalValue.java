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

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents ordinal string values. This currently is non-functional.
 * 
 * @author Andrew Post
 */
public final class OrdinalValue implements OrderedValue, Serializable {

    private static final long serialVersionUID = -1605459658420554439L;
    private String val;
    private List<String> allowedValues;
    private transient volatile int hashCode;
    
    public static OrdinalValue parse(String str) {
        return (OrdinalValue) ValueType.ORDINALVALUE.parse(str);
    }

    /**
     * Creates an ordinal value of a type with allowed values.
     *
     * @param value
     *            a {@link String}.
     * @param sortedAllowedValues
     *            the allowed values {@link List<String}.
     */
    OrdinalValue(String value, List<String> sortedAllowedValues) {
        throw new UnsupportedOperationException("Not implemented");
//        this.val = value;
//        this.allowedValues = new ArrayList<String>(sortedAllowedValues);
    }
    
    @Override
    public OrdinalValue replace() {
        return this;
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
    
    @Override
    public ValueType getType() {
        return ValueType.ORDINALVALUE;
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
     * Compares this value and another according to the defined order, or 
     * checks this number value for membership in a value list.
     * 
     * @param o a {@link Value}.
     * @return If the provided value is an {@link OrdinalValue}, returns
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
            return ValueComparator.UNKNOWN;
        }
        switch (o.getType()) {
            case ORDINALVALUE:
                OrdinalValue other = (OrdinalValue) o;
                if (allowedValues == null
                        || val == null
                        || other.allowedValues == null
                        || other.val == null
                        || !(allowedValues == other.allowedValues || allowedValues.equals(other.allowedValues))) {
                    return ValueComparator.UNKNOWN;
                }

                int c = allowedValues.indexOf(val) - allowedValues.indexOf(other.val);
                if (c == 0) {
                    return ValueComparator.EQUAL_TO;
                } else if (c > 0) {
                    return ValueComparator.GREATER_THAN;
                } else {
                    return ValueComparator.LESS_THAN;
                }
            case VALUELIST:
                ValueList<?> vl = (ValueList<?>) o;
                return equals(vl) ? ValueComparator.EQUAL_TO 
                        : ValueComparator.NOT_EQUAL_TO;
            default:
                return ValueComparator.UNKNOWN;
        }
    }
    
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
        final OrdinalValue other = (OrdinalValue) obj;
        if (!this.val.equals(other.val)) {
            return false;
        }
        if (this.allowedValues != other.allowedValues &&
                !this.allowedValues.equals(other.allowedValues)) {
            return false;
        }
        return true;
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
        //s.writeObject(this.val);
        throw new UnsupportedOperationException("Not implemented");
    }
    
    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        //String tmpVal = (String) s.readObject();
        throw new InvalidObjectException("Can't restore. Not implemented");
    }
}
