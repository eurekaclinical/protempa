package org.protempa.query.handler.table;

import java.util.List;
import java.util.Map;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.ReferenceDefinition;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

public final class ReferenceColumnSpec extends PropositionColumnSpec {

    private final String referenceName;

    public ReferenceColumnSpec(String referenceName, String[] propertyNames) {
        super(propertyNames);
        if (referenceName == null) {
            throw new IllegalArgumentException("referenceName cannot be null");
        }
        this.referenceName = referenceName;
    }

    public String getReferenceName() {
        return this.referenceName;
    }

    @Override
    public String[] columnNames(String propId, KnowledgeSource knowledgeSource) throws KnowledgeSourceReadException {
        PropositionDefinition prop = knowledgeSource.readPropositionDefinition(propId);
        ReferenceDefinition refDef = prop.referenceDefinition(this.referenceName);
        PropositionDefinition refProp = knowledgeSource.readPropositionDefinition(refDef.getPropositionId());
        return super.columnNames(refProp);
    }

    @Override
    public String[] columnValues(String key, Proposition proposition, Map<Proposition, List<Proposition>> derivations, Map<UniqueIdentifier, Proposition> references, KnowledgeSource knowledgeSource) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
