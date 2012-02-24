package org.protempa;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.arp.javautil.datastore.DataStore;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.protempa.proposition.Proposition;

class StatefulExecutionStrategy extends AbstractExecutionStrategy {

    @Override
    public void initialize() {
    }

    StatefulExecutionStrategy(AbstractionFinder abstractionFinder) {
        super(abstractionFinder);
        // TODO Auto-generated constructor stub
    }

    private StatefulSession applyRules(String keyId, List<?> objects)
            throws ProtempaException {
        StatefulSession workingMemory = ruleBase.newStatefulSession(false);
        ProtempaUtil.logger().log(Level.FINEST,
                "Adding {0} objects for key ID {1}",
                new Object[] { objects.size(), keyId });
        for (Object obj : objects) {
            workingMemory.insert(obj);
        }
        workingMemory.fireAllRules();
        int wmCount = 0;
        for (Iterator<?> itr = workingMemory.iterateObjects(); itr.hasNext();itr.next()) {
            wmCount++;
        }
        ProtempaUtil.logger().log(Level.FINEST,
                "Iterated over {0} objects", new Object[] { wmCount });
        return workingMemory;
    }

    @Override
    public Iterator<Proposition> execute(String keyId,
            Set<String> propositionIds, List<?> objects,
            DataStore<String, WorkingMemory> wmStore)
            throws ProtempaException {
        StatefulSession workingMemory = applyRules(keyId, objects);
        ProtempaUtil.logger().log(Level.FINEST,
                "Persisting working memory for key ID {0}", keyId);
        wmStore.put(keyId, workingMemory);
        workingMemory.dispose();
        ProtempaUtil.logger().log(Level.FINEST,
                "Persisted working memory for key ID {0}", keyId);

        return null;
    }

    @Override
    public void cleanup() {
    }
}