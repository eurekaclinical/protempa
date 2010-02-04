package org.protempa.proposition;

import org.protempa.proposition.value.Granularity;

public final class SimpleAbstractParameterInterval extends Interval {

	private static final long serialVersionUID = -7013968685426579897L;

	public SimpleAbstractParameterInterval() {
		super();
	}

	public SimpleAbstractParameterInterval(Long minStart, Long maxStart,
			Granularity startGranularity, Long minFinish, Long maxFinish,
			Granularity finishGranularity) {
		super(minStart, maxStart, startGranularity, minFinish, maxFinish,
				finishGranularity, null, null, null);

		if (minStart == null) {
			throw new IllegalArgumentException("minStart cannot be null");
		}
		if (maxStart == null) {
			throw new IllegalArgumentException("maxStart cannot be null");
		}
		if (minFinish == null) {
			throw new IllegalArgumentException("minFinish cannot be null");
		}
		if (maxFinish == null) {
			throw new IllegalArgumentException("maxFinish cannot be null");
		}
	}

	public SimpleAbstractParameterInterval(Long start,
			Granularity startGranularity, Long finish,
			Granularity finishGranularity) {
		super(start, startGranularity, finish, finishGranularity, null, null);

		if (start == null) {
			throw new IllegalArgumentException("start cannot be null");
		}
		if (finish == null) {
			throw new IllegalArgumentException("finish cannot be null");
		}
	}

	

}
