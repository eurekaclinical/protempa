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
