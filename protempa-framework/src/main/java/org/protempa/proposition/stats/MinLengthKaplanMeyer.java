package org.protempa.proposition.stats;

import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.value.Unit;

/**
 * Calculates survival based on the minlength of temporal propositions.
 * 
 * @author Andrew Post
 * 
 */
public final class MinLengthKaplanMeyer extends AbstractKaplanMeyer {

	public MinLengthKaplanMeyer() {
		super();
	}

	public MinLengthKaplanMeyer(Unit targetLengthUnit) {
		super(targetLengthUnit);
	}

	@Override
	protected Long length(TemporalProposition temporalProposition,
			Unit targetLengthUnit) {
		return temporalProposition.getInterval().minLengthIn(targetLengthUnit);
	}

}
