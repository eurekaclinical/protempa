package org.protempa;

import org.protempa.KnowledgeBase;
import org.protempa.LowLevelAbstractionDefinition;
import org.protempa.TemporalExtendedParameterDefinition;
import org.protempa.proposition.value.AbsoluteTimeUnit;
import org.protempa.proposition.value.NumberValue;

import junit.framework.TestCase;

/**
 * Note that we assume that we are processing data with timestamps in absolute
 * time.
 * 
 * @author Andrew Post
 */
public class TemporalExtendedParameterMinDurationTest extends TestCase {

	private TemporalExtendedParameterDefinition def1;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		KnowledgeBase kb = new KnowledgeBase();
		LowLevelAbstractionDefinition llad = new LowLevelAbstractionDefinition(
				kb, "TEST");
		this.def1 = new TemporalExtendedParameterDefinition(llad.getId());
		this.def1.setAbbreviatedDisplayName("t");
		this.def1.setDisplayName("test");
		this.def1.setValue(new NumberValue(13));
		this.def1.setMinLength(12);
		this.def1.setMinLengthUnit(AbsoluteTimeUnit.HOUR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		this.def1 = null;
	}

	public void testDoesBarelyMatchMinDuration12Hours() {
		assertTrue(this.def1.getMatches(ExtendedParameterDurationTestParameters
				.twelveHourParameter()));

	}

	public void testDoesMatchMinDuration12Hours() {
		assertTrue(this.def1.getMatches(ExtendedParameterDurationTestParameters
				.thirteenHourParameter()));

	}

	public void testDoesNotMatchMinDuration12Hours() {
		assertFalse(this.def1
				.getMatches(ExtendedParameterDurationTestParameters
						.elevenHourParameter()));
	}

}
