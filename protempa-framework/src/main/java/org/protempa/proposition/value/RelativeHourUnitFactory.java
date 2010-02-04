package org.protempa.proposition.value;

/**
 * Access to {@link RelativeHourUnit} objects.
 * 
 * @author Andrew Post
 * 
 */
public final class RelativeHourUnitFactory implements UnitFactory {

	public Unit toUnit(String name) {
		if (RelativeHourUnit.HOUR.getName().equals(name)) {
			return RelativeHourUnit.HOUR;
		} else {
			return null;
		}
	}

}
