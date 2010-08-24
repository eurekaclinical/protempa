package org.protempa.dsb.filter;

import java.util.Date;
import org.protempa.proposition.value.Granularity;

/**
 *
 * @author Andrew Post
 */
public class DateTimeFilter extends PositionFilter {
    public DateTimeFilter(String[] propIds, Date start,
            Granularity startGran, Date finish, Granularity finishGran) {
        super(propIds, start.getTime(), startGran, finish.getTime(),
                finishGran);
    }
}
