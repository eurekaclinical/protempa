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
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.PropositionVisitable;
import org.protempa.proposition.Relation;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.TemporalProposition;

public final class PairConsequence implements Consequence {

    private static final long serialVersionUID = -3641374073069516895L;

    private final PairDefinition def;
    private final Map<Proposition, List<Proposition>> derivations;

    PairConsequence(PairDefinition def,
            Map<Proposition, List<Proposition>> derivations) {
        assert def != null : "def cannot be null";
        this.derivations = derivations;
        this.def = def;
    }

    @Override
    public void evaluate(KnowledgeHelper knowledgeHelper,
            WorkingMemory workingMemory) throws Exception {
        Logger logger = ProtempaUtil.logger();
        List<PropositionVisitable> l = (List<PropositionVisitable>) knowledgeHelper.get(knowledgeHelper.getDeclaration("result"));
        TemporalPropositionListCreator c = new TemporalPropositionListCreator();
        c.visit(l);
        List<TemporalProposition> pl = c.getTemporalPropositionList();
        java.util.Collections.sort(pl, ProtempaUtil.TEMP_PROP_COMP);

        TemporalExtendedPropositionDefinition leftProp = def.getLeftHandProposition();
        TemporalExtendedPropositionDefinition rightProp = def.getRightHandProposition();
        Relation relation = def.getRelation();
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
                            new Sequence(def.getId(), tps));
                    Offsets temporalOffset = def.getTemporalOffset();
                    AbstractParameter result = AbstractParameterFactory.getFromAbstraction(
                            def.getId(),
                            segment,
                            tps,
                            null,
                            temporalOffset,
                            new TemporalExtendedPropositionDefinition[]{
                                def.getLeftHandProposition(),
                                def.getRightHandProposition()});
                    for (Proposition proposition : segment) {
                        Collections.putList(this.derivations, result,
                                proposition);
                        Collections.putList(this.derivations,
                                proposition, result);
                    }
                    knowledgeHelper.getWorkingMemory().insert(result);
                    logger.log(Level.FINER, "Asserted derived proposition{0}", result);
                }
            }
        }
    }
}
