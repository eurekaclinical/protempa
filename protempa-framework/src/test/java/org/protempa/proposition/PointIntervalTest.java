package org.protempa.proposition;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.AbsoluteTimeUnit;

import junit.framework.TestCase;

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
        this.interval = new SimpleInterval(d.getTime(),
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
