package org.protempa;

import org.protempa.proposition.Interval;
import org.protempa.proposition.Relation;
import org.protempa.proposition.value.Unit;

/**
 * @author Andrew Post
 */
public final class MinMaxGapFunction extends GapFunction {
	private Integer minimumGap = 0;

	private Unit minimumGapUnits;

	private Integer maximumGap;

	private Unit maximumGapUnits;

	private Relation relation;

	/**
	 * Instantiates an instance with the default minimum and maximum gap and
	 * units.
	 */
	public MinMaxGapFunction() {
		this(null, null, null, null);
	}

	public MinMaxGapFunction(Integer minimumGap, Unit minimumGapUnit,
			Integer maximumGap, Unit maximumGapUnit) {
		this.minimumGapUnits = minimumGapUnit;
		setMinimumGap(minimumGap);
		this.maximumGapUnits = maximumGapUnit;
		setMaximumGap(maximumGap);

	}

	@Override
	public boolean execute(Interval lhs, Interval rhs) {
		return this.relation.hasRelation(lhs, rhs);
	}

	public Integer getMinimumGap() {
		return this.minimumGap;
	}

	public Unit getMinimumGapUnit() {
		return this.minimumGapUnits;
	}

	public Integer getMaximumGap() {
		return this.maximumGap;
	}

	public Unit getMaximumGapUnit() {
		return this.maximumGapUnits;
	}

	public void setMaximumGap(Integer maximumGap) {
		if (maximumGap != null && maximumGap < 0) {
			this.maximumGap = 0;
		} else {
			this.maximumGap = maximumGap;
		}
		setRelation();
	}

	public void setMinimumGap(Integer minimumGap) {
		if (minimumGap == null || minimumGap < 0) {
			this.minimumGap = 0;
		} else {
			this.minimumGap = minimumGap;
		}
		setRelation();
	}

	public void setMaximumGapUnit(Unit unit) {
		this.maximumGapUnits = unit;
		setRelation();
	}

	public void setMinimumGapUnit(Unit unit) {
		this.minimumGapUnits = unit;
		setRelation();
	}

	private void setRelation() {
		this.relation = new Relation(null, null, null, null, null, null, null,
				null, this.minimumGap, this.minimumGapUnits, this.maximumGap,
				this.maximumGapUnits, null, null, null, null);
	}

	@Override
	protected String debugMessage() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(super.debugMessage());
		buffer.append("\tminimumGap=" + this.minimumGap + " "
				+ this.minimumGapUnits + "\n");
		buffer.append("\tmaximumGap=" + this.maximumGap + " "
				+ this.maximumGapUnits + "\n");
		return buffer.toString();
	}

}
