package org.protempa.proposition.value;

import java.text.AttributedCharacterIterator;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Andrew Post
 */
class ReprSimpleDateFormat extends SimpleDateFormat {

	private final static String PREPEND = "ABSOLUTE:";

	/**
	 * 
	 */
	private static final long serialVersionUID = -5276847494971693256L;

	ReprSimpleDateFormat(String pattern) {
		super(pattern);
	}

	@Override
	public StringBuffer format(Date obj, StringBuffer toAppendTo,
			FieldPosition pos) {
		StringBuffer result = super.format(obj, toAppendTo, pos);
		result.insert(0, PREPEND);
		return result;
	}

	@Override
	public Date parse(String source, ParsePosition pos) {
		String[] split = source.split(":", 2);
		if (!split[0].equals(PREPEND)) {
			return null;
		}
		return super.parse(split[1], pos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.text.SimpleDateFormat#formatToCharacterIterator(java.lang.Object)
	 */
	@Override
	public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
		throw new UnsupportedOperationException();
	}

}
