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
package org.protempa.proposition.comparator;

import java.util.Comparator;
import java.util.UUID;

import junit.framework.TestCase;

import org.protempa.proposition.DerivedSourceId;
import org.protempa.proposition.DerivedUniqueId;
import org.protempa.proposition.Event;
import org.protempa.proposition.interval.IntervalFactory;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.value.AbsoluteTimeGranularity;

/**
 *
 * @author Andrew Post
 */
public class MaxFinishTemporalPropositionComparatorTest extends TestCase {

    private static IntervalFactory ivalFactory = new IntervalFactory();
    
    private Comparator<TemporalProposition> comp;

    @Override
    protected void setUp() throws Exception {
        this.comp = new MaxFinishTemporalPropositionComparator();
    }

    @Override
    protected void tearDown() throws Exception {
        this.comp = null;
    }

    public void testCompareAllNull() {
        Event e1 = new Event("TEST", uid());
        e1.setInterval(ivalFactory.getInstance());
        Event e2 = new Event("TEST", uid());
        e2.setInterval(ivalFactory.getInstance());
        assertEquals(0, comp.compare(e1, e2));
    }

    public void testCompareLhsNull() {
        Event e1 = new Event("TEST", uid());
        e1.setInterval(ivalFactory.getInstance());
        Event e2 = new Event("TEST", uid());
        e2.setInterval(ivalFactory.getInstance(1L, AbsoluteTimeGranularity.DAY));
        assertEquals(1, comp.compare(e1, e2));
    }

    public void testCompareRhsNull() {
        Event e1 = new Event("TEST", uid());
        e1.setInterval(ivalFactory.getInstance(1L, AbsoluteTimeGranularity.DAY));
        Event e2 = new Event("TEST", uid());
        e2.setInterval(ivalFactory.getInstance());
        assertEquals(-1, comp.compare(e1, e2));
    }

    public void testCompareNotNullBefore() {
        Event e1 = new Event("TEST", uid());
        e1.setInterval(ivalFactory.getInstance(1L, AbsoluteTimeGranularity.DAY));
        Event e2 = new Event("TEST", uid());
        e2.setInterval(ivalFactory.getInstance(2L, AbsoluteTimeGranularity.DAY));
        assertEquals(-1, comp.compare(e1, e2));
    }

    public void testCompareNotNullEqual() {
        Event e1 = new Event("TEST", uid());
        e1.setInterval(ivalFactory.getInstance(3L, AbsoluteTimeGranularity.DAY));
        Event e2 = new Event("TEST", uid());
        e2.setInterval(ivalFactory.getInstance(3L, AbsoluteTimeGranularity.DAY));
        assertEquals(0, comp.compare(e1, e2));
    }

    public void testCompareNotNullAfter() {
        Event e1 = new Event("TEST", uid());
        e1.setInterval(ivalFactory.getInstance(5L, AbsoluteTimeGranularity.DAY));
        Event e2 = new Event("TEST", uid());
        e2.setInterval(ivalFactory.getInstance(4L, AbsoluteTimeGranularity.DAY));
        assertEquals(1, comp.compare(e1, e2));
    }
    
    private static UniqueId uid() {
        return new UniqueId(
                DerivedSourceId.getInstance(),
                new DerivedUniqueId(UUID.randomUUID().toString()));
    }
}
