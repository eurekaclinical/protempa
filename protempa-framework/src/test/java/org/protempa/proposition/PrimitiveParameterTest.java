package org.protempa.proposition;

import java.util.Calendar;

import org.protempa.DatabaseDataSourceType;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.NumberValue;

import junit.framework.TestCase;

/**
 * @author Andrew Post
 */
public class PrimitiveParameterTest extends TestCase {

    private PrimitiveParameter p;

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2007, Calendar.MARCH, 1, 15, 11);

        p = new PrimitiveParameter("TEST");
        p.setDataSourceType(new DatabaseDataSourceType("TEST"));
        p.setValue(new NumberValue(13));
        p.setTimestamp(cal.getTimeInMillis());
        p.setGranularity(AbsoluteTimeGranularity.MINUTE);
    }

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        p = null;
    }

    public void testIntervalMinStart() {
        Interval i = p.getInterval();
        assertEquals(Long.valueOf(1172779860000L), i.getMinimumStart());
    }

    public void testIntervalMaxStart() {
        Interval i = p.getInterval();
        assertEquals(Long.valueOf(1172779919999L), i.getMaximumStart());
    }

    public void testIntervalMinFinish() {
        Interval i = p.getInterval();
        assertEquals(Long.valueOf(1172779860000L), i.getMinimumFinish());
    }

    public void testIntervalMaxFinish() {
        Interval i = p.getInterval();
        assertEquals(Long.valueOf(1172779919999L), i.getMaximumFinish());
    }

    public void testIntervalMinDuration() {
        Interval i = p.getInterval();
        assertEquals(Long.valueOf(0), i.getMinimumLength());
    }

    public void testIntervalMaxDuration() {
        Interval i = p.getInterval();
        assertEquals(Long.valueOf(0), i.getMaximumLength());
    }

    public void testEqualAll() {
        PrimitiveParameter p2 = new PrimitiveParameter("TEST");
        p2.setDataSourceType(new DatabaseDataSourceType("TEST"));
        p2.setValue(new NumberValue(13));
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2007, Calendar.MARCH, 1, 15, 11);
        p2.setTimestamp(cal.getTimeInMillis());
        p2.setGranularity(AbsoluteTimeGranularity.MINUTE);
        assertTrue(p.isEqual(p2));
    }

    public void testIdsNotEqual() {
        PrimitiveParameter p2 = new PrimitiveParameter("TEST2");
        p2.setDataSourceType(new DatabaseDataSourceType("TEST"));
        p2.setValue(new NumberValue(13));
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2007, Calendar.MARCH, 1, 15, 11);
        p2.setTimestamp(cal.getTimeInMillis());
        p2.setGranularity(AbsoluteTimeGranularity.MINUTE);
        assertFalse("expected: " + p + "; actual: " + p2, p.isEqual(p2));
    }

    public void testTimestampsNotEqual() {
        PrimitiveParameter p2 = new PrimitiveParameter("TEST2");
        p2.setDataSourceType(new DatabaseDataSourceType("TEST"));
        p2.setValue(new NumberValue(13));
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2007, Calendar.MARCH, 1, 15, 12);
        p2.setTimestamp(cal.getTimeInMillis());
        p2.setGranularity(AbsoluteTimeGranularity.MINUTE);
        assertFalse("expected: " + p + "; actual: " + p2, p.isEqual(p2));
    }

    public void testGranularitiesNotEqual() {
        PrimitiveParameter p2 = new PrimitiveParameter("TEST2");
        p2.setDataSourceType(new DatabaseDataSourceType("TEST"));
        p2.setValue(new NumberValue(13));
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2007, Calendar.MARCH, 1, 15, 11);
        p2.setTimestamp(cal.getTimeInMillis());
        p2.setGranularity(AbsoluteTimeGranularity.SECOND);
        assertFalse("expected: " + p + "; actual: " + p2, p.isEqual(p2));
    }
}
