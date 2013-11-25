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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author Andrew Post
 */
public class DataSourceResultMapTest extends TestCase {

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
        this.map1 = new HashMap<>();
        this.map1.put("foo1", Arrays.asList(new String[]{"bar1", "baz1"}));
        this.map2 = new HashMap<>();
        this.map2.put("foo1", Arrays.asList(new String[]{"bar2", "baz2"}));
        this.map3 = new HashMap<>();
        this.map3.put("foo2", Arrays.asList(new String[]{"bar3", "baz3"}));
        List<Map<String, List<String>>> maps =
                new ArrayList<>(3);
        maps.add(this.map1);
        maps.add(this.map2);
        maps.add(this.map3);
        this.map = new DataSourceResultMap<>(maps);
    }

    @After
    @Override
    public void tearDown() {
        this.map = null;
    }

    public void testSize() {
        assertEquals(2, this.map.size());
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
        assertEquals(val, this.map.get("foo1"));
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
        Map<String, List<String>> aMap = new HashMap<>();
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
        Set<String> keys = new HashSet<>();
        keys.add("foo1");
        keys.add("foo2");
        assertEquals(keys, this.map.keySet());
    }

    public void testValues() {
        List<String> values1 = Arrays.asList(new String[]{"bar1", "baz1", "bar2", "baz2"});
        List<String> values3 = Arrays.asList(new String[]{"bar3", "baz3"});
        Collection<List<String>> expected = new ArrayList<>();
        expected.add(values1);
        expected.add(values3);
        Collection<List<String>> observed = this.map.values();
        assertEquals(new HashSet<>(expected), new HashSet<>(observed));
    }

    public void testEntrySet() {
        Map<String, List<String>> expectedMap = 
                new HashMap<>();
        expectedMap.put("foo1", Arrays.asList(new String[]{"bar1", "baz1", "bar2", "baz2"}));
        expectedMap.put("foo2", Arrays.asList(new String[]{"bar3", "baz3"}));
        Set<Map.Entry<String, List<String>>> expected = expectedMap.entrySet();
        Set<Map.Entry<String, List<String>>> observed = this.map.entrySet();
        assertEquals(expected, observed);
    }
}
