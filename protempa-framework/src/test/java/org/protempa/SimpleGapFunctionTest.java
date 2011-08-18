package org.protempa;

import java.util.UUID;

import junit.framework.TestCase;

import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.DerivedSourceId;
import org.protempa.proposition.DerivedUniqueId;
import org.protempa.proposition.IntervalFactory;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.value.AbsoluteTimeUnit;
import org.protempa.proposition.value.RelativeHourGranularity;

/**
 * Note that we assume that we are processing data with timestamps in absolute
 * time.
 * 
 * @author Andrew Post
 */
public class SimpleGapFunctionTest extends TestCase {
    private static final IntervalFactory intervalFactory =
            new IntervalFactory();

    private SimpleGapFunction gapFunction;

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        gapFunction = new SimpleGapFunction();
    }

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        gapFunction = null;
    }

    private static AbstractParameter hours1() {
        AbstractParameter p1 = new AbstractParameter("TEST", uid());
        p1.setDataSourceType(DerivedDataSourceType.getInstance());
        p1.setInterval(intervalFactory.getInstance(0L,
                RelativeHourGranularity.HOUR, 12L * 60 * 60 * 1000,
                RelativeHourGranularity.HOUR));
        return p1;
    }

    private static AbstractParameter hours2() {
        AbstractParameter p2 = new AbstractParameter("TEST", uid());
        p2.setDataSourceType(DerivedDataSourceType.getInstance());
        p2.setInterval(intervalFactory.getInstance(
                24L * 60 * 60 * 1000, RelativeHourGranularity.HOUR,
                25L * 60 * 60 * 1000, RelativeHourGranularity.HOUR));
        return p2;
    }

    private static AbstractParameter notHours1() {
        return hours1();
    }

    private static AbstractParameter notHours2() {
        AbstractParameter p2 = new AbstractParameter("TEST", uid());
        p2.setDataSourceType(DerivedDataSourceType.getInstance());
        p2.setInterval(intervalFactory.getInstance(
                240L * 60 * 60 * 1000, RelativeHourGranularity.HOUR,
                250L * 60 * 60 * 1000, RelativeHourGranularity.HOUR));
        return p2;
    }

    public void testGap12Hours() {
        gapFunction.setMaximumGap(12);
        gapFunction.setMaximumGapUnit(AbsoluteTimeUnit.HOUR);
        assertTrue(gapFunction.execute(hours1(), hours2()));
    }

    public void testNotGap12Hours() {
        gapFunction.setMaximumGap(12);
        gapFunction.setMaximumGapUnit(AbsoluteTimeUnit.HOUR);
        assertFalse(gapFunction.execute(notHours1(), notHours2()));
    }

    public void testGap14Hours() {
        gapFunction.setMaximumGap(14);
        gapFunction.setMaximumGapUnit(AbsoluteTimeUnit.HOUR);
        assertTrue(gapFunction.execute(hours1(), hours2()));
    }

    public void testNotGap14Hours() {
        gapFunction.setMaximumGap(14);
        gapFunction.setMaximumGapUnit(AbsoluteTimeUnit.HOUR);
        assertFalse(gapFunction.execute(notHours1(), notHours2()));
    }

    public void testGap6Hours() {
        gapFunction.setMaximumGap(6);
        gapFunction.setMaximumGapUnit(AbsoluteTimeUnit.HOUR);
        assertFalse(gapFunction.execute(hours1(), hours2()));
    }

    public void testNotGap6Hours() {
        gapFunction.setMaximumGap(6);
        gapFunction.setMaximumGapUnit(AbsoluteTimeUnit.HOUR);
        assertFalse(gapFunction.execute(notHours1(), notHours2()));
    }

    public void testNotGap11Hours() {
        gapFunction.setMaximumGap(11);
        gapFunction.setMaximumGapUnit(AbsoluteTimeUnit.HOUR);
        assertFalse(gapFunction.execute(notHours1(), notHours2()));
    }

    public void testNotGap0HourPrimitiveParameters() {
        gapFunction.setMaximumGap(0);
        AbstractParameter p1 = new AbstractParameter("TEST", uid());
        p1.setDataSourceType(DerivedDataSourceType.getInstance());
        p1.setInterval(intervalFactory.getInstance(0L, RelativeHourGranularity.HOUR, 0L,
                RelativeHourGranularity.HOUR));
        AbstractParameter p2 = new AbstractParameter("TEST", uid());
        long one = 1 * 60 * 60 * 1000;
        p2.setDataSourceType(DerivedDataSourceType.getInstance());
        p2.setInterval(intervalFactory.getInstance(one, RelativeHourGranularity.HOUR,
                one, RelativeHourGranularity.HOUR));
        assertFalse(gapFunction.execute(p1, p2));

    }
    
    private static UniqueId uid() {
        return new UniqueId(
                DerivedSourceId.getInstance(),
                new DerivedUniqueId(UUID.randomUUID().toString()));
    }
}
