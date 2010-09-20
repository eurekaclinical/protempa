package org.protempa;

/**
 * Represents a term pulled from a terminology in PROTEMPA.
 * 
 * @author Michel Mansour
 */

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
     * the description of this term
     */
    private String description;
    
    /*
     * the semantic type of this term
     */
    private String semanticType;
    
    /*
     * the short name of this term
     */
    private String abbrevDisplayName;
    
    /*
     * the code for this term in its terminology
     */
    private String code;
    
    /*
     * the terminology this term comes from
     */
    private Terminology terminology;
    
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
    
    public static Term withId(String id) {
        return new Term(id);
    }
    
    public static Term parseId(String id) {
        return new Term(id);
    }
    
    public static String toId(Term term) {
        return term.getCode();
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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the semanticType
     */
    public String getSemanticType() {
        return semanticType;
    }

    /**
     * @param semanticType the semanticType to set
     */
    public void setSemanticType(String semanticType) {
        this.semanticType = semanticType;
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
    
    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }
    
    /**
     * @return the terminology
     */
    public Terminology getTerminology() {
        return terminology;
    }
    
    /**
     * @param terminology the terminology to set
     */
    public void setTerminology(Terminology terminology) {
        this.terminology = terminology;
    }
}
