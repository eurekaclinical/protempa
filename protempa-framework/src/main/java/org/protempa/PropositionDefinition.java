package org.protempa;

import java.beans.PropertyChangeListener;
import java.io.Serializable;

/**
 * Foundation type for all objects that go in a Knowledge Base. Since knowledge
 * bases are serializable, then all knowledge base objects must be serializable
 * too. Every knowledge definition object has an unique id.
 * 
 * @author Andrew Post
 */
public interface PropositionDefinition extends Serializable, 
        PropositionDefinitionVisitable,
        PropositionDefinitionCheckedVisitable {

    /**
     * This proposition definition's display name.
     * 
     * @return a {@link String}. Guaranteed not to be <code>null</code>.
     */
    String getDisplayName();

    /**
     * This proposition definition's abbreviated display name.
     * 
     * @return a {@link String}. Guaranteed not to be <code>null</code>.
     */
    String getAbbreviatedDisplayName();

    /**
     * This proposition definition's id.
     * 
     * @return a {@link String}, guaranteed not to be <code>null</code>.
     */
    String getId();

    /**
     * Returns the ids of this proposition definition's children. These include
     * proposition definitions with abstracted-from, inverse-isa, and has-part
     * relations.
     * 
     * @return an array of {@link String}s, guaranteed not <code>null</code>.
     */
    String[] getDirectChildren();

    /**
     * Returns the ids of the proposition definitions that have an is-a
     * relationship with this proposition definition.
     * 
     * @return a {@link String[]} of proposition definition ids.
     */
    String[] getInverseIsA();

    /**
     * Returns whether instances of this proposition definition are
     * concatenable. A proposition is concatenable if, whenever it holds over
     * two consecutive time intervals, it holds also over their union.
     * 
     * @return <code>true</code> if concatenable, <code>false</code> if not.
     */
    boolean isConcatenable();

    /**
     * Returns whether intervals of this type are solid, i.e., never hold over
     * properly overlapping intervals. By definition, low-level abstraction 
     * intervals are solid.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    boolean isSolid();

    /**
     * Gets the ids of the {@link TermDefinition}s associated with this
     * proposition definition.
     * 
     * @return the term ids as an array of {@link String}s.
     */
    String[] getTermIds();

    PropertyDefinition[] getPropertyDefinitions();

    PropertyDefinition propertyDefinition(String name);

    ReferenceDefinition[] getReferenceDefinitions();

    ReferenceDefinition referenceDefinition(String name);

    void setInDataSource(boolean inDataSource);

    boolean getInDataSource();

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener);

    void removePropertyChangeListener(String proeprtyName,
            PropertyChangeListener listener);
}
