package org.arp.javautil.arrays;


import junit.framework.TestCase;

/**
 * Test for the contains method.
 * 
 * @author Andrew Post
 */
public class ContainsTest extends TestCase {

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
