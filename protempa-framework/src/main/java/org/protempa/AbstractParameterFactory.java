package org.protempa;

import org.protempa.proposition.DerivedSourceId;
import org.protempa.proposition.UniqueIdentifier;
import java.util.List;
import java.util.UUID;

import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.DerivedUniqueIdentifier;
import org.protempa.proposition.Interval;
import org.protempa.proposition.IntervalFactory;
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
            List<TemporalProposition> tps, Value value, Offsets temporalOffset,
            TemporalExtendedPropositionDefinition[] epds) {
        AbstractParameter result = new AbstractParameter(propId);
        result.setDataSourceType(DerivedDataSourceType.getInstance());
        result.setUniqueIdentifier(new UniqueIdentifier(
                DerivedSourceId.getInstance(),
                new DerivedUniqueIdentifier(UUID.randomUUID().toString())));

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
            List<TemporalProposition> tps,
            TemporalExtendedPropositionDefinition tepd,
            TemporalExtendedPropositionDefinition[] epds) {
        for (int i = 0; i < epds.length; i++) {
            if (epds[i] == tepd) {
                return tps.get(i);
            }
        }
        throw new AssertionError("never reached");
    }
}
