/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
package org.protempa;

import java.util.regex.Pattern;
import org.apache.commons.lang.ArrayUtils;

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
     * direct children of this term
     */
    private String[] directChildren;

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
        this.displayName = "";
        this.abbrevDisplayName = "";
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
        if (displayName == null)
            displayName = "";
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
        if (abbrevDisplayName == null)
            abbrevDisplayName = "";
        this.abbrevDisplayName = abbrevDisplayName;
    }

    /**
     * Returns a newly-created array of the ids of the terms with which this
     * term has an inverse is-a relationship.
     *
     * @return a {@link String[]}.
     */
    public String[] getInverseIsA() {
        return inverseIsA.clone();
    }

    /**
     * @param inverseIsA
     *            the inverseIsA to set
     */
    public void setInverseIsA(String[] inverseIsA) {
        if (inverseIsA == null)
            inverseIsA = ArrayUtils.EMPTY_STRING_ARRAY;
        this.inverseIsA = inverseIsA.clone();
    }

    /**
     * Returns a newly-created array of the ids of this term's direct children.
     *
     * @return a {@link String[]}.
     */
    public String[] getDirectChildren() {
        return directChildren.clone();
    }

    /**
     * @param directChildren
     *            the directChildren to set
     */
    public void setDirectChildren(String[] directChildren) {
        if (directChildren == null) {
            directChildren = ArrayUtils.EMPTY_STRING_ARRAY;
        }
        this.directChildren = directChildren.clone();
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
