package org.arp.javautil.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

/**
 * Tests for {@link Collections#putList}.
 * 
 * @author Andrew Post
 */
public class PutListTest extends TestCase {
    public void testPutIntoEmptyMap() {
        Map<String, List<String>> testMap =
                new HashMap<String, List<String>>();
        Collections.putList(testMap, "foo", "bar");
        List<String> expected = new ArrayList<String>();
        expected.add("bar");
        assertEquals(expected, testMap.get("foo"));
    }

    public void testAddToListInMap() {
        Map<String, List<String>> testMap =
                new HashMap<String, List<String>>();
        List<String> value = new ArrayList<String>();
        value.add("bar");
        testMap.put("foo", value);
        Collections.putList(testMap, "foo", "baz");
        List<String> expected = new ArrayList<String>();
        expected.add("bar");
        expected.add("baz");
        assertEquals(expected, testMap.get("foo"));
    }
}
