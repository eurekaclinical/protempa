package org.protempa.proposition.value;

/**
 * Access to {@link AbsoluteTimeUnit} objects.
 * 
 * @author Andrew Post
 *
 */
public final class AbsoluteTimeUnitFactory implements UnitFactory {

	@Override
    public Unit toUnit(String name) {
		return AbsoluteTimeUnit.nameToUnit(name);
	}

}
