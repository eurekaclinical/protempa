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
package org.protempa.proposition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import junit.framework.TestCase;

public class PropositionTest extends TestCase {
    
    public void testEqualsSameUid() {
        UniqueId uid = uid();
        Event e1 = new Event("foo", uid);
        Event e2 = new Event("bar", uid);
        assertTrue(e1.equals(e2));
    }
    
    public void testEqualsDifferentUid() {
        Event e1 = new Event("foo", uid());
        Event e2 = new Event("foo", uid());
        assertFalse(e1.equals(e2));
    }
    
    public void testEqualsNull() {
        Event e1 = new Event("foo", uid());
        assertFalse(e1.equals(null));
    }
    
    public void testHashCode() {
        Set<Proposition> ps = new HashSet<>();
        
        Event e1 = new Event("foo", uid());
        ps.add(e1);
        Event e2 = new Event("bar", uid());
        ps.add(e2);
        Event e3 = new Event("baz", uid());
        ps.add(e3);
        UniqueId quuxUid = uid();
        Event e4 = new Event("quux", quuxUid);
        ps.add(e4);
        
        assertEquals(4, ps.size());
        assertEquals(new Event("quux", quuxUid), e4);
        
        Event e5 = new Event("foo", uid());
        ps.add(e5);
        
        assertEquals(5, ps.size());
        
        for (Proposition p : ps) {
            assertTrue(assertOneOf(p, e1, e2, e3, e4, e5));
        }
        
        Map<Proposition, String> pmap = new HashMap<>();
        pmap.put(e1, "foo");
        pmap.put(e4, "quux");
        assertNull(pmap.put(e5, "foo"));
        assertEquals("quux", pmap.put(new Event("quux", quuxUid), "xuup"));
    }
    
    private boolean assertOneOf(Proposition toFind, Proposition... props) {
        for (Proposition p : props) {
            if (toFind.equals(p)) {
                return true;
            }
        }
        
        return false;
    }
    
    public static UniqueId uid() {
        return new UniqueId(
                DerivedSourceId.getInstance(),
                new DerivedUniqueId(UUID.randomUUID().toString()));
    }
}
