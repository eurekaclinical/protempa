package org.protempa.dsb.datasourceconstraint;

import org.protempa.proposition.DefaultInterval;
import org.protempa.proposition.value.Granularity;

/**
 *
 * @author Andrew Post
 */
public class PositionDataSourceConstraint extends AbstractDataSourceConstraint {
    private Long start;
    private Long finish;
    private Granularity startGranularity;
    private Granularity finishGranularity;
    private DefaultInterval ival;

    public PositionDataSourceConstraint(String propId) {
        super(propId);
    }

    public void setFinish(Long finish) {
        this.finish = finish;
        this.ival =
                new DefaultInterval(this.start,
                this.startGranularity,
                this.finish, this.finishGranularity);
    }

    public void setStart(Long start) {
        this.start = start;
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
     * @param startGranularity the startGranularity to set
     */
    public void setStartGranularity(Granularity startGranularity) {
        this.startGranularity = startGranularity;
        this.ival =
                new DefaultInterval(this.start,
                this.startGranularity,
                this.finish, this.finishGranularity);
    }

    /**
     * @return the finishGranularity
     */
    public Granularity getFinishGranularity() {
        return finishGranularity;
    }

    /**
     * @param finishGranularity the finishGranularity to set
     */
    public void setFinishGranularity(Granularity finishGranularity) {
        this.finishGranularity = finishGranularity;
        this.ival =
                new DefaultInterval(this.start,
                this.startGranularity,
                this.finish, this.finishGranularity);
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
    public void accept(DataSourceConstraintVisitor visitor) {
        visitor.visit(this);
    }


}
