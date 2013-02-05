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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.UUID;
import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.Context;
import org.protempa.proposition.DerivedSourceId;
import org.protempa.proposition.DerivedUniqueId;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.interval.IntervalFactory;
import org.protempa.proposition.value.Granularity;

/**
 *
 * @author Andrew Post
 */
class ContextDefinitionInducedByConsequence implements Consequence {

    private final DerivationsBuilder derivationsBuilder;
    private final ContextDefinition def;
    private transient IntervalFactory intervalFactory;

    ContextDefinitionInducedByConsequence(ContextDefinition def,
            DerivationsBuilder derivationsBuilder) {
        assert def != null : "def cannot be null";
        assert derivationsBuilder != null :
                "derivationsBuilder cannot be null";
        this.def = def;
        this.derivationsBuilder = derivationsBuilder;
        this.intervalFactory = new IntervalFactory();
    }

    private void readObject(ObjectInputStream ois) throws IOException,
            ClassNotFoundException {
        ois.defaultReadObject();
        this.intervalFactory = new IntervalFactory();
    }

    @Override
    public void evaluate(KnowledgeHelper kh, WorkingMemory wm) throws Exception {
        TemporalProposition prop = (TemporalProposition) wm.getObject(
                kh.getTuple().get(0));
        Context context = new Context(this.def.getPropositionId(), new UniqueId(
                DerivedSourceId.getInstance(),
                new DerivedUniqueId(UUID.randomUUID().toString())));
        ContextOffset temporalOffset = this.def.getOffset();
        Interval oldInterval = prop.getInterval();

        Long minStart;
        Long maxStart;
        Long minFinish;
        Long maxFinish;
        Granularity startGran;
        Granularity finishGran;

        minStart = temporalOffset.getStartIntervalSide()
                == IntervalSide.START
                ? prop.getInterval().getMinStart()
                : prop.getInterval().getMinFinish();
        maxStart = temporalOffset.getStartIntervalSide()
                == IntervalSide.START
                ? prop.getInterval().getMaxStart()
                : prop.getInterval().getMaxFinish();

        Integer startOffset = temporalOffset.getStartOffset();
        if (startOffset != null) {
            minStart += startOffset;
            maxStart += startOffset;
        }
        startGran = prop.getInterval().getStartGranularity();

        minFinish = temporalOffset.getFinishIntervalSide()
                == IntervalSide.START ? oldInterval.getMinStart()
                : oldInterval.getMinFinish();
        maxFinish = temporalOffset.getFinishIntervalSide()
                == IntervalSide.START ? oldInterval.getMaxStart()
                : oldInterval.getMaxFinish();
        Integer finishOffset = temporalOffset.getFinishOffset();
        if (finishOffset != null) {
            minFinish += finishOffset;
            maxFinish += finishOffset;
        }
        finishGran = oldInterval.getFinishGranularity();

        context.setInterval(this.intervalFactory.getInstance(minStart,
                maxStart, startGran, minFinish, maxFinish, finishGran));
        kh.getWorkingMemory().insert(context);

        this.derivationsBuilder.propositionAsserted(prop, context);
    }
}
