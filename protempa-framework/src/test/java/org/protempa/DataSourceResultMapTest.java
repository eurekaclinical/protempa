package org.protempa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author Andrew Post
 */
public class DataSourceResultMapTest extends TestCase {

    private static final class MockMapEntry implements Map.Entry<String, List<String>> {
        private volatile int hashCode = 0;
        private String key;
        private List<String> value;

        MockMapEntry(String key, String... value) {
            this.key = key;
            this.value = Arrays.asList(value);
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public List<String> getValue() {
            return value;
        }

        @Override
        public List<String> setValue(List<String> v) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Map.Entry<?, ?>)) {
                return false;
            }
            final Map.Entry<?, ?> other = (Map.Entry<?, ?>) obj;
            String myKey = getKey();
            Object otherKey = other.getKey();
            if (myKey != otherKey && (myKey == null || !myKey.equals(otherKey))) {
                return false;
            }
            List<String> myValue = getValue();
            Object otherValue = other.getValue();
            if (myValue != otherValue && (myValue == null || !myValue.equals(otherValue))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            if (this.hashCode == 0) {
                int hash = 3;
                hash = 41 * hash + (key != null ? key.hashCode() : 0);
                hash = 41 * hash + (value != null ? value.hashCode() : 0);
                this.hashCode = hash;
            }
            return this.hashCode;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

    }

    private DataSourceResultMap<String> map;
    private Map<String, List<String>> map1;
    private Map<String, List<String>> map2;
    private Map<String, List<String>> map3;

    public DataSourceResultMapTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    @Override
    public void setUp() {
        this.map1 = new HashMap<String, List<String>>();
        this.map1.put("foo1", Arrays.asList(new String[]{"bar1", "baz1"}));
        this.map2 = new HashMap<String, List<String>>();
        this.map2.put("foo1", Arrays.asList(new String[]{"bar2", "baz2"}));
        this.map3 = new HashMap<String, List<String>>();
        this.map3.put("foo2", Arrays.asList(new String[]{"bar3", "baz3"}));
        List<Map<String, List<String>>> maps =
                new ArrayList<Map<String, List<String>>>(3);
        maps.add(this.map1);
        maps.add(this.map2);
        maps.add(this.map3);
        this.map = new DataSourceResultMap<String>(maps);
    }

    @After
    @Override
    public void tearDown() {
        this.map = null;
    }

    public void testSize() {
        assertEquals(3, this.map.size());
    }

    public void testIsEmpty() {
        assertFalse(this.map.isEmpty());
    }

    public void testContainsKeyTrue() {
        assertTrue(this.map.containsKey("foo1"));
    }

    public void testContainsKeyFalse() {
        assertFalse(this.map.containsKey("baz"));
    }

    public void testContainsValueTrue() {
        List<String> val = Arrays.asList(new String[]{"bar1", "baz1"});
        assertTrue(this.map.containsValue(val));
    }

    public void testContainsValueFalse() {
        List<String> val = Arrays.asList(new String[]{"bar1", "foo1"});
        assertFalse(this.map.containsValue(val));
    }

    public void testGetContains() {
        List<String> val = Arrays.asList(new String[]{"bar1", "baz1", "bar2", "baz2"});
        assertEquals(this.map.get("foo1"), val);
    }

    public void testGetDoesNotContain() {
        List<String> val = Arrays.asList(new String[]{"bar1", "baz1"});
        assertFalse(val.equals(this.map.get("foo2")));
    }

    public void testPut() {
        try {
            this.map.put("shouldNotWork", Arrays.asList(new String[0]));
            fail();
        } catch (UnsupportedOperationException uoe) {
        }
    }

    public void testRemove() {
        try {
            this.map.remove("shouldNotWork");
            fail();
        } catch (UnsupportedOperationException uoe) {
        }
    }

    public void testPutAll() {
        Map<String, List<String>> aMap = new HashMap<String, List<String>>();
        aMap.put("shouldNotWork", Arrays.asList(new String[0]));
        try {
            this.map.putAll(aMap);
            fail();
        } catch (UnsupportedOperationException uoe) {
        }
    }

    public void testClear() {
        try {
            this.map.clear();
            fail();
        } catch (UnsupportedOperationException uoe) {
        }
    }

    public void testKeySet() {
        Set<String> keys = new HashSet<String>();
        keys.add("foo1");
        keys.add("foo2");
        assertEquals(keys, this.map.keySet());
    }

    public void testValues() {
        List<String> values1 = Arrays.asList(new String[]{"bar1", "baz1"});
        List<String> values2 = Arrays.asList(new String[]{"bar2", "baz2"});
        List<String> values3 = Arrays.asList(new String[]{"bar3", "baz3"});
        List<List<String>> values = new ArrayList<List<String>>();
        values.add(values1);
        values.add(values2);
        values.add(values3);
        assertEquals(values, this.map.values());
    }

    public void testEntrySet() {
        MockMapEntry e1 = new MockMapEntry("foo1", "bar1", "baz1", "bar2", "baz2");
        MockMapEntry e2 = new MockMapEntry("foo2", "bar3", "baz3");
        Set<MockMapEntry> expected = new HashSet<MockMapEntry>();
        expected.add(e1);
        expected.add(e2);
        assertEquals(expected, this.map.entrySet());
    }
}
