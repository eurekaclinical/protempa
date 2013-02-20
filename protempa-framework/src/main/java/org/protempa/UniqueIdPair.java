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

import org.protempa.proposition.UniqueId;

/**
 *
 * @author Andrew Post
 */
public class UniqueIdPair {
    private final String referenceName;
    private final UniqueId proposition;
    private final UniqueId reference;
    
    public UniqueIdPair(String referenceName, UniqueId proposition, 
            UniqueId reference) {
        assert referenceName != null : "referenceName cannot be null";
        assert proposition != null : "proposition cannot be null";
        assert reference != null : "reference cannot be null";
        this.referenceName = referenceName;
        this.proposition = proposition;
        this.reference = reference;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public UniqueId getProposition() {
        return proposition;
    }

    public UniqueId getReference() {
        return reference;
    }
    
    
}
