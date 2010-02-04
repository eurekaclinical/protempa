package org.protempa;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.PredicateExpression;
import org.drools.spi.Tuple;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.Sequence;


class PropositionPredicateExpression implements PredicateExpression {

	private static final long serialVersionUID = -8785520847545549070L;
    
	private final Set<String> propIds;

	PropositionPredicateExpression(String propositionId) {
        assert propositionId != null : "propositionId cannot be null";
		this.propIds = Collections.singleton(propositionId);
	}

	PropositionPredicateExpression(Set<String> propositionIds) {
        assert propositionIds != null : "propositionIds cannot be null";
		this.propIds = new HashSet<String>(propositionIds);
	}

	public boolean evaluate(Object arg0, Tuple arg1, Declaration[] arg2,
			Declaration[] arg3, WorkingMemory arg4, Object context)
			throws Exception {
        if (arg0 instanceof Proposition) {
            return this.propIds.contains(((Proposition) arg0).getId());
        } else {
            return this.propIds
                    .containsAll(((Sequence) arg0).getPropositionIds());
        }
	}

	public Object createContext() {
		return null;
	}

}
