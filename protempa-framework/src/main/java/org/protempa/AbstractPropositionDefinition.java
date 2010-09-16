package org.protempa;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Abstract base class for all knowledge definition classes.
 * 
 * FIXME We support event, abstraction, and primitive parameter definitions with
 * the same name, yet <code>Protempa</code>'s public API assumes that they
 * all share the same namespace. This needs to be fixed. I'm leaning toward
 * having all knowledge definitions share the same namespace, in which case, no
 * changes to Protempa's API would be required, and the duplicate id checking
 * could all occur in this class in a concrete implementation of
 * <code>setId0()</code>.
 * 
 * @author Andrew Post
 */
public abstract class AbstractPropositionDefinition implements
        PropositionDefinition {

    protected static final String DIRECT_CHILDREN_PROPERTY = "directChildren";
    private static final String[] EMPTY_STRING_ARR = new String[0];
    /**
     * The id of propositions created by this definition.
     */
    private final String id;
    /**
     * The display name of this knowledge definition.
     */
    private String displayName;
    /**
     * The abbreviated display name of this knowledge definition.
     */
    private String abbrevDisplayName;
    /**
     * An array of proposition definition id {@link String}s.
     */
    private String[] inverseIsA;
    protected String[] directChildren;
    private String[] termIds;
    private String description;
    private PropertyDefinition[] propertyDefinitions;
    protected final PropertyChangeSupport changes;

    /**
     * Creates a new knowledge definition.
     *
     * @param kb
     *            the <code>KnowledgeBase</code> in which to store this
     *            object.
     * @param id
     *            the requested {@link String} id of propositions created by
     *            this definition. If <code>null</code> or already taken by
     *            another knowledge definition, another id will be assigned
     *            (check with <code>getId()</code>).
     */
    protected AbstractPropositionDefinition(KnowledgeBase kb, String id) {
        if (kb == null) {
            throw new IllegalArgumentException(
                    "A knowledge base must be specified");
        }
        this.id = setId0(kb, id);
        this.directChildren = EMPTY_STRING_ARR;
        this.inverseIsA = EMPTY_STRING_ARR;
        this.displayName = "";
        this.abbrevDisplayName = "";
        this.description = "";
        this.propertyDefinitions = new PropertyDefinition[0];
        this.changes = new PropertyChangeSupport(this);
    }

    /**
     * Uses the knowledge base to validate the requested <code>id</code>, and
     * optionally provide a valid one of the requested <code>id</code> can't
     * be given out (e.g., because the requested <code>id</code> was
     * <code>null</code>, because the requested <code>id</code> is already
     * taken.
     *
     * @param kb
     *            this knowledge definition's {@link KnowledgeBase}.
     * @param id
     *            the requested id {@link String}.
     * @param fail
     *            if an <code>IllegalArgumentException</code> should be thrown
     *            when an invalid <code>id</code> is provided. If
     *            <code>true</code> the exception is thrown; if
     *            <code>false</code>, a valid id is automatically assigned.
     * @return the assigned id <code>String</code>.
     */
    private static String setId0(KnowledgeBase kb, String id) {
        if (id == null || id.length() == 0) {
            return kb.getNextKnowledgeDefinitionObjectId();
        } else if (!kb.isUniqueKnowledgeDefinitionObjectId(id)) {
            return kb.getNextKnowledgeDefinitionObjectId();
        } else {
            return id;
        }
    }

    @Override
    public final String getId() {
        return this.id;
    }

    @Override
    public final String getAbbreviatedDisplayName() {
        return this.abbrevDisplayName;
    }

    /**
     * Sets this knowledge definition's abbreviated display name.
     *
     * @param abbrev
     *            an abbreviated display name {@link String}.
     */
    public final void setAbbreviatedDisplayName(String abbrev) {
        if (abbrev == null) {
            abbrev = "";
        }
        String old = this.abbrevDisplayName;
        this.abbrevDisplayName = abbrev;
        this.changes.firePropertyChange("abbreviatedDisplayName", old, abbrev);
    }

    @Override
    public final String getDisplayName() {
        return this.displayName;
    }

    /**
     * Sets this proposition definition's human-readable display name.
     *
     * @param displayName
     *            a display name {@link String}.
     */
    public final void setDisplayName(String displayName) {
        if (displayName == null) {
            displayName = "";
        }
        String old = this.displayName;
        this.displayName = displayName;
        this.changes.firePropertyChange("displayName", old, displayName);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        if (description == null) {
            description = "";
        }
        String old = this.description;
        this.description = description;
        this.changes.firePropertyChange("description", old, this.description);
    }

    @Override
    public String[] getInverseIsA() {
        return inverseIsA.clone();
    }

    /**
     * Sets the children of this proposition definition.
     *
     * @param inverseIsA
     *            a {@link String[]} of proposition definition ids.
     */
    public void setInverseIsA(String[] inverseIsA) {
        if (inverseIsA == null) {
            inverseIsA = EMPTY_STRING_ARR;
        }
        String[] old = this.inverseIsA;
        this.inverseIsA = inverseIsA.clone();
        this.changes.firePropertyChange("inverseIsA", old, getInverseIsA());
        recalculateDirectChildren();
    }

    @Override
    public String[] getDirectChildren() {
        return this.directChildren.clone();
    }

    @Override
    public final String[] getTermIds() {
        return this.termIds.clone();
    }

    /**
     * Assigns this proposition with an associated {@link TermDefinition}.
     * @param termId a term id {@link String}.
     */
    public final void setTermIds(String[] termIds) {
        if (termIds == null) {
            termIds = EMPTY_STRING_ARR;
        }
        String[] old = this.termIds;
        this.termIds = termIds.clone();
        this.changes.firePropertyChange("termIds", old, getTermIds());
    }

    @Override
    public final PropertyDefinition[] getPropertyDefinitions() {
        return this.propertyDefinitions.clone();
    }

    public final void setPropertyDefinitions(
            PropertyDefinition[] propertyDefinitions) {
        if (propertyDefinitions == null) {
            propertyDefinitions = new PropertyDefinition[0];
        } else {
            propertyDefinitions = propertyDefinitions.clone();
        }
        PropertyDefinition[] old = this.propertyDefinitions.clone();
        this.propertyDefinitions = propertyDefinitions;
        this.changes.firePropertyChange("propertyDefinitions", old,
                this.propertyDefinitions);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.changes.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.changes.removePropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        this.changes.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        this.changes.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Resets this proposition definition to default values.
     */
    public void reset() {
        setDisplayName(null);
        setAbbreviatedDisplayName(null);
        setDescription(null);
        setInverseIsA(null);
        setTermIds(null);
        setPropertyDefinitions(null);
    }

    protected abstract void recalculateDirectChildren();

    protected Map<String, Object> toStringFields() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("id", this.id);
        result.put("displayName", this.displayName);
        result.put("description", this.description);
        result.put("abbreviatedDisplayName", this.abbrevDisplayName);
        result.put("inverseIsA", this.inverseIsA);
        result.put("directChildren", this.directChildren);
        result.put("termIds", this.termIds);
        result.put("concatenable", isConcatenable());
        result.put("solid", isSolid());
        result.put("propertyDefinitions", this.propertyDefinitions);
        return result;
    }
}
