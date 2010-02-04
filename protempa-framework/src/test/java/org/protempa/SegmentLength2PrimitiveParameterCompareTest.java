package org.protempa;

/**
 * Compares the output of <code>Segment</code> with that of
 * <code>ConstraintNetwork</code>.
 * 
 * @author Andrew Post
 */
public class SegmentLength2PrimitiveParameterCompareTest extends
		ConstraintNetworkSegmentComparer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		seg = SegmentTestParameters.getLength2PrimitiveParameterSegment();
		super.setUp();
	}

}
