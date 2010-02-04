package org.protempa;

import org.drools.WorkingMemory;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.AbstractPropositionVisitor;
import org.protempa.proposition.Context;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;

/**
 * TODO We should delegate the copying to another copying class.
 *
 * @author Andrew Post
 */
class PropositionCopier extends AbstractPropositionVisitor {

    private final String propId;
    private final WorkingMemory arg1;

    PropositionCopier(String propId, WorkingMemory workingMemory) {
        super();
        this.propId = propId;
        this.arg1 = workingMemory;
    }

    @Override
    public void visit(AbstractParameter p) {
        AbstractParameter param = new AbstractParameter(propId);
        param.setInterval(p.getInterval());
        param.setValue(p.getValue());
        this.arg1.insert(param);
    }

    @Override
    public void visit(Event event) {
        Event e = new Event(propId);
        e.setInterval(event.getInterval());
        this.arg1.insert(e);
    }

    @Override
    public void visit(PrimitiveParameter primitiveParameter) {
        PrimitiveParameter param = new PrimitiveParameter(propId);
        param.setInterval(primitiveParameter.getInterval());
        param.setValue(primitiveParameter.getValue());
        this.arg1.insert(param);
    }

    @Override
    public void visit(Context context) {
    }
}
