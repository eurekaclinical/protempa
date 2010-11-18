package org.protempa;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class TermSubsumption {
    private Set<String> termIds;
    
    private TermSubsumption(HashSet<String> termIds) {
        this.termIds = termIds;
    }
    
    public TermSubsumption() {
        this.termIds = new HashSet<String>();
    }
    
    public static TermSubsumption fromTerms(Collection<String> termIds) {
        return new TermSubsumption(new HashSet<String>(termIds));
    }
    
    public static TermSubsumption fromTerms(String... termIds) {
        HashSet<String> set = new HashSet<String>();
        for (String termId : termIds) {
            set.add(termId);
        }
        return new TermSubsumption(set);
    }
    
    public boolean containsTerm(String termId) {
        return termIds.contains(termId);
    }
    
    public Set<String> getTerms() {
        return termIds;
    }
}
