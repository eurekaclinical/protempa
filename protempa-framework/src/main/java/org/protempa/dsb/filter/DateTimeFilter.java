package org.protempa.dsb.filter;

import java.util.Date;
import org.protempa.proposition.value.Granularity;

/**
 * A filter for date/time ranges.
 * 
 * @author Andrew Post
 */
public class DateTimeFilter extends PositionFilter {

    /**
     * Initializes a filter with a date/time range.
     *
     * @param propIds a {@link String[]} of proposition ids on which to filter.
     * @param start the start {@link Date}.
     * @param startGran the {@link Granularity} with which to interpret the
     * start date.
     * @param finish the finish {@link Date}.
     * @param finishGran the {@link Granularity with which to interpret the
     * finish date.
     */
    public DateTimeFilter(String[] propIds, Date start,
            Granularity startGran, Date finish, Granularity finishGran) {
        super(propIds, start.getTime(), startGran, finish.getTime(),
                finishGran);
    }

    /**
     * Initializes a filter with a date/time range.
     *
     * @param propIds a {@link String[]} of proposition ids on which to filter.
     * @param start the start {@link Date}.
     * @param startGran the {@link Granularity} with which to interpret the
     * start date.
     * @param finish the finish {@link Date}.
     * @param finishGran the {@link Granularity with which to interpret the
     * finish date.
     * @param startSide the {@link Side} of the proposition to which to apply
     * the start bound. The default is {@link Side.START} (if
     * <code>null</code> is specified).
     * @param finishSide the {@link Side} of the proposition to which to
     * apply the finish bound. The default is {@link Side.FINISH} (if
     * <code>null</code> is specified).
     */
    public DateTimeFilter(String[] propIds, Date start,
            Granularity startGran, Date finish, Granularity finishGran,
            Side startSide, Side finishSide) {
        super(propIds, start.getTime(), startGran, finish.getTime(),
                finishGran, startSide, finishSide);
    }
}
