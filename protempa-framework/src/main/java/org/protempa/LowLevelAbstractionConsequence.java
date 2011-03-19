package org.protempa;

import java.util.logging.Level;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Sequence;

/**
 * @author Andrew Post
 */
final class LowLevelAbstractionConsequence implements Consequence {

    private static final long serialVersionUID = 2455607587534331595L;
    private final LowLevelAbstractionDefinition def;
    private final Algorithm algorithm;
    private final DerivationsBuilder derivationsBuilder;
    private final MyObjectAsserter objAsserter;

    private static class MyObjectAsserter implements ObjectAsserter {

        private WorkingMemory workingMemory;

        @Override
        public void assertObject(Object obj) {
            workingMemory.insert(obj);
            ProtempaUtil.logger().log(Level.FINER, "Asserted derived proposition {0}", obj);
        }
    }

    LowLevelAbstractionConsequence(
            LowLevelAbstractionDefinition simpleAbstractionDef,
            Algorithm algorithm, DerivationsBuilder derivationsBuilder) {
        this.def = simpleAbstractionDef;
        this.algorithm = algorithm;
        this.derivationsBuilder = derivationsBuilder;
        this.objAsserter = new MyObjectAsserter();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void evaluate(KnowledgeHelper arg0, WorkingMemory arg1)
            throws Exception {
        Sequence<PrimitiveParameter> seq = (Sequence<PrimitiveParameter>) arg1.getObject(arg0.getTuple().get(0));
        
        objAsserter.workingMemory = arg1;
        LowLevelAbstractionFinder.process(seq, this.def, this.algorithm,
                objAsserter, this.derivationsBuilder);
        objAsserter.workingMemory = null;
    }
}
