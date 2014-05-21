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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the possible valid comparisons between two {@link Value} objects.
 * 
 * Some rules:
 * <ul>
 *  <li>Values are always {@link ValueComparator#NOT_EQUAL_TO} 
 * <code>null</code>.
 *  <li>Values are always {@link ValueComparator#NOT_EQUAL_TO} a value of 
 * another type, except for the numeric types.
 *  <li>Two <code>null</code> values are {@link ValueComparator#EQUAL_TO}
 * each other.
 *  <li>A <code>null</code> value is {@link ValueComparator#NOT_EQUAL_TO} any
 * non-<code>null</code> value.
 * </ul>
 * 
 * @author Andrew Post
 */
public enum ValueComparator {

    /**
     * The first value is greater than (>) the second.
     */
    GREATER_THAN(">") {

        @Override
        public boolean includes(ValueComparator comparator) {
            if (comparator == null)
                throw new IllegalArgumentException("comparator cannot be null");
            return GREATER_THAN == comparator;
        }
    },
    /**
     * The first value is less than (<) the second.
     */
    LESS_THAN("<") {

        @Override
        public boolean includes(ValueComparator comparator) {
            if (comparator == null)
                throw new IllegalArgumentException("comparator cannot be null");
            return LESS_THAN == comparator;
        }
    },
    /**
     * The two values are equal (=).
     */
    EQUAL_TO("=") {

        @Override
        public boolean includes(ValueComparator comparator) {
            if (comparator == null)
                throw new IllegalArgumentException("comparator cannot be null");
            return EQUAL_TO == comparator;
        }
    },
    /**
     * The two values are not equal (!=).
     */
    NOT_EQUAL_TO("!=") {

        @Override
        public boolean includes(ValueComparator comparator) {
            if (comparator == null)
                throw new IllegalArgumentException("comparator cannot be null");
            return EQUAL_TO != comparator;
        }
    },
    /**
     * Unknown, meaning that Protempa cannot tell, for example, if a value
     * may be less than another value but Protempa cannot tell with complete
     * certainty.
     */
    UNKNOWN("?") {

        @Override
        public boolean includes(ValueComparator comparator) {
            if (comparator == null)
                throw new IllegalArgumentException("comparator cannot be null");
            return UNKNOWN == comparator;
        }
    },
    /**
     * The first value is greater than or equal to (>=) the second. Includes
     * {@link ValueComparator#EQUAL_TO} and 
     * {@link ValueComparator#GREATER_THAN}.
     */
    GREATER_THAN_OR_EQUAL_TO(">=") {

        @Override
        public boolean includes(ValueComparator comparator) {
            if (comparator == null)
                throw new IllegalArgumentException("comparator cannot be null");
            return EQUAL_TO == comparator
                    || GREATER_THAN == comparator
                    || GREATER_THAN_OR_EQUAL_TO == comparator;
        }
    },
    /**
     * The first value is less than or equal to (<=) the second. Includes
     * {@link ValueComparator#EQUAL_TO} and 
     * {@link ValueComparator#LESS_THAN}.
     */
    LESS_THAN_OR_EQUAL_TO("<=") {

        @Override
        public boolean includes(ValueComparator comparator) {
            if (comparator == null)
                throw new IllegalArgumentException("comparator cannot be null");
            return EQUAL_TO == comparator || LESS_THAN == comparator
                    || LESS_THAN_OR_EQUAL_TO == comparator;
        }
    },
    /**
     * The value is in the list.
     */
    IN("IN") {

        @Override
        public boolean includes(ValueComparator comparator) {
            if (comparator == null)
                throw new IllegalArgumentException("comparator cannot be null");
            return IN == comparator;
        }
    },
    /**
     * The value is not in the list.
     */
    NOT_IN("NOT_IN") {

        @Override
        public boolean includes(ValueComparator comparator) {
            if (comparator == null)
                throw new IllegalArgumentException("comparator cannot be null");
            return NOT_IN == comparator;
        }
    };

    private static final Map<String, ValueComparator> compStringToComp = 
            new HashMap<>();

    static {
        compStringToComp.put(LESS_THAN.getComparatorString(), LESS_THAN);
        compStringToComp.put(EQUAL_TO.getComparatorString(), EQUAL_TO);
        compStringToComp.put(GREATER_THAN.getComparatorString(), GREATER_THAN);
        compStringToComp.put(LESS_THAN_OR_EQUAL_TO.getComparatorString(),
                LESS_THAN_OR_EQUAL_TO);
        compStringToComp.put(GREATER_THAN_OR_EQUAL_TO.getComparatorString(),
                GREATER_THAN_OR_EQUAL_TO);
        compStringToComp.put(NOT_EQUAL_TO.getComparatorString(), NOT_EQUAL_TO);
        compStringToComp.put(IN.getComparatorString(), IN);
        compStringToComp.put(NOT_IN.getComparatorString(), NOT_IN);
        compStringToComp.put(UNKNOWN.getComparatorString(), UNKNOWN);
    }

    /**
     * Gets the comparison object corresponding to the given mathematical 
     * comparison symbol.
     * <ul>
     * <li>"&lt;" corresponds to <code>LESS_THAN</code>.</li>
     * <li>"&gt;" corresponds to <code>GREATER_THAN</code>.</li>
     * <li>"&lt;=" corresponds to <code>LESS_THAN_OR_EQUAL_TO</code>.</li>
     * <li>"&gt;=" corresponds to <code>GREATER_THAN_OR_EQUAL_TO</code>.</li>
     * <li>"=" corresponds to <code>EQUAL_TO</code>.</li>
     * <li>"!=" corresponds to <code>NOT_EQUAL_TO</code>.</li>
     * </ul>
     * 
     * @param compString
     *            a <code>String</code> from the above list.
     * @return a <code>ValueComparator</code> corresponding to the given
     *         comparison string, or <code>null</code> if one of the above
     *         strings was not passed in.
     * @throws ValueComparatorFormatException
     *             if <code>compString</code> could not be parsed.
     */
    public static ValueComparator parse(String compString) {
        ValueComparator result = compStringToComp.get(compString);
        if (result == null) {
            throw new ValueComparatorFormatException();
        }
        return compStringToComp.get(compString);
    }

    /**
     * The string associated with this comparator object.
     */
    private final String name;

    /**
     * Creates a comparison object with the given string.
     * 
     * @param name
     *            a <code>String</code>.
     * @param compatibleTypes
     *            a {@link ValueFactory[]} for the types that are applicable to
     *            this comparator..
     */
    private ValueComparator(String name) {
        this.name = name;
    }

    /**
     * Returns whether this {@link ValueComparator} is the same as or includes
     * the specified {@link ValueComparator}.
     * 
     * @param comparator
     *            a {@link ValueComparator}.
     * @return <code>true</code> if this {@link ValueComparator} is subsumed by
     *         the specified {@link ValueComparator}, <code>false</code>
     *         otherwise.
     */
    public abstract boolean includes(ValueComparator comparator);
    
    /**
     * Returns whether two values have the relationship specified by this
     * value comparator. It returns the same result as calling a value's
     * {@link Value#compare(org.protempa.proposition.value.Value) } method,
     * except it also handles gracefully the value being <code>null</code>.
     * 
     * @param lhsValue the left-hand-side value. May be <code>null</code>.
     * @param rhsValue the right-hand-side value. May be <code>null</code>.
     * @return <code>true</code> if the two values have the relationship
     * specified by this value comparator, <code>false</code> if not.
     */
    public boolean compare(Value lhsValue, Value rhsValue) {
        if (lhsValue != null) {
            return includes(lhsValue.compare(rhsValue));
        } else if (rhsValue != null) {
            return ValueComparator.NOT_EQUAL_TO.includes(this);
        } else {
            return ValueComparator.EQUAL_TO.includes(this);
        }
    }

    /**
     * Gets the string associated with this comparator object.
     * 
     * @return a {@link String}.
     */
    public final String getComparatorString() {
        return name;
    }
}
