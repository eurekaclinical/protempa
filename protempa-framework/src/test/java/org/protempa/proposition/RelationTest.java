package org.protempa.proposition;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;

import junit.framework.TestCase;

import org.protempa.DataSourceType;
import org.protempa.DatabaseDataSourceType;
import org.protempa.DerivedDataSourceType;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.AbsoluteTimeUnit;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.RelativeHourGranularity;
import org.protempa.proposition.value.RelativeHourUnit;
import org.protempa.proposition.value.Unit;

public class RelationTest extends TestCase {

    private static final IntervalFactory intervalFactory =
            new IntervalFactory();
    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(
            DateFormat.SHORT, Locale.US);
    private static final DateFormat DATE_TIME_FORMAT = 
            DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
            Locale.US);

    public void test1HourApartHours() {
        Interval i1 = new DefaultInterval(0L, RelativeHourGranularity.HOUR, 0L,
                RelativeHourGranularity.HOUR, null, null);
        Interval i2 = new DefaultInterval(1L * 60 * 60 * 1000,
                RelativeHourGranularity.HOUR, 1L * 60 * 60 * 1000,
                RelativeHourGranularity.HOUR, null, null);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 1, AbsoluteTimeUnit.HOUR, 1, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertTrue(r.hasRelation(i1, i2));
    }
	private static final DataSourceType dbDataSourceType = new DatabaseDataSourceType("MockTestDatabase");
	private static final DataSourceType derivedDataSourceType = new DerivedDataSourceType();

    public void test1MinuteApartHours() {
        Interval i1 = new DefaultInterval(0L, RelativeHourGranularity.HOUR, 0L,
                RelativeHourGranularity.HOUR, null, null);
        Interval i2 = new DefaultInterval(1L, RelativeHourGranularity.HOUR, 1L,
                RelativeHourGranularity.HOUR, null, null);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 1, AbsoluteTimeUnit.MINUTE, 1, AbsoluteTimeUnit.MINUTE,
                null, null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    public void testAtLeast1MinuteApartHours() {
        Interval i1 = new DefaultInterval(0L, RelativeHourGranularity.HOUR, 0L,
                RelativeHourGranularity.HOUR, null, null);
        Interval i2 = new DefaultInterval(1L * 60 * 60 * 1000,
                RelativeHourGranularity.HOUR, 1L * 60 * 60 * 1000,
                RelativeHourGranularity.HOUR, null, null);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 1, AbsoluteTimeUnit.MINUTE, null, null, null, null, null,
                null);
        assertTrue(r.hasRelation(i1, i2));
    }

    public void test1157PMAnd1159PMZeroDaysApart() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long elevenFiftyNine = format.parse("1/1/07 11:57 pm").getTime();
        long twelveOhOne = format.parse("1/1/07 11:59 pm").getTime();
        Interval i1 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(twelveOhOne,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 0, AbsoluteTimeUnit.DAY, 0, AbsoluteTimeUnit.DAY, null,
                null, null, null);
        assertTrue(r.hasRelation(i1, i2));
    }

    public void test1157PMAnd1159PMEquals() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long elevenFiftyNine = format.parse("1/1/07 11:57 pm").getTime();
        long twelveOhOne = format.parse("1/1/07 11:59 pm").getTime();
        Interval i1 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(0, AbsoluteTimeUnit.MINUTE, 0,
                AbsoluteTimeUnit.MINUTE, null, null, null, null, null, null,
                null, null, 0, AbsoluteTimeUnit.MINUTE, 0,
                AbsoluteTimeUnit.MINUTE);
        assertTrue(r.hasRelation(i1, i2));
    }

    public void test1157PMAnd1159PMEquals2() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long elevenFiftyNine = format.parse("1/1/07 11:57 pm").getTime();
        long twelveOhOne = format.parse("1/1/07 11:59 pm").getTime();
        Interval i1 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(0, null, 0, null, null, null, null, null,
                null, null, null, null, 0, null, 0, null);
        assertTrue(r.hasRelation(i1, i2));
    }

    public void test1159PMAnd1200AMExactlyOneDayApart() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long elevenFiftyNine = format.parse("1/1/07 11:59 pm").getTime();
        long twelveOhOne = format.parse("1/2/07 12:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(twelveOhOne,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 1, AbsoluteTimeUnit.DAY, 1, AbsoluteTimeUnit.DAY, null,
                null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    public void test1159PMAnd1259AMExactlyOneDayApart() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long elevenFiftyNine = format.parse("1/1/07 11:59 pm").getTime();
        long twelveOhOne = format.parse("1/2/07 12:59 am").getTime();
        Interval i1 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(twelveOhOne,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 1, AbsoluteTimeUnit.DAY, 1, AbsoluteTimeUnit.DAY, null,
                null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    public void test1159PMAnd1201AMExactlyOneHourApart() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long elevenFiftyNine = format.parse("1/1/07 11:59 pm").getTime();
        long twelveOhOne = format.parse("1/2/07 12:01 am").getTime();
        Interval i1 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(twelveOhOne,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 1, AbsoluteTimeUnit.HOUR, 1, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    public void test1159PMAnd1259AMExactlyOneHourApart() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long elevenFiftyNine = format.parse("1/1/07 11:59 pm").getTime();
        long twelveOhOne = format.parse("1/2/07 12:59 am").getTime();
        Interval i1 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(twelveOhOne,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 1, AbsoluteTimeUnit.HOUR, 1, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertTrue(r.hasRelation(i1, i2));
    }

    public void test1159PMAnd1AMExactlyOneHourApartFalse()
            throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long elevenFiftyNine = format.parse("1/1/07 11:59 pm").getTime();
        long twelveOhOne = format.parse("1/2/07 1:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(twelveOhOne,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 1, AbsoluteTimeUnit.HOUR, 1, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertTrue(r.hasRelation(i1, i2));
    }

    public void test1159PMAnd1AMExactlyTwoHoursApart() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long elevenFiftyNine = format.parse("1/1/07 11:59 pm").getTime();
        long twelveOhOne = format.parse("1/2/07 1:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(twelveOhOne,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 2, AbsoluteTimeUnit.HOUR, 2, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    public void test1159PMAnd1200AMExactlyOneHourApart() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long elevenFiftyNine = format.parse("1/1/07 11:59 pm").getTime();
        long twelveOhOne = format.parse("1/2/07 12:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(twelveOhOne,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 1, AbsoluteTimeUnit.HOUR, 1, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    public void test1159PMAnd1200AMExactlyZeroHoursApart()
            throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long elevenFiftyNine = format.parse("1/1/07 11:59 pm").getTime();
        long twelveOhOne = format.parse("1/2/07 12:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(twelveOhOne,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 0, AbsoluteTimeUnit.HOUR, 0, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertTrue(r.hasRelation(i1, i2));
    }

    public void test1159PMAnd1200AMExactlyTwoHoursApartFalse2()
            throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long elevenFiftyNine = format.parse("1/1/07 11:59 pm").getTime();
        long twelveOhOne = format.parse("1/2/07 12:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(twelveOhOne,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 2, AbsoluteTimeUnit.HOUR, 2, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    public void test1159PMAnd1201AMExactlyTwoHoursApart() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long elevenFiftyNine = format.parse("1/1/07 11:59 pm").getTime();
        long twelveOhOne = format.parse("1/2/07 12:01 am").getTime();
        Interval i1 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(twelveOhOne,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 2, AbsoluteTimeUnit.HOUR, 2, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    public void test1159PMAnd1201AMExactlyOneMinuteApart()
            throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long elevenFiftyNine = format.parse("1/1/07 11:59 pm").getTime();
        long twelveOhOne = format.parse("1/2/07 12:01 am").getTime();
        Interval i1 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(twelveOhOne,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 1, AbsoluteTimeUnit.MINUTE, 1, AbsoluteTimeUnit.MINUTE,
                null, null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    public void test1159PMAnd1201AMExactlyTwoMinutesApart()
            throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long elevenFiftyNine = format.parse("1/1/07 11:59 pm").getTime();
        long twelveOhOne = format.parse("1/2/07 12:01 am").getTime();
        Interval i1 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(twelveOhOne,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 2, AbsoluteTimeUnit.MINUTE, 2, AbsoluteTimeUnit.MINUTE,
                null, null, null, null);
        assertTrue(r.hasRelation(i1, i2));
    }

    public void test1159PMAnd1201AMExactlyThreeMinutesApart()
            throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long elevenFiftyNine = format.parse("1/1/07 11:59 pm").getTime();
        long twelveOhOne = format.parse("1/2/07 12:01 am").getTime();
        Interval i1 = intervalFactory.getInstance(elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE, elevenFiftyNine,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(twelveOhOne,
                AbsoluteTimeGranularity.MINUTE, twelveOhOne,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 3, AbsoluteTimeUnit.MINUTE, 3, AbsoluteTimeUnit.MINUTE,
                null, null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    // Spring forward
    public void testSpringForwardMinutes() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long justBefore = format.parse("3/11/07 1:55 am").getTime();
        long justAfter = format.parse("3/11/07 3:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(justBefore,
                AbsoluteTimeGranularity.MINUTE, justBefore,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(justAfter,
                AbsoluteTimeGranularity.MINUTE, justAfter,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 5, AbsoluteTimeUnit.MINUTE, 5, AbsoluteTimeUnit.MINUTE,
                null, null, null, null);
        assertTrue(r.hasRelation(i1, i2));
    }

    public void testSpringForwardMinutesFalse() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long justBefore = format.parse("3/11/07 1:55 am").getTime();
        long justAfter = format.parse("3/11/07 3:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(justBefore,
                AbsoluteTimeGranularity.MINUTE, justBefore,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(justAfter,
                AbsoluteTimeGranularity.MINUTE, justAfter,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 65, AbsoluteTimeUnit.MINUTE, 65, AbsoluteTimeUnit.MINUTE,
                null, null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    public void testSpringForwardHours() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long justBefore = format.parse("3/11/07 1:55 am").getTime();
        long justAfter = format.parse("3/11/07 3:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(justBefore,
                AbsoluteTimeGranularity.MINUTE, justBefore,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(justAfter,
                AbsoluteTimeGranularity.MINUTE, justAfter,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 0, AbsoluteTimeUnit.HOUR, 0, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertTrue(r.hasRelation(i1, i2));
    }

    public void testSpringForwardHoursFalse() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long justBefore = format.parse("3/11/07 1:55 am").getTime();
        long justAfter = format.parse("3/11/07 3:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(justBefore,
                AbsoluteTimeGranularity.MINUTE, justBefore,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(justAfter,
                AbsoluteTimeGranularity.MINUTE, justAfter,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 2, AbsoluteTimeUnit.HOUR, 2, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    // Fall back
    public void testFallBackMinutes() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long justBefore = format.parse("10/29/06 12:55 am").getTime();
        long justAfter = format.parse("10/29/06 2:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(justBefore,
                AbsoluteTimeGranularity.MINUTE, justBefore,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(justAfter,
                AbsoluteTimeGranularity.MINUTE, justAfter,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 125, AbsoluteTimeUnit.MINUTE, 125,
                AbsoluteTimeUnit.MINUTE, null, null, null, null);
        assertTrue(r.hasRelation(i1, i2));
    }

    public void testFallBackMinutesFalse() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long justBefore = format.parse("10/29/06 12:55 am").getTime();
        long justAfter = format.parse("10/29/06 2:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(justBefore,
                AbsoluteTimeGranularity.MINUTE, justBefore,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(justAfter,
                AbsoluteTimeGranularity.MINUTE, justAfter,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 65, AbsoluteTimeUnit.MINUTE, 65, AbsoluteTimeUnit.MINUTE,
                null, null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    public void testFallBackHours() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long justBefore = format.parse("10/29/06 12:55 am").getTime();
        long justAfter = format.parse("10/29/06 2:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(justBefore,
                AbsoluteTimeGranularity.MINUTE, justBefore,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(justAfter,
                AbsoluteTimeGranularity.MINUTE, justAfter,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 2, AbsoluteTimeUnit.HOUR, 2, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertTrue(r.hasRelation(i1, i2));
    }

    public void testFallBackHoursFalse() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long justBefore = format.parse("10/29/06 12:55 am").getTime();
        long justAfter = format.parse("10/29/06 2:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(justBefore,
                AbsoluteTimeGranularity.MINUTE, justBefore,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(justAfter,
                AbsoluteTimeGranularity.MINUTE, justAfter,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 1, AbsoluteTimeUnit.HOUR, 1, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    public void testFallBackHoursFalse2() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long justBefore = format.parse("10/29/06 12:55 am").getTime();
        long justAfter = format.parse("10/29/06 2:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(justBefore,
                AbsoluteTimeGranularity.MINUTE, justBefore,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(justAfter,
                AbsoluteTimeGranularity.MINUTE, justAfter,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 3, AbsoluteTimeUnit.HOUR, 3, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    public void testOneHourApart() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long justBefore = format.parse("10/28/06 1:00 am").getTime();
        long justAfter = format.parse("10/28/06 2:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(justBefore,
                AbsoluteTimeGranularity.MINUTE, justBefore,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(justAfter,
                AbsoluteTimeGranularity.MINUTE, justAfter,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 1, AbsoluteTimeUnit.HOUR, 1, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertTrue(r.hasRelation(i1, i2));
    }

    public void testOneHourApartFalse() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long justBefore = format.parse("10/28/06 1:00 am").getTime();
        long justAfter = format.parse("10/28/06 2:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(justBefore,
                AbsoluteTimeGranularity.MINUTE, justBefore,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(justAfter,
                AbsoluteTimeGranularity.MINUTE, justAfter,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 2, AbsoluteTimeUnit.HOUR, 2, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    public void testTwoHoursApart() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long justBefore = format.parse("10/28/06 12:55 am").getTime();
        long justAfter = format.parse("10/28/06 2:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(justBefore,
                AbsoluteTimeGranularity.MINUTE, justBefore,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(justAfter,
                AbsoluteTimeGranularity.MINUTE, justAfter,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 1, AbsoluteTimeUnit.HOUR, 1, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertTrue(r.hasRelation(i1, i2));
    }

    public void testTwoHoursApart2() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long justBefore = format.parse("10/28/06 12:00 am").getTime();
        long justAfter = format.parse("10/28/06 2:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(justBefore,
                AbsoluteTimeGranularity.MINUTE, justBefore,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(justAfter,
                AbsoluteTimeGranularity.MINUTE, justAfter,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 2, AbsoluteTimeUnit.HOUR, 2, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertTrue(r.hasRelation(i1, i2));
    }

    public void testTwoHoursApartFalse() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long justBefore = format.parse("10/28/06 12:55 am").getTime();
        long justAfter = format.parse("10/28/06 2:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(justBefore,
                AbsoluteTimeGranularity.MINUTE, justBefore,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(justAfter,
                AbsoluteTimeGranularity.MINUTE, justAfter,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 2, AbsoluteTimeUnit.HOUR, 2, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    public void testTwoHoursApartFalse3() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long justBefore = format.parse("10/28/06 12:01 am").getTime();
        long justAfter = format.parse("10/28/06 2:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(justBefore,
                AbsoluteTimeGranularity.MINUTE, justBefore,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(justAfter,
                AbsoluteTimeGranularity.MINUTE, justAfter,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 1, AbsoluteTimeUnit.HOUR, 1, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertTrue(r.hasRelation(i1, i2));
    }

    public void testOneHourApartFalse2() throws ParseException {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT, Locale.US);
        long justBefore = format.parse("10/28/06 12:00 am").getTime();
        long justAfter = format.parse("10/28/06 2:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(justBefore,
                AbsoluteTimeGranularity.MINUTE, justBefore,
                AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(justAfter,
                AbsoluteTimeGranularity.MINUTE, justAfter,
                AbsoluteTimeGranularity.MINUTE);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 1, AbsoluteTimeUnit.HOUR, 1, AbsoluteTimeUnit.HOUR, null,
                null, null, null);
        assertFalse(r.hasRelation(i1, i2));
    }

    public void testStartsOverTwoDays() throws ParseException {
        assertTrue(testBetweenStartsOverTwoDays(1, 2));
    }

    public void testStartsOverTwoDaysFalse2() throws ParseException {
        assertFalse(testBetweenStartsOverTwoDays(2, 2));
    }

    public void testStartsOverTwoDaysFalse3() throws ParseException {
        assertFalse(testBetweenStartsOverTwoDays(2, 3));
    }

    public void testStartsOverTwoDaysFalse4() throws ParseException {
        assertFalse(testBetweenStartsOverTwoDays(0, 0));
    }

    public void testStartsOverTwoDaysTrue2() throws ParseException {
        assertTrue(testBetweenStartsOverTwoDays(0, 2));
    }

    public void testStartsOverTwoDaysFalse() throws ParseException {
        assertFalse(testBetweenStartsOverTwoDays(1, 1));
    }

    private static boolean testBetweenStartsOverTwoDays(int one, int two)
            throws ParseException {
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, one, AbsoluteTimeUnit.DAY, two, AbsoluteTimeUnit.DAY,
                null, null, null, null);
        return testStartsOverTwoDays(one, two, r);
    }

    public void testSpanThreeDays23True() throws ParseException {
        assertTrue(testSpanStartsOverTwoDays(2, 3));
    }

    public void testSpanThreeDays33False() throws ParseException {
        assertFalse(testSpanStartsOverTwoDays(3, 3));
    }

    private static boolean testSpanStartsOverTwoDays(int one, int two)
            throws ParseException {
        Relation r = new Relation(null, null, null, null, one,
                AbsoluteTimeUnit.DAY, two, AbsoluteTimeUnit.DAY, null, null,
                null, null, null, null, null, null);
        return testStartsOverTwoDays(one, two, r);
    }

    private static boolean testStartsOverTwoDays(long one, long two, Relation r)
            throws ParseException {
        long timestamp1 = DATE_FORMAT.parse("1/1/07").getTime();
        long start2a = DATE_FORMAT.parse("1/2/07").getTime();
        long finish2 = DATE_FORMAT.parse("1/3/07").getTime();
        Interval i1 = intervalFactory.getInstance(timestamp1,
                AbsoluteTimeGranularity.DAY, timestamp1,
                AbsoluteTimeGranularity.DAY);
        Interval i2 = intervalFactory.getInstance(start2a, finish2,
                AbsoluteTimeGranularity.DAY, finish2, finish2,
                AbsoluteTimeGranularity.DAY);
        return r.hasRelation(i1, i2);
    }

    // Relative time hours
    public void test1HourApartHoursRelativeHour() {
        Interval i1 = new DefaultInterval(0L, RelativeHourGranularity.HOUR, 0L,
                RelativeHourGranularity.HOUR, null, null);
        Interval i2 = new DefaultInterval(1L * 60 * 60 * 1000,
                RelativeHourGranularity.HOUR, 1L * 60 * 60 * 1000,
                RelativeHourGranularity.HOUR, null, null);
        Relation r = new Relation(null, null, null, null, null, null, null,
                null, 1, RelativeHourUnit.HOUR, 1, RelativeHourUnit.HOUR, null,
                null, null, null);
        assertTrue(r.hasRelation(i1, i2));
    }

    public void testFebruaryMarchOneMonthApart() throws ParseException {
        assertTrue(testBetweenDatesMonth("2/1/07", "3/1/07", 1));
    }

    public void testFebruaryMarchOneMonthApart2() throws ParseException {
        assertTrue(testBetweenDatesMonth("2/1/07", "3/5/07", 1));
    }

    public void testFebruaryAprilOneMonthApartFalse() throws ParseException {
        assertFalse(testBetweenDatesMonth("2/1/07", "4/1/07", 1));
    }

    public void testFebruaryAprilTwoMonthsApart() throws ParseException {
        assertTrue(testBetweenDatesMonth("2/1/07", "4/1/07", 2));
    }

    public void testMarch1March31ZeroMonthsApart() throws ParseException {
        assertTrue(testBetweenDatesDay("3/1/07", "3/31/07", 0,
                AbsoluteTimeUnit.MONTH));
    }

    public void testMarchMarchZeroMonthsApart() throws ParseException {
        assertTrue(testBetweenDatesMonth("3/1/07", "3/31/07", 0));
    }

    public void testMarch1March31OneMonthApartFalse() throws ParseException {
        assertFalse(testBetweenDatesDay("3/1/07", "3/31/07", 1,
                AbsoluteTimeUnit.MONTH));
    }

    public void testMarch1March31TwoMonthsApartFalse() throws ParseException {
        assertFalse(testBetweenDatesDay("3/1/07", "3/31/07", 2,
                AbsoluteTimeUnit.MONTH));
    }

    public void testMarch1April1OneMonthApartFalse() throws ParseException {
        assertFalse(testBetweenDatesDay("3/1/07", "3/1/07", 1,
                AbsoluteTimeUnit.MONTH));
    }

    public void testMarchAprilOneMonthApart() throws ParseException {
        assertTrue(testBetweenDatesMonth("3/1/07", "4/1/07", 1));
    }

    public void testFebruary1March1OneMonthApart() throws ParseException {
        assertTrue(testBetweenDatesDay("2/1/07", "3/1/07", 1,
                AbsoluteTimeUnit.MONTH));
    }

    public void testFebruary1March1ZeroMonthsApartFalse() throws ParseException {
        assertFalse(testBetweenDatesDay("2/1/07", "3/1/07", 0,
                AbsoluteTimeUnit.MONTH));
    }

    public void testFebruary1March1TwoMonthsApartFalse() throws ParseException {
        assertFalse(testBetweenDatesDay("2/1/07", "3/1/07", 2,
                AbsoluteTimeUnit.MONTH));
    }

    public void test20032004OneYearApart() throws ParseException {
        assertTrue(testBetweenDatesDay("1/1/03", "1/1/04", 1,
                AbsoluteTimeUnit.YEAR));
    }

    public void test20032004OneYearApart2() throws ParseException {
        assertTrue(testBetweenDatesDay("1/1/03", "1/2/04", 1,
                AbsoluteTimeUnit.YEAR));
    }

    public void test20032004ZeroYearsApartFalse() throws ParseException {
        assertFalse(testBetweenDatesDay("1/1/03", "1/1/04", 0,
                AbsoluteTimeUnit.YEAR));
    }

    public void testEventAndPrimParamStartsAtMost4DaysAfterFalse()
            throws ParseException {
        TemporalPrimitiveParameterFactory ppf = new TemporalPrimitiveParameterFactory(
                DATE_TIME_FORMAT, AbsoluteTimeGranularity.MINUTE);
        PrimitiveParameter creat = ppf.getInstance("CREAT", "11/17/03 8:43 AM",
                new NumberValue(0.8), dbDataSourceType);

        TemporalEventFactory tf = new TemporalEventFactory(DATE_FORMAT,
                AbsoluteTimeGranularity.DAY);
        Event ct = tf.getInstance("74160", "11/12/03", derivedDataSourceType);
        Relation r = new Relation(-4, AbsoluteTimeUnit.DAY, null,
                AbsoluteTimeUnit.DAY, null, null, null, null, null, null, null,
                null, null, null, null, null);
        assertFalse(r.hasRelation(creat.getInterval(), ct.getInterval()));
    }

    public void testEventAndPrimParamStartsAtMost5DaysAfter()
            throws ParseException {
        TemporalPrimitiveParameterFactory ppf = new TemporalPrimitiveParameterFactory(
                DATE_TIME_FORMAT, AbsoluteTimeGranularity.MINUTE);
        PrimitiveParameter creat = ppf.getInstance("CREAT", "11/17/03 8:43 AM",
                new NumberValue(0.8), dbDataSourceType);

        TemporalEventFactory tf = new TemporalEventFactory(DATE_FORMAT,
                AbsoluteTimeGranularity.DAY);
        Event ct = tf.getInstance("74160", "11/12/03", derivedDataSourceType);
        Relation r = new Relation(-5, AbsoluteTimeUnit.DAY, null,
                AbsoluteTimeUnit.DAY, null, null, null, null, null, null, null,
                null, null, null, null, null);
        assertTrue(r.hasRelation(creat.getInterval(), ct.getInterval()));
    }

    public void testEventAndPrimParamStartsAtMost6DaysAfter()
            throws ParseException {
        TemporalPrimitiveParameterFactory ppf = new TemporalPrimitiveParameterFactory(
                DATE_TIME_FORMAT, AbsoluteTimeGranularity.MINUTE);
        PrimitiveParameter creat = ppf.getInstance("CREAT", "11/17/03 8:43 AM",
                new NumberValue(0.8), dbDataSourceType);

        TemporalEventFactory tf = new TemporalEventFactory(DATE_FORMAT,
                AbsoluteTimeGranularity.DAY);
        Event ct = tf.getInstance("74160", "11/12/03", derivedDataSourceType);
        Relation r = new Relation(-6, AbsoluteTimeUnit.DAY, null,
                AbsoluteTimeUnit.DAY, null, null, null, null, null, null, null,
                null, null, null, null, null);
        assertTrue(r.hasRelation(creat.getInterval(), ct.getInterval()));
    }

    public void testEventAndPrimParamStartsAtLeast5DaysAfter()
            throws ParseException {
        TemporalPrimitiveParameterFactory ppf = new TemporalPrimitiveParameterFactory(
                DATE_TIME_FORMAT, AbsoluteTimeGranularity.MINUTE);
        PrimitiveParameter creat = ppf.getInstance("CREAT", "11/17/03 8:43 AM",
                new NumberValue(0.8), dbDataSourceType);

        TemporalEventFactory tf = new TemporalEventFactory(DATE_FORMAT,
                AbsoluteTimeGranularity.DAY);
        Event ct = tf.getInstance("74160", "11/12/03", derivedDataSourceType);
        Relation r = new Relation(null, AbsoluteTimeUnit.DAY, -5,
                AbsoluteTimeUnit.DAY, null, null, null, null, null, null, null,
                null, null, null, null, null);
        assertTrue(r.hasRelation(creat.getInterval(), ct.getInterval()));
    }

    public void testEventAndPrimParamStartsAtMost5DaysBefore()
            throws ParseException {
        TemporalPrimitiveParameterFactory ppf = new TemporalPrimitiveParameterFactory(
                DATE_TIME_FORMAT, AbsoluteTimeGranularity.MINUTE);
        PrimitiveParameter creat = ppf.getInstance("CREAT", "11/17/03 8:43 AM",
                new NumberValue(0.8), dbDataSourceType);

        TemporalEventFactory tf = new TemporalEventFactory(DATE_FORMAT,
                AbsoluteTimeGranularity.DAY);
        Event ct = tf.getInstance("74160", "11/12/03", derivedDataSourceType);
        Relation r = new Relation(null, null, 5, AbsoluteTimeUnit.DAY, null,
                null, null, null, null, null, null, null, null, null, null,
                null);
        assertTrue(r.hasRelation(ct.getInterval(), creat.getInterval()));
    }

    public void testEventAndAbstractParamStartsAtMost5DaysBefore1()
            throws ParseException {
        TemporalAbstractParameterFactory apf = new TemporalAbstractParameterFactory(
                DATE_TIME_FORMAT, AbsoluteTimeGranularity.MINUTE);

        AbstractParameter creat = apf.getInstance("CREAT", "11/17/03 8:43 AM",
                "11/17/03 8:43 AM");

        TemporalEventFactory tf = new TemporalEventFactory(DATE_FORMAT,
                AbsoluteTimeGranularity.DAY);
        Event ct = tf.getInstance("74160", "11/12/03", derivedDataSourceType);
        Relation r = new Relation(null, null, 5, AbsoluteTimeUnit.DAY, null,
                null, null, null, null, null, null, null, null, null, null,
                null);
        assertTrue(r.hasRelation(ct.getInterval(), creat.getInterval()));
    }

    public void testEventAndAbstractParamStartsAtMost5DaysBefore2()
            throws ParseException {
        TemporalAbstractParameterFactory apf = new TemporalAbstractParameterFactory(
                DATE_TIME_FORMAT, AbsoluteTimeGranularity.MINUTE);

        AbstractParameter creat = apf.getInstance("CREAT", "11/17/03 8:43 AM",
                "11/18/03 8:43 AM");

        TemporalEventFactory tf = new TemporalEventFactory(DATE_FORMAT,
                AbsoluteTimeGranularity.DAY);
        Event ct = tf.getInstance("74160", "11/12/03", derivedDataSourceType);
        Relation r = new Relation(null, null, 5, AbsoluteTimeUnit.DAY, null,
                null, null, null, null, null, null, null, null, null, null,
                null);
        assertTrue(r.hasRelation(ct.getInterval(), creat.getInterval()));
    }

    /**
     * This should fail because the dates are within the same hour.
     */
    public void testIsGreaterThanOrEqualToDurationFalse() {
        try {
            TemporalPrimitiveParameterFactory ppf = new TemporalPrimitiveParameterFactory(
                    DATE_TIME_FORMAT, AbsoluteTimeGranularity.MINUTE);
            PrimitiveParameter pp1 = ppf.getInstance("BLAH", "1/1/07 1:00 am",
                    dbDataSourceType);
            PrimitiveParameter pp2 = ppf.getInstance("BLAH", "1/1/07 1:59 am",
                    dbDataSourceType);
            Relation r = new Relation(1, AbsoluteTimeUnit.HOUR, null, null,
                    null, null, null, null, null, null, null, null, null, null,
                    null, null);
            assertFalse(r.hasRelation(pp1.getInterval(), pp2.getInterval()));
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * This should succeed because the dates are in consecutive hours.
     */
    public void testIsGreaterThanOrEqualToDurationRelation() {
        try {
            TemporalPrimitiveParameterFactory ppf = new TemporalPrimitiveParameterFactory(
                    DATE_TIME_FORMAT, AbsoluteTimeGranularity.MINUTE);
            PrimitiveParameter pp1 = ppf.getInstance("BLAH", "1/1/07 1:00 am",
                    dbDataSourceType);
            PrimitiveParameter pp2 = ppf.getInstance("BLAH", "1/1/07 2:00 am",
                    dbDataSourceType);
            Relation r = new Relation(1, AbsoluteTimeUnit.HOUR, null, null,
                    null, null, null, null, null, null, null, null, null, null,
                    null, null);
            assertTrue(r.hasRelation(pp1.getInterval(), pp2.getInterval()));
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * This should succeed because the dates are in consecutive hours.
     */
    public void testIsLessThanOrEqualToDurationRelation() {
        try {
            TemporalPrimitiveParameterFactory ppf = new TemporalPrimitiveParameterFactory(
                    DATE_TIME_FORMAT, AbsoluteTimeGranularity.MINUTE);
            PrimitiveParameter pp1 = ppf.getInstance("BLAH", "1/1/07 1:00 am",
                    dbDataSourceType);
            PrimitiveParameter pp2 = ppf.getInstance("BLAH", "1/1/07 2:00 am",
                    dbDataSourceType);
            Relation r = new Relation(null, null, 1, AbsoluteTimeUnit.HOUR,
                    null, null, null, null, null, null, null, null, null, null,
                    null, null);
            assertTrue(r.hasRelation(pp1.getInterval(), pp2.getInterval()));
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * This should succeed because the dates are in consecutive hours.
     */
    public void testIsLessThanOrEqualToDuration2() {
        try {
            TemporalPrimitiveParameterFactory ppf = new TemporalPrimitiveParameterFactory(
                    DATE_TIME_FORMAT, AbsoluteTimeGranularity.MINUTE);
            PrimitiveParameter pp1 = ppf.getInstance("BLAH", "1/1/07 1:00 am",
                    dbDataSourceType);
            PrimitiveParameter pp2 = ppf.getInstance("BLAH", "1/1/07 2:01 am",
                    dbDataSourceType);
            Relation r = new Relation(null, null, 1, AbsoluteTimeUnit.HOUR,
                    null, null, null, null, null, null, null, null, null, null,
                    null, null);
            assertTrue(r.hasRelation(pp1.getInterval(), pp2.getInterval()));
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * This should succeed because the dates are in consecutive hours.
     */
    public void testIsLessThanOrEqualToDuration3() {
        try {
            TemporalPrimitiveParameterFactory ppf = new TemporalPrimitiveParameterFactory(
                    DATE_TIME_FORMAT, AbsoluteTimeGranularity.MINUTE);
            PrimitiveParameter pp1 = ppf.getInstance("BLAH", "1/1/07 1:01 am",
                    dbDataSourceType);
            PrimitiveParameter pp2 = ppf.getInstance("BLAH", "1/1/07 2:00 am",
                    dbDataSourceType);
            Relation r = new Relation(null, null, 1, AbsoluteTimeUnit.HOUR,
                    null, null, null, null, null, null, null, null, null, null,
                    null, null);
            assertTrue(r.hasRelation(pp1.getInterval(), pp2.getInterval()));
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * This should succeed because the dates are in consecutive hours.
     */
    public void testIsLessThanOrEqualToDuration4() {
        try {
            TemporalPrimitiveParameterFactory ppf = new TemporalPrimitiveParameterFactory(
                    DATE_TIME_FORMAT, AbsoluteTimeGranularity.MINUTE);
            PrimitiveParameter pp1 = ppf.getInstance("BLAH", "1/1/07 1:01 am",
                    dbDataSourceType);
            PrimitiveParameter pp2 = ppf.getInstance("BLAH", "1/1/07 2:59 am",
                    dbDataSourceType);
            Relation r = new Relation(null, null, 1, AbsoluteTimeUnit.HOUR,
                    null, null, null, null, null, null, null, null, null, null,
                    null, null);
            assertTrue(r.hasRelation(pp1.getInterval(), pp2.getInterval()));
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * This should succeed because the dates are in consecutive hours.
     */
    public void testIsLessThanOrEqualToDurationFalseRelation() {
        try {
            TemporalPrimitiveParameterFactory ppf = new TemporalPrimitiveParameterFactory(
                    DATE_TIME_FORMAT, AbsoluteTimeGranularity.MINUTE);
            PrimitiveParameter pp1 = ppf.getInstance("BLAH", "1/1/07 1:01 am",
                    dbDataSourceType);
            PrimitiveParameter pp2 = ppf.getInstance("BLAH", "1/1/07 3:00 am",
                    dbDataSourceType);
            Relation r = new Relation(null, null, 1, AbsoluteTimeUnit.HOUR,
                    null, null, null, null, null, null, null, null, null, null,
                    null, null);
            assertTrue(r.hasRelation(pp1.getInterval(), pp2.getInterval()));
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    public void testIsGreaterThanOrEqualToDuration() throws ParseException {
        long time1 = DATE_TIME_FORMAT.parse("1/1/07 1:00 am").getTime();
        long time2 = DATE_TIME_FORMAT.parse("1/1/07 2:01 am").getTime();
        Interval i1 = intervalFactory.getInstance(time1, AbsoluteTimeGranularity.MINUTE,
                time1, AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(time2, AbsoluteTimeGranularity.MINUTE,
                time2, AbsoluteTimeGranularity.MINUTE);
        assertTrue(Relation.isGreaterThanOrEqualToDuration(
                AbsoluteTimeUnit.HOUR, i1.getMaximumFinish(), i2.getMinimumStart(), 1));
    }

    public void testIsLessThanOrEqualToDurationFalse() throws ParseException {
        long time1 = DATE_TIME_FORMAT.parse("1/1/07 1:00 am").getTime();
        long time2 = DATE_TIME_FORMAT.parse("1/1/07 2:01 am").getTime();
        Interval i1 = intervalFactory.getInstance(time1, AbsoluteTimeGranularity.MINUTE,
                time1, AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(time2, AbsoluteTimeGranularity.MINUTE,
                time2, AbsoluteTimeGranularity.MINUTE);
        assertTrue(Relation.isLessThanOrEqualToDuration(AbsoluteTimeUnit.HOUR,
                i1.getMaximumFinish(), i2.getMinimumStart(), 1));
    }

    public void testIsGreaterThanOrEqualToDurationEqual() throws ParseException {
        long time1 = DATE_TIME_FORMAT.parse("1/1/07 1:00 am").getTime();
        long time2 = DATE_TIME_FORMAT.parse("1/1/07 2:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(time1, AbsoluteTimeGranularity.MINUTE,
                time1, AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(time2, AbsoluteTimeGranularity.MINUTE,
                time2, AbsoluteTimeGranularity.MINUTE);
        assertTrue(Relation.isGreaterThanOrEqualToDuration(
                AbsoluteTimeUnit.HOUR, i1.getMinimumFinish(), i2.getMinimumStart(), 1));
    }

    public void testIsLessThanOrEqualToDurationEqual() throws ParseException {
        long time1 = DATE_TIME_FORMAT.parse("1/1/07 1:00 am").getTime();
        long time2 = DATE_TIME_FORMAT.parse("1/1/07 2:00 am").getTime();
        Interval i1 = intervalFactory.getInstance(time1, AbsoluteTimeGranularity.MINUTE,
                time1, AbsoluteTimeGranularity.MINUTE);
        Interval i2 = intervalFactory.getInstance(time2, AbsoluteTimeGranularity.MINUTE,
                time2, AbsoluteTimeGranularity.MINUTE);
        assertTrue(Relation.isLessThanOrEqualToDuration(AbsoluteTimeUnit.HOUR,
                i1.getMinimumFinish(), i2.getMinimumStart(), 1));
    }

    /**
     * This succeeds because the dates are within the same hour.
     */
    public void testIsLessThanOrEqualToDuration() {
        try {
            long time1 = DATE_TIME_FORMAT.parse("1/1/07 1:00 am").getTime();
            long time2 = DATE_TIME_FORMAT.parse("1/1/07 1:59 am").getTime();
            Interval i1 = intervalFactory.getInstance(time1,
                    AbsoluteTimeGranularity.MINUTE, time1,
                    AbsoluteTimeGranularity.MINUTE);
            Interval i2 = intervalFactory.getInstance(time2,
                    AbsoluteTimeGranularity.MINUTE, time2,
                    AbsoluteTimeGranularity.MINUTE);
            assertFalse(Relation.isLessThanOrEqualToDuration(
                    AbsoluteTimeUnit.HOUR, i1.getMinimumFinish(), i2.getMinimumStart(), 1));
        } catch (ParseException e) {
            throwBadDateStringsException(e);
        }
    }

    /**
     * This should succeed because the dates are within the same hour.
     */
    public void testIsGreaterThanOrEqualToDuration2() {
        try {
            long time1 = DATE_TIME_FORMAT.parse("1/1/07 1:00 am").getTime();
            long time2 = DATE_TIME_FORMAT.parse("1/1/07 1:59 am").getTime();
            Interval i1 = intervalFactory.getInstance(time1,
                    AbsoluteTimeGranularity.MINUTE, time1,
                    AbsoluteTimeGranularity.MINUTE);
            Interval i2 = intervalFactory.getInstance(time2,
                    AbsoluteTimeGranularity.MINUTE, time2,
                    AbsoluteTimeGranularity.MINUTE);
            assertTrue(Relation.isGreaterThanOrEqualToDuration(
                    AbsoluteTimeUnit.HOUR, i1.getMinimumFinish(), i2.getMinimumStart(), 1));
        } catch (ParseException e) {
            throwBadDateStringsException(e);
        }
    }

    /**
     * This works because the two dates are in consecutive months.
     */
    public void testOneMonthLessThanOrEqualToApart() {
        try {
            long date1 = DATE_FORMAT.parse("2/1/07").getTime();
            long date2 = DATE_FORMAT.parse("3/1/07").getTime();
            Interval i1 = intervalFactory.getInstance(date1, AbsoluteTimeGranularity.DAY,
                    date1, AbsoluteTimeGranularity.DAY);
            Interval i2 = intervalFactory.getInstance(date2, AbsoluteTimeGranularity.DAY,
                    date2, AbsoluteTimeGranularity.DAY);
            assertTrue(Relation.isLessThanOrEqualToDuration(
                    AbsoluteTimeUnit.MONTH, i1.getMinimumFinish(), i2.getMinimumStart(), 1));
        } catch (ParseException e) {
            throwBadDateStringsException(e);
        }
    }

    /**
     * This works because the two dates are in consecutive months.
     */
    public void testOneMonthLessThanOrEqualToApart2() {
        try {
            long date1 = DATE_FORMAT.parse("2/1/07").getTime();
            long date2 = DATE_FORMAT.parse("3/2/07").getTime();
            Interval i1 = intervalFactory.getInstance(date1, AbsoluteTimeGranularity.DAY,
                    date1, AbsoluteTimeGranularity.DAY);
            Interval i2 = intervalFactory.getInstance(date2, AbsoluteTimeGranularity.DAY,
                    date2, AbsoluteTimeGranularity.DAY);
            assertTrue(Relation.isLessThanOrEqualToDuration(
                    AbsoluteTimeUnit.MONTH, i1.getMinimumFinish(), i2.getMinimumStart(), 1));
        } catch (ParseException e) {
            throwBadDateStringsException(e);
        }
    }

    /**
     * This works because the two dates are in consecutive months.
     */
    public void testOneMonthGreaterThanOrEqualToApart() {
        try {
            long date1 = DATE_FORMAT.parse("2/1/07").getTime();
            long date2 = DATE_FORMAT.parse("3/1/07").getTime();
            Interval i1 = intervalFactory.getInstance(date1, AbsoluteTimeGranularity.DAY,
                    date1, AbsoluteTimeGranularity.DAY);
            Interval i2 = intervalFactory.getInstance(date2, AbsoluteTimeGranularity.DAY,
                    date2, AbsoluteTimeGranularity.DAY);
            assertTrue(Relation.isGreaterThanOrEqualToDuration(
                    AbsoluteTimeUnit.MONTH, i1.getMinimumFinish(), i2.getMinimumStart(), 1));
        } catch (ParseException e) {
            throwBadDateStringsException(e);
        }
    }

    /**
     * They are in the same month, so this should succeed.
     */
    public void testOneMonthGreaterThanOrEqualToApart2() {
        try {
            long date1 = DATE_FORMAT.parse("2/1/07").getTime();
            long date2 = DATE_FORMAT.parse("2/28/07").getTime();
            Interval i1 = intervalFactory.getInstance(date1, AbsoluteTimeGranularity.DAY,
                    date1, AbsoluteTimeGranularity.DAY);
            Interval i2 = intervalFactory.getInstance(date2, AbsoluteTimeGranularity.DAY,
                    date2, AbsoluteTimeGranularity.DAY);
            assertTrue(Relation.isGreaterThanOrEqualToDuration(
                    AbsoluteTimeUnit.MONTH, i1.getMinimumFinish(), i2.getMinimumStart(), 1));
        } catch (ParseException e) {
            throwBadDateStringsException(e);
        }
    }

    /**
     * They are in the same month, so this should succeed.
     */
    public void testOneMonthGreaterThanOrEqualToApart3() {
        try {
            long date1 = DATE_FORMAT.parse("2/1/07").getTime();
            long date2 = DATE_FORMAT.parse("2/27/07").getTime();
            Interval i1 = intervalFactory.getInstance(date1, AbsoluteTimeGranularity.DAY,
                    date1, AbsoluteTimeGranularity.DAY);
            Interval i2 = intervalFactory.getInstance(date2, AbsoluteTimeGranularity.DAY,
                    date2, AbsoluteTimeGranularity.DAY);
            assertTrue(Relation.isGreaterThanOrEqualToDuration(
                    AbsoluteTimeUnit.MONTH, i1.getMinimumFinish(), i2.getMinimumStart(), 1));
        } catch (ParseException e) {
            throwBadDateStringsException(e);
        }
    }

    public void testBeforeToday() {
        Relation gap = new Relation(null, null, null, null, null, null, null,
                null, 1, null, null, null, null, null, null, null);
        Interval iToday = new DefaultInterval(1117598400000L,
                AbsoluteTimeGranularity.DAY, 1117598400000L,
                AbsoluteTimeGranularity.DAY, 0L, null);
        Interval iOrder = new DefaultInterval(1117576800000L,
                AbsoluteTimeGranularity.MINUTE, null,
                AbsoluteTimeGranularity.MINUTE, 0L, null);
        assertTrue(gap.hasRelation(iOrder, iToday));
    }

    public void testIntervalsAtLeast30DaysApart() throws ParseException {
        Interval earlier = intervalFactory.getInstance(
                DATE_FORMAT.parse("1/1/09").getTime(),
                AbsoluteTimeGranularity.DAY,
                DATE_FORMAT.parse("1/5/09").getTime(),
                AbsoluteTimeGranularity.DAY);
        Interval later = intervalFactory.getInstance(
                DATE_FORMAT.parse("2/10/09").getTime(),
                AbsoluteTimeGranularity.DAY,
                DATE_FORMAT.parse("2/12/09").getTime(),
                AbsoluteTimeGranularity.DAY);
        Relation relation = new Relation(null, null, null, null, null, null,
                null, null,
                31, AbsoluteTimeUnit.DAY, null, AbsoluteTimeUnit.DAY, null,
                null, null, null);
        assertTrue(relation.hasRelation(earlier, later));
    }

    private static boolean testBetweenDatesDay(String first, String second,
            int distanceBetween, Unit unitsBetween) throws ParseException {
        return testBetween(DATE_FORMAT.parse(first).getTime(),
                AbsoluteTimeGranularity.DAY, DATE_FORMAT.parse(second).getTime(), AbsoluteTimeGranularity.DAY,
                distanceBetween, unitsBetween);
    }

    private static boolean testBetweenDatesMonth(String first, String second,
            int distanceBetween) throws ParseException {
        return testBetween(DATE_FORMAT.parse(first).getTime(),
                AbsoluteTimeGranularity.MONTH, DATE_FORMAT.parse(second).getTime(), AbsoluteTimeGranularity.MONTH,
                distanceBetween, AbsoluteTimeUnit.MONTH);
    }

    private static boolean testBetween(long first, Granularity firstGran,
            long second, Granularity secondGran, int distanceBetween,
            Unit unitsBetween) {
        Interval i1 = intervalFactory.getInstance(first, firstGran, first, firstGran);
        Interval i2 = intervalFactory.getInstance(second, secondGran, second, secondGran);
        Relation r = newRelationBetween(distanceBetween, unitsBetween);
        return r.hasRelation(i1, i2);
    }

    private static Relation newRelationBetween(int distanceBetween,
            Unit unitsBetween) {
        return new Relation(null, null, null, null, null, null, null, null,
                distanceBetween, unitsBetween, distanceBetween, unitsBetween,
                null, null, null, null);
    }

    private static void throwBadDateStringsException(ParseException e) {
        throw new IllegalStateException("The date strings are bad.", e);
    }
}
