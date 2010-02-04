package org.protempa;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.Algorithm;
import org.protempa.LowLevelAbstractionDefinition;
import org.protempa.LowLevelAbstractionFinder;
import org.protempa.ObjectAsserter;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Sequence;


/**
 * @author Andrew Post
 */
final class LowLevelAbstractionConsequence implements Consequence {

	private static final long serialVersionUID = 2455607587534331595L;

	private final LowLevelAbstractionDefinition def;

	private final Algorithm algorithm;

	private static class MyObjectAsserter implements ObjectAsserter {
		private WorkingMemory workingMemory;

		public void assertObject(Object obj) {
			workingMemory.insert(obj);
		}
	}

	LowLevelAbstractionConsequence(
			LowLevelAbstractionDefinition simpleAbstractionDef,
			Algorithm algorithm) {
		this.def = simpleAbstractionDef;
		this.algorithm = algorithm;
	}

	@SuppressWarnings("unchecked")
	public void evaluate(KnowledgeHelper arg0, WorkingMemory arg1)
			throws Exception {
		Sequence<PrimitiveParameter> seq = (Sequence<PrimitiveParameter>) arg1
				.getObject(arg0.getTuple().get(0));
		MyObjectAsserter objAsserter = new MyObjectAsserter();
		objAsserter.workingMemory = arg1;
		LowLevelAbstractionFinder.process(seq, this.def, this.algorithm,
				objAsserter);
	}
}