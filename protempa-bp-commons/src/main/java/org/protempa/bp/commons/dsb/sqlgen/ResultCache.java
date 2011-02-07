package org.protempa.bp.commons.dsb.sqlgen;

import java.util.List;
import java.util.Map;

import org.arp.javautil.map.CacheMap;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

public class ResultCache<P extends Proposition> {
    private final Map<String, List<P>> patientCache;
    private final Map<UniqueIdentifier, P> propositionCache;
    private final Map<UniqueIdentifier, Location> conversionMap;

    public ResultCache() {
        this.patientCache = new CacheMap<String, List<P>>();
        this.propositionCache = new CacheMap<UniqueIdentifier, P>();
        this.conversionMap = new CacheMap<UniqueIdentifier, Location>();
    }

    public void put(UniqueIdentifier uid, P prop) {
        // first, we add to or update the propositions cache
        this.propositionCache.put(uid, prop);

        // now we update the patients cache using the conversion map, if
        // we know where the proposition is located in the patient cache
        if (this.conversionMap.containsKey(uid)) {
            Location loc = this.conversionMap.get(uid);
            List<P> propList = this.patientCache.get(loc
                    .getPatientKey());
            propList.set(loc.getIndex(), prop);
            this.patientCache.put(loc.getPatientKey(), propList);
        }
    }

    public void put(String key, List<P> propList) {
        // first, we add to or update the patients cache
        this.patientCache.put(key, propList);

        // now we add to or update the propositions cache, and also record
        // the mapping from one cache to the other in the conversion map
        int index;
        for (index = 0; index < propList.size(); index++) {
            P prop = propList.get(index);
            UniqueIdentifier uid = prop.getUniqueIdentifier();
            this.propositionCache.put(uid, prop);

            // we add a conversion from the UID to a location in the 
            // patients cache if it doesn't already exist
            if (!this.conversionMap.containsKey(uid)) {
                Location loc = new Location(key, index);
                this.conversionMap.put(uid, loc);
            }
        }
    }

    class Location {
        private String patientKey;
        private int index;

        public Location(String key, int idx) {
            this.patientKey = key;
            this.index = idx;
        }

        public String getPatientKey() {
            return patientKey;
        }

        public void setPatientKey(String patientKey) {
            this.patientKey = patientKey;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}
