package org.protempa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arp.javautil.collections.Collections;
import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Proposition;
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
    private final Map<Proposition, List<Proposition>> derivations;

    /**
     *
     * @param def
     *            a {@link HighLevelAbstractionDefinition}, cannot be
     *            <code>null</code>.
     * @param columns
     *            the number of parameters, must be greater than zero.
     */
    HighLevelAbstractionConsequence(HighLevelAbstractionDefinition def,
            TemporalExtendedPropositionDefinition[] epds, Map<Proposition, List<Proposition>> derivations) {
        assert def != null : "def cannot be null";
        assert epds != null : "epds cannot be null";
        int col = epds.length;
        assert col > 0 : "columns must be > 0, was " + col;
        this.cad = def;
        this.columns = col;
        this.epds = epds;
        this.derivations = derivations;
    }

    @Override
    public void evaluate(KnowledgeHelper arg0, WorkingMemory arg1)
            throws Exception {
        Logger logger = ProtempaUtil.logger();
        List<TemporalProposition> tps = parameters(arg0.getTuple(), arg1);
        Segment<TemporalProposition> segment =
                new Segment<TemporalProposition>(
                new Sequence(cad.getId(), tps));
        Offsets temporalOffset = cad.getTemporalOffset();
        AbstractParameter result =
                AbstractParameterFactory.getFromAbstraction(cad.getId(),
                segment, tps, null, temporalOffset, epds);
        for (Proposition proposition : segment) {
            Collections.putList(this.derivations, result, proposition);
            Collections.putList(this.derivations, proposition, result);
        }
        arg0.getWorkingMemory().insert(result);
        logger.log(Level.FINER, "Asserted derived proposition {0}", result);
    }
    
    @SuppressWarnings("unchecked")
    private List<TemporalProposition> parameters(Tuple arg0,
            WorkingMemory arg1) {
        List<TemporalProposition> sequences =
                new ArrayList<TemporalProposition>(columns);
        for (int i = 0; i < columns; i++) {
            sequences.add((TemporalProposition) arg1.getObject(arg0.get(i)));
        }
        return sequences;
    }
}
