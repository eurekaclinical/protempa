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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protempa.proposition.interval;

import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.AbsoluteTimeUnit;

/**
 *
 * @author Andrew Post
 */
public abstract class AbstractIntervalUtilTestBase extends TestCase {
    
    private AbsoluteTimeIntervalFactory ivalFactory;
    private static final Calendar cal = Calendar.getInstance();

    @Override
    protected void setUp() throws Exception {
        this.ivalFactory = new AbsoluteTimeIntervalFactory();
        cal.clear();
    }
    
    @Override
    protected void tearDown() throws Exception {
        this.ivalFactory = null;
    }
    
    public final void testDistanceBetweenWithSmallerUnits() {
        assertEquals(40320L, distanceBetween(AbsoluteTimeUnit.MINUTE));
    }
    
    public final void testDistanceBetweenWithBiggerUnits() {
        assertEquals(1L, distanceBetween(AbsoluteTimeUnit.MONTH));
    }
    
    public final void testDistanceBetweenWithBiggerBiggerUnits() {
        assertEquals(0L, distanceBetween(AbsoluteTimeUnit.YEAR));
    }
    
    protected abstract long distanceBetween(AbsoluteTimeUnit absoluteTimeUnit);
    
    public final void testDistanceBtwFormattedShort() {
        assertEquals("28 d", distanceBetweenFormattedShort());
    }

    public final void testDistanceBtwFormattedShortWithSmallerUnits() {
        assertEquals("40,320 min", 
                distanceBetweenFormattedShort(AbsoluteTimeUnit.MINUTE));
    }

    public final void testDistanceBtwFormattedShortWithBiggerUnits() {
        assertEquals("1 mo",
                distanceBetweenFormattedShort(AbsoluteTimeUnit.MONTH));
    }

    public final void testDistanceBtwFormattedShortWithBiggerBiggerUnits() {
        assertEquals("0 y", 
                distanceBetweenFormattedShort(AbsoluteTimeUnit.YEAR));
    }
    
    protected abstract String distanceBetweenFormattedShort();
    
    protected abstract String distanceBetweenFormattedShort(
            AbsoluteTimeUnit absoluteTimeUnit);
    
    public final void testDistanceBtwFormattedMedium() {
        assertEquals("28 day", distanceBetweenFormattedMedium());
    }

    public final void testDistanceBtwFormattedMediumWithSmallerUnits() {
        assertEquals("40,320 min", 
                distanceBetweenFormattedMedium(AbsoluteTimeUnit.MINUTE));
    }

    public final void testDistanceBtwFormattedMediumWithBiggerUnits() {
        assertEquals("1 mo", 
                distanceBetweenFormattedMedium(AbsoluteTimeUnit.MONTH));
    }

    public final void testDistanceBtwFormattedMediumWithBiggerBiggerUnits() {
        assertEquals("0 yr",
                distanceBetweenFormattedMedium(AbsoluteTimeUnit.YEAR));
    }
    
    protected abstract String distanceBetweenFormattedMedium();
    
    protected abstract String distanceBetweenFormattedMedium(
            AbsoluteTimeUnit absoluteTimeUnit);
    
    public final void testDistanceBtwFormattedLong() {
        assertEquals("28 day(s)", distanceBetweenFormattedLong());
    }

    public final void testDistanceBtwFormattedLongWithSmallerUnits() {
        assertEquals("40,320 minute(s)",
                distanceBetweenFormattedLong(AbsoluteTimeUnit.MINUTE));
    }

    public final void testDistanceBtwFormattedLongWithBiggerUnits() {
        assertEquals("1 month(s)", 
                distanceBetweenFormattedLong(AbsoluteTimeUnit.MONTH));
    }

    public final void testDistanceBtwFormattedLongWithBiggerBiggerUnits() {
        assertEquals("0 year(s)",
                distanceBetweenFormattedLong(AbsoluteTimeUnit.YEAR));
    }
    
    protected abstract String distanceBetweenFormattedLong();
    
    protected abstract String distanceBetweenFormattedLong(
            AbsoluteTimeUnit absoluteTimeUnit);
    
    protected Interval ival1() {
        cal.set(2011, Calendar.JANUARY, 1);
        Date d1 = cal.getTime();
        cal.set(2011, Calendar.FEBRUARY, 1);
        Date d2 = cal.getTime();
        return ivalFactory.getInstance(d1, AbsoluteTimeGranularity.DAY, 
                d2, AbsoluteTimeGranularity.DAY);
    }

    protected Interval ival2() {
        cal.set(2011, Calendar.MARCH, 1);
        Date d1 = cal.getTime();
        cal.set(2011, Calendar.APRIL, 1);
        Date d2 = cal.getTime();
        return ivalFactory.getInstance(d1, AbsoluteTimeGranularity.DAY, 
                d2, AbsoluteTimeGranularity.DAY);
    }
}
