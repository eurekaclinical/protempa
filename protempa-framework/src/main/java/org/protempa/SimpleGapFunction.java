package org.protempa;

import java.beans.PropertyChangeSupport;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.proposition.Interval;
import org.protempa.proposition.Relation;
import org.protempa.proposition.value.Unit;

/**
 * @author Andrew Post
 */
public final class SimpleGapFunction extends GapFunction {

    private static final long serialVersionUID = -6154012083447646091L;
    private Integer maximumGap;
    private Unit maximumGapUnits;
    private Relation relation;
    protected final PropertyChangeSupport changes;

    /**
     * Instantiates an instance with the default maximum gap and units.
     */
    public SimpleGapFunction() {
        this(null, null);
    }

    /**
     * Initializes a gap function with a maximum gap and units.
     * 
     * @param maximumGap an {@link Integer} >= 0. A <code>null</code> value is
     * interpreted as <code>0</code>.
     * @param maximumGapUnit a {@link Unit}.
     */
    public SimpleGapFunction(Integer maximumGap, Unit maximumGapUnit) {
        if (maximumGap == null) {
            maximumGap = 0;
        }
        if (maximumGap < 0) {
            throw new IllegalArgumentException("maximumGap must be >= 0");
        }
        this.maximumGapUnits = maximumGapUnit;
        this.maximumGap = maximumGap;
        setRelation();
        this.changes = new PropertyChangeSupport(this);

    }

    @Override
    public boolean execute(Interval lhs, Interval rhs) {
        return this.relation.hasRelation(lhs, rhs);
    }

    /**
     * Returns the minimum distance between instances of an
     * <code>AbstractionDefinition</code> that are concatenable. The default
     * value is <code>null</code>.
     *
     * @return an {@link Integer}.
     */
    public Integer getMaximumGap() {
        return maximumGap;
    }

    public Unit getMaximumGapUnit() {
        return maximumGapUnits;
    }

    /**
     * Sets the maximum distance between instances of this
     * <code>AbstractionDefinition</code> that are concatenable. The default
     * value is <code>null</code>.
     *
     * @param maximumGap
     *            The {@link Integer} to set. If <code>< 0</code>, the
     *            <code>maximumGap</code> is set to <code>0</code>.
     */
    public void setMaximumGap(Integer maximumGap) {
        if (maximumGap == null) {
            maximumGap = 0;
        }
        //TODO veto maximumGaps < 0.
        Integer old = this.maximumGap;
        this.maximumGap = maximumGap;
        setRelation();
        this.changes.firePropertyChange("maximumGap", old, this.maximumGap);
    }

    public void setMaximumGapUnit(Unit unit) {
        Unit old = this.maximumGapUnits;
        this.maximumGapUnits = unit;
        setRelation();
        this.changes.firePropertyChange("maximumGapUnit", old,
                this.maximumGapUnits);
    }

    private void setRelation() {
        this.relation = new Relation(null, null, null, null, null, null, null,
                null, 0, this.maximumGapUnits, this.maximumGap,
                this.maximumGapUnits, null, null, null, null);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("maximumGap", this.maximumGap)
                .append("maximumGapUnits", this.maximumGapUnits)
                .toString();
    }
}
