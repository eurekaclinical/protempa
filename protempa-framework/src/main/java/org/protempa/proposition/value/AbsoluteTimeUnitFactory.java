package org.protempa.proposition.value;

/**
 * Access to {@link AbsoluteTimeUnit} objects.
 * 
 * @author Andrew Post
 *
 */
public final class AbsoluteTimeUnitFactory implements UnitFactory {

	public Unit toUnit(String name) {
		return AbsoluteTimeUnit.nameToUnit(name);
	}

}
