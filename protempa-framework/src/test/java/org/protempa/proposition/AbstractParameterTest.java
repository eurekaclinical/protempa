package org.protempa.proposition;

import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.SimpleAbstractParameterInterval;
import org.protempa.proposition.value.RelativeHourGranularity;

import junit.framework.TestCase;

/**
 * @author Andrew Post
 * 
 */
public class AbstractParameterTest extends TestCase {
	private AbstractParameter p;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		p = new AbstractParameter("TEST");
		p
				.setInterval(new SimpleAbstractParameterInterval(0L,
						RelativeHourGranularity.HOUR, 12L,
						RelativeHourGranularity.HOUR));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		p = null;
	}

	/**
	 * Having the method here stops JUnit from complaining that there are no
	 * tests...
	 */
	public void testEmptyTest() {

	}

}
