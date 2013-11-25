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
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.protempa.proposition.Proposition;

public class ExtendedPropositionDefinition implements Serializable {

    private static final long serialVersionUID = 3835638971180620664L;

    private String propositionId;

    private String displayName;

    private String abbreviatedDisplayName;

    private Set<PropertyConstraint> propertyConstraints;

    private volatile int hashCode;

    public ExtendedPropositionDefinition(String propositionId) {
        if (propositionId == null) {
            throw new IllegalArgumentException(
                    "A propositionId must be specified");
        }
        this.propositionId = propositionId;
        this.propertyConstraints = new HashSet<>();
    }

    /**
     * @return the proposition id <code>String</code>.
     */
    public String getPropositionId() {
        return propositionId;
    }

    /**
     * @return the <code>Set</code> of property constraints
     */
    public Set<PropertyConstraint> getPropertyConstraints() {
        return propertyConstraints;
    }

    /**
     * Returns whether a parameter has the same id and value, and consistent
     * duration as specified by this extended parameter definition.
     * 
     * @param proposition
     *            a <code>Proposition</code>
     * @return <code>true</code> if <code>proposition</code> has the same id and
     *         value, consistent duration, and property values that match any
     *         property constraints as specified by this extended parameter
     *         definition, or <code>false</code> if not, or if
     *         <code>proposition</code> is <code>null</code>.
     */
    public boolean getMatches(Proposition proposition) {
        if (proposition == null) {
            return false;
        } else {
            String pId = proposition.getId();
            if (propositionId != pId && !propositionId.equals(pId)) {
                return false;
            }
            // check that all property constraints are met
            if (propertyConstraints.size() > 0) {
                for (PropertyConstraint pc : propertyConstraints) {
                    for (String propertyName : proposition.getPropertyNames()) {
                        if (pc.getPropertyName().equals(propertyName)) {
                            if (!proposition.getProperty(propertyName)
                                    .compare(pc.getValue())
                                    .equals(pc.getValueComp())) {
                                return false;
                            }
                        }
                    }
                }
            }

            return true;
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAbbreviatedDisplayName() {
        return abbreviatedDisplayName;
    }

    public String getShortDisplayName() {
        String abbrevDisplayName = this.getAbbreviatedDisplayName();
        if (abbrevDisplayName != null && abbrevDisplayName.length() > 0) {
            return abbrevDisplayName;
        } else {
            return this.getDisplayName();
        }
    }

    public void setAbbreviatedDisplayName(String abbreviatedDisplayName) {
        this.abbreviatedDisplayName = abbreviatedDisplayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = super.hashCode();
        }
        return this.hashCode;
    }

    public boolean hasEqualFields(ExtendedPropositionDefinition obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        return propositionId.equals(obj.propositionId);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
