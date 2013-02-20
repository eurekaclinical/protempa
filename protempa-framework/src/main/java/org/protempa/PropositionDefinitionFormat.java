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

/**
 * Constructs a string containing a proposition definition's abbreviated display
 * name if it exists, otherwise the full display name.
 * 
 * @author Andrew Post
 */
public class PropositionDefinitionFormat {

    /**
     * Constructs a string containing the named proposition definition's
     * abbreviated display name if it exists, otherwise the full display name.
     *
     * @param propId
     *            the id {@link String} of a proposition definition.
     * @param ks
     *            the {@link KnowledgeSource} containing the proposition
     *            definition.
     * @return a {@link String}.
     */
    public String shortDisplayName(String propId, KnowledgeSource ks)
            throws KnowledgeSourceReadException {
        if (ks != null) {
            return shortDisplayName(ks.readPropositionDefinition(propId));
        } else {
            return propId;
        }
    }

    /**
     * Constructs a string containing a proposition definition's abbreviated
     * display name if it exists, otherwise the full display name.
     *
     * @param def
     *            a {@link PropositionDefinition}.
     * @return a {@link String}.
     */
    public String shortDisplayName(PropositionDefinition def) {
        if (def == null) {
            return null;
        }
        String abbrevName = def.getAbbreviatedDisplayName();
        if (abbrevName.length() > 0) {
            return abbrevName;
        } else {
            return def.getDisplayName();
        }
    }
}
