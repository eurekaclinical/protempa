package org.protempa.ksb.protege;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import java.util.Collection;
import org.protempa.KnowledgeSourceReadException;

/**
 * Factory for constructing a PROTEMPA abstraction definition from a Protege
 * parameter.
 * 
 * @author Andrew Post
 */
final class InstanceConverterFactory {

    private final ConnectionManager connectionManager;
    private final PropositionConverter primitiveParameterConverter;
    private final PropositionConverter eventConverter;
    private final PropositionConverter constantConverter;
    private final PropositionConverter lowLevelAbstractionConverter;
    private final PropositionConverter sliceConverter;
    private final PropositionConverter highLevelAbstractionConverter;
    private final PropositionConverter pairAbstractionConverter;
    private Cls primitiveParameterCls;
    private Cls simpleAbstractionCls;
    private Cls sliceAbstractionCls;
    private Cls complexAbstractionCls;
    private Cls eventCls;
    private Cls constantCls;
    private Cls pairAbstractionCls;

    InstanceConverterFactory(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.primitiveParameterConverter =
                new PrimitiveParameterConverter();
        this.eventConverter = new EventConverter();
        this.constantConverter = new ConstantConverter();
        this.lowLevelAbstractionConverter = new LowLevelAbstractionConverter();
        this.sliceConverter = new SliceConverter();
        this.highLevelAbstractionConverter =
                new HighLevelAbstractionConverter();
        this.pairAbstractionConverter = new PairAbstractionConverter();
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
    PropositionConverter getInstance(Instance parameter)
            throws KnowledgeSourceReadException {
        if (parameter == null) {
            return null;
        } else {
            if (this.primitiveParameterCls == null) {
                this.primitiveParameterCls =
                        this.connectionManager.getCls("PrimitiveParameter");
            }
            if (this.simpleAbstractionCls == null) {
                this.simpleAbstractionCls =
                        this.connectionManager.getCls("SimpleAbstraction");
            }
            if (this.sliceAbstractionCls == null) {
                this.sliceAbstractionCls =
                        this.connectionManager.getCls("SliceAbstraction");
            }
            if (this.complexAbstractionCls == null) {
                this.complexAbstractionCls =
                        this.connectionManager.getCls("ComplexAbstraction");
            }
            if (this.eventCls == null) {
                this.eventCls =
                        this.connectionManager.getCls("Event");
            }
            if (this.constantCls == null) {
                this.constantCls =
                        this.connectionManager.getCls("Constant");
            }
            if (this.pairAbstractionCls == null) {
                this.pairAbstractionCls =
                        this.connectionManager.getCls("PairAbstraction");
            }
            if (this.connectionManager.hasType(parameter, this.eventCls)) {
                return this.eventConverter;
            } else if (this.connectionManager.hasType(parameter,
                    this.primitiveParameterCls)) {
                return this.primitiveParameterConverter;
            } else if (this.connectionManager.hasType(parameter,
                    this.constantCls)) {
                return this.constantConverter;
            } else if (this.connectionManager.hasType(parameter,
                    this.simpleAbstractionCls)) {
                return this.lowLevelAbstractionConverter;
            } else if (this.connectionManager.hasType(parameter,
                    this.sliceAbstractionCls)) {
                return this.sliceConverter;
            } else if (this.connectionManager.hasType(parameter,
                    this.complexAbstractionCls)) {
                return this.highLevelAbstractionConverter;
            } else if (this.connectionManager.hasType(parameter,
                    this.pairAbstractionCls)) {
                return this.pairAbstractionConverter;
            } else {
                throw new AssertionError("Invalid type ");
            }
        }
    }

    void close() {
        this.primitiveParameterCls = null;
        this.simpleAbstractionCls = null;
        this.sliceAbstractionCls = null;
        this.complexAbstractionCls = null;
        this.eventCls = null;
        this.constantCls = null;
        this.pairAbstractionCls = null;
    }
}
