package org.arp.javautil.arrays;

import org.arp.javautil.arrays.Arrays;

import junit.framework.TestCase;

/**
 * Test for the contains method.
 * 
 * @author Andrew Post
 */
public class ContainsTest extends TestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testContainsObjectTrue() {
		assertTrue(Arrays.contains(new String[] { "hello", "world", "!" },
				"world"));
	}

	public void testContainsObjectFalse() {
		assertFalse(Arrays.contains(new String[] { "hello", "world", "!" },
				"goodbye"));
	}

	public void testContainsObjectNullArray() {
		assertFalse(Arrays.contains(null, "world"));
	}

	public void testContainsObjectNullTrue() {
		assertTrue(Arrays.contains(new String[] { "hello", null, "!" }, null));
	}

	public void testContainsObjectNullFalse() {
		assertTrue(Arrays.contains(new String[] { "hello", null, "!" }, "!"));
	}

}
