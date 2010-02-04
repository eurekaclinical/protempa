package org.protempa.proposition;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.protempa.proposition.DefaultInterval;
import org.protempa.proposition.Interval;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.AbsoluteTimeUnit;

import junit.framework.TestCase;

public class DefaultIntervalTest extends TestCase {

	private static final DateFormat DATE_FORMAT = DateFormat
			.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);

	private Date d;
	private DefaultInterval interval;

	@Override
	protected void setUp() throws Exception {
		d = DATE_FORMAT.parse("1/1/07 1:00 am");
		this.interval = new DefaultInterval(d.getTime(),
				AbsoluteTimeGranularity.MINUTE, d.getTime(),
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
		Interval i2 = new DefaultInterval(d.getTime(),
				AbsoluteTimeGranularity.MINUTE, d2.getTime(),
				AbsoluteTimeGranularity.MINUTE, null, null);
		assertEquals(Long.valueOf(720), i2.getMinLength());
	}

	public void testDefaultInterval12HoursMaxDistance() throws ParseException {
		Date d2 = DATE_FORMAT.parse("1/1/07 1:00 pm");
		Interval i2 = new DefaultInterval(d.getTime(),
				AbsoluteTimeGranularity.MINUTE, d2.getTime(),
				AbsoluteTimeGranularity.MINUTE, null, null);
		assertEquals(Long.valueOf(720), i2.getMaxLength());
	}

	public void testDefaultInterval12HoursDistanceUnit() throws ParseException {
		Date d2 = DATE_FORMAT.parse("1/1/07 1:00 pm");
		Interval i2 = new DefaultInterval(d.getTime(),
				AbsoluteTimeGranularity.MINUTE, d2.getTime(),
				AbsoluteTimeGranularity.MINUTE, null, null);
		assertEquals(AbsoluteTimeUnit.MINUTE, i2.getLengthUnit());
	}

	public void testStartSpecifiedOnly() {
		Interval i = new DefaultInterval(1117310400000L,
				AbsoluteTimeGranularity.MINUTE, null, null, null, null);
		assertEquals(Long.valueOf(1117310400000L), i.getMinimumStart());
	}

}
