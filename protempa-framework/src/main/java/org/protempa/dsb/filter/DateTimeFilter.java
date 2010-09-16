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
}
