package org.protempa;

import java.util.UUID;
import java.util.logging.Level;

import org.drools.WorkingMemory;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.AbstractPropositionVisitor;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Context;
import org.protempa.proposition.DerivedUniqueIdentifier;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.UniqueIdentifier;

/**
 * TODO We should delegate the copying to another copying class.
 * 
 * @author Andrew Post
 */
class PropositionCopier extends AbstractPropositionVisitor {

    private final String propId;
    private final DerivationsBuilder derivationsBuilder;
    private WorkingMemory workingMemory;

    PropositionCopier(String propId, DerivationsBuilder derivationsBuilder) {
        super();
        this.derivationsBuilder = derivationsBuilder;
        this.propId = propId;
    }

    public void setWorkingMemory(WorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    @Override
    public void visit(AbstractParameter p) {
        assert this.workingMemory != null : "workingMemory wasn't set";
        AbstractParameter param = new AbstractParameter(propId);
        param.setDataSourceType(DerivedDataSourceType.getInstance());
        param.setUniqueIdentifier(new UniqueIdentifier(
                DerivedSourceId.getInstance(),
                new DerivedUniqueIdentifier(UUID.randomUUID().toString())));
        param.setInterval(p.getInterval());
        param.setValue(p.getValue());
        this.workingMemory.insert(param);
        this.derivationsBuilder.propositionAsserted(p, param);
        ProtempaUtil.logger().log(Level.FINER, "Asserted derived proposition {0}", param);
    }

    @Override
    public void visit(Event event) {
        assert this.workingMemory != null : "workingMemory wasn't set";
        Event e = new Event(propId);
        e.setInterval(event.getInterval());
        e.setDataSourceType(DerivedDataSourceType.getInstance());
        e.setUniqueIdentifier(new UniqueIdentifier(
                DerivedSourceId.getInstance(),
                new DerivedUniqueIdentifier(UUID.randomUUID().toString())));
        this.workingMemory.insert(e);
        this.derivationsBuilder.propositionAsserted(event, e);
        ProtempaUtil.logger().log(Level.FINER, "Asserted derived proposition {0}", e);
    }

    @Override
    public void visit(PrimitiveParameter primitiveParameter) {
        assert this.workingMemory != null : "workingMemory wasn't set";
        PrimitiveParameter param = new PrimitiveParameter(propId);
        param.setTimestamp(primitiveParameter.getTimestamp());
        param.setGranularity(primitiveParameter.getGranularity());
        param.setValue(primitiveParameter.getValue());
        param.setDataSourceType(DerivedDataSourceType.getInstance());
        param.setUniqueIdentifier(new UniqueIdentifier(
                DerivedSourceId.getInstance(),
                new DerivedUniqueIdentifier(UUID.randomUUID().toString())));
        this.workingMemory.insert(param);
        this.derivationsBuilder.propositionAsserted(primitiveParameter, param);
        ProtempaUtil.logger().log(Level.FINER, "Asserted derived proposition {0}", param);
    }

    @Override
    public void visit(Constant constant) {
        assert this.workingMemory != null : "workingMemory wasn't set";
        Constant newConstant = new Constant(propId);
        newConstant.setDataSourceType(DerivedDataSourceType.getInstance());
        newConstant.setUniqueIdentifier(new UniqueIdentifier(
                DerivedSourceId.getInstance(),
                new DerivedUniqueIdentifier(UUID.randomUUID().toString())));
        this.workingMemory.insert(newConstant);
        this.derivationsBuilder.propositionAsserted(constant, newConstant);
        ProtempaUtil.logger().log(Level.FINER, "Asserted derived proposition {0}", newConstant);
    }



    @Override
    public void visit(Context context) {
        assert this.workingMemory != null : "workingMemory wasn't set";
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
