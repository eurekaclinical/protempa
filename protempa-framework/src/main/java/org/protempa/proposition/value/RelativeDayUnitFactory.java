package org.protempa.proposition.value;

/**
 * Access to {@link RelativeDayUnit} objects.
 * 
 * @author Andrew Post
 * 
 */
public final class RelativeDayUnitFactory implements UnitFactory {

	@Override
    public Unit toUnit(String name) {
		if (RelativeDayUnit.DAY.getName().equals(name)) {
			return RelativeDayUnit.DAY;
		} else {
			return null;
		}
	}

}
