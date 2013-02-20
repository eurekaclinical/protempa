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
package org.protempa.proposition.interval;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.AbsoluteTimeUnit;
import static
        org.protempa.proposition.value.AbsoluteTimeGranularityUtil.asPosition;

import junit.framework.TestCase;

public class DefaultIntervalTest extends TestCase {

	private static final DateFormat DATE_FORMAT = DateFormat
			.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);

	private Date d;
	private DefaultInterval interval;

	@Override
	protected void setUp() throws Exception {
		d = DATE_FORMAT.parse("1/1/07 1:00 am");
		this.interval = new DefaultInterval(asPosition(d),
				AbsoluteTimeGranularity.MINUTE, asPosition(d),
				AbsoluteTimeGranularity.MINUTE, Long.valueOf(0L), null);
	}

	@Override
	protected void tearDown() throws Exception {
		this.interval = null;
		this.d = null;
	}

	public void testPrimitiveParameterMinStart() {
		/*
		 * Primitive parameter interval as per Combi et al. Methods Inf. Med.
		 * 1995;34:458-74.
		 */
		assertTrue(interval.isValid());
	}

	public void testDefaultIntervalMinimumDistanceZero() {
		assertEquals(Long.valueOf(0L), interval.getMinimumLength());
	}

	public void testDefaultIntervalMaximumDistanceZero() {
		assertEquals(Long.valueOf(0L), interval.getMaximumLength());
	}

	public void testDefaultInterval12HoursMinDistance() throws ParseException {
		Date d2 = DATE_FORMAT.parse("1/1/07 1:00 pm");
		Interval i2 = new DefaultInterval(asPosition(d),
				AbsoluteTimeGranularity.MINUTE, asPosition(d2),
				AbsoluteTimeGranularity.MINUTE, null, null);
		assertEquals(Long.valueOf(720), i2.getMinLength());
	}

	public void testDefaultInterval12HoursMaxDistance() throws ParseException {
		Date d2 = DATE_FORMAT.parse("1/1/07 1:00 pm");
		Interval i2 = new DefaultInterval(asPosition(d),
				AbsoluteTimeGranularity.MINUTE, asPosition(d2),
				AbsoluteTimeGranularity.MINUTE, null, null);
		assertEquals(Long.valueOf(720), i2.getMaxLength());
	}

	public void testDefaultInterval12HoursDistanceUnit() throws ParseException {
		Date d2 = DATE_FORMAT.parse("1/1/07 1:00 pm");
		Interval i2 = new DefaultInterval(asPosition(d),
				AbsoluteTimeGranularity.MINUTE, asPosition(d2),
				AbsoluteTimeGranularity.MINUTE, null, null);
		assertEquals(AbsoluteTimeUnit.MINUTE, i2.getLengthUnit());
	}

	public void testStartSpecifiedOnly() {
		Interval i = new DefaultInterval(1117310400000L,
				AbsoluteTimeGranularity.MINUTE, null, null, null, null);
		assertEquals(Long.valueOf(1117310400000L), i.getMinimumStart());
	}

}
