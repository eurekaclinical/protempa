package org.protempa;

public final class Term {
    
    /*
     * the unique ID of this term
     */
    private final String id;
    
    /*
     * the display name of this term
     */
    private String displayName;
    
    /*
     * the short name of this term
     */
    private String abbrevDisplayName;
    
    /*
     * term IDs related to this one
     */
    private String[] inverseIsA;
    
    /*
     * direct children of this term, stored as IDs
     */
    private String[] directChildren;
    
    private Term(String id) {
        this.id = id;
    }
    
    static Term withId(String id) {
        return new Term(id);
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return the abbrevDisplayName
     */
    public String getAbbrevDisplayName() {
        return abbrevDisplayName;
    }

    /**
     * @param abbrevDisplayName the abbrevDisplayName to set
     */
    public void setAbbrevDisplayName(String abbrevDisplayName) {
        this.abbrevDisplayName = abbrevDisplayName;
    }

    /**
     * @return the inverseIsA
     */
    public String[] getInverseIsA() {
        return inverseIsA;
    }

    /**
     * @param inverseIsA the inverseIsA to set
     */
    public void setInverseIsA(String[] inverseIsA) {
        this.inverseIsA = inverseIsA;
    }

    /**
     * @return the directChildren
     */
    public String[] getDirectChildren() {
        return directChildren;
    }

    /**
     * @param directChildren the directChildren to set
     */
    public void setDirectChildren(String[] directChildren) {
        this.directChildren = directChildren;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
}
