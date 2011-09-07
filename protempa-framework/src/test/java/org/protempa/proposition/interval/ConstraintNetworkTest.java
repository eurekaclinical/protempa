package org.protempa.proposition.interval;

import org.protempa.proposition.value.AbsoluteTimeGranularity;

import junit.framework.TestCase;
import org.arp.javautil.graph.WeightFactory;

/**
 * @author Andrew Post
 */
public class ConstraintNetworkTest extends TestCase {

    private static final IntervalFactory intervalFactory =
            new IntervalFactory();
    private static final WeightFactory weightFactory = new WeightFactory();

    /**
     * Constructor for ConstraintNetworkTest.
     *
     * @param arg0
     */
    public ConstraintNetworkTest(String arg0) {
        super(arg0);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEmptyNetworkConsistency() {
        assertTrue(new ConstraintNetwork().getConsistent());
    }

    /*
     * TESTING PAIRS OF INTERVALS REPRESENTING PRIMITIVE PARAMETERS. SUCH
     * INTERVALS ARE CONSTRUCTED AS PER Combi et al. Methods Inf. Med.
     * 1995;34:458-74.
     */
    public void testMinStartBetweenTwoPrimitiveParameterIntervals() {
        Interval interval1 = intervalFactory.getInstance(1172823060000L, AbsoluteTimeGranularity.MINUTE,
                1172823060000L, AbsoluteTimeGranularity.MINUTE);
        Interval interval2 = intervalFactory.getInstance(1172823120000L, AbsoluteTimeGranularity.MINUTE,
                1172823120000L, AbsoluteTimeGranularity.MINUTE);
        ConstraintNetwork cn = new ConstraintNetwork(2);
        cn.addInterval(interval1);
        cn.addInterval(interval2);
        assertEquals(weightFactory.getInstance(1172823060000L), cn.getMinimumStart());
    }

    public void testMaxStartBetweenTwoPrimitiveParameterIntervals() {
        Interval interval1 = intervalFactory.getInstance(1172823060000L, AbsoluteTimeGranularity.MINUTE,
                1172823060000L, AbsoluteTimeGranularity.MINUTE);
        Interval interval2 = intervalFactory.getInstance(1172823120000L, AbsoluteTimeGranularity.MINUTE,
                1172823120000L, AbsoluteTimeGranularity.MINUTE);
        ConstraintNetwork cn = new ConstraintNetwork(2);
        cn.addInterval(interval1);
        cn.addInterval(interval2);
        assertEquals(weightFactory.getInstance(1172823119999L), cn.getMaximumStart());
    }

    public void testMinFinishBetweenTwoPrimitiveParameterIntervals() {
        Interval interval1 = intervalFactory.getInstance(1172823060000L, AbsoluteTimeGranularity.MINUTE,
                1172823060000L, AbsoluteTimeGranularity.MINUTE);
        Interval interval2 = intervalFactory.getInstance(1172823120000L, AbsoluteTimeGranularity.MINUTE,
                1172823120000L, AbsoluteTimeGranularity.MINUTE);
        ConstraintNetwork cn = new ConstraintNetwork();
        cn.addInterval(interval1);
        cn.addInterval(interval2);
        assertEquals(weightFactory.getInstance(1172823120000L), cn.getMinimumFinish());
    }

    public void testMaxFinishBetweenTwoPrimitiveParameterIntervals() {
        Interval interval1 = intervalFactory.getInstance(1172823060000L, AbsoluteTimeGranularity.MINUTE,
                1172823060000L, AbsoluteTimeGranularity.MINUTE);
        Interval interval2 = intervalFactory.getInstance(1172823120000L, AbsoluteTimeGranularity.MINUTE,
                1172823120000L, AbsoluteTimeGranularity.MINUTE);
        ConstraintNetwork cn = new ConstraintNetwork(2);
        cn.addInterval(interval1);
        cn.addInterval(interval2);
        assertEquals(weightFactory.getInstance(1172823179999L), cn.getMaximumFinish());
    }

    public void testMinDistanceBetweenTwoPrimitiveParameterIntervals() {
        Interval interval1 = intervalFactory.getInstance(1172823060000L, AbsoluteTimeGranularity.MINUTE,
                1172823060000L, AbsoluteTimeGranularity.MINUTE);
        Interval interval2 = intervalFactory.getInstance(1172823120000L, AbsoluteTimeGranularity.MINUTE,
                1172823120000L, AbsoluteTimeGranularity.MINUTE);
        ConstraintNetwork cn = new ConstraintNetwork(2);
        cn.addInterval(interval1);
        cn.addInterval(interval2);
        assertEquals(weightFactory.getInstance(1), cn.getMinimumDuration());
    }

    public void testMaxDistanceBetweenTwoPrimitiveParameterIntervals() {
        Interval interval1 = intervalFactory.getInstance(1172823060000L, AbsoluteTimeGranularity.MINUTE,
                1172823060000L, AbsoluteTimeGranularity.MINUTE);
        Interval interval2 = intervalFactory.getInstance(1172823120000L, AbsoluteTimeGranularity.MINUTE,
                1172823120000L, AbsoluteTimeGranularity.MINUTE);
        ConstraintNetwork cn = new ConstraintNetwork(2);
        cn.addInterval(interval1);
        cn.addInterval(interval2);
        assertEquals(weightFactory.getInstance(119999), cn.getMaximumDuration());
    }
}
