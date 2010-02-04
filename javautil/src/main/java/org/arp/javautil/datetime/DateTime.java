package org.arp.javautil.datetime;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Andrew Post
 */
public final class DateTime {
	private static final Calendar utilityCal = Calendar.getInstance();

	/**
	 * 
	 */
	private DateTime() {
		super();
	}

	/**
	 * Merges {@link java.sql.Date} and {@link java.sql.Time} objects
	 * into a single <code>java.util.Date</code> object.
	 *
	 * @param date
	 *            a {@link java.sql.Date} object that is assumed to be
	 *            normalized (the hour, minute, second and millisecond values
	 *            are set to <code>0</code>). Cannot be <code>null</code> while
	 *            <code>time</code> is not.
	 * @param time
	 *            a {@link java.sql.Time} object. The date components are
	 *            assumed to be set to the "zero epoch" value of January 1,
	 *            1970. If null, the returned date will have its hour, minute,
	 *            and second values set to <code>0</code>.
	 * @return a new {@link Date} object, or <code>null</code> if both the
	 *         <code>date</code> and <code>time</code> parameters are
	 *         <code>null</code>.
	 */
	public static Date mergeDateAndTime(java.sql.Date date, 
            java.sql.Time time) {
        if (date == null && time == null)
			return null;
		if (date == null)
			throw new IllegalArgumentException(
					"date is null while time is not; time=" + time);
		synchronized (utilityCal) {
			if (date != null) {
				utilityCal.setTime(date);
			} else {
				utilityCal.clear();
			}
			int year = utilityCal.get(Calendar.YEAR);
			int month = utilityCal.get(Calendar.MONTH);
			int day = utilityCal.get(Calendar.DATE);
			if (time != null) {
				utilityCal.setTime(time);
			}
			int hour = utilityCal.get(Calendar.HOUR_OF_DAY);
			int minute = utilityCal.get(Calendar.MINUTE);
			int second = utilityCal.get(Calendar.SECOND);
			utilityCal.clear();
			utilityCal.set(Calendar.YEAR, year);
			utilityCal.set(Calendar.MONTH, month);
			utilityCal.set(Calendar.DATE, day);
			utilityCal.set(Calendar.HOUR_OF_DAY, hour);
			utilityCal.set(Calendar.MINUTE, minute);
			utilityCal.set(Calendar.SECOND, second);
			return utilityCal.getTime();
		}
	}
}