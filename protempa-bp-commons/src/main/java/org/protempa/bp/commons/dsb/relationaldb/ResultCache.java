/*
 * #%L
 * Protempa Commons Backend Provider
 * %%
 * Copyright (C) 2012 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.bp.commons.dsb.relationaldb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.arp.javautil.collections.Collections;
import org.arp.javautil.datastore.DataStore;
import org.protempa.datastore.PropositionStoreCreator;
import org.protempa.datastore.UniqueIdUniqueIdStoreCreator;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

public class ResultCache<P extends Proposition> {

    private Map<String, List<P>> inMemoryPatientCache;
    private final List<Map<String, List<P>>> patientCache;
    private Map<UniqueId, List<UniqueId>> tmpInMemoryRefCache;
    private int patientCacheNumber;
    private Map<String, List<P>> currentPatientCache;
    private Map<String, String> propIdToEntitySpecNames;
    private int indexForAnyAdded;
    private Set<String> keyIds;
    private Map<String, List<DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>>>> refCache;

    ResultCache() {
        this.inMemoryPatientCache = new HashMap<String, List<P>>();
        this.patientCache = new ArrayList<Map<String, List<P>>>();
        this.currentPatientCache = PropositionStoreCreator.<P> getInstance()
                .newCacheStore();
        this.patientCache.add(this.currentPatientCache);
        this.refCache = new HashMap<String, List<DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>>>>();
        this.tmpInMemoryRefCache =
                new HashMap<UniqueId, List<UniqueId>>();
        this.propIdToEntitySpecNames = new HashMap<String, String>();
        this.keyIds = new HashSet<String>();
    }

    public Map<String, List<P>> getPatientCache() {
        this.inMemoryPatientCache = null;
        this.tmpInMemoryRefCache = null;
        return new BDBDataSourceResultMap<P>(this.patientCache, 
                this.refCache, this.propIdToEntitySpecNames, this.keyIds);
    }

    boolean anyAdded() {
        return !this.patientCache.get(indexForAnyAdded).isEmpty();
    }

    void addReference(UniqueId uid, UniqueId refUid) {
        Collections.putList(this.tmpInMemoryRefCache, uid, refUid);
    }

    void flushReferences(RefResultProcessor<P> resultProcessor) {
        DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>> databaseMap =
                UniqueIdUniqueIdStoreCreator.getInstance().newCacheStore();
        Collections.putList(this.refCache, 
                resultProcessor.getEntitySpec().getName(), databaseMap);
        String refName = 
                    resultProcessor.getReferenceSpec().getReferenceName();
        for (Map.Entry<UniqueId, List<UniqueId>> me :
                this.tmpInMemoryRefCache.entrySet()) {
            UniqueId key = me.getKey();
            List<UniqueId> uids = me.getValue();
            List<UniqueIdUniqueIdStoreCreator.Reference> refs = new ArrayList<UniqueIdUniqueIdStoreCreator.Reference>(uids.size());
            for (UniqueId uid : uids) {
                refs.add(new UniqueIdUniqueIdStoreCreator.Reference(refName, uid));
            }
            databaseMap.put(key, refs);
        }
        this.tmpInMemoryRefCache.clear();
    }
    
    void add(String keyId, P proposition) {
        assert keyId != null : "keyId cannot be null";
        /*
         * We used to intern the keyId here, but that in retrospect was a bad
         * idea because there could be millions of them.
         */
        Collections.putList(this.inMemoryPatientCache, keyId, proposition);
    }

    void flush(SQLGenResultProcessor resultProcessor) {
        String entitySpecName = resultProcessor.getEntitySpec().getName();
        assert entitySpecName != null : "entitySpecName cannot be null";
        for (Map.Entry<String, List<P>> me : this.inMemoryPatientCache
                .entrySet()) {
            List<P> propList = me.getValue();
            for (P prop : propList) {
                this.propIdToEntitySpecNames.put(prop.getId(), entitySpecName);
            }
            String keyId = me.getKey();
            /**
             * After inMemoryPatientCache is cleared, all values will be an
             * empty list. Thus, we need to check if the list is not empty, or
             * addAll will remove values from the cache, add nothing to them,
             * and add them back... The absence of the isEmpty check caused
             * massive performance degradation.
             */
            if (propList != null && !propList.isEmpty()) {
                addAll(keyId, propList);
            }
        }
        for (List<P> value : this.inMemoryPatientCache.values()) {
            value.clear();
        }
        
        this.currentPatientCache = PropositionStoreCreator.<P> getInstance()
                .newCacheStore();
        this.patientCache.add(this.currentPatientCache);
        this.patientCacheNumber++;
    }

    void clearTmp() {
        this.indexForAnyAdded = this.patientCacheNumber;
    }

    private void put(String keyId, List<P> propList) {
        // first, we add to or update the patients cache
        this.currentPatientCache.put(keyId, propList);
        this.keyIds.add(keyId);
    }
    
    private void addAll(String keyId, List<P> propositions) {
        List<P> propList = this.currentPatientCache.remove(keyId);

        if (propList == null) {
            propList = new ArrayList<P>();
        }
        propList.addAll(propositions);
        put(keyId, propList);
    }
}
