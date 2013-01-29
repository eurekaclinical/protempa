/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
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


import junit.framework.TestCase;

/**
 * @author Andrew Post
 */
public class InequalityNumberValueTest extends TestCase {
    
    public InequalityNumberValueTest(String arg0) {
        super(arg0);
    }

    @Override
    public void setUp() throws Exception {
    }

    @Override
    public void tearDown() throws Exception {
    }

    public void testLessThanGreaterThan() {
        InequalityNumberValue val = new InequalityNumberValue(
                ValueComparator.GREATER_THAN, 20);
        assertEquals(ValueComparator.GREATER_THAN,
                val.compare(new InequalityNumberValue(
                ValueComparator.LESS_THAN, 10)));
    }

    public void testLessThanUnknown() {
        InequalityNumberValue val = new InequalityNumberValue(
                ValueComparator.GREATER_THAN, 20);
        assertEquals(ValueComparator.UNKNOWN, 
                val.compare(new InequalityNumberValue(
                ValueComparator.LESS_THAN, 40)));
    }

    public void testLessThanGreaterThanSame() {
        InequalityNumberValue val = new InequalityNumberValue(
                ValueComparator.GREATER_THAN, 20);
        assertEquals(ValueComparator.GREATER_THAN,
                val.compare(new InequalityNumberValue(
                ValueComparator.LESS_THAN, 20)));
    }

    public void testLessThanSameDouble() {
        InequalityNumberValue val = new InequalityNumberValue(
                ValueComparator.GREATER_THAN, 20);
        assertEquals(ValueComparator.GREATER_THAN,
                val.compare(NumberValue.getInstance(20)));
    }

    public void testLessThanNumberValue() {
        InequalityNumberValue val = new InequalityNumberValue(
                ValueComparator.LESS_THAN, .4);
        assertEquals(ValueComparator.LESS_THAN,
                val.compare(NumberValue.getInstance(.8)));
    }

    public void testLessThanNumberValue2() {
        InequalityNumberValue val =
                new InequalityNumberValue(ValueComparator.LESS_THAN, 4);
        NumberValue numberValue = NumberValue.getInstance(3);
        assertEquals(ValueComparator.UNKNOWN, val.compare(numberValue));
    }

    public void testCompareDateValue() {
        InequalityNumberValue val = new InequalityNumberValue(
                ValueComparator.LESS_THAN, .4);
        DateValue dateValue = DateValue.getInstance();
        assertEquals(ValueComparator.UNKNOWN, val.compare(dateValue));
    }
}
