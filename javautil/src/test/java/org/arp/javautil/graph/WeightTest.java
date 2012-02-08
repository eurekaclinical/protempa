/*
 * #%L
 * JavaUtil
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
package org.arp.javautil.graph;


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
		assertTrue(WeightFactory.POS_INFINITY.greaterThan(Long.MAX_VALUE));
	}

	public void testNegInfinityLessThanNotInfinity() {
		assertTrue(WeightFactory.NEG_INFINITY.lessThan(Long.MIN_VALUE));
	}

	public void testCompareNegInfinityPosInfinity() {
		assertTrue(WeightFactory.NEG_INFINITY.compareTo(WeightFactory.POS_INFINITY) == -1);
	}

	public void testComparePosInfinityNegInfinity() {
		assertTrue(WeightFactory.POS_INFINITY.compareTo(WeightFactory.NEG_INFINITY) == 1);
	}

	public void testComparePosInfinityPosInfinity() {
		assertEquals(WeightFactory.POS_INFINITY, WeightFactory.POS_INFINITY);
	}

	public void testCompareNegInfinityNegInfinity() {
		assertEquals(WeightFactory.NEG_INFINITY, WeightFactory.NEG_INFINITY);
	}

	public void testIsWithinRange1() {
		assertTrue((new Weight(20)).isWithinRange(new Weight(20),
				new Weight(20)));
	}

	public void testIsWithinRange2() {
		assertTrue((new Weight(20)).isWithinRange(WeightFactory.ZERO,
				WeightFactory.POS_INFINITY));
	}

	public void testIsWithinRange3() {
		assertFalse((new Weight(20)).isWithinRange(WeightFactory.ZERO,
				WeightFactory.NEG_INFINITY));
	}
}
