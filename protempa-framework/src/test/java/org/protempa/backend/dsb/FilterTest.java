package org.protempa.backend.dsb;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Andrew Post
 */
public class FilterTest {
    @Test
    public void testFilterChainToArrayLength() {
        MockFilter f1 = new MockFilter("foo");
        MockFilter f2 = new MockFilter("bar");
        MockFilter f3 = new MockFilter("baz");
        f1.setAnd(f2);
        f2.setAnd(f3);
        Assert.assertEquals(3, f1.filterChainToArray().length);
    }
    
    @Test
    public void testFilterChainLength() {
        MockFilter f1 = new MockFilter("foo");
        MockFilter f2 = new MockFilter("bar");
        MockFilter f3 = new MockFilter("baz");
        f1.setAnd(f2);
        f2.setAnd(f3);
        Assert.assertEquals(3, f1.chainLength());
    }
}
