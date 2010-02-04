package org.protempa.proposition.value;

import org.protempa.proposition.value.InequalityNumberValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.ValueComparator;

import junit.framework.TestCase;

/**
 * @author Andrew Post
 */
public class InequalityNumberValueTest extends TestCase {

	/**
	 * Constructor for InequalityDoubleValueTest.
	 * 
	 * @param arg0
	 */
	public InequalityNumberValueTest(String arg0) {
		super(arg0);
	}

	@Override
	public void setUp() throws Exception {

	}

	@Override
	public void tearDown() throws Exception {

	}

	public void testLessThanGreaterThan() {
		InequalityNumberValue val = new InequalityNumberValue(
				ValueComparator.GREATER_THAN, 20);
		assertTrue(val.compareInequalityNumberValue(new InequalityNumberValue(
				ValueComparator.LESS_THAN, 10)) == ValueComparator.GREATER_THAN);
	}

	public void testLessThanUnknown() {
		InequalityNumberValue val = new InequalityNumberValue(
				ValueComparator.GREATER_THAN, 20);
		assertTrue(val.compareInequalityNumberValue(new InequalityNumberValue(
				ValueComparator.LESS_THAN, 40)) == ValueComparator.UNKNOWN);
	}

	public void testLessThanGreaterThanSame() {
		InequalityNumberValue val = new InequalityNumberValue(
				ValueComparator.GREATER_THAN, 20);
		assertTrue(val.compareInequalityNumberValue(new InequalityNumberValue(
				ValueComparator.LESS_THAN, 20)) == ValueComparator.GREATER_THAN);
	}

	public void testLessThanSameDouble() {
		InequalityNumberValue val = new InequalityNumberValue(
				ValueComparator.GREATER_THAN, 20);
		assertTrue(val.compareNumberValue(new NumberValue(20)) == ValueComparator.GREATER_THAN);
	}

	public void testLessThanDouble() {
		InequalityNumberValue val = new InequalityNumberValue(
				ValueComparator.LESS_THAN, .4);
		assertTrue(val.compareNumberValue(new NumberValue(.8)) == ValueComparator.LESS_THAN);
	}
}
