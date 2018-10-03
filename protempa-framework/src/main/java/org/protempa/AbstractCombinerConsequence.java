package org.protempa;

/*-
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2018 Emory University
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

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.Proposition;

/**
 *
 * @author Andrew Post
 * @param <P> an implementation of the Proposition interface.
 */
public abstract class AbstractCombinerConsequence<P extends Proposition> implements Consequence {
    
    private static final Logger LOGGER = Logger.getLogger(AbstractCombinerConsequence.class.getName());

    private final DerivationsBuilder derivationsBuilder;

    AbstractCombinerConsequence(DerivationsBuilder derivationsBuilder) {
        this.derivationsBuilder = derivationsBuilder;
    }
    
    @Override
    public void evaluate(KnowledgeHelper kh, WorkingMemory wm) throws Exception {
        InternalFactHandle a1f = kh.getTuple().get(0);
        P a1 = (P) wm.getObject(a1f);
        InternalFactHandle a2f = kh.getTuple().get(1);
        P a2 = (P) wm.getObject(a2f);
        P result = newCombinedFact(a1, a2, wm);
        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST, "Created {0} from {1} and {2}", new Object[]{result, a1, a2});
        }
        kh.retract(a1f);
        kh.retract(a2f);
        kh.insertLogical(result);
        Set<Proposition> a1PropBackward = this.derivationsBuilder.propositionRetractedBackward(a1);
        Set<Proposition> a2PropBackward = this.derivationsBuilder.propositionRetractedBackward(a2);
        for (Proposition prop : a1PropBackward) {
            this.derivationsBuilder.propositionReplaceForward(prop, a1, result);
            this.derivationsBuilder.propositionAssertedBackward(prop, result);
        }
        for (Proposition prop : a2PropBackward) {
            this.derivationsBuilder.propositionReplaceForward(prop, a2, result);
            this.derivationsBuilder.propositionAssertedBackward(prop, result);
        }
        LOGGER.log(Level.FINER, "Asserted derived proposition {0}", result);
    }

    protected abstract P newCombinedFact(P a1, P a2, WorkingMemory wm);
    
}
