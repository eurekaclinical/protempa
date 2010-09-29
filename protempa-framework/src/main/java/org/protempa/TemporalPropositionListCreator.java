package org.protempa;

import java.util.ArrayList;
import java.util.List;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.AbstractPropositionVisitor;
import org.protempa.proposition.ConstantProposition;
import org.protempa.proposition.Context;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.PropositionVisitable;
import org.protempa.proposition.TemporalProposition;

/**
 * Helper for constructing a list of temporal propositions from a list of
 * proposition visitables that contains no constant parameters.
 * 
 * @author Andrew Post
 */
class TemporalPropositionListCreator extends AbstractPropositionVisitor {
    private final List<TemporalProposition> tempPropList;

    TemporalPropositionListCreator() {
        this.tempPropList = new ArrayList<TemporalProposition>();
    }

    List<TemporalProposition> getTemporalPropositionList() {
        return this.tempPropList;
    }

    void visit(List<PropositionVisitable> propositionVisitableList) {
        for (PropositionVisitable pv : propositionVisitableList) {
            pv.accept(this);
        }
    }

    @Override
    public void visit(AbstractParameter abstractParameter) {
        this.tempPropList.add(abstractParameter);
    }

    @Override
    public void visit(Event event) {
        this.tempPropList.add(event);
    }

    @Override
    public void visit(PrimitiveParameter primitiveParameter) {
        this.tempPropList.add(primitiveParameter);
    }

    @Override
    public void visit(ConstantProposition constantParameter) {
        throw new AssertionError("no constant parameters allowed!");
    }

    @Override
    public void visit(Context context) {
        this.tempPropList.add(context);
    }


}
