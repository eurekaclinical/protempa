package org.protempa.ksb.protege;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;

/**
 * Factory for constructing a PROTEMPA abstraction definition from a Protege
 * parameter.
 * 
 * @author Andrew Post
 */
final class InstanceConverterFactory {

    private InstanceConverterFactory() {
    }

    /**
     * Gets an appropriate <code>ProtegeParameterConverter</code> for
     * constructing a PROTEMPA abstraction definition from the given Protege
     * parameter instance.
     *
     * @param parameter
     *            a Protege parameter <code>Instance</code> object.
     * @param config
     *            configuration properties.
     * @return an appropriate <code>ProtegeParameterConverter</code> object,
     *         or <code>null</code> if the given <code>instance</code> is
     *         <code>null</code> or not a Protege parameter instance.
     */
    static PropositionConverter getInstance(Instance parameter) {
        KnowledgeBase kb = parameter.getKnowledgeBase();
        if (parameter == null) {
            return null;
        } else if (parameter.hasType(kb.getCls("PrimitiveParameter"))) {
            return new PrimitiveParameterConverter();
        } else if (parameter.hasType(kb.getCls("SimpleAbstraction"))) {
            return new LowLevelAbstractionConverter();
        } else if (parameter.hasType(kb.getCls("SliceAbstraction"))) {
            return new SliceConverter();
        } else if (parameter.hasType(kb.getCls("ComplexAbstraction"))) {
            return new HighLevelAbstractionConverter();
        } else if (parameter.hasType(kb.getCls("Event"))) {
            return new EventConverter();
        } else if (parameter.hasType(kb.getCls("Constant"))) {
            return new ConstantConverter();
        } else if (parameter.hasType(kb.getCls("PairAbstraction"))) {
            return new PairAbstractionConverter();
        }
        else {
            return null;
        }
    }
}
