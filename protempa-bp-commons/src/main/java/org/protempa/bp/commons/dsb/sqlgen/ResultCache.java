package org.protempa.bp.commons.dsb.sqlgen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.arp.javautil.collections.Collections;
import org.arp.javautil.map.DatabaseMap;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

public class ResultCache<P extends Proposition> {

    private Map<String, List<P>> inMemoryPatientCache;
    private final List<Map<String, List<P>>> patientCache;
    private Map<UniqueIdentifier, List<UniqueIdentifier>> tmpInMemoryRefCache;
    private List<DatabaseMap<UniqueIdentifier, List<UniqueIdentifier>>> tmpReferenceCache;
    private DatabaseMap<UniqueIdentifier, Location> conversionMap;
    private int patientCacheNumber;
    private int tmpReferenceCacheNumber = -1;
    private int indexForAnyAdded;

    ResultCache() {
        this.inMemoryPatientCache = new HashMap<String, List<P>>();
        this.patientCache = new ArrayList<Map<String, List<P>>>();
        this.patientCache.add(new DatabaseMap<String, List<P>>());
        this.conversionMap = new DatabaseMap<UniqueIdentifier, Location>();
        this.tmpReferenceCache =
                new ArrayList<DatabaseMap<UniqueIdentifier, List<UniqueIdentifier>>>();
        this.tmpInMemoryRefCache = new HashMap<UniqueIdentifier, List<UniqueIdentifier>>();
    }

    public Map<String, List<P>> getPatientCache() {
        this.inMemoryPatientCache = null;
        this.conversionMap.shutdown();
        this.conversionMap = null;
        this.tmpInMemoryRefCache = null;
        this.tmpReferenceCache = null;
        return new BerkeleyDBDataSourceResultMap<P>(this.patientCache);
    }

    boolean anyAdded() {
        return !this.patientCache.get(indexForAnyAdded).isEmpty();
    }

    void addReference(UniqueIdentifier uid, UniqueIdentifier refUid) {
        Collections.putList(this.tmpInMemoryRefCache, uid, refUid);
    }

    void flushReferences() {
        this.tmpReferenceCache.add(new DatabaseMap<UniqueIdentifier, List<UniqueIdentifier>>());
        this.tmpReferenceCacheNumber++;
        DatabaseMap<UniqueIdentifier, List<UniqueIdentifier>> dm =
                this.tmpReferenceCache.get(this.tmpReferenceCacheNumber);
        for (Iterator<Map.Entry<UniqueIdentifier, List<UniqueIdentifier>>> itr =
                this.tmpInMemoryRefCache.entrySet().iterator();
                itr.hasNext();) {
            Map.Entry<UniqueIdentifier, List<UniqueIdentifier>> me = itr.next();
            dm.put(me.getKey(), me.getValue());
            itr.remove();
        }
    }

    void flushReferencesFull(RefResultProcessor<P> resultProcessor) {
        for (Iterator<DatabaseMap<UniqueIdentifier, List<UniqueIdentifier>>> itr = this.tmpReferenceCache.iterator(); itr.hasNext();) {
            DatabaseMap<UniqueIdentifier, List<UniqueIdentifier>> dm = itr.next();
            for (UniqueIdentifier uid : dm.keySet()) {
                Location loc = this.conversionMap.get(uid);
                if (loc != null) {
                    Map<String, List<P>> pc = this.patientCache.get(loc.cacheNumber);
                    List<P> propositions = pc.remove(loc.patientKey);
                    for (P proposition : propositions) {
                        for (DatabaseMap<UniqueIdentifier, List<UniqueIdentifier>> dm2 : this.tmpReferenceCache) {
                            List<UniqueIdentifier> uids = dm2.remove(proposition.getUniqueIdentifier());
                            if (uids != null) {
                                resultProcessor.addReferences(proposition, uids);
                            }
                        }
                    }
                    pc.put(loc.patientKey, propositions);
                }
            }
            dm.shutdown();
            itr.remove();
        }
        this.tmpReferenceCache.clear();
        this.tmpReferenceCacheNumber = -1;
    }

    void add(String keyId, P proposition) {
        assert keyId != null : "keyId cannot be null";
        /*
         * We used to intern the keyId here, but that in retrospect was a bad 
         * idea because there could be millions of them.
         */
        Collections.putList(this.inMemoryPatientCache, keyId, proposition);
    }

    void flush(boolean hasRefs) {
        for (Map.Entry<String, List<P>> me :
                this.inMemoryPatientCache.entrySet()) {
            List<P> propList = me.getValue();
            String keyId = me.getKey();
            /**
             * After inMemoryPatientCache is cleared, all values will be an
             * empty list. Thus, we need to check if the list is not empty,
             * or addAll will remove values from the cache, add nothing to 
             * them, and add them back... The absence of the isEmpty check
             * caused massive performance degradation.
             */
            if (propList != null && !propList.isEmpty()) {
                addAll(keyId, propList, hasRefs);
            }
        }
        for (List<P> value : this.inMemoryPatientCache.values()) {
            value.clear();
        }
        this.patientCache.add(new DatabaseMap<String, List<P>>());
        this.patientCacheNumber++;
    }

    void clearTmp() {
        this.conversionMap.clear();
        this.indexForAnyAdded = this.patientCacheNumber;
    }

    private void put(String key, List<P> propList, boolean hasRefs) {
        // first, we add to or update the patients cache
        this.patientCache.get(this.patientCacheNumber).put(key, propList);

        if (hasRefs) {
            // now we add to or update the propositions cache, and also record
            // the mapping from one cache to the other in the conversion map
            for (int i = 0, n = propList.size(); i < n; i++) {
                P prop = propList.get(i);
                UniqueIdentifier uid = prop.getUniqueIdentifier();

                // we add a conversion from the UID to a location in the
                // patients cache if it doesn't already exist
                if (!this.conversionMap.containsKey(uid)) {
                    Location loc =
                            new Location(key, i, this.patientCacheNumber);
                    this.conversionMap.put(uid, loc);
                }
            }
        }
    }

    private void addAll(String keyId, List<P> propositions, boolean hasRefs) {
        List<P> propList =
                this.patientCache.get(this.patientCacheNumber).remove(keyId);
        if (propList == null) {
            propList = new ArrayList<P>();
        }
        propList.addAll(propositions);
        put(keyId, propList, hasRefs);
    }
    
    /**
     * Stores an index to a proposition for a key.
     *
     * This class has to be static or CacheMap will try to pull the ResultCache
     * into the cache, and ResultCache is not serializable.
     */
    static class Location implements Serializable {

        private static final long serialVersionUID = 5829710457303104633L;
        String patientKey;
        int index;
        int cacheNumber;

        Location(String key, int idx, int cacheNumber) {
            this.patientKey = key;
            this.index = idx;
            this.cacheNumber = cacheNumber;
        }
    }
}
