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

import org.protempa.proposition.interval.ConstraintNetworkSegmentComparer;

/**
 * Compares the output of <code>Segment</code> with that of
 * <code>ConstraintNetwork</code>.
 * 
 * @author Andrew Post
 */
public class SegmentLength1PrimitiveParameterCompareTest extends
		ConstraintNetworkSegmentComparer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		seg = SegmentTestParameters.getLength1PrimitiveParameterSegment();
		super.setUp();
	}

}
