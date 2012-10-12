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
package org.protempa;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
public class DataSourceResultMapNullInputTest extends TestCase {

    private DataSourceResultMap<String> map;
//    private Map<String, List<String>> map1;
//    private Map<String, List<String>> map2;
//    private Map<String, List<String>> map3;

    public DataSourceResultMapNullInputTest() {
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
        this.map = new DataSourceResultMap<String>(null);
    }

    @After
    @Override
    public void tearDown() {
        this.map = null;
    }

    public void testSize() {
        assertEquals(0, this.map.size());
    }

    public void testIsEmpty() {
        assertTrue(this.map.isEmpty());
    }

    public void testContainsKeyFalse1() {
        assertFalse(this.map.containsKey("foo1"));
    }

    public void testContainsKeyFalse() {
        assertFalse(this.map.containsKey("baz"));
    }

    public void testContainsValueFalse1() {
        List<String> val = Arrays.asList(new String[]{"bar1", "baz1"});
        assertFalse(this.map.containsValue(val));
    }

    public void testContainsValueFalse() {
        List<String> val = Arrays.asList(new String[]{"bar1", "foo1"});
        assertFalse(this.map.containsValue(val));
    }

    public void testGetContains() {
        assertEquals(null, this.map.get("foo1"));
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
        assertEquals(Collections.emptySet(), this.map.keySet());
    }

    public void testValues() {
        Collection<List<String>> observed = this.map.values();
        assertEquals(Collections.emptySet(), new HashSet<List<String>>(observed));
    }

    public void testEntrySet() {
        Set<Map.Entry<String, List<String>>> observed = this.map.entrySet();
        assertEquals(Collections.emptySet(), observed);
    }
}
