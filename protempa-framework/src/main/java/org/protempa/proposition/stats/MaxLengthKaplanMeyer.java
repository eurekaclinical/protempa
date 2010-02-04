package org.protempa.proposition.stats;

import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.value.Unit;

/**
 * Calculates survival based on temporal propositions' maxlength.
 * 
 * @author Andrew Post
 * 
 */
public final class MaxLengthKaplanMeyer extends AbstractKaplanMeyer {

	public MaxLengthKaplanMeyer() {
		super();
	}

	public MaxLengthKaplanMeyer(Unit targetLengthUnit) {
		super(targetLengthUnit);
	}

	@Override
	protected Long length(TemporalProposition temporalProposition,
			Unit targetLengthUnit) {
		return temporalProposition.getInterval().maxLengthIn(targetLengthUnit);
	}

}
