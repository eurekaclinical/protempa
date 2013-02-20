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

import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;

/**
 *
 * @author Andrew Post
 */
public class DateValueTest extends TestCase {
    private Date nowDate;

    @Override
    protected void setUp() throws Exception {
        this.nowDate = new Date();
    }

    @Override
    protected void tearDown() throws Exception {
        this.nowDate = null;
    }
    
    
    public void testCompareNumberValue() {
        DateValue now = DateValue.getInstance(this.nowDate);
        NumberValue num = NumberValue.getInstance(0L);
        assertEquals(ValueComparator.UNKNOWN, now.compare(num));
    }
    
    public void testCompareNominalValue() {
        DateValue now = DateValue.getInstance(this.nowDate);
        Value str = NominalValue.getInstance("foo");
        assertEquals(ValueComparator.UNKNOWN, now.compare(str));
    }
    
    public void testCompareListValue() {
        DateValue now = DateValue.getInstance(this.nowDate);
        Value list = new ValueList<DateValue>();
        assertEquals(ValueComparator.NOT_IN, now.compare(list));
    }
    
    public void testCompareDatesEqualTo() {
        DateValue now = DateValue.getInstance(this.nowDate);
        DateValue now2 = DateValue.getInstance(this.nowDate);
        assertEquals(ValueComparator.EQUAL_TO, now.compare(now2));
    }
    
    public void testCompareDatesLessThan() {
        Calendar cal = Calendar.getInstance();
        DateValue now = DateValue.getInstance(cal.getTime());
        cal.add(Calendar.MONTH, 1);
        DateValue plus1Month = DateValue.getInstance(cal.getTime());
        assertEquals(ValueComparator.LESS_THAN, now.compare(plus1Month));
    }
    
    public void testNullArg() {
        DateValue.getInstance(null);
    }
}
