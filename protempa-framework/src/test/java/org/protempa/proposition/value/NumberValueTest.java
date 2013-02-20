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

import java.math.BigDecimal;
import java.text.NumberFormat;



import java.util.Date;
import junit.framework.TestCase;

/**
 * @author Andrew Post
 */
public class NumberValueTest extends TestCase {

    private NumberValue val;

    /**
     * Constructor for GreaterThanDoubleValueTest.
     *
     * @param arg0
     */
    public NumberValueTest(String arg0) {
        super(arg0);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        this.val = NumberValue.getInstance(20);
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        this.val = null;
    }

    public void testGreaterThanTrueCompareTo() {
        assertTrue(val.compareTo(NumberValue.getInstance(10)) > 0);
    }

    public void testGreaterThanTrueCompare() {
        assertTrue(val.compare(NumberValue.getInstance(10)) == ValueComparator.GREATER_THAN);
    }

    public void testGreaterThanFalseCompareTo() {
        assertFalse(val.compareTo(NumberValue.getInstance(30)) > 0);
    }

    public void testGreaterThanFalseCompare() {
        assertFalse(val.compare(NumberValue.getInstance(30)) == ValueComparator.GREATER_THAN);
    }

    public void testLessThanTrueCompareTo() {
        assertTrue(val.compareTo(NumberValue.getInstance(30)) < 0);
    }

    public void testLessThanTrueCompare() {
        assertTrue(val.compare(NumberValue.getInstance(30)) == ValueComparator.LESS_THAN);
    }

    public void testEqualToTrue() {
        assertTrue(val.equals(NumberValue.getInstance(20)));
    }

    public void testEqualToFalse() {
        assertFalse(val.equals(NumberValue.getInstance(30)));
    }

    public void testGreaterThanInequalityTrue() {
        assertTrue(val.compare(new InequalityNumberValue(
                ValueComparator.GREATER_THAN, 30)) == ValueComparator.LESS_THAN);
    }

    public void testLessThanInequalityGreaterThan() {
        assertTrue(val.compare(new InequalityNumberValue(
                ValueComparator.LESS_THAN, 10)) == ValueComparator.GREATER_THAN);
    }

    public void testLessThanInequalityUnknown() {
        assertTrue(val.compare(new InequalityNumberValue(
                ValueComparator.LESS_THAN, 30)) == ValueComparator.UNKNOWN);
    }

    public void testLessThanInequalitySameDoubleGreaterThan() {
        assertTrue(val.compare(new InequalityNumberValue(
                ValueComparator.LESS_THAN, 20)) == ValueComparator.GREATER_THAN);
    }
    
    public void testCompareDateValue() {
        assertEquals(ValueComparator.UNKNOWN, 
                val.compare(DateValue.getInstance(new Date())));
    }
    
    public void testNullArg() {
        assertEquals(NumberValue.getInstance(BigDecimal.ZERO), 
                NumberValue.getInstance(null));
    }

    public void testDecimalPlaces1() {
        assertEquals("30", ValueType.NUMBERVALUE.parse("30").getFormatted());
    }

    public void testDecimalPlaces2() {
        assertEquals("30.0", 
                ValueType.NUMBERVALUE.parse("30.0").getFormatted());
    }

    public void testDecimalPlaces3() {
        assertEquals("30.00", 
                ValueType.NUMBERVALUE.parse("30.00").getFormatted());
    }

    public void testFormattingBigInteger() {
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        assertEquals("1000", format.format(new BigDecimal("1000")));
    }
}
