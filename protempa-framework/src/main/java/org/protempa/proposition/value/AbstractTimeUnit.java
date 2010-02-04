package org.protempa.proposition.value;

/**
 * Base class for defining time units.
 * 
 * @author Andrew Post
 */
public abstract class AbstractTimeUnit implements Unit {

	private transient String name;
	private transient String pluralName;
	private transient String abbreviation;
	private transient long length;
	private transient int calUnits;

	AbstractTimeUnit(String name, String pluralName, String abbreviation,
			String shortFormat, String mediumFormat, String longFormat,
			long length, int calUnits) {
		this.name = name;
		this.pluralName = pluralName;
		this.abbreviation = abbreviation;
		this.length = length;
		this.calUnits = calUnits;
	}

	public String getPluralName() {
		return pluralName;
	}

	public String getName() {
		return name;
	}

	public String getAbbreviatedName() {
		return abbreviation;
	}

	public long getLength() {
		return length;
	}

	/**
	 * The equivalent units in {@link java.util.Calendar}.
	 * 
	 * @return a unit <code>int</code>
	 */
	public int getCalendarUnits() {
		return this.calUnits;
	}

	/**
	 * Returns the unit's name.
	 * 
	 * @see java.lang.Object#toString()
	 * @see #getName()
	 */
	@Override
	public String toString() {
		return name;
	}

}
