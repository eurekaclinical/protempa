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

import java.util.Set;
import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.Tuple;
import org.protempa.proposition.AbstractParameter;

/**
 *
 * @author Andrew Post
 */
class AbstractParameterPredicateExpression extends PropositionPredicateExpression {
    private static final long serialVersionUID = 1;
    
    private final String contextId;
    
    AbstractParameterPredicateExpression(Set<String> paramIds, 
            String contextId) {
        super(paramIds);
        this.contextId = contextId;
    }
    
    AbstractParameterPredicateExpression(String[] paramIds, String contextId) {
        super(paramIds);
        this.contextId = contextId;
    }
    
    AbstractParameterPredicateExpression(String paramId, String contextId) {
        super(paramId);
        this.contextId = contextId;
    }

    @Override
    public boolean evaluate(Object arg0, Tuple arg1, Declaration[] arg2, 
            Declaration[] arg3, WorkingMemory arg4, Object context) 
            throws Exception {
        AbstractParameter p = (AbstractParameter) arg0;
        if (!super.evaluate(arg0, arg1, arg2, arg3, arg4, context)) {
            return false;
        }
        
        if (this.contextId != null) {
            if (!this.contextId.equals(p.getContextId())) {
                return false;
            }
        }
        
        return true;
    }
    
    
}
