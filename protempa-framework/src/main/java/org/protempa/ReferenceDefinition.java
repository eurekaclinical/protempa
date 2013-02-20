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

/**
 * Defines a proposition definition's references.
 * 
 * @author Andrew Post
 * @see {@link PropertyDefinition}.
 */
public class ReferenceDefinition implements Serializable {
    private static final long serialVersionUID = -8746451343197890264L;
    
    private final String name;
    private final String[] propositionIds;

    public ReferenceDefinition(String name, String[] propositionIds) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        ProtempaUtil.checkArray(propositionIds, "propositionIds");
        if (propositionIds.length == 0)
            throw new IllegalArgumentException(
                    "propositionIds must have at least one element");
        
        this.name = name.intern();
        this.propositionIds = propositionIds.clone();
    }

    /**
     * Gets the name of the reference.
     *
     * @return a reference name {@link String}.
     * Guaranteed not <code>null</code>.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the allowed proposition ids for this reference.
     *
     * @return a {@link String[]} of proposition ids. Guaranteed not
     * <code>null</code>.
     */
    public String[] getPropositionIds() {
        return this.propositionIds.clone();
    }

}
