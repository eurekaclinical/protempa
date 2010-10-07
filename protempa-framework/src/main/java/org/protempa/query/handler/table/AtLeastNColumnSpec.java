package org.protempa.query.handler.table;

import java.util.List;
import java.util.Map;
import org.arp.javautil.arrays.Arrays;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

public final class AtLeastNColumnSpec extends AbstractColumnSpec {

    private final int n;
    private final String referenceName;
    private final String[] propositionIds;
    private final String[] derivedPropositionIds;
    private final ColumnSpecConstraint[] constraints;

    public AtLeastNColumnSpec(String referenceName, int n) {
        this(referenceName, n, null, null);
    }

    public AtLeastNColumnSpec(String referenceName, int n,
            String[] propositionIds,
            String[] derivedPropositionIds) {
        this(referenceName, n, propositionIds, derivedPropositionIds, null);
    }

    public AtLeastNColumnSpec(String referenceName, int n,
            String[] propositionIds,
            String[] derivedPropositionIds,
            ColumnSpecConstraint[] constraints) {
        if (referenceName == null) {
            throw new IllegalArgumentException("referenceName cannot be null");
        }
        if (n < 1) {
            throw new IllegalArgumentException("n must be at least 1");
        }
        if (constraints != null) {
            ProtempaUtil.checkArrayForNullElement(constraints, "constraints");
        }
        if (derivedPropositionIds != null) {
            ProtempaUtil.checkArrayForNullElement(derivedPropositionIds,
                    "derivedPropositionIds");
        }
        if (propositionIds != null) {
            ProtempaUtil.checkArrayForNullElement(propositionIds,
                    "propositionIds");
        }
        this.n = n;
        this.referenceName = referenceName;
        if (derivedPropositionIds != null) {
            this.derivedPropositionIds = derivedPropositionIds.clone();
        } else {
            this.derivedPropositionIds = new String[0];
        }
        if (constraints != null) {
            this.constraints = constraints.clone();
        } else {
            this.constraints = new ColumnSpecConstraint[0];
        }
        if (propositionIds != null) {
            this.propositionIds = propositionIds.clone();
        } else {
            this.propositionIds = new String[0];
        }
    }

    @Override
    public String[] columnNames(String propId,
            KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        StringBuilder builder = new StringBuilder();
        builder.append("atleast");
        builder.append(this.n);
        builder.append('(');
        builder.append(this.referenceName);
        if (this.propositionIds.length > 0) {
            builder.append('.');
            builder.append(Util.propositionDefinitionDisplayNames(
                    this.propositionIds, knowledgeSource));
        }
        if (this.derivedPropositionIds.length > 0) {
            String derivedPropDisplayNames =
                    Util.propositionDefinitionDisplayNames(
                    this.derivedPropositionIds, knowledgeSource);
            builder.append("->");
            builder.append(derivedPropDisplayNames);
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
            for (UniqueIdentifier uid : uids) {
                Proposition reffedProp = references.get(uid);
                if (this.propositionIds.length == 0
                        || Arrays.contains(
                        this.propositionIds, reffedProp.getId())) {
                    count++;
                }
            }
        } else {
            for (UniqueIdentifier uid : uids) {
                Proposition reffedProp = references.get(uid);
                if (this.propositionIds.length == 0
                        || Arrays.contains(this.propositionIds, reffedProp.getId())) {
                    List<Proposition> derivedProps =
                            derivations.get(reffedProp);
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
        }
        return new String[]{count >= this.n ? "true" : "false"};
    }
}
