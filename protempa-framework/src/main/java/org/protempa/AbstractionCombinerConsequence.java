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

import java.util.Date;
import org.drools.WorkingMemory;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.ProviderBasedUniqueIdFactory;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.UniqueIdFactory;

/**
 *
 * @author Andrew Post
 */
class AbstractionCombinerConsequence extends AbstractCombinerConsequence<AbstractParameter> {
    private static final long serialVersionUID = -7984448674528718012L;
    
    public AbstractionCombinerConsequence(DerivationsBuilder derivationsBuilder) {
        super(derivationsBuilder);
    }

    @Override
    protected AbstractParameter newCombinedFact(AbstractParameter a1, 
            AbstractParameter a2, WorkingMemory wm) {
        String a1Id = a1.getId();
        Sequence<AbstractParameter> s = new Sequence<>(a1Id, 2);
        s.add(a1);
        s.add(a2);
        Segment<AbstractParameter> segment = new Segment<>(s);
        JBossRulesDerivedLocalUniqueIdValuesProvider provider = 
                new JBossRulesDerivedLocalUniqueIdValuesProvider(wm, a1Id);
        UniqueIdFactory factory = new ProviderBasedUniqueIdFactory(provider);
        AbstractParameter result = new AbstractParameter(a1Id, factory.getInstance());
        result.setSourceSystem(SourceSystem.DERIVED);
        result.setInterval(segment.getInterval());
        result.setValue(a1.getValue());
        result.setCreateDate(new Date());
        return result;
    }
    
}
