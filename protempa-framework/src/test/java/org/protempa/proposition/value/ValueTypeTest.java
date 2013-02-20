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



import java.util.Calendar;
import junit.framework.TestCase;

/**
 * @author Andrew Post
 */
public class ValueTypeTest extends TestCase {

    public void testPointZero() {
        assertEquals(new NumberValue(6.0), ValueType.VALUE.parse("6.0"));
        assertEquals("6.0", ValueType.VALUE.parse("6.0").getFormatted());
        assertEquals(new NumberValue(6.0), ValueType.VALUE.parse("6.0"));
        assertEquals("6.0", ValueType.VALUE.parse("6.0").getFormatted());
    }

    public void testValueParseInequalityValue() {
        assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
                new BigDecimal("0.1")),
                ValueType.VALUE.parse("<0.1"));
    }

    public void testInequalityDoubleParseValue() {
        assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
                new BigDecimal("0.1")),
                ValueType.INEQUALITYNUMBERVALUE.parse("<0.1"));
        assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
                new BigDecimal("0.4")),
                ValueType.INEQUALITYNUMBERVALUE.parse("<0.4"));
    }

    public void testNumberValueParserLeadingWhitespace() {
        assertEquals(NumberValue.getInstance(3),
                ValueType.NUMBERVALUE.parse("    3"));
    }

    public void testNumberFormatParserTrailingWhitespace() {
        assertEquals(NumberValue.getInstance(3),
                ValueType.NUMBERVALUE.parse("3    "));
    }

    public void testNumberFormatParserWhitespace() {
        assertEquals(NumberValue.getInstance(3),
                ValueType.NUMBERVALUE.parse("  3    "));
    }

    public void testPrecedingWhitespace() {
        assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
                new BigDecimal("0.4")),
                ValueType.INEQUALITYNUMBERVALUE.parse("       <0.4"));
    }

    public void testInterveningWhitespace() {
        assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
                new BigDecimal("0.4")),
                ValueType.INEQUALITYNUMBERVALUE.parse("<    0.4"));
    }

    public void testWhitespaceAfter() {
        assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
                new BigDecimal("0.4")),
                ValueType.INEQUALITYNUMBERVALUE.parse("<0.4     "));
    }

    public void testWhitespaceAllOver() {
        assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
                new BigDecimal("0.4")),
                ValueType.INEQUALITYNUMBERVALUE.parse("      <   0.4     "));
    }
    
    public void testParseDateViaDateValue() {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2011, Calendar.JANUARY, 1);
        DateValue expected = DateValue.getInstance(cal.getTime());
        DateValue observed = DateValue.parse("1/1/2011");
        assertEquals(expected, observed);
    }
    
    public void testParseDateViaValueType() {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2011, Calendar.JANUARY, 1);
        Value expected = DateValue.getInstance(cal.getTime());
        Value observed = ValueType.DATEVALUE.parse("1/1/2011");
        assertEquals(expected, observed);
    }
}
