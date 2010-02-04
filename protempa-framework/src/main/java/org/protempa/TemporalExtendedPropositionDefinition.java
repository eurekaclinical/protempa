package org.protempa;

import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.value.Unit;

/**
 * @author Andrew Post
 */
public class TemporalExtendedPropositionDefinition extends
		ExtendedPropositionDefinition {

	private static final long serialVersionUID = -125025061319511802L;

	private Integer minLength = 0;

	private Unit minLengthUnit;

	private Integer maxLength;

	private Unit maxLengthUnit;

	public TemporalExtendedPropositionDefinition(String propositionId) {
		super(propositionId);
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public Unit getMaxLengthUnit() {
		return maxLengthUnit;
	}

	/**
	 * @param maxLength
	 *            The maxDuration to set.
	 */
	public void setMaxLength(Integer maxLength) {
		if (this.maxLength != null && this.maxLength < 0) {
			this.maxLength = 0;
		} else {
			this.maxLength = maxLength;
		}
	}

	public void setMaxLengthUnit(Unit units) {
		this.maxLengthUnit = units;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.TemporalExtendedDefinition#getMinDurationTime()
	 */
	public Integer getMinLength() {
		return minLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.TemporalExtendedDefinition#getMinDurationUnits()
	 */
	public Unit getMinLengthUnit() {
		return minLengthUnit;
	}

	/**
	 * @param minDuration
	 *            The minDuration to set.
	 */
	public void setMinLength(Integer minDuration) {
		if (minDuration == null || minDuration < 0) {
			this.minLength = 0;
		} else {
			this.minLength = minDuration;
		}
	}

	public void setMinLengthUnit(Unit units) {
		this.minLengthUnit = units;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.TemporalExtendedDefinition#getMatches(org.virginia.pbhs.parameters.Proposition)
	 */
	@Override
	public boolean getMatches(Proposition proposition) {
		if (!super.getMatches(proposition)) {
			return false;
		}

		if (!(proposition instanceof TemporalProposition)) {
			return false;
		}

		TemporalProposition tp = (TemporalProposition) proposition;
		if (this.minLength != null
				&& tp.getInterval().isLengthLessThan(this.minLength,
						this.minLengthUnit)) {
			return false;
		}
		if (this.maxLength != null
				&& tp.getInterval().isLengthGreaterThan(this.maxLength,
						this.maxLengthUnit)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.TemporalExtendedDefinition#hasEqualFields(org.protempa.ExtendedPropositionDefinition)
	 */
	@Override
	public boolean hasEqualFields(ExtendedPropositionDefinition obj) {
		if (!super.hasEqualFields(obj)) {
			return false;
		}

		if (!(obj instanceof TemporalExtendedPropositionDefinition)) {
			return false;
		}

		TemporalExtendedPropositionDefinition other = (TemporalExtendedPropositionDefinition) obj;
		boolean result = (minLength == null ? other.minLength == null
				: (minLength == other.minLength || minLength
						.equals(other.minLength)))
				&& (minLengthUnit == null ? other.minLengthUnit == null
						: (minLengthUnit == other.minLengthUnit || minLengthUnit
								.equals(other.minLengthUnit)))
				&& (maxLength == null ? other.maxLength == null
						: (maxLength == other.maxLength || maxLength
								.equals(other.maxLength)))
				&& (maxLengthUnit == null ? other.maxLengthUnit == null
						: (maxLengthUnit == other.maxLengthUnit || maxLengthUnit
								.equals(other.maxLengthUnit)));
		return result;
	}

	@Override
	public String toString() {
		return "TemporalExtendedPropositionDefinition: "
				+ this.getPropositionId() + "; " + this.minLength + " "
				+ this.minLengthUnit + "; " + this.maxLength + " "
				+ this.maxLengthUnit;
	}

}
