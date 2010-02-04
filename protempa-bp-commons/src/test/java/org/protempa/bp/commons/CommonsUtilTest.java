package org.protempa.bp.commons;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;


/**
 *
 * @author Andrew Post
 */
public class CommonsUtilTest {
    
    /**
     * Test of resourceBundle method, of class CommonsUtil.
     */
    @Test
    public void testResourceBundle() {
        assertNotNull(CommonsUtil.resourceBundle());
    }

}
