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

import java.io.IOException;

import org.junit.Test;

import org.protempa.FinderException;

/**
 * Tests finding events defined in a Protege knowledge base.
 * 
 * @author Andrew Post
 * 
 */
public class AbstractEventsTest extends AbstractHELLPAllKeysOneParameterTest {

	@Test
	public void testICD9_630() throws Exception {
		TestProtempaRunner protempaRunner = new TestProtempaRunner("ICD-9_630");
		protempaRunner.run();
		assertEquals(22, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testICD9_631() throws Exception {
		TestProtempaRunner protempaRunner = new TestProtempaRunner("ICD-9_631");
		protempaRunner.run();
		assertEquals(3, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testICD9_632() throws Exception {
		TestProtempaRunner protempaRunner = new TestProtempaRunner("ICD-9_632");
		protempaRunner.run();
		assertEquals(10, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testICD9_633_1() throws Exception {
		TestProtempaRunner protempaRunner = new TestProtempaRunner(
				"ICD-9_633.1");
		protempaRunner.run();
		assertEquals(12, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testICD9_633_9() throws Exception {
		TestProtempaRunner protempaRunner = new TestProtempaRunner(
				"ICD-9_633.9");
		protempaRunner.run();
		assertEquals(19, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testICD9s_Pregnancy() throws Exception {
		TestProtempaRunner protempaRunner = new TestProtempaRunner(
				"ICD-9s_Pregnancy");
		protempaRunner.run();
		assertEquals(66, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testICD9_EctopicAndMolarPregnancy() throws Exception {
		TestProtempaRunner protempaRunner = new TestProtempaRunner(
				"ICD-9_EctopicAndMolarPregnancy");
		protempaRunner.run();
		assertEquals(66, protempaRunner.getNumberOfIntervalsFound());
	}

	@Test
	public void testICD9_Group11() throws Exception {
		TestProtempaRunner protempaRunner = new TestProtempaRunner(
				"ICD-9_Group11");
		protempaRunner.run();
		assertEquals(578, protempaRunner.getNumberOfIntervalsFound());
	}
	
	@Test
	public void testICD9_SeverePreeclampsia() throws Exception {
		TestProtempaRunner protempaRunner = new TestProtempaRunner("ICD-9_642.5_codes");
		protempaRunner.run();
		assertEquals(172, protempaRunner.getNumberOfKeysFound());
	}
}
