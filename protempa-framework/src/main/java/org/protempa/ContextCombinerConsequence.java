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

import java.util.Date;
import java.util.logging.Logger;
import org.drools.WorkingMemory;
import org.protempa.proposition.Context;
import org.protempa.proposition.ProviderBasedUniqueIdFactory;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.UniqueIdFactory;

/**
 *
 * @author Andrew Post
 */
class ContextCombinerConsequence extends AbstractCombinerConsequence<Context> {
    private static final long serialVersionUID = -7984448674528718012L;
    private final Logger logger;

    public ContextCombinerConsequence(DerivationsBuilder derivationsBuilder) {
        super(derivationsBuilder);
        this.logger = ProtempaUtil.logger();
    }

    @Override
    protected Context newCombinedFact(Context a1, Context a2, WorkingMemory wm) {
        String a1Id = a1.getId();
        Sequence<Context> s = new Sequence<>(a1Id, 2);
        s.add(a1);
        s.add(a2);
        Segment<Context> segment = new Segment<>(s);
        UniqueIdFactory uidFactory = new ProviderBasedUniqueIdFactory(
                new JBossRulesDerivedLocalUniqueIdValuesProvider(wm, a1Id));
        Context result = new Context(a1Id, uidFactory.getInstance());
        result.setCreateDate(new Date());
        result.setSourceSystem(SourceSystem.DERIVED);
        result.setInterval(segment.getInterval());
        return result;
    }
    
}
