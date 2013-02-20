/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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

import org.protempa.proposition.Segment;

import junit.framework.TestCase;

public abstract class SegmentChecker extends TestCase {

    protected Long minStart;
    protected Long maxStart;
    protected Long minFinish;
    protected Long maxFinish;
    protected Long minDuration;
    protected Long maxDuration;
    protected Segment seg;

    public void testLength1MinStart() {
        assertEquals(minStart, seg.getInterval().getMinimumStart());
    }

    public void testLength1MaxStart() {
        assertEquals(maxStart, seg.getInterval().getMaximumStart());
    }

    public void testLength1MinFinish() {
        assertEquals(minFinish, seg.getInterval().getMinimumFinish());
    }

    public void testLength1MaxFinish() {
        assertEquals(maxFinish, seg.getInterval().getMaximumFinish());
    }

    public void testLength1MinDuration() {
        assertEquals(minDuration, seg.getInterval().getMinimumLength());
    }

    public void testLength1MaxDuration() {
        assertEquals(maxDuration, seg.getInterval().getMaximumLength());
    }
}
