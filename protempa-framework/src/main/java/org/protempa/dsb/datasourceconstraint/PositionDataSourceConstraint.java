package org.protempa.dsb.datasourceconstraint;

/**
 *
 * @author Andrew Post
 */
public class PositionDataSourceConstraint extends AbstractDataSourceConstraint {
    private Long minStart;
    private Long maxFinish;

    public PositionDataSourceConstraint(String propId) {
        super(propId);
    }

    /**
     * @return the start
     */
    public Long getMinimumStart() {
        return minStart;
    }

    /**
     * @param start the start to set
     */
    public void setMinimumStart(Long  minStart) {
        this.minStart = minStart;
    }

    /**
     * @return the finish
     */
    public Long getMaximumFinish() {
        return this.maxFinish;
    }

    /**
     * @param finish the finish to set
     */
    public void setMaximumFinish(Long maxFinish) {
        this.maxFinish = maxFinish;
    }

    public void accept(AbstractDataSourceConstraintVisitor visitor) {
        visitor.visit(this);
    }


}
