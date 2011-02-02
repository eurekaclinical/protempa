package org.arp.javautil.map;

import java.util.Map;

import junit.framework.TestCase;

public class MapTest extends TestCase {
    
    private Map<String,String> testMap;
    private final int CACHE_SIZE = 5000;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testMap = new CacheMap<String,String>();
        int i;
        for (i = 0; i < this.CACHE_SIZE; i++) {
            testMap.put("Key" + i, "Value" + i);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testContains () {
        boolean contains = this.testMap.containsKey("Key23");
        assertEquals(true, contains);
    }
    
    public void testSize () {
        int size = this.testMap.size();
        assertEquals(this.CACHE_SIZE, size);
    }
}
