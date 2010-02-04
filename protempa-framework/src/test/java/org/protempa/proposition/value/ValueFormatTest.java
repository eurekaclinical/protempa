package org.protempa.proposition.value;

import java.math.BigDecimal;

import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueFormat;


import junit.framework.TestCase;

/**
 * @author Andrew Post
 */
public class ValueFormatTest extends TestCase {

	/*
	 * @see TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInequalityDoubleParseValue() {
		assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
				new BigDecimal("0.1")), ValueFormat.parse("<0.1"));
		assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
				new BigDecimal("0.4")), ValueFormat.parse("<0.4"));
	}

	public void testNumberValueParserLeadingWhitespace() {
		assertEquals(new NumberValue(3), ValueFormat.parse("    3"));
	}

	public void testNumberFormatParserTrailingWhitespace() {
		assertEquals(new NumberValue(3), ValueFormat.parse("3    "));
	}

	public void testNumberFormatParserWhitespace() {
		assertEquals(new NumberValue(3), ValueFormat.parse("  3    "));
	}

	public void testPrecedingWhitespace() {
		assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
				new BigDecimal("0.4")), ValueFormat.parse("       <0.4"));
	}

	public void testInterveningWhitespace() {
		assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
				new BigDecimal("0.4")), ValueFormat.parse("<    0.4"));
	}

	public void testWhitespaceAfter() {
		assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
				new BigDecimal("0.4")), ValueFormat.parse("<0.4     "));
	}

	public void testWhitespaceAllOver() {
		assertEquals(new InequalityNumberValue(ValueComparator.LESS_THAN,
				new BigDecimal("0.4")), ValueFormat.parse("      <   0.4     "));
	}

}