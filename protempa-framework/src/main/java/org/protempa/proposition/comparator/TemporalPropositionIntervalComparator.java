package org.protempa.proposition.comparator;

import java.io.Serializable;
import java.util.Comparator;
import org.protempa.proposition.TemporalProposition;

/**
 * For sorting temporal propositions by interval. It sorts by the
 * beginning of a pair of intervals first, followed by the end of a pair of
 * intervals.
 *
 * @author Andrew Post
 */
public class TemporalPropositionIntervalComparator
        implements Comparator<TemporalProposition>, Serializable {

    private static final long serialVersionUID = -6827741153860737954L;

    @Override
    public int compare(TemporalProposition o1, TemporalProposition o2) {
        return o1.getInterval().compareTo(o2.getInterval());
    }
}
