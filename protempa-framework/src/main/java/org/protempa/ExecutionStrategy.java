package org.protempa;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.arp.javautil.datastore.DataStore;
import org.drools.WorkingMemory;
import org.protempa.proposition.Proposition;

interface ExecutionStrategy {

    void initialize();

    Iterator<Proposition> execute(String keyIds,
            Set<String> propositionIds, List<?> objects,
            DataStore<String, WorkingMemory> wm) throws ProtempaException;

    void cleanup();

    void createRuleBase(Set<String> propIds, DerivationsBuilder listener,
            QuerySession qs) throws ProtempaException;
}