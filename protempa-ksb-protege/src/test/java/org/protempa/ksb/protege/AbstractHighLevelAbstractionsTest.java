package org.protempa.ksb.protege;

import static org.junit.Assert.assertEquals;


import org.junit.Test;


/**
 * Tests finding high-level abstractions defined in a Protege knowledge base.
 * 
 * @author Andrew Post
 * 
 */
public abstract class AbstractHighLevelAbstractionsTest extends
		AbstractHELLPAllKeysOneParameterTest {

	@Test
	public void testHELLP_I() throws Exception {
		TestProtempaRunner protempaRunner = new TestProtempaRunner("HELLP_I");
		protempaRunner.run();
		assertEquals(43, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testHELLP_II() throws Exception {
		TestProtempaRunner protempaRunner = new TestProtempaRunner("HELLP_II");
		protempaRunner.run();
		assertEquals(38, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testHELLP_FIRST_RECOVERING_PLATELETS() throws Exception {
		TestProtempaRunner protempaRunner = new TestProtempaRunner(
				"HELLP_FIRST_RECOVERING_PLATELETS");
		protempaRunner.run();
		assertEquals(75, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testHELLP_RECURRING_AND_RECOVERING_PLATELETS()
			throws Exception {
		TestProtempaRunner protempaRunner = new TestProtempaRunner(
				"HELLP_RECURRING_AND_RECOVERING_PLATELETS");
		protempaRunner.run();
		assertEquals(12, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testHELLP_RECURRING_PLATELETS() throws Exception {
		TestProtempaRunner protempaRunner = new TestProtempaRunner(
				"HELLP_RECURRING_PLATELETS");
		protempaRunner.run();
		assertEquals(18, protempaRunner.getNumberOfIntervalsFound());
	}

}
