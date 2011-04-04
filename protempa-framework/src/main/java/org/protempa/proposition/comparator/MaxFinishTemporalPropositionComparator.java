package org.protempa.proposition.comparator;

import java.util.Comparator;
import org.protempa.proposition.TemporalProposition;

/**
 * For sorting temporal propositions by time interval. It sorts by the
 * end of a pair of intervals.
 *
 * @author Andrew Post
 * @param <T> an instanceof {@link TemporalProposition}.
 */
public class MaxFinishTemporalPropositionComparator implements 
        Comparator<TemporalProposition> {

    @Override
    public int compare(TemporalProposition p0, TemporalProposition p1) {
        Long p0MaximumFinish = p0.getInterval().getMaximumFinish();
        Long p1MaximumFinish = p1.getInterval().getMaximumFinish();
        if (p0MaximumFinish == p1MaximumFinish) {
            return 0;
        } else if (p0MaximumFinish == null) {
            return 1;
        } else if (p1MaximumFinish == null) {
            return -1;
        } else {
            return p0MaximumFinish.compareTo(p1MaximumFinish);
        }
    }
}
