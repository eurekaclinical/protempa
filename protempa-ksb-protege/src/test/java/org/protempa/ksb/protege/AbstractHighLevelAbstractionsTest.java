/*
 * #%L
 * Protempa Protege Knowledge Source Backend
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
