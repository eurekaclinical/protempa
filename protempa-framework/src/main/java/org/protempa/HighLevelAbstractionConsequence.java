package org.protempa;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.TemporalProposition;

/**
 * @author Andrew Post
 */
class HighLevelAbstractionConsequence implements Consequence {

    private static final long serialVersionUID = -833609244124008166L;
    private final HighLevelAbstractionDefinition cad;
    private final int columns;
    private final TemporalExtendedPropositionDefinition[] epds;

    /**
     *
     * @param def
     *            a {@link HighLevelAbstractionDefinition}, cannot be
     *            <code>null</code>.
     * @param columns
     *            the number of parameters, must be greater than zero.
     */
    HighLevelAbstractionConsequence(HighLevelAbstractionDefinition def,
            TemporalExtendedPropositionDefinition[] epds) {
        assert def != null : "def cannot be null";
        assert epds != null : "epds cannot be null";
        int col = epds.length;
        assert col > 0 : "columns must be > 0, was " + col;
        this.cad = def;
        this.columns = col;
        this.epds = epds;
    }

    @Override
    public void evaluate(KnowledgeHelper arg0, WorkingMemory arg1)
            throws Exception {
        Segment<TemporalProposition> segment =
                new Segment<TemporalProposition>(
                parameters(arg0.getTuple(), arg1));
        Offsets temporalOffset = cad.getTemporalOffset();
        AbstractParameter result =
                AbstractParameterFactory.getFromAbstraction(cad.getId(),
                segment, null, temporalOffset, epds);
        arg0.getWorkingMemory().insert(result);
    }
    
    @SuppressWarnings("unchecked")
    private Sequence<TemporalProposition> parameters(Tuple arg0,
            WorkingMemory arg1) {
        Sequence<TemporalProposition> sequences =
                new Sequence<TemporalProposition>(
                cad.getId(), columns);
        for (int i = 0; i < columns; i++) {
            sequences.add((TemporalProposition) arg1.getObject(arg0.get(i)));
        }
        
        return sequences;
    }
}
