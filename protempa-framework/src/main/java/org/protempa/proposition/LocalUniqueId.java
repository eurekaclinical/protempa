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
 * For implementations of unique identifier specific to a data source backend
 * or to derived values. It implements {@link Cloneable} because
 * {@link UniqueIdentifier}'s constructor clones the instance of it that is
 * passed in. It implements {@link Serializable} so that unique identifiers
 * can be serialized along with {@link Proposition}s.
 *
 * @author Andrew Post
 */
public interface LocalUniqueId extends Cloneable, Serializable {

    /**
     * Makes a shallow copy of this instance.
     *
     * @return a {@link LocalUniqueIdentifier} that is equal to this instance.
     */
    LocalUniqueId clone();
    
    String getId();
}
