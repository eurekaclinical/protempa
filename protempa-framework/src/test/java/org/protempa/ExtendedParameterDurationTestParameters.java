package org.protempa;

import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Interval;
import org.protempa.proposition.SimpleAbstractParameterInterval;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.NumberValue;

/**
 * Note that we assume that we are processing data with timestamps in absolute
 * time.
 * 
 * @author Andrew Post
 */
final class ExtendedParameterDurationTestParameters {

	private ExtendedParameterDurationTestParameters() {

	}

	static AbstractParameter twelveHourParameter() {
		AbstractParameter param = new AbstractParameter("TEST");
		param.setValue(new NumberValue(13));
		Interval ival = new SimpleAbstractParameterInterval(0L,
				AbsoluteTimeGranularity.HOUR, 12L * 60 * 60 * 1000,
				AbsoluteTimeGranularity.HOUR);
		param.setInterval(ival);
		return param;
	}

	static AbstractParameter thirteenHourParameter() {
		AbstractParameter param = new AbstractParameter("TEST");
		param.setValue(new NumberValue(13));
		param.setInterval(new SimpleAbstractParameterInterval(0L,
				AbsoluteTimeGranularity.HOUR, 13L * 60 * 60 * 1000,
				AbsoluteTimeGranularity.HOUR));
		return param;
	}

	static AbstractParameter elevenHourParameter() {
		AbstractParameter param = new AbstractParameter("TEST");
		param.setValue(new NumberValue(13));
		param.setInterval(new SimpleAbstractParameterInterval(0L,
				AbsoluteTimeGranularity.HOUR, 11L * 60 * 60 * 1000,
				AbsoluteTimeGranularity.HOUR));
		return param;
	}
}
