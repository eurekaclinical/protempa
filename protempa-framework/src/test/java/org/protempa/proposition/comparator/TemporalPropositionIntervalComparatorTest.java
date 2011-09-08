package org.protempa.proposition.comparator;

import java.util.Comparator;
import org.protempa.proposition.TemporalProposition;

/**
 * Tests {@link TemporalPropositionIntervalComparator}.
 * 
 * @author Andrew Post
 */
public class TemporalPropositionIntervalComparatorTest extends 
        AbstractTempPropIvalComparatorTestBase {

    @Override
    protected Comparator<TemporalProposition> newComparator() {
        return new TemporalPropositionIntervalComparator();
    }
}
