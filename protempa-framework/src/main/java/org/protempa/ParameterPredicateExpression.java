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

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.PredicateExpression;
import org.drools.spi.Tuple;
import org.protempa.proposition.Parameter;
import org.protempa.proposition.value.Value;

class ParameterPredicateExpression implements PredicateExpression {

    private static final long serialVersionUID = -8276489182248585309L;
    private final String parameterId;
    private final Value value;

    ParameterPredicateExpression(String parameterId, Value value) {
        this.parameterId = parameterId;
        this.value = value;
    }

    @Override
    public boolean evaluate(Object arg0, Tuple arg1, Declaration[] arg2,
            Declaration[] arg3, WorkingMemory arg4, Object context)
            throws Exception {
        Parameter p = (Parameter) arg0;
        return (parameterId == null || parameterId == p.getId() || parameterId
                .equals(p.getId()))
                && (value == null || value == p.getValue() || value.equals(p
                .getValue()));
    }

    @Override
    public Object createContext() {
        return null;
    }
}
