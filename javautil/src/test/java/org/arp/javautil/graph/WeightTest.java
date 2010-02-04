package org.arp.javautil.graph;

import org.arp.javautil.graph.Weight;

import junit.framework.TestCase;

/**
 * @author Andrew Post
 */
public class WeightTest extends TestCase {

	/**
	 * Constructor for WeightTest.
	 * 
	 * @param arg0
	 */
	public WeightTest(String arg0) {
		super(arg0);
	}

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

	public void testAdd() {
		assertEquals(new Weight(3L).add(new Weight(1L)).value(), 4L);
	}

	public void testSubtract() {
		assertEquals(new Weight(3L).subtract(new Weight(1L)).value(), 2L);
	}

	public void testCopyConstructor() {
		Weight w = new Weight(3L);
		assertEquals(w, new Weight(w));
	}

	public void testGreaterThanNotInfinity() {
		assertTrue((new Weight(100000000L)).greaterThan(100000L));
	}

	public void testLessThanNotInfinity() {
		assertTrue((new Weight(1L)).lessThan(2L));
	}

	public void testPosInfinityGreaterThanNotInfinity() {
		assertTrue(Weight.POS_INFINITY.greaterThan(Long.MAX_VALUE));
	}

	public void testNegInfinityLessThanNotInfinity() {
		assertTrue(Weight.NEG_INFINITY.lessThan(Long.MIN_VALUE));
	}

	public void testCompareNegInfinityPosInfinity() {
		assertTrue(Weight.NEG_INFINITY.compareTo(Weight.POS_INFINITY) == -1);
	}

	public void testComparePosInfinityNegInfinity() {
		assertTrue(Weight.POS_INFINITY.compareTo(Weight.NEG_INFINITY) == 1);
	}

	public void testComparePosInfinityPosInfinity() {
		assertEquals(Weight.POS_INFINITY, Weight.POS_INFINITY);
	}

	public void testCompareNegInfinityNegInfinity() {
		assertEquals(Weight.NEG_INFINITY, Weight.NEG_INFINITY);
	}

	public void testIsWithinRange1() {
		assertTrue((new Weight(20)).isWithinRange(new Weight(20),
				new Weight(20)));
	}

	public void testIsWithinRange2() {
		assertTrue((new Weight(20)).isWithinRange(Weight.ZERO,
				Weight.POS_INFINITY));
	}

	public void testIsWithinRange3() {
		assertFalse((new Weight(20)).isWithinRange(Weight.ZERO,
				Weight.NEG_INFINITY));
	}
}
