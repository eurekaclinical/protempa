package org.protempa.proposition.stats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.AbstractPropositionVisitor;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.TemporalParameter;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.value.Unit;
import org.protempa.proposition.value.Value;


/**
 * Calculates length frequencies of temporal propositions.
 * 
 * @author Andrew Post
 * 
 */
public abstract class AbstractKaplanMeyer extends AbstractPropositionVisitor {
	private final Map<SurvivalKey, Map<Long, Integer>> survival;
	private final Unit targetLengthUnit;

	static class SurvivalKey {
		private String id;
		private Value value;
		private volatile int hashCode = 0;

		SurvivalKey(String id, Value value) {
			this.id = id;
			this.value = value;
		}

		public String getId() {
			return this.id;
		}

		public Value getValue() {
			return this.value;
		}

		@Override
		public int hashCode() {
			if (hashCode == 0) {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((id == null) ? 0 : id.hashCode());
				result = prime * result
						+ ((value == null) ? 0 : value.hashCode());
				hashCode = result;
			}
			return hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof SurvivalKey))
				return false;
			final SurvivalKey other = (SurvivalKey) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "SurvivalKey: id=" + this.id + "; value=" + this.value;
		}
	}

	static class LengthProportionPair {
		private long length;
		private float proportion;

		LengthProportionPair(long length, float proportion) {
			this.length = length;
			this.proportion = proportion;
		}

		public long getLength() {
			return this.length;
		}

		public float getProportion() {
			return this.proportion;
		}

		@Override
		public String toString() {
			return "Bin: length=" + this.length + "; proportion="
					+ this.proportion;
		}
	}

	AbstractKaplanMeyer() {
		this(null);
	}

	AbstractKaplanMeyer(Unit targetLengthUnit) {
		this.survival = new HashMap<SurvivalKey, Map<Long, Integer>>();
		this.targetLengthUnit = targetLengthUnit;
	}

	public final LengthProportionPair[] getSurvivalCurveData() {
		Collection<Map<Long, Integer>> vals = this.survival.values();
		List<LengthCountPair> valsSorted = new ArrayList<LengthCountPair>();
		for (Map<Long, Integer> val : vals) {
			for (Map.Entry<Long, Integer> e : val.entrySet()) {
				valsSorted.add(new LengthCountPair(e.getKey(), e.getValue()));
			}
		}
		Collections.sort(valsSorted);
		int totalCount = valsSorted.size();
		LengthProportionPair[] result = new LengthProportionPair[totalCount + 1];
		result[0] = new LengthProportionPair(0, 1f);
		long oldLength = 0L;
		boolean first = true;
		int count = totalCount;
		float totalCountf = (float) totalCount;
		for (int i = 0, n = valsSorted.size(); i < n; i++) {
			LengthCountPair lcp = valsSorted.get(i);
			long lcpLength = lcp.length;
			if (!first) {
				if (lcpLength > oldLength) {
					count--;
				}
			} else {
				first = false;
			}
			result[i + 1] = new LengthProportionPair(lcpLength, count
					/ totalCountf);
			oldLength = lcpLength;
		}

		return result;
	}

	public final Map<SurvivalKey, Map<Long, Integer>> getData() {
		return Collections.unmodifiableMap(this.survival);
	}

	public final Map<Long, Integer> get(String id) {
		return this.survival.get(makeKey(id, null));
	}

	public final Map<Long, Integer> get(String id, Value val) {
		return this.survival.get(makeKey(id, val));
	}

	@Override
	public final void visit(AbstractParameter abstractParameter) {
		increment(abstractParameter);
	}

	@Override
	public final void visit(Event event) {
		processTemporalProposition(makeKey(event.getId(), null), length(event,
				this.targetLengthUnit));
	}

	@Override
	public final void visit(PrimitiveParameter primitiveParameter) {
		increment(primitiveParameter);
	}

	private void increment(TemporalParameter temporalParameter) {
		SurvivalKey key = makeKey(temporalParameter.getId(), temporalParameter
				.getValue());
		processTemporalProposition(key, length(temporalParameter,
				this.targetLengthUnit));
	}

	protected abstract Long length(TemporalProposition temporalProposition,
			Unit targetLengthUnit);

	private void processTemporalProposition(SurvivalKey key, Long length) {
		Map<Long, Integer> val = this.survival.get(key);
		if (val == null) {
			val = new HashMap<Long, Integer>();
			val.put(length, 1);
			survival.put(key, val);
		} else {
			Integer count = val.get(length);
			if (count == null)
				val.put(length, 1);
			else
				val.put(length, count + 1);
		}
	}

	private static SurvivalKey makeKey(String id, Value val) {
		return new SurvivalKey(id, val);
	}

	private static class LengthCountPair implements Comparable<LengthCountPair> {
		long length;
		Integer count;

		LengthCountPair(long length, Integer count) {
			this.length = length;
			this.count = count;
		}

		@Override
        public int compareTo(LengthCountPair o) {
			if (this.length > o.length)
				return 1;
			else if (this.length < o.length)
				return -1;
			else
				return 0;
		}

		@Override
		public String toString() {
			return "LengthCountPair: length=" + this.length + "; count="
					+ this.count;
		}
	}

}
