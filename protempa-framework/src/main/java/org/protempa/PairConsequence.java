package org.protempa;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Relation;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.TemporalProposition;

public final class PairConsequence implements Consequence {

    private static final long serialVersionUID = -3641374073069516895L;
    private final DerivationsBuilder derivationsBuilder;
    private final TemporalExtendedPropositionDefinition[] leftAndRightHandSide;
    private final String propId;
    private final Offsets temporalOffset;
    private final Relation relation;

    PairConsequence(PairDefinition def,
            DerivationsBuilder derivationsBuilder) {
        assert def != null : "def cannot be null";
        this.derivationsBuilder = derivationsBuilder;
        this.leftAndRightHandSide = 
                new TemporalExtendedPropositionDefinition[] {
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
        List<TemporalProposition> pl = 
                (List<TemporalProposition>) knowledgeHelper.get(
                    knowledgeHelper.getDeclaration("result"));
        java.util.Collections.sort(pl, ProtempaUtil.TEMP_PROP_COMP);

        TemporalExtendedPropositionDefinition leftProp =
                this.leftAndRightHandSide[0];
        TemporalExtendedPropositionDefinition rightProp =
                this.leftAndRightHandSide[1];
        Segment<TemporalProposition> segment = null;
        Sequence<TemporalProposition> seq =
                            new Sequence<TemporalProposition>(this.propId);
        for (int i = 0, n = pl.size() - 1; i < n; i++) {
            TemporalProposition left = pl.get(i);
            TemporalProposition right = pl.get(i + 1);
            if (leftProp.getMatches(left) && rightProp.getMatches(right)) {
                if (relation.hasRelation(left.getInterval(),
                        right.getInterval())) {
                    seq.add(left);
                    seq.add(right);
                    if (segment == null) {
                        segment = new Segment<TemporalProposition>(seq);
                    } else {
                        segment.resetState(seq);
                    }
                    AbstractParameter result =
                            AbstractParameterFactory.getFromAbstraction(
                            this.propId,
                            segment,
                            seq,
                            null,
                            this.temporalOffset,
                            this.leftAndRightHandSide);

                    knowledgeHelper.getWorkingMemory().insert(result);
                    derivationsBuilder.propositionAsserted(left, result);
                    derivationsBuilder.propositionAsserted(right, result);

                    seq.clear();

                    logger.log(Level.FINER, "Asserted derived proposition {0}",
                            result);
                }
            }
        }
    }
}
