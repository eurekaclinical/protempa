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
import java.util.Date;
import java.util.Locale;

import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.AbsoluteTimeUnit;

import junit.framework.TestCase;
import org.protempa.proposition.value.AbsoluteTimeGranularityUtil;

/**
 * @author Andrew Post
 */
public class PointIntervalTest extends TestCase {

    private static final DateFormat DATE_FORMAT = 
            DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
            Locale.US);
    private Interval interval;

    /**
     * Constructor for IntervalTest.
     *
     * @param arg0
     */
    public PointIntervalTest(String arg0) {
        super(arg0);
    }

    @Override
    protected void setUp() throws Exception {
        Date d = DATE_FORMAT.parse("3/2/07 3:11 am");
        this.interval = new SimpleInterval(
                AbsoluteTimeGranularityUtil.asPosition(d),
                AbsoluteTimeGranularity.MINUTE);
    }

    @Override
    protected void tearDown() throws Exception {
        this.interval = null;
    }

    public void testPointIntervalMinimumDistanceZero() {
        assertEquals(Long.valueOf(0L), interval.getMinimumLength());
    }

    public void testPointIntervalMinDistanceZero() {
        assertEquals(Long.valueOf(0L), interval.getMinLength());
    }

    public void testPointIntervalDistanceUnitMinute() {
        assertEquals(AbsoluteTimeUnit.MINUTE, interval.getLengthUnit());
    }

    public void testPointIntervalMaximumDistanceZero() {
        assertEquals(Long.valueOf(0L), interval.getMaximumLength());
    }

    public void testPointIntervalMaxDistanceZero() {
        assertEquals(Long.valueOf(0L), interval.getMaxLength());
    }
}
