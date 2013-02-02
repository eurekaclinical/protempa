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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protempa;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.EvalExpression;
import org.drools.spi.Tuple;
import org.protempa.proposition.AbstractParameter;

/**
 *
 * @author arpost
 */
class AbstractionCombinerCondition implements EvalExpression {
    private static final long serialVersionUID = -3292416251502209461L;
    private final AbstractionDefinition abstractionDefinition;
    private final HorizontalTemporalInference hti = new HorizontalTemporalInference();

    public AbstractionCombinerCondition(AbstractionDefinition abstractionDefinition) {
        this.abstractionDefinition = abstractionDefinition;
    }

    @Override
    public boolean evaluate(Tuple arg0, Declaration[] arg1, WorkingMemory arg2, Object context) throws Exception {
        AbstractParameter a1 = (AbstractParameter) arg2.getObject(arg0.get(0));
        AbstractParameter a2 = (AbstractParameter) arg2.getObject(arg0.get(1));
        return a1 != a2 && (a1.getValue() == a2.getValue() || (a1.getValue() != null && a1.getValue().equals(a2.getValue()))) && (a1.getInterval().compareTo(a2.getInterval()) <= 0) && (hti.execute(this.abstractionDefinition, a1, a2) || abstractionDefinition.getGapFunction().execute(a1, a2));
    }

    @Override
    public Object createContext() {
        return null;
    }
    
}
