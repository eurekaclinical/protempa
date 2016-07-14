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
package org.protempa.proposition;

import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.List;
import org.protempa.QuerySession;
import org.protempa.SourceSystem;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.visitor.PropositionCheckedVisitable;
import org.protempa.proposition.visitor.PropositionVisitable;

/**
 * A data record. All implementations of this interface have property change 
 * support methods, but no change events are fired at present.
 * 
 * In Proposition, the equals() method is used to compare two instances of 
 * Proposition.  Two propositions are equal if they have the same 
 * datasource backend id, and unique identifier.  If either instance has a 
 * null datasource backend id, or null unique identifier, a object refernce 
 * check is made for equality.
 * 
 * The create, update, delete and download timestamp fields are database audit-
 * style fields to indicate when the record was created in the source system,
 * updated in the source system, deleted from the source system, and downloaded
 * from the source system. A data source backend may provide its own timestamps 
 * if the source system does not provide accurate values. The create and 
 * download timestamps are not used in any data computations. The update 
 * timestamp is intended for use by query results handlers to determine when a 
 * record has been updated. The presence of a non-null delete timestamp is 
 * intended for use by query results handlers to determine whether a record has 
 * been deleted. In the future, the temporal abstraction process may use these 
 * fields.
 * 
 * @author Andrew Post
 */
public interface Proposition extends PropositionVisitable,
        PropositionCheckedVisitable {

    /**
     * Gets this proposition's data type.
     * 
     * @return the identification {@link String} for the type of data
     *         represented by this proposition.
     */
    String getId();

    /**
     * Returns the data source type of the Proposition.
     * @return a {@link SourceSystem}.
     */
    SourceSystem getSourceSystem();

    /**
     * Adds a {@link PropertyChangeListener} to the listener list. The listener
     * is registered for all bound properties of this class (none at present).
     * 
     * If listener is null, no exception is thrown and no action is performed.
     * 
     * @param l
     *            the {@link PropertyChangeListener} to be added.
     */
    void addPropertyChangeListener(PropertyChangeListener l);

    /**
     * Removes a {@link PropertyChangeListener} from the listener list. This
     * method should be used to remove {@link PropertyChangeListener}s that were
     * registered for all bound properties of this class.
     * 
     * If listener is null, no exception is thrown and no action is performed.
     * 
     * @param l
     *            the {@link PropertyChangeListener} to be removed
     */
    void removePropertyChangeListener(PropertyChangeListener l);

    /**
     * Determines if the specified object is a {@link Proposition} and has
     * the same field values as this proposition.
     * 
     * @param prop an {@link Object}.
     * @return <code>true</code> if the specified object is a
     * {@link Proposition} and has the same field values, <code>false</code>
     * otherwise.
     */
    boolean isEqual(Object prop);

    /**
     * Gets the value of the specified property.
     *
     * @param name the name {@link String} of a valid property. Cannot be
     * <code>null</code>.
     * @return the {@link Value} of the specified property.
     */
    Value getProperty(String name);

    /**
     * Gets the global unique identifiers for the propositions that have the
     * specified 1:N relationship with this proposition.
     *
     * @param name the name of the relationship. Cannot be <code>null</code>.
     * @return a {@link List<UniqueIdentifier>} of global unique identifiers.
     * Guaranteed not <code>null</code>.
     * @see QuerySession#getReferences(org.protempa.proposition.Proposition,
     * java.lang.String) to get the propositions with the specified
     * relationship.
     */
    List<UniqueId> getReferences(String name);

    /**
     * Get the property names for the proposition
     * 
     * @return an array containing the names of all the properties contained
     */
    String[] getPropertyNames();

    /**
     * Get the reference names for the proposition
     * 
     * @return an array containing the names of all the references contained
     */
    String[] getReferenceNames();

    /**
     * Gets this proposition's global unique identifier.
     *
     * @return a {@link UniqueIdentifier}.
     */
    UniqueId getUniqueId();
    
    /**
     * Gets the timestamp this proposition was downloaded from the source 
     * system. If this proposition was derived, gets the date it was derived. A 
     * <code>null</code> value means the proposition was not downloaded from a
     * source system, or the source system did not provide an accurate
     * downloaded timestamp. Protempa does not use this timestamp in any 
     * fashion to control how propositions are used in computing temporal 
     * abstractions.
     * 
     * @return a timestamp, <code>null</code>.
     */
    Date getDownloadDate();
    
    /**
     * Gets the timestamp this proposition was created in the source system, or 
     * for derived propositions, the timestamp it was created through the 
     * temporal abstraction process. A <code>null</code> value means the create
     * timestamp was not recorded, or the source system did not have an
     * accurate created timestamp. Returned values may be in the future, if
     * that is what the source system provided. Protempa does not use this 
     * timestamp in any fashion to control how propositions are used in 
     * computing temporal abstractions.
     * 
     * @return a timestamp, or <code>null</code>.
     */
    Date getCreateDate();
    
    /**
     * Gets the timestamp when this proposition was last updated in the source
     * system, or for derived propositions, the timestamp it was last updated 
     * through the temporal abstraction process. Used to determine when a
     * proposition has been updated. A <code>null</code> value means that the
     * proposition's value has not been updated. When a source system does
     * not provide accurate update timestamps, a data source backend may set 
     * this value to the downloaded timestamp to ensure that Protempa does not 
     * miss an update.
     * 
     * @return a timestamp, or <code>null</code>.
     */
    Date getUpdateDate();
    
    /**
     * Gets the timestamp this proposition was deleted from the source system, 
     * or for derived propositions, the timestamp it was invalidated due to 
     * data changes. Used to determine when to delete or invalidate a 
     * proposition. A <code>null</code> value means the proposition should not 
     * be deleted. Any non-null timestamp will cause the proposition to be 
     * deleted, even a timestamp in the future. A data source backend may set 
     * this timestamp to the downloaded timestamp if it knows a proposition 
     * should be deleted but the source system does not provide an accurate 
     * delete timestamp.
     *
     * @return a timestamp, or <code>null</code>.
     */
    Date getDeleteDate();
}
