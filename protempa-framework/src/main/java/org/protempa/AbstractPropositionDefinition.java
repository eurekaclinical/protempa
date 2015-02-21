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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Abstract base class for all knowledge definition classes.
 *
 * FIXME We support event, abstraction, and primitive parameter definitions with
 * the same name, yet
 * <code>Protempa</code>'s public API assumes that they all share the same
 * namespace. This needs to be fixed. I'm leaning toward having all knowledge
 * definitions share the same namespace, in which case, no changes to Protempa's
 * API would be required, and the duplicate id checking could all occur in this
 * class in a concrete implementation of
 * <code>setId0()</code>.
 *
 * @author Andrew Post
 */
public abstract class AbstractPropositionDefinition implements
        PropositionDefinition {

    private static final long serialVersionUID = -2754387751719003721L;
    private static final PropertyDefinition[] EMPTY_PROPERTIES =
            new PropertyDefinition[0];
    private static final ReferenceDefinition[] EMPTY_REFERENCES =
            new ReferenceDefinition[0];
    protected static final String CHILDREN_PROPERTY = "children";
    /**
     * The id of propositions created by this definition.
     */
    private final String id;
    private String propositionId;
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
    protected String[] children;
    private String[] termIds;
    private String description;
    private PropertyDefinition[] propertyDefinitions;
    private ReferenceDefinition[] referenceDefinitions;
    private boolean inDataSource;
    protected PropertyChangeSupport changes;
    private SourceId sourceId;
    private Date accessed;
    private Date created;
    private Date updated;
    private Date downloaded;
    private String version;

    /**
     * Creates a new knowledge definition.
     *
     * @param id the requested {@link String} id of propositions created by this
     * definition. If <code>null</code> or already taken by another knowledge
     * definition, another id will be assigned (check with
     * <code>getId()</code>).
     */
    AbstractPropositionDefinition(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id.intern();
        this.propositionId = this.id;
        this.children = ArrayUtils.EMPTY_STRING_ARRAY;
        this.inverseIsA = ArrayUtils.EMPTY_STRING_ARRAY;
        this.termIds = ArrayUtils.EMPTY_STRING_ARRAY;
        this.displayName = "";
        this.abbrevDisplayName = "";
        this.description = "";
        this.propertyDefinitions = EMPTY_PROPERTIES;
        this.referenceDefinitions = EMPTY_REFERENCES;
        this.sourceId = NotRecordedSourceId.getInstance();
    }

    @Override
    public final String getId() {
        return this.id;
    }

    @Override
    public final String getPropositionId() {
        return this.propositionId;
    }

    /**
     * Sets the proposition id of propositions derived by this proposition
     * definition.
     * 
     * @param propId a proposition id. Pass in <code>null</code> to set the
     * value of this field to the value of this proposition definition's
     * <code>id</code> field.
     */
    public final void setPropositionId(String propId) {
        if (propId == null) {
            this.propositionId = this.id;
        } else {
            this.propositionId = propId;
        }
    }

    @Override
    public final String getAbbreviatedDisplayName() {
        return this.abbrevDisplayName;
    }

    /**
     * Sets this knowledge definition's abbreviated display name.
     *
     * @param abbrev an abbreviated display name {@link String}.
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
     * @param displayName a display name {@link String}.
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

    @Override
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
     * @param inverseIsA a {@link String[]} of proposition definition ids. No
     * <code>null</code> or duplicate elements allowed.
     */
    public void setInverseIsA(String... inverseIsA) {
        String[] old = this.inverseIsA;
        ProtempaUtil.checkArrayForNullElement(inverseIsA, "inverseIsA");
        ProtempaUtil.checkArrayForDuplicates(inverseIsA, "inverseIsA");
        this.inverseIsA = inverseIsA.clone();
        if (this.changes != null) {
            this.changes.firePropertyChange("inverseIsA", old, getInverseIsA());
        }
        recalculateChildren();
    }

    @Override
    public String[] getChildren() {
        return this.children.clone();
    }

    @Override
    public final String[] getTermIds() {
        return this.termIds.clone();
    }

    /**
     * Assigns this proposition with associated {@link TermDefinition} ids.
     *
     * @param termId a term definition id {@link String}. No <code>null</code>
     * or duplicate elements allowed.
     */
    public final void setTermIds(String... termIds) {
        String[] old = this.termIds;
        ProtempaUtil.checkArrayForNullElement(termIds, "termIds");
        ProtempaUtil.checkArrayForDuplicates(termIds, "termIds");
        this.termIds = termIds.clone();

        if (this.changes != null) {
            this.changes.firePropertyChange("termIds", old, getTermIds());
        }
    }

    @Override
    public final PropertyDefinition[] getPropertyDefinitions() {
        return this.propertyDefinitions.clone();
    }

    @Override
    public final PropertyDefinition propertyDefinition(String id) {
        for (PropertyDefinition propertyDefinition :
                this.propertyDefinitions) {
            if (propertyDefinition.getId().equals(id)) {
                return propertyDefinition;
            }
        }
        return null;
    }

    public final void setPropertyDefinitions(
            PropertyDefinition... propertyDefinitions) {
        PropertyDefinition[] old = this.propertyDefinitions;
        ProtempaUtil.checkArrayForNullElement(propertyDefinitions,
                "propertyDefinitions");
        ProtempaUtil.checkArrayForDuplicates(propertyDefinitions,
                "propertyDefinitions");
        this.propertyDefinitions = propertyDefinitions.clone();
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
            if (referenceDefinition.getId().equals(name)) {
                return referenceDefinition;
            }
        }
        return null;
    }

    public final void setReferenceDefinitions(
            ReferenceDefinition... referenceDefinitions) {
        ReferenceDefinition[] old = this.referenceDefinitions;
        ProtempaUtil.checkArrayForNullElement(referenceDefinitions,
                "referenceDefinitions");
        ProtempaUtil.checkArrayForDuplicates(referenceDefinitions,
                "referenceDefinitions");
        this.referenceDefinitions = referenceDefinitions.clone();

        if (this.changes != null) {
            this.changes.firePropertyChange("referenceDefinitions", old,
                    this.referenceDefinitions);
        }
    }

    @Override
    public boolean getInDataSource() {
        return this.inDataSource;
    }

    public void setInDataSource(boolean inDataSource) {
        boolean old = this.inDataSource;
        this.inDataSource = inDataSource;
        if (this.changes != null) {
            this.changes.firePropertyChange("inDataSource", old,
                    this.inDataSource);
        }
    }

    @Override
    public SourceId getSourceId() {
        return this.sourceId;
    }

    public void setSourceId(SourceId sourceId) {
        if (sourceId == null) {
            this.sourceId = NotRecordedSourceId.getInstance();
        } else {
            this.sourceId = sourceId;
        }
    }

    @Override
    public Date getAccessed() {
        return this.accessed;
    }

    @Override
    public Date getCreated() {
        return this.created;
    }

    @Override
    public Date getUpdated() {
        return this.updated;
    }

    public void setAccessed(Date accessed) {
        this.accessed = accessed;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
    @Override
    public Date getDownloaded() {
        return this.downloaded;
    }

    public void setDownloaded(Date downloaded) {
        this.downloaded = downloaded;
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
        setInverseIsA(ArrayUtils.EMPTY_STRING_ARRAY);
        setTermIds(ArrayUtils.EMPTY_STRING_ARRAY);
        setPropertyDefinitions(new PropertyDefinition[]{});
        setReferenceDefinitions(new ReferenceDefinition[]{});
        setInDataSource(false);
        setAccessed(null);
        setCreated(null);
        setUpdated(null);
    }

    protected abstract void recalculateChildren();

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }
}
