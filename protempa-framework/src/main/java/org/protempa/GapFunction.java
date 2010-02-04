package org.protempa;

import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Interval;
import org.protempa.proposition.Segment;

public abstract class GapFunction {
	public static final GapFunction DEFAULT = new SimpleGapFunction();

	public final boolean execute(AbstractParameter lhs, AbstractParameter rhs) {
		if (lhs == null || rhs == null) {
			return false;
		} else {
			return execute(lhs.getInterval(), rhs.getInterval());
		}
	}

	final boolean execute(Segment lhs, Segment rhs) {
		if (lhs == null || rhs == null) {
			return false;
		} else {
			return execute(lhs.getInterval(), rhs.getInterval());
		}
	}

	public abstract boolean execute(Interval lhs, Interval rhs);

	protected String debugMessage() {
		return getClass().getName();
	}
}
