package org.protempa.proposition.value;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;

import org.protempa.proposition.value.AbsoluteTimeGranularity;

import junit.framework.TestCase;

public class AbsoluteTimeGranularityTest extends TestCase {
	private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(
			DateFormat.SHORT, Locale.US);

	public void testLatestMonthApril() throws ParseException {
		long april1 = DATE_FORMAT.parse("4/1/07").getTime();
		assertEquals(april1 + 30 * 24 * 60 * 60 * 1000L - 1,
				AbsoluteTimeGranularity.MONTH.latest(april1));
	}
	
	public void testLatestMonthFebruary2007() throws ParseException {
		long feb1 = DATE_FORMAT.parse("2/1/07").getTime();
		assertEquals(feb1 + 28 * 24 * 60 * 60 * 1000L - 1,
				AbsoluteTimeGranularity.MONTH.latest(feb1));
	}

	public void testEarliestMonth() throws ParseException {
		long april1 = DATE_FORMAT.parse("4/1/07").getTime();
		assertEquals(april1, AbsoluteTimeGranularity.MONTH.earliest(april1));
	}
}
