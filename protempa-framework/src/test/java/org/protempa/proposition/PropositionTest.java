package org.protempa.proposition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import junit.framework.TestCase;

public class PropositionTest extends TestCase {
    public void testHashCode() {
        Set<Proposition> ps = new HashSet<Proposition>();
        
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
        
        Map<Proposition, String> pmap = new HashMap<Proposition, String>();
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
    
    private static UniqueId uid() {
        return new UniqueId(
                DerivedSourceId.getInstance(),
                new DerivedUniqueId(UUID.randomUUID().toString()));
    }
}
