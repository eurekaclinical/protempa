package org.protempa.ksb.protege;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import org.protempa.FinderException;

/**
 * Tests finding low-level abstractions defined in a Protege knowledge base.
 * 
 * @author Andrew Post
 * 
 */
public abstract class AbstractLowLevelAbstractionsTest extends
		AbstractHELLPAllKeysOneParameterTest {

	@Test
	public void testADMIT_STATE() throws IOException, FinderException {
		TestProtempaRunner protempaRunner = new TestProtempaRunner(
				"ADMIT_STATE");
		protempaRunner.run();
		assertEquals(761, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testAGGREGATE_PLATELETS_STATE_2() throws IOException,
			FinderException {
		TestProtempaRunner protempaRunner = new TestProtempaRunner(
				"AGGREGATE_PLATELETS_STATE_2");
		protempaRunner.run();
		assertEquals(146, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testAST_STATE() throws IOException, FinderException {
		TestProtempaRunner protempaRunner = new TestProtempaRunner("AST_STATE");
		protempaRunner.run();
		assertEquals(1512, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testAST_TREND() throws IOException, FinderException {
		TestProtempaRunner protempaRunner = new TestProtempaRunner("AST_TREND");
		protempaRunner.run();
		assertEquals(922, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testDISCHARGE_STATE() throws IOException, FinderException {
		TestProtempaRunner protempaRunner = new TestProtempaRunner(
				"DISCHARGE_STATE");
		protempaRunner.run();
		assertEquals(761, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testFDP_STATE() throws IOException, FinderException {
		TestProtempaRunner protempaRunner = new TestProtempaRunner("FDP_STATE");
		protempaRunner.run();
		assertEquals(12, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testLDH_STATE() throws IOException, FinderException {
		TestProtempaRunner protempaRunner = new TestProtempaRunner("LDH_STATE");
		protempaRunner.run();
		assertEquals(1293, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testLDH_TREND() throws IOException, FinderException {
		TestProtempaRunner protempaRunner = new TestProtempaRunner("LDH_TREND");
		protempaRunner.run();
		assertEquals(707, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testPLATELETS_STATE() throws IOException, FinderException {
		TestProtempaRunner protempaRunner = new TestProtempaRunner(
				"PLATELETS_STATE");
		protempaRunner.run();
		assertEquals(1941, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testPLATELETS_TREND() throws IOException, FinderException {
		TestProtempaRunner protempaRunner = new TestProtempaRunner(
				"PLATELETS_TREND");
		protempaRunner.run();
		assertEquals(1950, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testPLATELETS_TREND_EXTD() throws IOException, FinderException {
		TestProtempaRunner protempaRunner = new TestProtempaRunner(
				"PLATELETS_TREND_EXTD");
		protempaRunner.run();
		assertEquals(1883, protempaRunner.getNumberOfIntervalsFound());
	}
}
