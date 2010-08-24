package org.protempa.dsb.filter;

import org.protempa.proposition.DefaultInterval;
import org.protempa.proposition.value.Granularity;

/**
 *
 * @author Andrew Post
 */
public class PositionFilter extends AbstractFilter {
    private Long start;
    private Long finish;
    private Granularity startGranularity;
    private Granularity finishGranularity;
    private DefaultInterval ival;

    public PositionFilter(String[] propIds, Long start,
            Granularity startGran, Long finish, Granularity finishGran) {
        super(propIds);
        this.start = start;
        this.startGranularity = startGran;
        this.finish = finish;
        this.finishGranularity = finishGran;
        this.ival =
                new DefaultInterval(this.start,
                this.startGranularity,
                this.finish, this.finishGranularity);
    }

    /**
     * @return the startGranularity
     */
    public Granularity getStartGranularity() {
        return startGranularity;
    }

    /**
     * @return the finishGranularity
     */
    public Granularity getFinishGranularity() {
        return finishGranularity;
    }

    public Long getMaximumFinish() {
        if (this.ival != null)
            return this.ival.getMaximumFinish();
        else
            return null;
    }

    public Long getMaximumStart() {
        if (this.ival != null)
            return this.ival.getMaximumStart();
        else
            return null;
    }

    public Long getMinimumFinish() {
        if (this.ival != null)
            return this.ival.getMinimumFinish();
        else
            return null;
    }

    public Long getMinimumStart() {
        if (this.ival != null)
            return this.ival.getMinimumStart();
        else
            return null;
    }

    @Override
    public void accept(FilterVisitor visitor) {
        visitor.visit(this);
    }


}
