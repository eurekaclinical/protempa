package org.protempa;

import org.protempa.proposition.ConstraintNetwork;
import org.protempa.proposition.Segment;

import junit.framework.TestCase;

/**
 * @author Andrew Post
 */
public abstract class ConstraintNetworkSegmentComparer extends TestCase {

    protected Segment seg;
    protected ConstraintNetwork cn;

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        int size = seg.size();
        cn = new ConstraintNetwork(size);
        for (int i = 0; i < size; i++) {
            cn.addInterval(seg.get(i).getInterval());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        seg = null;
        cn = null;
    }

    public void testLength1MinStart() {
        assertEquals(Long.valueOf(cn.getMinimumStart().value()), seg.getInterval().getMinimumStart());
    }

    public void testLength1MaxStart() {
        assertEquals(Long.valueOf(cn.getMaximumStart().value()), seg.getInterval().getMaximumStart());
    }

    public void testLength1MinFinish() {
        assertEquals(Long.valueOf(cn.getMinimumFinish().value()), seg.getInterval().getMinimumFinish());
    }

    public void testLength1MaxFinish() {
        assertEquals(Long.valueOf(cn.getMaximumFinish().value()), seg.getInterval().getMaximumFinish());
    }

    public void testLength1MinDuration() {
        assertEquals(Long.valueOf(cn.getMinimumDuration().value()), seg.getInterval().getMinimumLength());
    }

    public void testLength1MaxDuration() {
        assertEquals(Long.valueOf(cn.getMaximumDuration().value()), seg.getInterval().getMaximumLength());
    }
}
