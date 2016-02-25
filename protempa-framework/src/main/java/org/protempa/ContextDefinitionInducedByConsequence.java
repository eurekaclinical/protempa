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

import org.protempa.proposition.interval.Interval.Side;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.Context;
import org.protempa.proposition.ProviderBasedUniqueIdFactory;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.UniqueIdFactory;
import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.interval.IntervalFactory;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Unit;

/**
 *
 * @author Andrew Post
 */
class ContextDefinitionInducedByConsequence implements Consequence {

    private final DerivationsBuilder derivationsBuilder;
    private final ContextDefinition def;
    private transient IntervalFactory intervalFactory;
    private final long earliestTime;
    private final long latestTime;

    ContextDefinitionInducedByConsequence(ContextDefinition def,
            DerivationsBuilder derivationsBuilder) {
        assert def != null : "def cannot be null";
        assert derivationsBuilder != null :
                "derivationsBuilder cannot be null";
        this.def = def;
        this.derivationsBuilder = derivationsBuilder;
        this.intervalFactory = new IntervalFactory();
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.set(cal.getMinimum(Calendar.YEAR),
                cal.getMinimum(Calendar.MONTH),
                cal.getMinimum(Calendar.DAY_OF_MONTH),
                cal.getMinimum(Calendar.HOUR),
                cal.getMinimum(Calendar.MINUTE),
                cal.getMinimum(Calendar.SECOND));
        this.earliestTime = cal.getTimeInMillis();
        this.latestTime = Long.MAX_VALUE;
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
        JBossRulesDerivedLocalUniqueIdValuesProvider provider = new JBossRulesDerivedLocalUniqueIdValuesProvider(wm, this.def.getPropositionId());
        UniqueIdFactory factory = new ProviderBasedUniqueIdFactory(provider);
        Context context = new Context(this.def.getPropositionId(), factory.getInstance());
        ContextOffset temporalOffset = this.def.getOffset();
        Interval oldInterval = prop.getInterval();

        Long minStart;
        Long maxStart;
        Long minFinish;
        Long maxFinish;
        Granularity startGran;
        Granularity finishGran;

        minStart = temporalOffset.getStartIntervalSide()
                == Side.START
                ? prop.getInterval().getMinStart()
                : prop.getInterval().getMinFinish();
        maxStart = temporalOffset.getStartIntervalSide()
                == Side.START
                ? prop.getInterval().getMaxStart()
                : prop.getInterval().getMaxFinish();

        Integer startOffset = temporalOffset.getStartOffset();

        if (startOffset != null) {
            Unit startOffsetUnits = temporalOffset.getStartOffsetUnits();
            if (startOffsetUnits != null) {
                minStart = startOffsetUnits.addToPosition(minStart, startOffset);
                maxStart = startOffsetUnits.addToPosition(maxStart, startOffset);
            } else {
                minStart += startOffset;
                maxStart += startOffset;
            }
            startGran = prop.getInterval().getStartGranularity();
        } else {
            minStart = this.earliestTime;
            maxStart = this.earliestTime;
            startGran = null;
        }
        

        minFinish = temporalOffset.getFinishIntervalSide()
                == Side.START ? oldInterval.getMinStart()
                : oldInterval.getMinFinish();
        maxFinish = temporalOffset.getFinishIntervalSide()
                == Side.START ? oldInterval.getMaxStart()
                : oldInterval.getMaxFinish();
        Integer finishOffset = temporalOffset.getFinishOffset();
        if (finishOffset != null) {
            Unit finishOffsetUnits = temporalOffset.getFinishOffsetUnits();
            if (finishOffsetUnits != null) {
                minFinish = finishOffsetUnits.addToPosition(minFinish, finishOffset);
                maxFinish = finishOffsetUnits.addToPosition(maxFinish, finishOffset);
            } else {
                minFinish += finishOffset;
                maxFinish += finishOffset;
            }
            finishGran = oldInterval.getFinishGranularity();
        } else {
            minFinish = this.latestTime;
            maxFinish = this.latestTime;
            finishGran = null;
        }
        

        context.setInterval(this.intervalFactory.getInstance(minStart,
                maxStart, startGran, minFinish, maxFinish, finishGran));
        context.setCreateDate(new Date());
        kh.getWorkingMemory().insert(context);

        this.derivationsBuilder.propositionAsserted(prop, context);
    }
}
