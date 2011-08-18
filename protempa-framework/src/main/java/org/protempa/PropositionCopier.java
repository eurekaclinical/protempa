package org.protempa;

import java.io.Serializable;
import org.protempa.proposition.DerivedSourceId;
import org.protempa.proposition.UniqueId;
import java.util.UUID;
import java.util.logging.Level;

import org.drools.WorkingMemory;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.AbstractPropositionVisitor;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Context;
import org.protempa.proposition.DerivedUniqueId;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;

/**
 * Creates derived propositions when discovered by PROTEMPA that have the same
 * characteristics as the proposition from which it was derived 
 * (e.g., interval, value). Users
 * should create one instance of this class per proposition to derive. 
 * Whenever a proposition with that id is to be created, the user should first 
 * call the corresponding copier's
 * {@link #grab(org.drools.WorkingMemory)} method with the current
 * {@link WorkingMemory} instance, call 
 * {@link org.protempa.proposition.Proposition#accept(org.protempa.proposition.PropositionVisitor)}
 * with the copier as the argument, and then call the copier's 
 * {@link #release()} method. NOTE: failing to call {@link #release()} will 
 * cause a memory leak!
 * 
 * @author Andrew Post
 */
class PropositionCopier extends AbstractPropositionVisitor implements Serializable {

    private static final long serialVersionUID = 1236050515546951710L;
    
    private final String propId;
    private final DerivationsBuilder derivationsBuilder;
    private WorkingMemory workingMemory;

    /**
     * Instances a copier with the id of the proposition to derive, and a
     * {@link DerivationsBuilder} to store links between the source and derived
     * propositions.
     * 
     * @param propId the id {@link String} of the proposition to derive.
     * Cannot be <code>null</code>.
     * @param derivationsBuilder a {@link DerivationsBuilder}. Cannot be
     * <code>null</code>.
     */
    PropositionCopier(String propId, DerivationsBuilder derivationsBuilder) {
        assert propId != null : "propId cannot be null";
        assert derivationsBuilder != null : 
                "derivationsBuilder cannot be null";
        this.derivationsBuilder = derivationsBuilder;
        this.propId = propId;
    }

    /**
     * Grabs this copier for use by a Drools consequence.
     * 
     * @param workingMemory the consequence's {@link WorkingMemory}. Cannot
     * be <code>null</code>.
     */
    void grab(WorkingMemory workingMemory) {
        assert workingMemory != null : "workingMemory cannot be null";
        assert this.workingMemory == null : "The previous user of this copier forgot to call release!";
        if (this.workingMemory != null) {
            ProtempaUtil.logger().log(Level.WARNING, 
                    "The previous user of this copier forgot to call release. This causes a memory leak!");
        }
        this.workingMemory = workingMemory;
    }
    
    /**
     * Releases this copier for use by another Drools consequence.
     */
    void release() {
        this.workingMemory = null;
    }

    /**
     * Creates a derived abstract parameter with the id specified in the
     * constructor and the same characteristics (e.g., data source type,
     * interval, value, etc.).
     * 
     * @param abstractParameter an {@link AbstractParameter}. Cannot be 
     * <code>null</code>.
     */
    @Override
    public void visit(AbstractParameter abstractParameter) {
        assert this.workingMemory != null : "workingMemory wasn't set";
        AbstractParameter param = new AbstractParameter(propId, new UniqueId(
                DerivedSourceId.getInstance(),
                new DerivedUniqueId(UUID.randomUUID().toString())));
        param.setDataSourceType(DerivedDataSourceType.getInstance());
        param.setInterval(abstractParameter.getInterval());
        param.setValue(abstractParameter.getValue());
        this.workingMemory.insert(param);
        this.derivationsBuilder.propositionAsserted(abstractParameter, param);
        ProtempaUtil.logger().log(Level.FINER, "Asserted derived proposition {0}", param);
    }

    /**
     * Creates a derived event with the id specified in the
     * constructor and the same characteristics (e.g., data source type,
     * interval, etc.).
     * 
     * @param event an {@link Event}. Cannot be <code>null</code>.
     */
    @Override
    public void visit(Event event) {
        assert this.workingMemory != null : "workingMemory wasn't set";
        Event e = new Event(propId, new UniqueId(
                DerivedSourceId.getInstance(),
                new DerivedUniqueId(UUID.randomUUID().toString())));
        e.setInterval(event.getInterval());
        e.setDataSourceType(DerivedDataSourceType.getInstance());
        this.workingMemory.insert(e);
        this.derivationsBuilder.propositionAsserted(event, e);
        ProtempaUtil.logger().log(Level.FINER, "Asserted derived proposition {0}", e);
    }

    /**
     * Creates a derived primitive parameter with the id specified in the
     * constructor and the same characteristics (e.g., data source type,
     * interval, value, etc.).
     * 
     * @param primitiveParameter a {@link PrimitiveParameter}. Cannot be 
     * <code>null</code>.
     */
    @Override
    public void visit(PrimitiveParameter primitiveParameter) {
        assert this.workingMemory != null : "workingMemory wasn't set";
        PrimitiveParameter param = new PrimitiveParameter(propId, new UniqueId(
                DerivedSourceId.getInstance(),
                new DerivedUniqueId(UUID.randomUUID().toString())));
        param.setTimestamp(primitiveParameter.getTimestamp());
        param.setGranularity(primitiveParameter.getGranularity());
        param.setValue(primitiveParameter.getValue());
        param.setDataSourceType(DerivedDataSourceType.getInstance());
        this.workingMemory.insert(param);
        this.derivationsBuilder.propositionAsserted(primitiveParameter, param);
        ProtempaUtil.logger().log(Level.FINER, "Asserted derived proposition {0}", param);
    }

    /**
     * Creates a derived constant with the id specified in the
     * constructor and the same characteristics (e.g., data source type,
     * interval, value, etc.).
     * 
     * @param constant a {@link Constant}. Cannot be <code>null</code>.
     */
    @Override
    public void visit(Constant constant) {
        assert this.workingMemory != null : "workingMemory wasn't set";
        Constant newConstant = new Constant(propId, new UniqueId(
                DerivedSourceId.getInstance(),
                new DerivedUniqueId(UUID.randomUUID().toString())));
        newConstant.setDataSourceType(DerivedDataSourceType.getInstance());
        this.workingMemory.insert(newConstant);
        this.derivationsBuilder.propositionAsserted(constant, newConstant);
        ProtempaUtil.logger().log(Level.FINER, "Asserted derived proposition {0}", newConstant);
    }



    /**
     * Unsupported operation!
     * 
     * @param context 
     */
    @Override
    public void visit(Context context) {
        assert this.workingMemory != null : "workingMemory wasn't set";
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
