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

import java.io.Serializable;
import java.util.Date;

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
     * @return a {@link String}. Guaranteed not <code>null</code>.
     */
    String getDisplayName();

    /**
     * This proposition definition's abbreviated display name.
     * 
     * @return a {@link String}. Guaranteed not <code>null</code>.
     */
    String getAbbreviatedDisplayName();
    
    /**
     * A longer description of the proposition definition.
     * 
     * @return a {@link String}. Guaranteed not <code>null</code>.
     */
    String getDescription();

    /**
     * This proposition definition's id.
     * 
     * @return a {@link String}, guaranteed not to be <code>null</code>.
     */
    String getId();
    
    /**
     * The proposition id of propositions derived by this proposition 
     * definition. The default value is the value of this proposition 
     * definition's <code>id</code> field.
     * 
     * @return a proposition id.
     */
    String getPropositionId();

    /**
     * Returns the ids of this proposition definition's children. These include
     * proposition definitions with abstracted-from, inverse-isa, and has-part
     * relations.
     * 
     * @return an array of {@link String}s, guaranteed not <code>null</code>.
     */
    String[] getChildren();

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
     * properly overlapping intervals. For example, a proposition representing
     * a pregnancy is solid because it is not possible for two pregnancies to
     * overlap in time. This field could be used for data and interval 
     * validation. It alternatively could be used to merge overlapping
     * propositions if there are multiple ways to infer a proposition. This
     * field is settable for some types of propositions.
     *
     * @return <code>true</code> if solid, <code>false</code> if not.
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

    /**
     * Returns the reference definition with the given name.
     * @param name a reference definition name {@link String}.
     * @return the {@link ReferenceDefinition} with the given name, or
     * <code>null</code> if this proposition definition has no reference 
     * definition with the given name
     */
    ReferenceDefinition referenceDefinition(String name);

    boolean getInDataSource();

    SourceId getSourceId();
    
    /**
     * Returns the version number of this proposition definition, for 
     * knowledge source backends that maintain version information.
     * 
     * @return a version {@link String}, or <code>null</code> if the 
     * knowledge source backend does not maintain version information. The 
     * string's format is specified to the knowledge source backend and the
     * original information source.
     */
    String getVersion();
    
    /**
     * Returns the timestamp of when this proposition definition's information
     * was downloaded from its source, e.g., a terminology standard, if such
     * information is available.
     * 
     * @return a {@link Date}, or <code>null</code> if no download timestamp
     * information is available.
     */
    Date getDownloaded();
    
    /**
     * Returns the timestamp of when this proposition definition was accessed from the knowledge source.
     * 
     * @return a {@link Date}. Guaranteed not <code>null</code>.
     */
    Date getAccessed();
    
    /**
     * Returns the timestamp of when this proposition definition was created,
     * if the knowledge source backend maintains this information.
     * 
     * @return a {@link Date}, or <code>null</code> if the knowledge source
     * backend does not maintain created timestamp information.
     */
    Date getCreated();
    
    /**
     * Returns the timestamp of when this proposition definition was last 
     * updated, if the knowledge source backend maintains this information.
     * 
     * @return a {@link Date}, or <code>null</code> if the knowledge source
     * backend does not maintain last updated timestamp information.
     */
    Date getUpdated();
    
    Attribute[] getAttributes();
    
    Attribute attribute(String name);
    
}
