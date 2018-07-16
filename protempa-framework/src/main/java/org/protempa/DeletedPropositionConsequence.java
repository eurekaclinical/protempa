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

import java.util.logging.Logger;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.Proposition;

/**
 *
 * @author Andrew Post
 */
class DeletedPropositionConsequence implements Consequence {
    private static final long serialVersionUID = 1L;
    private final Logger logger;
    private final DerivationsBuilder derivationsBuilder;

    public DeletedPropositionConsequence(DerivationsBuilder derivationsBuilder) {
        this.derivationsBuilder = derivationsBuilder;
        this.logger = ProtempaUtil.logger();
    }

    @Override
    public void evaluate(KnowledgeHelper kh, WorkingMemory wm) throws Exception {
        InternalFactHandle factHandle = kh.getTuple().get(0);
        Proposition prop = (Proposition) wm.getObject(factHandle);
        kh.retract(factHandle);
        this.derivationsBuilder.propositionRetractedBackward(prop);
        this.derivationsBuilder.propositionRetractedForward(prop);
    }

}