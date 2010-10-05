package org.protempa.query.handler.table;

import java.util.List;
import java.util.Map;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
public final class LengthColumnSpec extends AbstractColumnSpec {

    public LengthColumnSpec() {
    }

    @Override
    public String[] columnNames(String rowPropositionId, 
            KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        return new String[] {"length"};
    }

    @Override
    public String[] columnValues(String key, Proposition proposition, 
            Map<Proposition, List<Proposition>> derivations,
            Map<UniqueIdentifier, Proposition> references,
            KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        if (!(proposition instanceof TemporalProposition))
            throw new IllegalArgumentException(proposition.getId() +
                    " is not a temporal proposition");
        TemporalProposition tp = (TemporalProposition) proposition;
        return new String[] {tp.getLengthFormattedShort()};
    }

}
