package org.protempa.proposition.comparator;

import java.io.Serializable;
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
        Comparator<TemporalProposition>, Serializable {

    private static final long serialVersionUID = -798502518274204010L;

    @Override
    public int compare(TemporalProposition p0, TemporalProposition p1) {
        return p0.getInterval().getMaximumFinish().compareTo(
                p1.getInterval().getMaximumFinish());
    }
}
