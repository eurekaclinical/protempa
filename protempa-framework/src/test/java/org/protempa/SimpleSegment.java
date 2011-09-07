package org.protempa;

import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.interval.IntervalFactory;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.TemporalProposition;

/**
 * @author Andrew Post
 */
final class SimpleSegment<T extends TemporalProposition> extends Segment<T> {
    private static final IntervalFactory intervalFactory =
            new IntervalFactory();

    SimpleSegment(Sequence<T> seq) {
        super(seq);
    }

    SimpleSegment(Sequence<T> seq, int firstIndex, int lastIndex) {
        super(seq, firstIndex, lastIndex);
    }

    SimpleSegment(SimpleSegment<T> simpleSegment) {
        super(simpleSegment);
    }

    @Override
    protected Interval intervalCreator() {
        if (isEmpty()) {
            return null;
        } else if (size() == 1) {
            Interval ival = first().getInterval();
            return intervalFactory.getInstance(ival.getMinStart(),
                    ival.getMaxStart(),
                    ival.getStartGranularity(), ival.getMinFinish(),
                    ival.getMaxFinish(), ival.getFinishGranularity());
        } else {
            return super.intervalCreator();
        }
    }
}
