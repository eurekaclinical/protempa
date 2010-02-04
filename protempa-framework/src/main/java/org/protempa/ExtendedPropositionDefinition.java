package org.protempa;

import java.io.Serializable;

import org.protempa.proposition.Proposition;


public class ExtendedPropositionDefinition implements Serializable {

	private static final long serialVersionUID = 3835638971180620664L;

	private String propositionId;

	private String displayName;

	private String abbreviatedDisplayName;
	
	private volatile int hashCode;

	public ExtendedPropositionDefinition(String propositionId) {
		if (propositionId == null) {
			throw new IllegalArgumentException(
					"A propositionId must be specified");
		}
		this.propositionId = propositionId;
	}

	/**
	 * @return the proposition id <code>String</code>.
	 */
	public String getPropositionId() {
		return propositionId;
	}

	/**
	 * Returns whether a parameter has the same id and value, and consistent
	 * duration as specified by this extended parameter definition.
	 * 
	 * @param proposition
	 *            a <code>Proposition</code>
	 * @return <code>true</code> if <code>proposition</code> has the same id
	 *         and value, and consistent duration as specified by this extended
	 *         parameter definition, or <code>false</code> if not, or if
	 *         <code>proposition</code> is <code>null</code>.
	 */
	public boolean getMatches(Proposition proposition) {
		if (proposition == null) {
			return false;
		} else {
			String pId = proposition.getId();
			if (propositionId != pId && !propositionId.equals(pId)) {
				return false;
			}

			return true;
		}
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getAbbreviatedDisplayName() {
		return abbreviatedDisplayName;
	}

	public String getShortDisplayName() {
		String abbrevDisplayName = this.getAbbreviatedDisplayName();
		if (abbrevDisplayName != null && abbrevDisplayName.length() > 0) {
			return abbrevDisplayName;
		} else {
			return this.getDisplayName();
		}
	}

	public void setAbbreviatedDisplayName(String abbreviatedDisplayName) {
		this.abbreviatedDisplayName = abbreviatedDisplayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (this.hashCode == 0) {
			this.hashCode = super.hashCode();
		}
		return this.hashCode;
	}

	public boolean hasEqualFields(ExtendedPropositionDefinition obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		return propositionId.equals(obj.propositionId);
	}

	@Override
	public String toString() {
		return "Extended proposition: " + propositionId;
	}
}
