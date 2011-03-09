package org.protempa;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.arp.javautil.arrays.Arrays;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.PredicateExpression;
import org.drools.spi.Tuple;
import org.protempa.proposition.Proposition;

class PropositionPredicateExpression implements PredicateExpression {

    private static final long serialVersionUID = -8785520847545549070L;
    private final Set<String> propIds;

    PropositionPredicateExpression(String propositionId) {
        assert propositionId != null : "propositionId cannot be null";
        this.propIds = Collections.singleton(propositionId);
    }

    PropositionPredicateExpression(String[] propositionIds) {
        assert propositionIds != null : "propositionIds cannot be null";
        this.propIds = Arrays.asSet(propositionIds);
    }

    PropositionPredicateExpression(Set<String> propositionIds) {
        assert propositionIds != null : "propositionIds cannot be null";
        this.propIds = new HashSet(propositionIds);
    }

    @Override
    public boolean evaluate(Object arg0, Tuple arg1, Declaration[] arg2,
            Declaration[] arg3, WorkingMemory arg4, Object context)
            throws Exception {
        return this.propIds.contains(((Proposition) arg0).getId());
    }

    @Override
    public Object createContext() {
        return null;
    }
}
