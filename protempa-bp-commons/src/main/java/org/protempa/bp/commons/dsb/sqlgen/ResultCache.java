package org.protempa.bp.commons.dsb.sqlgen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arp.javautil.map.CacheMap;
import org.protempa.DataSourceResultMap;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

public class ResultCache<P extends Proposition> {

    private final Map<String, List<P>> inMemoryPatientCache;
    private final Map<RefCacheKey, List<P>> refInMemoryPatientCache;
    private final List<Map<String, List<P>>> patientCache;
    private final Map<UniqueIdentifier, Location> conversionMap;
    private int patientCacheNumber;

    ResultCache() {
        this.inMemoryPatientCache = new HashMap<String, List<P>>();
        this.refInMemoryPatientCache = new HashMap<RefCacheKey, List<P>>();
        this.patientCache = new ArrayList<Map<String, List<P>>>();
        this.patientCache.add(new CacheMap<String, List<P>>());
        this.conversionMap = new CacheMap<UniqueIdentifier, Location>(500000);
    }

    P addReference(UniqueIdentifier uid, UniqueIdentifier refuid) {
        Location loc = this.conversionMap.get(uid);
        assert loc != null : "Could not find the location for proposition "
                + uid;
        RefCacheKey rck = new RefCacheKey();
        rck.cacheNumber = loc.cacheNumber;
        rck.patientKey = loc.patientKey;
        List<P> props = this.refInMemoryPatientCache.get(rck);
        if (props == null) {
            props = this.patientCache.get(loc.cacheNumber).get(loc.patientKey);
            this.refInMemoryPatientCache.put(rck, props);
        }
        return props.get(loc.index);
    }

    void flushReferences() {
        for (Map.Entry<RefCacheKey, List<P>> me :
                this.refInMemoryPatientCache.entrySet()) {
            RefCacheKey rck = me.getKey();
            List<P> rpl = me.getValue();
            this.patientCache.get(rck.cacheNumber).put(rck.patientKey, rpl);
        }
        this.refInMemoryPatientCache.clear();
    }

    private void addAll(String keyId, List<P> propositions) {
        List<P> propList =
                this.patientCache.get(this.patientCacheNumber).get(keyId);
        if (propList == null) {
            propList = new ArrayList<P>(1000);
        }
        propList.addAll(propositions);
        put(keyId, propList);
    }

    void add(String keyId, P proposition) {
        keyId = keyId.intern();
        List<P> propList = this.inMemoryPatientCache.get(keyId);
        if (propList == null) {
            propList = new ArrayList<P>(1000);
            this.inMemoryPatientCache.put(keyId, propList);
        }
        propList.add(proposition);
    }

    void flush() {
        for (Map.Entry<String, List<P>> me :
                this.inMemoryPatientCache.entrySet()) {
            List<P> propList = me.getValue();
            String keyId = me.getKey();
            if (propList != null) {
                addAll(keyId, propList);
            }
        }
        this.inMemoryPatientCache.clear();
        this.patientCache.add(new CacheMap<String, List<P>>());
        this.patientCacheNumber++;
    }

    private void put(String key, List<P> propList) {
        // first, we add to or update the patients cache
        this.patientCache.get(this.patientCacheNumber).put(key, propList);

        // now we add to or update the propositions cache, and also record
        // the mapping from one cache to the other in the conversion map
        for (int index = 0; index < propList.size(); index++) {
            P prop = propList.get(index);
            UniqueIdentifier uid = prop.getUniqueIdentifier();

            // we add a conversion from the UID to a location in the
            // patients cache if it doesn't already exist
            if (!this.conversionMap.containsKey(uid)) {
                Location loc =
                        new Location(key, index, this.patientCacheNumber);
                this.conversionMap.put(uid, loc);
            }
        }
    }

    public Map<String, List<P>> getPatientCache() {
        return new DataSourceResultMap<P>(this.patientCache);
    }

    private static class RefCacheKey {

        String patientKey;
        int cacheNumber;

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final RefCacheKey other = (RefCacheKey) obj;
            if ((this.patientKey == null) ? (other.patientKey != null)
                    : !this.patientKey.equals(other.patientKey)) {
                return false;
            }
            if (this.cacheNumber != other.cacheNumber) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 53 * hash + (this.patientKey != null ? this.patientKey.hashCode() : 0);
            hash = 53 * hash + this.cacheNumber;
            return hash;
        }
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
