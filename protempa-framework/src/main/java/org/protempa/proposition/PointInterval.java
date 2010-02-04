package org.protempa.proposition;

import org.protempa.proposition.value.Granularity;

/**
 * An Interval for completely specified minimum and maximum starts and finishes,
 * and 0 duration.
 * 
 * @author Andrew Post
 */
public final class PointInterval extends Interval {

	private static final long serialVersionUID = 3429887577277940890L;

	public PointInterval(Long minStart, Long maxStart,
			Granularity startGranularity, Long minFinish, Long maxFinish,
			Granularity finishGranularity) {
		super(minStart, maxStart, startGranularity, minFinish, maxFinish,
				finishGranularity, null, null, null);
	}

	public PointInterval(Long start, Granularity startGranularity, Long finish,
			Granularity finishGranularity) {
		super(start, startGranularity, finish, finishGranularity, null, null);
	}

	public PointInterval(Long timestamp, Granularity granularity) {
		super(timestamp, granularity, timestamp, granularity, null, null);
	}

}
