package org.protempa;

import java.util.Iterator;

import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Interval;
import org.protempa.proposition.PropositionUtil;
import org.protempa.proposition.Segment;
import org.protempa.proposition.TemporalParameter;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Value;


/**
 * FIXME If there are two input propositions of the same type and value, the
 * behavior is unpredictable (which one it chooses for an offset). We should
 * use extended propositions for this, not straight abstraction definitions.
 *
 * FIXME we ignore the units specified in Offsets. The ecode assumes that the units
 * of Segment's interval and the offets are the same, I think.
 *
 * @author Andrew Post
 */
public final class AbstractParameterFactory {

	/**
	 * Private constructor.
	 */
	private AbstractParameterFactory() {
		super();
	}

	/**
	 * 
	 * @param propId
	 * @param segments
	 * @param temporalOffset
	 * @return
	 */
	public static AbstractParameter getFromAbstraction(String propId,
			Segment<? extends TemporalProposition> segment, Value value,
			Offsets temporalOffset) {
		AbstractParameter result = new AbstractParameter(propId);

		Long minStart = null;
		Long maxStart = null;
		Long minFinish = null;
		Long maxFinish = null;

		Interval segmentIval = segment.getInterval();
		Granularity startGran;
		if (temporalOffset == null
				|| temporalOffset.getStartAbstractParamId() == null) {
			if (temporalOffset != null) {
				minStart = segmentIval.getMinStart()
						+ temporalOffset.getStartOffset();
				maxStart = segmentIval.getMaxStart()
						+ temporalOffset.getStartOffset();
			}
			startGran = segment.getStartGranularity();
		} else {
			TemporalProposition param = matchingTemporalProposition(segment,
					temporalOffset.getStartAbstractParamId(), temporalOffset
							.getStartAbstractParamValue());

			if (param != null) {
				minStart = temporalOffset.getStartIntervalSide() == IntervalSide.START ? param
						.getInterval().getMinStart()
						: param.getInterval().getMinFinish();
				maxStart = temporalOffset.getStartIntervalSide() == IntervalSide.START ? param
						.getInterval().getMaxStart()
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
			startGran = param.getStartGranularity();
		}

		Granularity finishGran;
		if (temporalOffset == null
				|| temporalOffset.getFinishAbstractParamId() == null) {
			if (temporalOffset != null) {
				minFinish = segmentIval.getMinFinish()
						+ temporalOffset.getFinishOffset();
				maxFinish = segmentIval.getMaxFinish()
						+ temporalOffset.getFinishOffset();
			}
			finishGran = segment.getFinishGranularity();
		} else {
			TemporalProposition param = matchingTemporalProposition(segment,
					temporalOffset.getFinishAbstractParamId(), temporalOffset
							.getFinishAbstractParamValue());

			if (param != null) {
				minFinish = temporalOffset.getFinishIntervalSide() == IntervalSide.START ? param
						.getInterval().getMinStart()
						: param.getInterval().getMinFinish();
				maxFinish = temporalOffset.getFinishIntervalSide() == IntervalSide.START ? param
						.getInterval().getMaxStart()
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
			finishGran = param.getFinishGranularity();
		}

		if (temporalOffset == null) {
			result.setInterval(segmentIval);
		} else {
			result.setInterval(PropositionUtil.createInterval(minStart,
					maxStart, startGran, minFinish, maxFinish, finishGran));
		}

		result.setValue(value);
		
		return result;
	}

	private static TemporalProposition matchingTemporalProposition(
			Segment<? extends TemporalProposition> segment, String id,
			Value value) {
		TemporalProposition param = null;
		for (Iterator<? extends TemporalProposition> itr = segment.iterator(); itr
				.hasNext();) {
			param = itr.next();
			if (id.equals(param.getId())) {
				if (param instanceof TemporalParameter) {
					if (value == null ? ((TemporalParameter) param).getValue() == null
							: value.equals(((TemporalParameter) param)
									.getValue())) {
						break;
					}
				} else if (value == null) {
					break;
				}
			}
		}
		return param;
	}
}
