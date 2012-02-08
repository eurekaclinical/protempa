/*
 * #%L
 * Protempa Framework
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
package org.protempa;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

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

    /**
	 * 
	 */
	private static final long serialVersionUID = -2754387751719003721L;
	private static final PropertyDefinition[] EMPTY_PROPERTIES =
            new PropertyDefinition[0];
    private static final ReferenceDefinition[] EMPTY_REFERENCES =
            new ReferenceDefinition[0];
    protected static final String DIRECT_CHILDREN_PROPERTY = "directChildren";
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
    private ReferenceDefinition[] referenceDefinitions;
    private boolean inDataSource;
    protected PropertyChangeSupport changes;

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
        this.id = setId0(kb, id).intern();
        this.directChildren = ArrayUtils.EMPTY_STRING_ARRAY;
        this.inverseIsA = ArrayUtils.EMPTY_STRING_ARRAY;
        this.displayName = "";
        this.abbrevDisplayName = "";
        this.description = "";
        this.propertyDefinitions = EMPTY_PROPERTIES;
        this.referenceDefinitions = EMPTY_REFERENCES;
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
            throw new IllegalArgumentException("duplicate proposition definition id: " + id);
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
        if (this.changes != null) {
            this.changes.firePropertyChange("abbreviatedDisplayName", old, abbrev);
        }
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
        if (this.changes != null) {
            this.changes.firePropertyChange("displayName", old, displayName);
        }
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
        if (this.changes != null) {
            this.changes.firePropertyChange("description", old, this.description);
        }
    }

    @Override
    public String[] getInverseIsA() {
        return inverseIsA.clone();
    }

    /**
     * Sets the children of this proposition definition.
     *
     * @param inverseIsA
     *            a {@link String[]} of proposition definition ids. No
     *            <code>null</code> or duplicate elements allowed.
     */
    public void setInverseIsA(String[] inverseIsA) {
        String[] old = this.inverseIsA;
        if (inverseIsA == null) {
            this.inverseIsA = ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            ProtempaUtil.checkArrayForNullElement(inverseIsA, "inverseIsA");
            ProtempaUtil.checkArrayForDuplicates(inverseIsA, "inverseIsA");
            this.inverseIsA = inverseIsA.clone();
        }
        if (this.changes != null) {
            this.changes.firePropertyChange("inverseIsA", old, getInverseIsA());
        }
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
     * Assigns this proposition with associated {@link TermDefinition} ids.
     * 
     * @param termId a term definition id {@link String}.  No
     *            <code>null</code> or duplicate elements allowed.
     */
    public final void setTermIds(String[] termIds) {
        String[] old = this.termIds;
        if (termIds == null) {
            this.termIds = ArrayUtils.EMPTY_STRING_ARRAY;
        } else {
            ProtempaUtil.checkArrayForNullElement(termIds, "termIds");
            ProtempaUtil.checkArrayForDuplicates(termIds, "termIds");
            this.termIds = termIds.clone();
        }

        if (this.changes != null) {
            this.changes.firePropertyChange("termIds", old, getTermIds());
        }
    }

    @Override
    public final PropertyDefinition[] getPropertyDefinitions() {
        return this.propertyDefinitions.clone();
    }

    @Override
    public final PropertyDefinition propertyDefinition(String name) {
        for (PropertyDefinition propertyDefinition :
                this.propertyDefinitions) {
            if (propertyDefinition.getName().equals(name)) {
                return propertyDefinition;
            }
        }
        return null;
    }

    public final void setPropertyDefinitions(
            PropertyDefinition[] propertyDefinitions) {
        PropertyDefinition[] old = this.propertyDefinitions;
        if (propertyDefinitions == null) {
            this.propertyDefinitions = EMPTY_PROPERTIES;
        } else {
            ProtempaUtil.checkArrayForNullElement(propertyDefinitions,
                    "propertyDefinitions");
            ProtempaUtil.checkArrayForDuplicates(propertyDefinitions,
                    "propertyDefinitions");
            this.propertyDefinitions = propertyDefinitions.clone();
        }
        if (this.changes != null) {
            this.changes.firePropertyChange("propertyDefinitions", old,
                    this.propertyDefinitions);
        }
    }

    @Override
    public final ReferenceDefinition[] getReferenceDefinitions() {
        return this.referenceDefinitions.clone();
    }

    @Override
    public final ReferenceDefinition referenceDefinition(String name) {
        for (ReferenceDefinition referenceDefinition :
                this.referenceDefinitions) {
            if (referenceDefinition.getName().equals(name)) {
                return referenceDefinition;
            }
        }
        return null;
    }

    public final void setReferenceDefinitions(
            ReferenceDefinition[] referenceDefinitions) {
        ReferenceDefinition[] old = this.referenceDefinitions;
        if (referenceDefinitions == null) {
            this.referenceDefinitions = EMPTY_REFERENCES;
        } else {
            ProtempaUtil.checkArrayForNullElement(referenceDefinitions,
                    "referenceDefinitions");
            ProtempaUtil.checkArrayForDuplicates(referenceDefinitions,
                    "referenceDefinitions");
            this.referenceDefinitions = referenceDefinitions.clone();
        }

        if (this.changes != null) {
            this.changes.firePropertyChange("referenceDefinitions", old,
                    this.referenceDefinitions);
        }
    }
    
    @Override
    public boolean getInDataSource() {
        return this.inDataSource;
    }

    @Override
    public void setInDataSource(boolean inDataSource) {
        boolean old = this.inDataSource;
        this.inDataSource = inDataSource;
        if (this.changes != null) {
            this.changes.firePropertyChange("inDataSource", old,
                    this.inDataSource);
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (this.changes == null) {
            this.changes = new PropertyChangeSupport(this);
        }
        if (this.changes != null) {
            this.changes.addPropertyChangeListener(listener);
        }
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (this.changes != null) {
            this.changes.removePropertyChangeListener(listener);
        }
    }

    @Override
    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        if (this.changes == null) {
            this.changes = new PropertyChangeSupport(this);
        }
        this.changes.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        if (this.changes != null) {
            this.changes.removePropertyChangeListener(propertyName, listener);
        }
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
        setReferenceDefinitions(null);
        setInDataSource(false);
    }

    protected abstract void recalculateDirectChildren();

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).append("displayName", this.displayName).append("description", this.description).append("abbreviatedDisplayName", this.abbrevDisplayName).append("inverseIsA", this.inverseIsA).append("directChildren", this.directChildren).append("termIds", this.termIds).append("concatenable", isConcatenable()).append("solid", isSolid()).append("propertyDefinitions", this.propertyDefinitions).append("referenceDefinitions", this.referenceDefinitions).toString();
    }
}
