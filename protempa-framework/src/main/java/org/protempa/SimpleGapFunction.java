package org.protempa;

import org.protempa.proposition.Interval;
import org.protempa.proposition.Relation;
import org.protempa.proposition.value.Unit;

/**
 * @author Andrew Post
 */
public final class SimpleGapFunction extends GapFunction {
	private Integer maximumGap;

	private Unit maximumGapUnits;

	private Relation relation;

	/**
	 * Instantiates an instance with the default maximum gap and units.
	 */
	public SimpleGapFunction() {
		this(null, null);
	}

	public SimpleGapFunction(Integer maximumGap, Unit maximumGapUnit) {
		this.maximumGapUnits = maximumGapUnit;
		setMaximumGap(maximumGap);

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
		if (maximumGap != null && maximumGap < 0) {
			this.maximumGap = 0;
		} else {
			this.maximumGap = maximumGap;
		}
		setRelation();
	}

	public void setMaximumGapUnit(Unit unit) {
		this.maximumGapUnits = unit;
		setRelation();
	}

	private void setRelation() {
		this.relation = new Relation(null, null, null, null, null, null, null,
				null, 0, this.maximumGapUnits, this.maximumGap,
				this.maximumGapUnits, null, null, null, null);
	}

	@Override
	protected String debugMessage() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(super.debugMessage());
		buffer.append("\tmaximumGap=" + this.maximumGap + " "
				+ this.maximumGapUnits + "\n");
		return buffer.toString();
	}

}
