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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;
import org.protempa.proposition.CompoundInterval;
import org.protempa.proposition.CompoundValuedInterval;
import org.protempa.proposition.IntervalSectioner;
import org.protempa.proposition.Context;
import org.protempa.proposition.DerivedSourceId;
import org.protempa.proposition.DerivedUniqueId;
import org.protempa.proposition.TemporalParameter;
import org.protempa.proposition.ContextIntervalSectioner;
import org.protempa.proposition.UniqueId;

/**
 *
 * @author Andrew Post
 */
class ContextDefinitionSubContextConsequence implements Consequence {
    private final DerivationsBuilder derivationsBuilder;
    private final ContextDefinition def;

    ContextDefinitionSubContextConsequence(ContextDefinition def, 
            DerivationsBuilder derivationsBuilder) {
        assert def != null : "def cannot be null";
        assert derivationsBuilder != null : 
                "derivationsBuilder cannot be null";
        this.def = def;
        this.derivationsBuilder = derivationsBuilder;
    }

    @Override
    public void evaluate(KnowledgeHelper kh, WorkingMemory wm) throws Exception {
        int n = this.def.getSubContexts().length;
        List<Context> subContexts = new ArrayList<Context>(n);
        Tuple tuple = kh.getTuple();
        for (int i = 0; i < n; i++) {
            subContexts.add((Context) wm.getObject(tuple.get(i)));
        }
        
        List<CompoundInterval<Context>> intervals = 
                new ContextIntervalSectioner()
                .buildIntervalList(subContexts);
        for (CompoundInterval<Context> cvi : intervals) {
            Set<Context> props = cvi.getTemporalPropositions();
            
            boolean hasAll = true;
            for (Context subContext : subContexts) {
                if (!props.contains(subContext)) {
                    hasAll = false;
                    break;
                }
            }
            if (hasAll) {
                Context context = new Context(this.def.getId(), new UniqueId(
                    DerivedSourceId.getInstance(),
                    new DerivedUniqueId(UUID.randomUUID().toString())));
                context.setInterval(cvi.getInterval());
                kh.getWorkingMemory().insert(context);
                for (Context subContext : subContexts) {
                    this.derivationsBuilder.propositionAsserted(subContext, 
                            context);
                }
            }
        }
    }
    
}
