package org.protempa;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Definition of the constraints required to infer an abstract parameter.
 * 
 * @author Andrew Post
 */
public abstract class AbstractAbstractionDefinition extends
        AbstractPropositionDefinition implements AbstractionDefinition {

    private static final long serialVersionUID = 8075373039175465215L;
    private GapFunction gapFunction = GapFunction.DEFAULT;
    

    protected AbstractAbstractionDefinition(KnowledgeBase kb, String id) {
        super(kb, id);
        kb.addAbstractionDefinition(this);
    }
    
    @Override
    public GapFunction getGapFunction() {
        return gapFunction;
    }

    public void setGapFunction(GapFunction gapFunction) {
        if (gapFunction == null) {
            gapFunction = GapFunction.DEFAULT;
        }
        GapFunction old = this.gapFunction;
        this.gapFunction = gapFunction;
        this.changes.firePropertyChange("gapFunction", old, this.gapFunction);
    }

    @Override
    public void reset() {
        super.reset();
        gapFunction = GapFunction.DEFAULT;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("gapFunction", this.gapFunction)
                .toString();
    }

}
