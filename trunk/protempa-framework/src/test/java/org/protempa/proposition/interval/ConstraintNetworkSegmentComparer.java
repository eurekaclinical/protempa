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
package org.protempa.proposition.interval;

import org.protempa.proposition.Segment;

import junit.framework.TestCase;

/**
 * @author Andrew Post
 */
public abstract class ConstraintNetworkSegmentComparer extends TestCase {

    protected Segment seg;
    protected ConstraintNetwork cn;

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        int size = seg.size();
        cn = new ConstraintNetwork(size);
        for (int i = 0; i < size; i++) {
            cn.addInterval(seg.get(i).getInterval());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        seg = null;
        cn = null;
    }

    public void testLength1MinStart() {
        assertEquals(Long.valueOf(cn.getMinimumStart().value()), seg.getInterval().getMinimumStart());
    }

    public void testLength1MaxStart() {
        assertEquals(Long.valueOf(cn.getMaximumStart().value()), seg.getInterval().getMaximumStart());
    }

    public void testLength1MinFinish() {
        assertEquals(Long.valueOf(cn.getMinimumFinish().value()), seg.getInterval().getMinimumFinish());
    }

    public void testLength1MaxFinish() {
        assertEquals(Long.valueOf(cn.getMaximumFinish().value()), seg.getInterval().getMaximumFinish());
    }

    public void testLength1MinDuration() {
        assertEquals(Long.valueOf(cn.getMinimumDuration().value()), seg.getInterval().getMinimumLength());
    }

    public void testLength1MaxDuration() {
        assertEquals(Long.valueOf(cn.getMaximumDuration().value()), seg.getInterval().getMaximumLength());
    }
}
