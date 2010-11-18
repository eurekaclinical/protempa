package org.protempa;

import java.util.regex.Pattern;

/**
 * Represents a term pulled from a terminology in PROTEMPA.
 * 
 * @author Michel Mansour
 */

public final class Term {

    /*
     * the unique ID of this term: <terminology id>:<term id> (<SAB>:<term
     * code>)
     */
    private final String id;

    /*
     * the code for this term in its terminology
     */
    private final String code;

    /*
     * the terminology this term comes from
     */
    private final Terminology terminology;

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
     * term IDs related to this one
     */
    private String[] inverseIsA;

    /*
     * direct children of this term, stored as terms
     */
    private Term[] directChildren;

    /*
     * TermID pattern
     */
    private static Pattern idPattern;

    static {
        idPattern = Pattern.compile(".+:.+");
    }

    private static boolean checkId(String id) {
        return idPattern.matcher(id).matches();
    }

    private Term(String id, String terminology, String code) {
        this(id, Terminology.withName(terminology), code);
    }

    private Term(String id, Terminology terminology, String code) {
        this.id = id;
        this.terminology = terminology;
        this.code = code;
    }

    private Term(String terminology, String code) {
        this(makeId(Terminology.withName(terminology), code), terminology, code);
    }

    /**
     * Creates and returns a new <code>Term</code> with the given identifier
     * 
     * @param id
     *            the term's unique identifier
     * @return a new <code>Term</code> with the given identifier
     */
    public static Term withId(String id) throws MalformedTermIdException {
        if (checkId(id)) {
            String[] parts = parseId(id);
            Term term = new Term(id, parts[0], parts[1]);
            return term;
        } else {
            throw new MalformedTermIdException(
                    "Bad term ID: "
                            + id
                            + "\nTerm IDs must be of the form: <terminology name>:<terminology code>");
        }
    }

    public static Term fromTerminologyAndCode(String terminology, String code) {
        return new Term(terminology, code);
    }

    /**
     * Defines how term IDs are constructed
     * 
     */
    private static String makeId(Terminology terminology, String code) {
        return terminology.getName() + ":" + code;
    }

    private static String[] parseId(String id) {
        return id.split(":");
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName
     *            the displayName to set
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
     * @param description
     *            the description to set
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
     * @param semanticType
     *            the semanticType to set
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
     * @param abbrevDisplayName
     *            the abbrevDisplayName to set
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
     * @param inverseIsA
     *            the inverseIsA to set
     */
    public void setInverseIsA(String[] inverseIsA) {
        this.inverseIsA = inverseIsA;
    }

    /**
     * @return the directChildren
     */
    public Term[] getDirectChildren() {
        return directChildren;
    }

    /**
     * @param directChildren
     *            the directChildren to set
     */
    public void setDirectChildren(Term[] directChildren) {
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
}
