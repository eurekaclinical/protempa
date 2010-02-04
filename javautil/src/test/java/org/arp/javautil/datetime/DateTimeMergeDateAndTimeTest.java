package org.arp.javautil.datetime;

import java.util.Calendar;
import java.util.Date;

import org.arp.javautil.datetime.DateTime;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Andrew Post
 */
public class DateTimeMergeDateAndTimeTest extends TestCase {
	private java.sql.Date date;

	private java.sql.Time time;

	/*
	 * @see TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Calendar cal = Calendar.getInstance();

		cal.clear();
		cal.set(Calendar.YEAR, 2004);
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		cal.set(Calendar.DATE, 16);

		date = new java.sql.Date(cal.getTimeInMillis());

		cal.clear();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 20);
		cal.set(Calendar.SECOND, 22);

		time = new java.sql.Time(cal.getTimeInMillis());
	}

	/*
	 * @see TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		date = null;
		time = null;
	}

	/**
	 * Constructor for PatientPatternsUtilitiesTest.
	 * 
	 * @param arg0
	 */
	public DateTimeMergeDateAndTimeTest(String arg0) {
		super(arg0);
	}

	public void testMergeDateAndTimeYear() {
		Calendar cal = Calendar.getInstance();
		Date result = DateTime.mergeDateAndTime(date, time);
		cal.setTime(result);
		Assert.assertEquals(2004, cal.get(Calendar.YEAR));
	}

	public void testMergeDateAndTimeMonth() {
		Calendar cal = Calendar.getInstance();
		Date result = DateTime.mergeDateAndTime(date, time);
		cal.setTime(result);
		Assert.assertEquals(Calendar.DECEMBER, cal.get(Calendar.MONTH));
	}

	public void testMergeDateAndTimeDate() {
		Calendar cal = Calendar.getInstance();
		Date result = DateTime.mergeDateAndTime(date, time);
		cal.setTime(result);
		Assert.assertEquals(16, cal.get(Calendar.DATE));
	}

	public void testMergeDateAndTimeHour() {
		Calendar cal = Calendar.getInstance();
		Date result = DateTime.mergeDateAndTime(date, time);
		cal.setTime(result);
		Assert.assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
	}

	public void testMergeDateAndTimeMinute() {
		Calendar cal = Calendar.getInstance();
		Date result = DateTime.mergeDateAndTime(date, time);
		cal.setTime(result);
		Assert.assertEquals(20, cal.get(Calendar.MINUTE));
	}

	public void testMergeDateAndTimeSecond() {
		Calendar cal = Calendar.getInstance();
		Date result = DateTime.mergeDateAndTime(date, time);
		cal.setTime(result);
		Assert.assertEquals(22, cal.get(Calendar.SECOND));
	}
}
