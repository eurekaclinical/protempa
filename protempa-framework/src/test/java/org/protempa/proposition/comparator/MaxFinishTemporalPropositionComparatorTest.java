package org.protempa.proposition.comparator;

import java.util.Comparator;
import junit.framework.TestCase;
import org.protempa.proposition.Event;
import org.protempa.proposition.IntervalFactory;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.value.AbsoluteTimeGranularity;

/**
 *
 * @author Andrew Post
 */
public class MaxFinishTemporalPropositionComparatorTest extends TestCase {

    private static IntervalFactory ivalFactory = new IntervalFactory();
    
    private Comparator<TemporalProposition> comp;

    @Override
    protected void setUp() throws Exception {
        this.comp = new MaxFinishTemporalPropositionComparator();
    }

    @Override
    protected void tearDown() throws Exception {
        this.comp = null;
    }

    public void testCompareAllNull() {
        Event e1 = new Event("TEST");
        e1.setInterval(ivalFactory.getInstance());
        Event e2 = new Event("TEST");
        e2.setInterval(ivalFactory.getInstance());
        assertEquals(0, comp.compare(e1, e2));
    }

    public void testCompareLhsNull() {
        Event e1 = new Event("TEST");
        e1.setInterval(ivalFactory.getInstance());
        Event e2 = new Event("TEST");
        e2.setInterval(ivalFactory.getInstance(1L, AbsoluteTimeGranularity.DAY));
        assertEquals(1, comp.compare(e1, e2));
    }

    public void testCompareRhsNull() {
        Event e1 = new Event("TEST");
        e1.setInterval(ivalFactory.getInstance(1L, AbsoluteTimeGranularity.DAY));
        Event e2 = new Event("TEST");
        e2.setInterval(ivalFactory.getInstance());
        assertEquals(-1, comp.compare(e1, e2));
    }

    public void testCompareNotNullBefore() {
        Event e1 = new Event("TEST");
        e1.setInterval(ivalFactory.getInstance(1L, AbsoluteTimeGranularity.DAY));
        Event e2 = new Event("TEST");
        e2.setInterval(ivalFactory.getInstance(2L, AbsoluteTimeGranularity.DAY));
        assertEquals(-1, comp.compare(e1, e2));
    }

    public void testCompareNotNullEqual() {
        Event e1 = new Event("TEST");
        e1.setInterval(ivalFactory.getInstance(3L, AbsoluteTimeGranularity.DAY));
        Event e2 = new Event("TEST");
        e2.setInterval(ivalFactory.getInstance(3L, AbsoluteTimeGranularity.DAY));
        assertEquals(0, comp.compare(e1, e2));
    }

    public void testCompareNotNullAfter() {
        Event e1 = new Event("TEST");
        e1.setInterval(ivalFactory.getInstance(5L, AbsoluteTimeGranularity.DAY));
        Event e2 = new Event("TEST");
        e2.setInterval(ivalFactory.getInstance(4L, AbsoluteTimeGranularity.DAY));
        assertEquals(1, comp.compare(e1, e2));
    }
}
