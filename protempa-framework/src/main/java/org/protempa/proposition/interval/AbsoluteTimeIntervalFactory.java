package org.protempa.proposition.interval;

import java.util.Date;
import org.protempa.proposition.value.Granularity;

/**
 * Factory for constructing intervals from Java dates.
 * 
 * @author Andrew Post
 */
public class AbsoluteTimeIntervalFactory {
    private IntervalFactory factory;
    
    public AbsoluteTimeIntervalFactory() {
        this.factory = new IntervalFactory();
    }

    public Interval getInstance() {
        return factory.getInstance();
    }

    public Interval getInstance(Date position, Granularity gran) {
        return factory.getInstance(
                position != null ? position.getTime() : null, gran);
    }

    public Interval getInstance(Date start, Granularity startGran, 
            Date finish, Granularity finishGran) {
        return factory.getInstance(start != null ? start.getTime() : null, 
                startGran, finish != null ? finish.getTime() : null, 
                finishGran);
    }

    public Interval getInstance(Date minStart, Date maxStart, 
            Granularity startGran, Date minFinish, Date maxFinish, 
            Granularity finishGran) {
        return factory.getInstance(
                minStart != null ? minStart.getTime() : null,
                maxStart != null ? maxStart.getTime() : null, startGran, 
                minFinish != null ? minFinish.getTime() : null, 
                maxFinish != null ? maxFinish.getTime() : null, finishGran);
    }
}
