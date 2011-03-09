package org.protempa;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.PropositionVisitable;
import org.protempa.proposition.Relation;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.TemporalProposition;

public final class PairConsequence implements Consequence {

    private static final long serialVersionUID = -3641374073069516895L;

    private final DerivationsBuilder derivationsBuilder;
    private final TemporalPropositionListCreator creator;
    private final TemporalExtendedPropositionDefinition[] leftAndRightHandSide;
    private final String propId;
    private final Offsets temporalOffset;
    private final Relation relation;

    PairConsequence(PairDefinition def, DerivationsBuilder derivationsBuilder) {
        assert def != null : "def cannot be null";
        this.derivationsBuilder = derivationsBuilder;
        this.creator = new TemporalPropositionListCreator();
        this.leftAndRightHandSide = new TemporalExtendedPropositionDefinition[]{
                                def.getLeftHandProposition(),
                                def.getRightHandProposition()};
        this.propId = def.getId();
        this.temporalOffset = def.getTemporalOffset();
        this.relation = def.getRelation();
    }

    @Override
    public void evaluate(KnowledgeHelper knowledgeHelper,
            WorkingMemory workingMemory) throws Exception {
        Logger logger = ProtempaUtil.logger();
        List<PropositionVisitable> l = (List<PropositionVisitable>) knowledgeHelper.get(knowledgeHelper.getDeclaration("result"));
        this.creator.reset();
        this.creator.visit(l);
        List<TemporalProposition> pl = this.creator.getTemporalPropositionList();
        java.util.Collections.sort(pl, ProtempaUtil.TEMP_PROP_COMP);

        TemporalExtendedPropositionDefinition leftProp = this.leftAndRightHandSide[0];
        TemporalExtendedPropositionDefinition rightProp = this.leftAndRightHandSide[1];
        for (int i = 0; i < pl.size() - 1; i++) {
            TemporalProposition left = pl.get(i);
            TemporalProposition right = pl.get(i + 1);
            if (leftProp.getMatches(left) && rightProp.getMatches(right)) {
                if (relation.hasRelation(left.getInterval(),
                        right.getInterval())) {
                    List<TemporalProposition> tps = new ArrayList<TemporalProposition>();
                    tps.add(left);
                    tps.add(right);
                    Segment<TemporalProposition> segment = new Segment<TemporalProposition>(
                            new Sequence(this.propId, tps));
                    AbstractParameter result = AbstractParameterFactory.getFromAbstraction(
                            this.propId,
                            segment,
                            tps,
                            null,
                            this.temporalOffset,
                            this.leftAndRightHandSide);
                    
                    knowledgeHelper.getWorkingMemory().insert(result);
                    for (Proposition proposition : segment) {
                        derivationsBuilder.propositionAsserted(proposition, result);
                    }
                    
                    logger.log(Level.FINER, "Asserted derived proposition{0}", result);
                }
            }
        }
    }
}
