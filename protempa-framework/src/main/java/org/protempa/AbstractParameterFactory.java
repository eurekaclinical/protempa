/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
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

import java.util.Arrays;
import java.util.List;

import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.interval.IntervalFactory;
import org.protempa.proposition.Segment;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Value;

/**
 * FIXME If there are two input propositions of the same type and value, the
 * behavior is unpredictable (which one it chooses for an offset). We should use
 * extended propositions for this, not straight abstraction definitions.
 * 
 * FIXME we ignore the units specified in Offsets. The ecode assumes that the
 * units of Segment's interval and the offets are the same, I think.
 * 
 * @author Andrew Post
 */
public final class AbstractParameterFactory {

    private static final IntervalFactory intervalFactory = 
            new IntervalFactory();

    /**
     * Private constructor.
     */
    private AbstractParameterFactory() {
    }

    /**
     * 
     * @param propId
     * @param segments
     * @param temporalOffset
     * @return
     */
    public static AbstractParameter getFromAbstraction(String propId,
            Segment<? extends TemporalProposition> segment,
            List<? extends TemporalProposition> tps, Value value, TemporalPatternOffset temporalOffset,
            TemporalExtendedPropositionDefinition[] epds, String contextId) {
        AbstractParameter result = new AbstractParameter(propId);
        result.setDataSourceType(DataSourceType.DERIVED);
        result.setContextId(contextId);

        Long minStart = null;
        Long maxStart = null;
        Long minFinish = null;
        Long maxFinish = null;
        Granularity startGran;
        Granularity finishGran = null;

        Interval segmentIval = segment.getInterval();
        
        if (temporalOffset == null
                || temporalOffset
                .getStartTemporalExtendedPropositionDefinition() == null) {
            if (temporalOffset != null) {
                minStart = segmentIval.getMinStart()
                        + temporalOffset.getStartOffset();
                maxStart = segmentIval.getMaxStart()
                        + temporalOffset.getStartOffset();
            }
            startGran = segment.getStartGranularity();
        } else {
            TemporalProposition param = matchingTemporalProposition(tps,
                temporalOffset.getStartTemporalExtendedPropositionDefinition(),
                epds);

            if (param != null) {
                minStart = temporalOffset.getStartIntervalSide() 
                        == IntervalSide.START 
                        ? param.getInterval().getMinStart() 
                        : param.getInterval().getMinFinish();
                maxStart = temporalOffset.getStartIntervalSide() 
                        == IntervalSide.START 
                        ? param.getInterval().getMaxStart() 
                        : param.getInterval().getMaxFinish();
            } else {
                minStart = segmentIval.getMinStart();
                maxStart = segmentIval.getMaxStart();
            }

            Integer startOffset = temporalOffset.getStartOffset();
            if (startOffset != null) {
                minStart += startOffset;
                maxStart += startOffset;
            }
            startGran = param.getInterval().getStartGranularity();
        }
        
        if (temporalOffset == null
                || temporalOffset
                .getFinishTemporalExtendedPropositionDefinition() == null) {
            if (temporalOffset != null
                    && temporalOffset.getFinishIntervalSide()
                    == IntervalSide.START) {
                minFinish = segmentIval.getMinFinish()
                        + temporalOffset.getFinishOffset();
                maxFinish = segmentIval.getMaxFinish()
                        + temporalOffset.getFinishOffset();
                finishGran = segment.getFinishGranularity();
            }
        } else {
            TemporalProposition param = matchingTemporalProposition(tps,
                    temporalOffset
                    .getFinishTemporalExtendedPropositionDefinition(), epds);

            if (param != null) {
                minFinish = temporalOffset.getFinishIntervalSide() == 
                        IntervalSide.START ? param.getInterval().getMinStart() 
                        : param.getInterval().getMinFinish();
                maxFinish = temporalOffset.getFinishIntervalSide() == 
                        IntervalSide.START ? param.getInterval().getMaxStart() 
                        : param.getInterval().getMaxFinish();
            } else {
                minFinish = segmentIval.getMinFinish();
                maxFinish = segmentIval.getMaxFinish();
            }
            Integer finishOffset = temporalOffset.getFinishOffset();
            if (finishOffset != null) {
                minFinish += finishOffset;
                maxFinish += finishOffset;
            }
            finishGran = param.getInterval().getFinishGranularity();
        }

        if (temporalOffset == null) {
            result.setInterval(segmentIval);
        } else {
            result.setInterval(intervalFactory.getInstance(minStart, maxStart,
                    startGran, minFinish, maxFinish, finishGran));
        }

        result.setValue(value);

        return result;
    }

    private static TemporalProposition matchingTemporalProposition(
            List<? extends TemporalProposition> tps,
            TemporalExtendedPropositionDefinition tepd,
            TemporalExtendedPropositionDefinition[] epds) {
        for (int i = 0; i < epds.length; i++) {
            if (epds[i] == tepd) {
                return tps.get(i);
            }
        }
        throw new AssertionError("No proposition in " + tps + 
                " matches temporal extended proposition definition " + tepd + 
                " from " + Arrays.toString(epds));
    }
}
