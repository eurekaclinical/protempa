package org.protempa.dsb.filter;

import java.util.Map;
import org.arp.javautil.string.StringUtil;
import org.protempa.proposition.Interval;
import org.protempa.proposition.IntervalFactory;
import org.protempa.proposition.value.Granularity;

/**
 *
 * @author Andrew Post
 */
public class PositionFilter extends AbstractFilter {
    private static final IntervalFactory intervalFactory =
            new IntervalFactory();

    private final Interval ival;

    public PositionFilter(String[] propIds, Long start,
            Granularity startGran, Long finish, Granularity finishGran) {
        super(propIds);
        this.ival = intervalFactory.getInstance(start, startGran,
                finish, finishGran);
    }

    /**
     * @return the startGranularity
     */
    public Granularity getStartGranularity() {
        return this.ival.getStartGranularity();
    }

    /**
     * @return the finishGranularity
     */
    public Granularity getFinishGranularity() {
        return this.ival.getFinishGranularity();
    }

    public Long getMaximumFinish() {
        if (this.ival != null) {
            return this.ival.getMaximumFinish();
        } else {
            return null;
        }
    }

    public Long getMaximumStart() {
        if (this.ival != null) {
            return this.ival.getMaximumStart();
        } else {
            return null;
        }
    }

    public Long getMinimumFinish() {
        if (this.ival != null) {
            return this.ival.getMinimumFinish();
        } else {
            return null;
        }
    }

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
    protected Map<String,Object> toStringFields() {
        Map<String,Object> result = super.toStringFields();
        result.put("ival", this.ival);
        return result;
    }



    @Override
    public String toString() {
        return StringUtil.getToString(getClass(), toStringFields());
    }
}
