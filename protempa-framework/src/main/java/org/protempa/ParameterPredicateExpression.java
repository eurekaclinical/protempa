package org.protempa;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.PredicateExpression;
import org.drools.spi.Tuple;
import org.protempa.proposition.Parameter;
import org.protempa.proposition.value.Value;


final class ParameterPredicateExpression implements PredicateExpression {

	private static final long serialVersionUID = -8276489182248585309L;
	private String parameterId;
	private Value value;

	ParameterPredicateExpression(String parameterId, Value value) {
		this.parameterId = parameterId;
		this.value = value;
	}

	@Override
    public boolean evaluate(Object arg0, Tuple arg1, Declaration[] arg2,
			Declaration[] arg3, WorkingMemory arg4, Object context)
			throws Exception {
		Parameter p = (Parameter) arg0;
		return (parameterId == null || parameterId == p.getId() || parameterId
				.equals(p.getId()))
				&& (value == null || value == p.getValue() || value.equals(p
						.getValue()));
	}

	@Override
    public Object createContext() {
		return null;
	}

}