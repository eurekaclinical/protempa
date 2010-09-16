package org.protempa;

import java.util.Map;

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
    protected Map<String, Object> toStringFields() {
        Map<String,Object> fields = super.toStringFields();
        fields.put("gapFunction", this.gapFunction);
        return fields;
    }


}
