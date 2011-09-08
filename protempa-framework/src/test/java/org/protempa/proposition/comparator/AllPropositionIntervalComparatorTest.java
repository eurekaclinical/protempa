package org.protempa.proposition.comparator;

import java.util.Comparator;
import org.protempa.proposition.Proposition;

/**
 * Tests {@link AllPropositionIntervalComparator}.
 * 
 * @author Andrew Post
 */
public class AllPropositionIntervalComparatorTest extends
        AbstractTempPropIvalComparatorTestBase {

    @Override
    protected Comparator<Proposition> newComparator() {
        return new AllPropositionIntervalComparator();
    }
    
    
}
