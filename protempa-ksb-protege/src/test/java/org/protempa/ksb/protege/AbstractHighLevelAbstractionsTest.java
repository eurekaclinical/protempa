package org.protempa.ksb.protege;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import org.protempa.FinderException;

/**
 * Tests finding high-level abstractions defined in a Protege knowledge base.
 * 
 * @author Andrew Post
 * 
 */
public abstract class AbstractHighLevelAbstractionsTest extends
		AbstractHELLPAllKeysOneParameterTest {

	@Test
	public void testHELLP_I() throws IOException, FinderException {
		TestProtempaRunner protempaRunner = new TestProtempaRunner("HELLP_I");
		protempaRunner.run();
		assertEquals(43, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testHELLP_II() throws IOException, FinderException {
		TestProtempaRunner protempaRunner = new TestProtempaRunner("HELLP_II");
		protempaRunner.run();
		assertEquals(38, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testHELLP_FIRST_RECOVERING_PLATELETS() throws IOException,
			FinderException {
		TestProtempaRunner protempaRunner = new TestProtempaRunner(
				"HELLP_FIRST_RECOVERING_PLATELETS");
		protempaRunner.run();
		assertEquals(75, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testHELLP_RECURRING_AND_RECOVERING_PLATELETS()
			throws IOException, FinderException {
		TestProtempaRunner protempaRunner = new TestProtempaRunner(
				"HELLP_RECURRING_AND_RECOVERING_PLATELETS");
		protempaRunner.run();
		assertEquals(12, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testHELLP_RECURRING_PLATELETS() throws IOException,
			FinderException {
		TestProtempaRunner protempaRunner = new TestProtempaRunner(
				"HELLP_RECURRING_PLATELETS");
		protempaRunner.run();
		assertEquals(18, protempaRunner.getNumberOfIntervalsFound());
	}

}
