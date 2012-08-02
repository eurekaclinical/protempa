/*
 * #%L
 * Protempa Framework
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
public class TemporalExtendedParameterMaxDurationTest extends TestCase {

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
		this.def1.setMaxLength(12);
		this.def1.setMaxLengthUnit(AbsoluteTimeUnit.HOUR);
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

	public void testDoesNotMatchMinDuration12Hours() {
		assertFalse(this.def1
				.getMatches(ExtendedParameterDurationTestParameters
						.thirteenHourParameter()));
	}

	public void testDoesMatchMinDuration12Hours() {
		assertTrue(this.def1.getMatches(ExtendedParameterDurationTestParameters
				.elevenHourParameter()));
	}

}
