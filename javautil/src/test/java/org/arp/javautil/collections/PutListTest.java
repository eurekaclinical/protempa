/*
 * #%L
 * JavaUtil
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