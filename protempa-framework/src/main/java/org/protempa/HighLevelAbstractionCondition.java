package org.protempa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.EvalExpression;
import org.drools.spi.Tuple;
import org.protempa.proposition.Relation;
import org.protempa.proposition.TemporalProposition;


/**
 * High level abstraction definition condition.
 * 
 * FIXME This will fail if we have multiple inputs of the same type and value,
 * due to the key chosen in the parameters map.
 * 
 * @author Andrew Post
 */
class HighLevelAbstractionCondition implements EvalExpression {

	private static final long serialVersionUID = -4946151589366639279L;

	private final HighLevelAbstractionDefinition def;

	private final TemporalExtendedPropositionDefinition[] epds;

	private int parameterMapCapacity;

	private List<List<TemporalExtendedPropositionDefinition>> epdPairs;

	private Map<List<TemporalExtendedPropositionDefinition>, Relation> epdToRelation;

	HighLevelAbstractionCondition(HighLevelAbstractionDefinition def,
			TemporalExtendedPropositionDefinition[] epds) {
		this.def = def;
		this.epds = epds;
		this.parameterMapCapacity = this.epds.length * 4 / 3 + 1;
		this.epdPairs = new ArrayList<List<TemporalExtendedPropositionDefinition>>(
				def.getTemporalExtendedPropositionDefinitionPairs());
		this.epdToRelation = new HashMap<List<TemporalExtendedPropositionDefinition>, Relation>(
				this.parameterMapCapacity);
		for (List<TemporalExtendedPropositionDefinition> pair : this.epdPairs) {
			this.epdToRelation.put(pair, this.def.getRelation(pair));
		}
	}

	/*
	 * (non-Javadoc) Each tuple passed in has length == the number of component
	 * abstraction definitions and may contain duplicates.
	 * 
	 * @see org.drools.spi.Condition#isAllowed(org.drools.spi.Tuple)
	 */
	public boolean evaluate(Tuple arg0, Declaration[] arg1, WorkingMemory arg2,
			Object context) throws Exception {
		Map<TemporalExtendedPropositionDefinition, TemporalProposition> parameterMap = new HashMap<TemporalExtendedPropositionDefinition, TemporalProposition>(
				this.parameterMapCapacity);
		for (int i = 0; i < this.epds.length; i++) {
			TemporalProposition tp = (TemporalProposition) arg2.getObject(arg0
					.get(i));
			parameterMap.put(epds[i], tp);
		}

		return HighLevelAbstractionFinder.find(this.epdToRelation,
				this.epdPairs, parameterMap);
	}

	public Object createContext() {
		return null;
	}
}