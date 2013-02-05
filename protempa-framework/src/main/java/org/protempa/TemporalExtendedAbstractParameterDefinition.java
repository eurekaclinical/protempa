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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.value.Value;

/**
 *
 * @author Andrew Post
 */
public class TemporalExtendedAbstractParameterDefinition 
        extends TemporalExtendedParameterDefinition {
    private final String contextId;
    
    public TemporalExtendedAbstractParameterDefinition(String parameterId) {
        super(parameterId);
        this.contextId = null;
    }

    public TemporalExtendedAbstractParameterDefinition(String parameterId, 
            Value value) {
        super(parameterId, value);
        this.contextId = null;
    }
    
    public TemporalExtendedAbstractParameterDefinition(String parameterId, 
            Value value, String contextId) {
        super(parameterId, value);
        this.contextId = contextId;
    }

    public String getContextId() {
        return this.contextId;
    }

    @Override
    public boolean getMatches(Proposition proposition) {
        if (!(proposition instanceof AbstractParameter)) {
            return false;
        }
        AbstractParameter ap = (AbstractParameter) proposition;
        
        if (!super.getMatches(proposition)) {
            return false;
        }
        
        if (this.contextId != null) {
            if (!this.contextId.equals(ap.getContextId())) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public boolean hasEqualFields(ExtendedPropositionDefinition obj) {
        if (!(obj instanceof TemporalExtendedAbstractParameterDefinition)) {
            return false;
        }
        TemporalExtendedAbstractParameterDefinition teapd = 
                (TemporalExtendedAbstractParameterDefinition) obj;
        if (!super.hasEqualFields(obj)) {
            return false;
        }
        
        return this.contextId == teapd.contextId || this.contextId.equals(teapd.contextId);
    }
    
    
    
    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }
}
