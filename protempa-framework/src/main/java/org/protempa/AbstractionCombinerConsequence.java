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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;

/**
 *
 * @author Andrew Post
 */
class AbstractionCombinerConsequence implements Consequence {
    private static final long serialVersionUID = -7984448674528718012L;
    private final DerivationsBuilder derivationsBuilder;

    public AbstractionCombinerConsequence(DerivationsBuilder derivationsBuilder) {
        this.derivationsBuilder = derivationsBuilder;
    }

    @Override
    public void evaluate(KnowledgeHelper arg0, WorkingMemory arg1) throws Exception {
        InternalFactHandle a1f = arg0.getTuple().get(0);
        AbstractParameter a1 = (AbstractParameter) arg1.getObject(a1f);
        String a1Id = a1.getId();
        InternalFactHandle a2f = arg0.getTuple().get(1);
        AbstractParameter a2 = (AbstractParameter) arg1.getObject(a2f);
        Sequence<AbstractParameter> s = new Sequence<AbstractParameter>(a1Id, 2);
        s.add(a1);
        s.add(a2);
        Segment<AbstractParameter> segment = new Segment<AbstractParameter>(s);
        AbstractParameter result = new AbstractParameter(a1Id);
        result.setDataSourceType(DataSourceType.DERIVED);
        result.setInterval(segment.getInterval());
        result.setValue(a1.getValue());
        Logger logger = ProtempaUtil.logger();
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Created {0} from {1} and {2}", new Object[]{result, a1, a2});
        }
        arg1.retract(a1f);
        arg1.retract(a2f);
        arg1.insert(result);
        // There should not be any forward derivations yet.
        // List<Proposition> a1PropForward =
        // this.derivationsBuilder.propositionRetractedForward(a1);
        List<Proposition> a1PropBackward = this.derivationsBuilder.propositionRetractedBackward(a1);
        // There should not be any forward derivations yet.
        // List<Proposition> a2PropForward =
        // this.derivationsBuilder.propositionRetractedForward(a2);
        List<Proposition> a2PropBackward = this.derivationsBuilder.propositionRetractedBackward(a2);
        for (Proposition prop : a1PropBackward) {
            this.derivationsBuilder.propositionReplaceForward(prop, a1, result);
            this.derivationsBuilder.propositionAssertedBackward(prop, result);
        }
        for (Proposition prop : a2PropBackward) {
            this.derivationsBuilder.propositionReplaceForward(prop, a2, result);
            this.derivationsBuilder.propositionAssertedBackward(prop, result);
        }
        logger.log(Level.FINER, "Asserted derived proposition {0}", result);
    }
    
}
