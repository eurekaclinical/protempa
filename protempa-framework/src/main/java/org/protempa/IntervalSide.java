package org.protempa;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class IntervalSide implements Serializable {
	
	private static final long serialVersionUID = 468658456314872055L;

	public static final IntervalSide START = new IntervalSide("start");

	public static final IntervalSide FINISH = new IntervalSide("finish");

	private static final IntervalSide[] VALUES = { START, FINISH };

	private static int nextOrdinal = 0;

	private transient String desc;

	private int ordinal = nextOrdinal++;

	private IntervalSide(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return desc;
	}

	public static IntervalSide intervalSide(String name) {
		if (name == null) {
			return null;
		} else if (name.equalsIgnoreCase("start")) {
			return START;
		} else if (name.equalsIgnoreCase("finish")) {
			return FINISH;
		} else {
			return null;
		}
	}

	/**
	 * Used by built-in serialization.
	 * 
	 * @return the unserialized object.
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		return VALUES[ordinal];
	}
}