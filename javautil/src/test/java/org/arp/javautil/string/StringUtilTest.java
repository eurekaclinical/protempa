package org.arp.javautil.string;

import org.arp.javautil.string.StringUtil;

import junit.framework.TestCase;

/**
 * @author Andrew Post
 */
public class StringUtilTest extends TestCase {

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

	public void testNeitherEmptyNorNull() {
		assertFalse(StringUtil.getEmptyOrNull("hello world"));
	}

	public void testEmpty() {
		assertTrue(StringUtil.getEmptyOrNull(""));
	}

	public void testNull() {
		assertTrue(StringUtil.getEmptyOrNull(null));
	}

}
