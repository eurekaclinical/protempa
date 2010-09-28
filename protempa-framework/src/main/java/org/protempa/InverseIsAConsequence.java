package org.protempa;

import java.util.List;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.PropositionVisitable;


/**
 * Creates a new event.
 * 
 * @author Andrew Post
 * 
 */
class InverseIsAConsequence implements Consequence {

	private static final long serialVersionUID = 6157152982863451759L;
	private final String eventId;
	private final Map<Proposition, List<Proposition>> derivations;

	InverseIsAConsequence(String eventId, Map<Proposition, List<Proposition>> derivations) {
	    this.derivations = derivations;
		this.eventId = eventId;
	}

	public void evaluate(KnowledgeHelper arg0, WorkingMemory arg1)
			throws Exception {
        PropositionVisitable o = (PropositionVisitable) 
                arg1.getObject(arg0.getTuple().get(0));
        o.accept(new PropositionCopier(this.eventId, arg1, this.derivations));
	}

}
