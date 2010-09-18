package org.protempa.dsb.filter;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.proposition.Interval;
import org.protempa.proposition.IntervalFactory;
import org.protempa.proposition.value.Granularity;

/**
 * A filter for a position (e.g., date/time) range.
 *
 * @author Andrew Post
 */
public class PositionFilter extends AbstractFilter {
    private static final IntervalFactory intervalFactory =
            new IntervalFactory();

    private final Interval ival;

    /**
     * Initializes a filter with a position range.
     *
     * @param propIds a {@link String[]} of proposition ids on which to filter.
     * @param start the start position in Protempa's {@link Long}
     * representation.
     * @param startGran the {@link Granularity} with which to interpret the
     * start position.
     * @param finish the finish position in Protempa's {@link Long}
     * representation.
     * @param finishGran the {@link Granularity} with which to interpret the
     * finish position.
     */
    public PositionFilter(String[] propIds, Long start,
            Granularity startGran, Long finish, Granularity finishGran) {
        super(propIds);
        this.ival = intervalFactory.getInstance(start, startGran,
                finish, finishGran);
    }

    /**
     * Returns the {@link Granularity} with which to interpret the
     * start position.
     * 
     * @return the startGranularity a {@link Granularity}.
     */
    public Granularity getStartGranularity() {
        return this.ival.getStartGranularity();
    }

    /**
     * Returns the {@link Granularity} with which to interpret the
     * finish position.
     *
     * @return the finishGranularity a {@link Granularity}.
     */
    public Granularity getFinishGranularity() {
        return this.ival.getFinishGranularity();
    }

    /**
     * Returns the maximum finish position in Protempa's {@link Long}
     * representation.
     *
     * @return a {@link Long}.
     */
    public Long getMaximumFinish() {
        if (this.ival != null) {
            return this.ival.getMaximumFinish();
        } else {
            return null;
        }
    }

    /**
     * Returns the maximum start position in Protempa's {@link Long}
     * representation.
     *
     * @return a {@link Long}.
     */
    public Long getMaximumStart() {
        if (this.ival != null) {
            return this.ival.getMaximumStart();
        } else {
            return null;
        }
    }

    /**
     * Returns the minimum finish position in Protempa's {@link Long}
     * representation.
     *
     * @return a {@link Long}.
     */
    public Long getMinimumFinish() {
        if (this.ival != null) {
            return this.ival.getMinimumFinish();
        } else {
            return null;
        }
    }

    /**
     * Returns the minimum start position in Protempa's {@link Long}
     * representation.
     *
     * @return a {@link Long}.
     */
    public Long getMinimumStart() {
        if (this.ival != null) {
            return this.ival.getMinimumStart();
        } else {
            return null;
        }
    }

    @Override
    public void accept(FilterVisitor visitor) {
        visitor.visit(this);
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
