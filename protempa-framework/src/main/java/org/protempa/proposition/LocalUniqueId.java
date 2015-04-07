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

import java.io.Serializable;

/**
 * A unique identifier for a proposition retrieved from a data source 
 * backend. The identifier must be unique for all propositions with a given
 * proposition id retrieved from the same data source backend. For any 
 * proposition, the identifier must be the same every time it is retrieved
 * from the data source backend. The combination of this identifier and a 
 * {@link SourceId} must uniquely identify a proposition across all data source 
 * backends.
 * 
 * The interface extends {@link Cloneable} because
 * {@link UniqueIdentifier}'s constructor clones the instance of it that is
 * passed in. It extends {@link Serializable} so that unique identifiers
 * can be serialized along with {@link Proposition}s.
 *
 * @author Andrew Post
 */
public interface LocalUniqueId extends Cloneable, Serializable {

    /**
     * Gets a string identifier that is unique to a proposition for all
     * propositions with its proposition id. This string must be the same
     * every time the proposition is accessed from the data source backend.
     * @return string representation of this unique identifier.
     */
    String getId();
    
    /**
     * Gets a number that is to unique to the proposition for all propositions
     * for a keyid with the same proposition id. This number must be the same 
     * every time the proposition is accessed from the data source backend. If 
     * no such number can be constructed from the data in the data source, then 
     * you may return the same number for every proposition, but keep in mind 
     * that some query results handlers rely on a number to distinguish 
     * instances of data with the same value recorded at the same time. Such 
     * query results handlers may treat propositions with the same proposition 
     * id and numerical local unique id as duplicates.
     * 
     * @return a numerical value unique to propositions with this id.
     */
    long getNumericalId();
    
    /**
     * Makes a shallow copy of this instance.
     *
     * @return a {@link LocalUniqueId} that is equal to this instance.
     */
    LocalUniqueId clone();
}
