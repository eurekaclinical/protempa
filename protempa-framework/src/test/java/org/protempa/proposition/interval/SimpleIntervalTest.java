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
package org.protempa.proposition.interval;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.AbsoluteTimeUnit;

import junit.framework.TestCase;

public class SimpleIntervalTest extends TestCase {

    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);
    private Date d;
    private SimpleInterval interval;

    @Override
    protected void setUp() throws Exception {
        d = DATE_FORMAT.parse("1/1/07 1:00 am");
        this.interval = new SimpleInterval(d.getTime(),
                AbsoluteTimeGranularity.MINUTE, d.getTime(),
                AbsoluteTimeGranularity.MINUTE);
    }

    @Override
    protected void tearDown() throws Exception {
        this.interval = null;
        this.d = null;
    }

    public void testMinimumDistanceZero() {
        assertEquals(Long.valueOf(0L), interval.getMinimumLength());
    }

    public void testMaximumDistanceZero() {
        assertEquals(Long.valueOf(0L), interval.getMaximumLength());
    }

    public void test12HoursMinDistance() throws ParseException {
        Date d2 = DATE_FORMAT.parse("1/1/07 1:00 pm");
        Interval i2 = new SimpleInterval(d.getTime(),
                AbsoluteTimeGranularity.MINUTE, d2.getTime(),
                AbsoluteTimeGranularity.MINUTE);
        assertEquals(Long.valueOf(720), i2.getMinLength());
    }

    public void test12HoursMaxDistance() throws ParseException {
        Date d2 = DATE_FORMAT.parse("1/1/07 1:00 pm");
        Interval i2 = new SimpleInterval(d.getTime(),
                AbsoluteTimeGranularity.MINUTE, d2.getTime(),
                AbsoluteTimeGranularity.MINUTE);
        assertEquals(Long.valueOf(720), i2.getMaxLength());
    }

    public void test12HoursDistanceUnit() throws ParseException {
        Date d2 = DATE_FORMAT.parse("1/1/07 1:00 pm");
        Interval i2 = new SimpleInterval(d.getTime(),
                AbsoluteTimeGranularity.MINUTE, d2.getTime(),
                AbsoluteTimeGranularity.MINUTE);
        assertEquals(AbsoluteTimeUnit.MINUTE, i2.getLengthUnit());
    }

    public void test12HoursReallyInHoursGranularity() throws ParseException {
        Date d2 = DATE_FORMAT.parse("1/1/07 1:00 pm");
        Interval i2 = new SimpleInterval(d.getTime(),
                AbsoluteTimeGranularity.MINUTE, d2.getTime(),
                AbsoluteTimeGranularity.MINUTE);
        long distance = AbsoluteTimeGranularity.MINUTE.distance(interval.getMinStart(), i2.getMinFinish(), i2.getFinishGranularity(),
                AbsoluteTimeUnit.HOUR);
        assertEquals(12L, distance);
    }

    public void test12HoursReallyInHoursInterval() throws ParseException {
        Date d2 = DATE_FORMAT.parse("1/1/07 1:00 pm");
        Interval i2 = new SimpleInterval(d.getTime(),
                AbsoluteTimeGranularity.MINUTE, d2.getTime(),
                AbsoluteTimeGranularity.MINUTE);
        long distance = i2.minLengthIn(AbsoluteTimeUnit.HOUR);
        assertEquals(12L, distance);
    }

    public void test12HoursReallyInSecondsInterval() throws ParseException {
        Date d2 = DATE_FORMAT.parse("1/1/07 1:00 pm");
        Interval i2 = new SimpleInterval(d.getTime(),
                AbsoluteTimeGranularity.MINUTE, d2.getTime(),
                AbsoluteTimeGranularity.MINUTE);
        long distance = i2.minLengthIn(AbsoluteTimeUnit.SECOND);
        assertEquals(43200L, distance);
    }

    public void test12HoursMinimumDistance() throws ParseException {
        Date d2 = DATE_FORMAT.parse("1/1/07 1:00 pm");
        Interval i2 = new SimpleInterval(d.getTime(),
                AbsoluteTimeGranularity.MINUTE, d2.getTime(),
                AbsoluteTimeGranularity.MINUTE);
        assertEquals(Long.valueOf(43140001L), i2.getMinimumLength());
    }

    public void test12HoursMaximumDistance() throws ParseException {
        Date d2 = DATE_FORMAT.parse("1/1/07 1:00 pm");
        Interval i2 = new SimpleInterval(d.getTime(),
                AbsoluteTimeGranularity.MINUTE, d2.getTime(),
                AbsoluteTimeGranularity.MINUTE);
        assertEquals(Long.valueOf(43259999L), i2.getMaximumLength());
    }

    public void testOneMinuteMinimumDistance() throws ParseException {
        Date d2 = DATE_FORMAT.parse("1/1/07 1:01 am");
        Interval i2 = new SimpleInterval(d.getTime(),
                AbsoluteTimeGranularity.MINUTE, d2.getTime(),
                AbsoluteTimeGranularity.MINUTE);
        assertEquals(Long.valueOf(1L), i2.getMinimumLength());
    }

    public void testOneMinuteMinDistance() throws ParseException {
        Date d2 = DATE_FORMAT.parse("1/1/07 1:01 am");
        Interval i2 = new SimpleInterval(d.getTime(),
                AbsoluteTimeGranularity.MINUTE, d2.getTime(),
                AbsoluteTimeGranularity.MINUTE);
        assertEquals(Long.valueOf(60L), i2.minLengthIn(AbsoluteTimeUnit.SECOND));
    }
}
