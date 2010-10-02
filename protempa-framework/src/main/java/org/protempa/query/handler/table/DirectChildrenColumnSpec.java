package org.protempa.query.handler.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.comparator.AllPropositionIntervalComparator;

public final class DirectChildrenColumnSpec extends PropositionColumnSpec {

    public static enum Order {

        INCREASING, DECREASING, NONE
    }
    private final String propositionId;
    private final Order order;

    public DirectChildrenColumnSpec(String propositionId, String[] propertyNames, Order order) {
        super(propertyNames);
        if (order == null) {
            order = Order.NONE;
        }
        if (propositionId == null) {
            throw new IllegalArgumentException("propositionId cannot be null");
        }
        this.propositionId = propositionId;
        this.order = order;
    }

    @Override
    public String[] columnNames(String propId, KnowledgeSource knowledgeSource) throws KnowledgeSourceReadException {
        PropositionDefinition propDef = knowledgeSource.readPropositionDefinition(propositionId);
        String[] directChildren = propDef.getDirectChildren();
        String[][] colNames = new String[directChildren.length][];
        for (int i = 0; i < directChildren.length; i++) {
            String childPropId = directChildren[i];
            PropositionDefinition childPropDef = knowledgeSource.readPropositionDefinition(childPropId);
            colNames[i] = super.columnNames(childPropDef);
        }
        return super.columnNames(propDef);
    }

    @Override
    public String[] columnValues(String key, Proposition proposition, Map<Proposition, List<Proposition>> derivations, Map<UniqueIdentifier, Proposition> references, KnowledgeSource knowledgeSource) throws KnowledgeSourceReadException {
        List<String> result = new ArrayList<String>();
        List<Proposition> derived = new ArrayList(derivations.get(proposition));
        if (this.order == Order.INCREASING) {
            Collections.sort(derived, new AllPropositionIntervalComparator());
        } else if (this.order == Order.DECREASING) {
            Collections.sort(derived, Collections.reverseOrder(new AllPropositionIntervalComparator()));
        }
        for (Proposition prop : derived) {
            String[] vals = super.columnValues(key, prop, derivations, references, knowledgeSource);
            for (String val : vals) {
                result.add(val);
            }
        }
        return result.toArray(new String[result.size()]);
    }
}
