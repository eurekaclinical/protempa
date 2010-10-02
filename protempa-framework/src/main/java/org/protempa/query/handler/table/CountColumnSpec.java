package org.protempa.query.handler.table;

import java.util.List;
import java.util.Map;
import org.arp.javautil.arrays.Arrays;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

public final class CountColumnSpec extends AbstractColumnSpec {

    private final String referenceName;
    private final String[] derivedPropositionIds;
    private final ColumnSpecConstraint[] constraints;

    public CountColumnSpec(String referenceName) {
        this(referenceName, null);
    }

    public CountColumnSpec(String referenceName,
            String[] derivedPropositionIds) {
        this(referenceName, derivedPropositionIds, null);
    }

    public CountColumnSpec(String referenceName, 
            String[] derivedPropositionIds,
            ColumnSpecConstraint[] constraints) {
        if (referenceName == null) {
            throw new IllegalArgumentException("referenceName cannot be null");
        }
        if (constraints != null) {
            ProtempaUtil.checkArrayForNullElement(constraints, "constraints");
        }
        if (derivedPropositionIds != null) {
            ProtempaUtil.checkArrayForNullElement(derivedPropositionIds,
                    "derivedPropositionIds");
        }
        this.referenceName = referenceName;
        if (derivedPropositionIds != null)
            this.derivedPropositionIds = derivedPropositionIds.clone();
        else
            this.derivedPropositionIds = new String[0];
        if (constraints != null) {
            this.constraints = constraints.clone();
        } else {
            this.constraints = new ColumnSpecConstraint[0];
        }
    }

    @Override
    public String[] columnNames(String propId, KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        PropositionDefinition derivedPropDef = null;
        StringBuilder builder = new StringBuilder();
        builder.append("count(");
        builder.append(this.referenceName);
        if (derivedPropDef != null) {
            builder.append("->");
            builder.append(Util.propositionDefinitionDisplayNames(
                    this.derivedPropositionIds, knowledgeSource));
        }
        constraintHeaderString(constraints);
        builder.append(')');
        return new String[]{builder.toString()};
    }

    @Override
    public String[] columnValues(String key, Proposition proposition,
            Map<Proposition, List<Proposition>> derivations,
            Map<UniqueIdentifier, Proposition> references,
            KnowledgeSource knowledgeSource) {
        List<UniqueIdentifier> uids =
                proposition.getReferences(this.referenceName);
        int count = 0;
        if (this.derivedPropositionIds.length == 0) {
            count = uids.size();
        } else {
            for (UniqueIdentifier uid : uids) {
                Proposition reffedProp = references.get(uid);
                List<Proposition> derivedProps = derivations.get(reffedProp);
                for (Proposition derivedProp : derivedProps) {
                    if (!constraintsCheckCompatible(derivedProp,
                            this.constraints)) {
                        continue;
                    }
                    if (Arrays.contains(this.derivedPropositionIds,
                            derivedProp.getId())) {
                        count++;
                    }
                }
            }
        }
        return new String[]{"" + count};
    }
}
