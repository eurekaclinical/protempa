package org.protempa.ksb.protege;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import org.protempa.KnowledgeSourceReadException;

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
    static PropositionConverter getInstance(Instance parameter,
            ConnectionManager cm) throws KnowledgeSourceReadException {
        if (parameter == null) {
            return null;
        } else if (parameter.hasType(cm.getCls("PrimitiveParameter"))) {
            return new PrimitiveParameterConverter();
        } else if (parameter.hasType(cm.getCls("SimpleAbstraction"))) {
            return new LowLevelAbstractionConverter();
        } else if (parameter.hasType(cm.getCls("SliceAbstraction"))) {
            return new SliceConverter();
        } else if (parameter.hasType(cm.getCls("ComplexAbstraction"))) {
            return new HighLevelAbstractionConverter();
        } else if (parameter.hasType(cm.getCls("Event"))) {
            return new EventConverter();
        } else if (parameter.hasType(cm.getCls("Constant"))) {
            return new ConstantConverter();
        } else if (parameter.hasType(cm.getCls("PairAbstraction"))) {
            return new PairAbstractionConverter();
        }
        else {
            return null;
        }
    }
}
