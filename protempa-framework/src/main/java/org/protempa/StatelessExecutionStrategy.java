package org.protempa;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.arp.javautil.datastore.DataStore;
import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.WorkingMemory;
import org.protempa.proposition.Proposition;

class StatelessExecutionStrategy extends AbstractExecutionStrategy {

    private StatelessSession statelessSession;

    public StatelessExecutionStrategy(AbstractionFinder abstractionFinder) {
        super(abstractionFinder);
    }
    
    @Override
    public void initialize() {
        this.statelessSession = ruleBase.newStatelessSession();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Proposition> execute(String keyId,
            Set<String> propositionIds, List<?> objects,
            DataStore<String, WorkingMemory> wm) throws ProtempaException {
        StatelessSessionResult result = this.statelessSession
                .executeWithResults(objects);
        return result.iterateObjects();
    }

    @Override
    public void cleanup() {
        getAbstractionFinder().clear();
    }

}