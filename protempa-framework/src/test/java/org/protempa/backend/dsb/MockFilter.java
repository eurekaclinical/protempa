package org.protempa.backend.dsb;

import org.protempa.backend.dsb.filter.AbstractFilter;
import org.protempa.backend.dsb.filter.FilterVisitor;

/**
 *
 * @author Andrew Post
 */
public class MockFilter extends AbstractFilter {

    public MockFilter(String... propositionIds) {
        super(propositionIds);
    }

    @Override
    public void accept(FilterVisitor visitor) {
        throw new UnsupportedOperationException("visitors not supported yet");
    }

}
