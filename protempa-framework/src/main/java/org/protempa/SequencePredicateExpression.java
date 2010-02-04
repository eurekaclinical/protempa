package org.protempa;

import java.util.Set;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.PredicateExpression;
import org.drools.spi.Tuple;
import org.protempa.proposition.Sequence;


class SequencePredicateExpression implements PredicateExpression {

	private static final long serialVersionUID = 5478534666910990106L;

	private Set<String> dataTypes;

	SequencePredicateExpression(Set<String> dataTypes) {
		this.dataTypes = dataTypes;
	}

	public boolean evaluate(Object arg0, Tuple arg1, Declaration[] arg2,
			Declaration[] arg3, WorkingMemory arg4, Object context)
			throws Exception {
		Sequence<?> other = (Sequence<?>) arg0;
		return dataTypes == null || dataTypes.equals(other.getPropositionIds());
	}

	public Object createContext() {
		return null;
	}

}
