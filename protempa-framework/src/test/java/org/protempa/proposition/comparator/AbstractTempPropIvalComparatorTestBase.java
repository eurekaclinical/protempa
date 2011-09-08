package org.protempa.proposition.comparator;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import junit.framework.TestCase;
import org.protempa.proposition.Event;
import org.protempa.proposition.PropositionTest;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.interval.AbsoluteTimeIntervalFactory;
import org.protempa.proposition.value.AbsoluteTimeGranularity;

/**
 * Abstract class for testing comparators of temporal propositions by interval. 
 * Subclasses only need to implement {@link #newComparator() }.
 * 
 * @author Andrew Post
 */
abstract class AbstractTempPropIvalComparatorTestBase extends TestCase {
    private static Calendar cal = Calendar.getInstance();
    private AbsoluteTimeIntervalFactory ivalFactory;
    private Comparator<? super TemporalProposition> comp;

    @Override
    protected void setUp() throws Exception {
        this.ivalFactory = new AbsoluteTimeIntervalFactory();
        this.comp = newComparator();
        cal.clear();
    }

    @Override
    protected void tearDown() throws Exception {
        this.ivalFactory = null;
        this.comp = null;
    }
    
    public void testBefore() {
        run(1, 4, 5, 6, -1);
    }
    
    public void testAfter() {
        run(14, 15, 1, 7, 1);
    }
    
    public void testContains() {
        run(1, 4, 2, 3, -1);
    }
    
    public void testDuring() {
        run(20, 21, 19, 22, 1);
    }
    
    public void testOverlaps() {
        run(1, 4, 2, 5, -1);
    }
    
    public void testOverlappedBy() {
        run(25, 27, 24, 26, 1);
    }
    
    public void equal() {
        run(1, 5, 1, 5, 0);
    }
    
    public void starts() {
        run(1, 5, 1, 10, -1);
    }
    
    public void startedBy() {
        run(10, 20, 10, 15, 1);
    }
    
    public void finishes() {
        run(15, 20, 10, 20, 1);
    }
    
    public void finishedBy() {
        run(1, 10, 4, 10, -1);
    }
    
    /**
     * Return the comparator to use.
     * 
     * @return a comparator for temporal propositions.
     */
    protected abstract Comparator<? super TemporalProposition> newComparator();
    
    /**
     * Tests whether two propositions are compared correctly.
     * @param dayOfMonth1 the start of the interval of the first proposition.
     * @param dayOfMonth2 the finish of the interval of the first proposition.
     * @param dayOfMonth3 the start of the interval of the second proposition.
     * @param dayOfMonth4 the finish of the interval of the second proposition.
     * @param expected the expected return value.
     */
    protected final void run(int dayOfMonth1, int dayOfMonth2, int dayOfMonth3, 
            int dayOfMonth4, int expected) {
        cal.set(2011, Calendar.JANUARY, dayOfMonth1);
        Date date1 = cal.getTime();
        cal.set(2011, Calendar.JANUARY, dayOfMonth2);
        Date date2 = cal.getTime();
        cal.set(2011, Calendar.JANUARY, dayOfMonth3);
        Date date3 = cal.getTime();
        cal.set(2011, Calendar.JANUARY, dayOfMonth4);
        Date date4 = cal.getTime();
        Event prop1 = new Event("foo", PropositionTest.uid());
        prop1.setInterval(this.ivalFactory.getInstance(
                date1, AbsoluteTimeGranularity.DAY, 
                date2, AbsoluteTimeGranularity.DAY));
        Event prop2 = new Event("bar", PropositionTest.uid());
        prop2.setInterval(this.ivalFactory.getInstance(
                date3, AbsoluteTimeGranularity.DAY,
                date4, AbsoluteTimeGranularity.DAY));
        this.comp.compare(prop1, prop2);
        assertEquals(expected, this.comp.compare(prop1, prop2));
    }
}
