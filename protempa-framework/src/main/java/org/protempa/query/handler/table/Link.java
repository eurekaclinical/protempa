package org.protempa.query.handler.table;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.protempa.KnowledgeSource;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

/**
 *
 * @author Andrew Post
 */
public interface Link {
    String headerFragment();

    Collection<Proposition> traverse(Proposition proposition,
            Map<Proposition, List<Proposition>> derivations,
            Map<UniqueIdentifier, Proposition> references,
            KnowledgeSource knowledgeSource);
}
