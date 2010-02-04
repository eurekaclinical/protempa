package org.protempa.proposition.value;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the possible valid comparisons between two <code>Value</code>
 * objects.
 * 
 * @author Andrew Post
 */
public enum ValueComparator {

	/**
	 * The first value is greater than (>) the second.
	 */
	GREATER_THAN(">") {

		@Override
		public boolean getConsistent(ValueComparator comparator) {
			return GREATER_THAN.equals(comparator);
		}
	},

	/**
	 * The first value is less than (<) the second.
	 */
	LESS_THAN("<") {

		@Override
		public boolean getConsistent(ValueComparator comparator) {
			return LESS_THAN.equals(comparator);
		}
	},

	/**
	 * The two values are equal (=).
	 */
	EQUAL_TO("=") {

		@Override
		public boolean getConsistent(ValueComparator comparator) {
			return EQUAL_TO.equals(comparator);
		}
	},

	/**
	 * Unknown, meaning that the two values are not comparable, for example,
	 * comparing a number to a string.
	 */
	UNKNOWN("?") {

		@Override
		public boolean getConsistent(ValueComparator comparator) {
			return UNKNOWN.equals(comparator);
		}
	},

	/**
	 * The first value is greater than or equal to (>=) the second.
	 */
	GREATER_THAN_OR_EQUAL_TO(">=") {

		@Override
		public boolean getConsistent(ValueComparator comparator) {
			return EQUAL_TO.equals(comparator)
					|| GREATER_THAN.equals(comparator)
					|| GREATER_THAN_OR_EQUAL_TO.equals(comparator);
		}
	},

	/**
	 * The first value is less than or equal to (<=) the second.
	 */
	LESS_THAN_OR_EQUAL_TO("<=") {

		@Override
		public boolean getConsistent(ValueComparator comparator) {
			return EQUAL_TO.equals(comparator) || LESS_THAN.equals(comparator)
					|| LESS_THAN_OR_EQUAL_TO.equals(comparator);
		}
	};

	private static final Map<String, ValueComparator> compStringToComp = new HashMap<String, ValueComparator>();
	static {
		compStringToComp.put(LESS_THAN.getComparatorString(), LESS_THAN);
		compStringToComp.put(EQUAL_TO.getComparatorString(), EQUAL_TO);
		compStringToComp.put(GREATER_THAN.getComparatorString(), GREATER_THAN);
		compStringToComp.put(LESS_THAN_OR_EQUAL_TO.getComparatorString(),
				LESS_THAN_OR_EQUAL_TO);
		compStringToComp.put(GREATER_THAN_OR_EQUAL_TO.getComparatorString(),
				GREATER_THAN_OR_EQUAL_TO);
		compStringToComp.put(UNKNOWN.getComparatorString(), UNKNOWN);
	}

	/**
	 * Gets the comparison object corresponding to the given string.
	 * <ul>
	 * <li>"&lt;" corresponds to <code>LESS_THAN</code>.</li>
	 * <li>"&gt;" corresponds to <code>GREATER_THAN</code>.</li>
	 * <li>"&lt;=" corresponds to <code>LESS_THAN_OR_EQUAL_TO</code>.</li>
	 * <li>"&gt;=" corresponds to <code>GREATER_THAN_OR_EQUAL_TO</code>.</li>
	 * <li>"=" corresponds to <code>EQUAL_TO</code>.</li>
	 * <li>"!=" corresponds to <code>NOT_EQUAL_TO</code>.</li>
	 * </ul>
	 * 
	 * @param compString
	 *            a <code>String</code> from the above list.
	 * @return a <code>ValueComparator</code> corresponding to the given
	 *         comparison string, or <code>null</code> if one of the above
	 *         strings was not passed in.
	 * @throws ValueComparatorFormatException
	 *             if <code>compString</code> could not be parsed.
	 */
	public static ValueComparator parse(String compString) {
		ValueComparator result = compStringToComp.get(compString);
		if (result == null) {
			throw new ValueComparatorFormatException();
		}
		return compStringToComp.get(compString);
	}

	/**
	 * The string associated with this comparator object.
	 */
	private final String name;

	/**
	 * Creates a comparison object with the given string.
	 * 
	 * @param name
	 *            a <code>String</code>.
	 */
	private ValueComparator(String name) {
		this.name = name;
	}

	public abstract boolean getConsistent(ValueComparator comparator);

	/**
	 * Gets the string associated with this comparator object.
	 * 
	 * @return a <code>String</code>.
	 */
	public String getComparatorString() {
		return name;
	}
}