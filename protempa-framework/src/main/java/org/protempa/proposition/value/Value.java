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

import java.text.Format;

/**
 * A value (number, string, category, etc.). PROTEMPA implements its own value
 * system rather than use the built-in primitive or object types in
 * Java. PROTEMPA implements a special numeric type for representing 
 * "inequalities" such as <code>&gt; 100</code> for representing laboratory
 * test results when the result is outside of the reportable range of the test.
 * PROTEMPA implements a single number type that represents integers and
 * floating point numbers with preservation of significant digits. Values can
 * be compared for natural order to any other value regardless of type.
 * PROTEMPA provides parsers that attempt to pick an appropriate value type 
 * when the contents of the string are of an unknown type. PROTEMPA provides
 * flexible formatting into strings for display purposes. PROTEMPA's value
 * system includes a value type for dates and for lists of values. Values
 * have a special {@link #compare(org.protempa.proposition.value.Value)} method
 * for comparing values by natural order. The return type of this method,
 * a {@link ValueComparator}, can return greater-than, less-than, equal-to
 * or unknown, the latter being for when the values being compared are not
 * comparable. Most values can be converted into built-in primitives or object
 * types. Values' types are represented by the {@link ValueType} enum.
 * 
 * @author Andrew Post
 */
public interface Value extends ValueVisitable {

    /**
     * Returns a string representing this value for display purposes.
     * 
     * @return a {@link String}. Guaranteed not <code>null</code>.
     */
    String getFormatted();
    
    /**
     * Returns a string representing this value, formatted by the 
     * provided {@link Format}.
     * 
     * @param format a {@link Format}. Passing in a <code>null</code> will
     * result in the same behavior as {@link #getFormatted() }.
     * @return a {@link String}.
     */
    String format(Format format);

    /**
     * Returns whether the given value is greater, less than, equal to or not
     * equal to this value. If the two values are not comparable, it
     * returns "unknown". If this object is being compared to a list value, it 
     * tests list membership. This method cannot be used to compare two lists
     * for equality. It is intended for checking whether a value satisfies
     * a constraint.
     * 
     * @param val a {@link Value}. If <code>null</code>,
     * {@link ValueComparator#UNKNOWN} is returned.
     * @return a {@link ValueComparator indicating that the given value is
     * greater, less than, equal to, not equal to, in or not in this value. If 
     * the two values are not comparable, {@link ValueComparator#UNKNOWN} is 
     * returned.
     */
    ValueComparator compare(Value val);

    /**
     * Returns this object's type, guaranteed not <code>null</code>.
     * 
     * @return a {@link ValueType}.
     */
    ValueType getType();

    /**
     * Returns a value from a cache that is equal to this one, if such a value
     * exists. Otherwise, it returns itself.
     * 
     * @return a {@link Value} for which {@link #equals(Value) }
     * is true.
     */
    Value replace();
    
    ValueBuilder asBuilder();
}
