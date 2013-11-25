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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A boolean value.
 * 
 * @author Andrew Post
 */
public final class BooleanValue implements Value, Serializable {

    private static final long serialVersionUID = 3913347786451127004L;
    public static final BooleanValue TRUE = new BooleanValue(true);
    public static final BooleanValue FALSE = new BooleanValue(false);
    private boolean val;
    private transient volatile int hashCode;
    
    /**
     * Parses a boolean from a string. The parser expects "true" or "false"
     * (case insensitive).
     * 
     * @param str a string representing a boolean. 
     * @return a boolean value, or <code>null</code> if no boolean could be 
     * parsed.
     */
    public static BooleanValue parse(String str) {
        return (BooleanValue) ValueType.BOOLEANVALUE.parse(str);
    }

    /**
     * Creates a new boolean value. Use {@link BooleanValue#TRUE} or
     * {@link BooleanValue#FALSE} instead.
     * 
     * @param val a java boolean.
     */
    public BooleanValue(boolean val) {
        this.val = val;
    }

    /**
     * Creates a boolean value (<code>true</code> or <code>false</code>). Use
     * {@link BooleanValue#TRUE} or {@link BooleanValue#FALSE} instead.
     *
     * @param val
     *            a <code>Boolean</code> object. If <code>null</code>, this
     *            <code>BooleanValue</code> object is set to
     *            <code>false</code>
     */
    public BooleanValue(Boolean val) {
        if (val != null) {
            this.val = val.booleanValue();
        } else {
            this.val = false;
        }
    }
    
    @Override
    public BooleanValue replace() {
        return this;
    }

    /**
     * Returns the Java {@link Boolean} corresponding to this object.
     * 
     * @return {@link Boolean#TRUE} or {@link Boolean#FALSE}.
     */
    public Boolean getBoolean() {
        return Boolean.valueOf(this.val);
    }

    /**
     * Returns the Java boolean corresponding to this object.
     * 
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean booleanValue() {
        return val;
    }
    
    @Override
    public String getFormatted() {
        return Boolean.toString(this.val);
    }
    
    @Override
    public ValueType getType() {
        return ValueType.BOOLEANVALUE;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BooleanValue other = (BooleanValue) obj;
        if (this.val != other.val) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int hash = 3;
            hash = 17 * hash + (this.val ? 1 : 0);
            this.hashCode = hash;
        }
        return this.hashCode;
    }

    @Override
    public void accept(ValueVisitor valueVisitor) {
        if (valueVisitor == null) {
            throw new IllegalArgumentException("valueVisitor cannot be null");
        }
        valueVisitor.visit(this);
    }

    /**
     * Returns {@link ValueComparator#UNKNOWN}, as boolean values do not
     * have a natural order, unless the provided value is a value list, for
     * which this method tests for membership in the list.
     * 
     * @param o a {@link Value}.
     * @return {@link ValueComparator#UNKNOWN}, if the provided value is not a
     * value list. If the provided value is a value list, it returns 
     * {@link ValueComparator#IN} if this boolean value is a member and
     * {@link ValueComparator#NOT_IN} if this boolean value is not a member.
     */
    @Override
    public ValueComparator compare(Value val) {
        if (val == null || val != ValueType.VALUELIST) {
            return ValueComparator.UNKNOWN;
        }
        ValueList<?> vl = (ValueList<?>) val;
        return vl.contains(this) ? ValueComparator.IN : ValueComparator.NOT_IN;
    }
    
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.writeBoolean(this.val);
    }
    
    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        this.val = s.readBoolean();
    }
}
