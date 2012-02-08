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
package org.arp.javautil.map;

import java.util.Map;

import junit.framework.TestCase;

import org.arp.javautil.map.ImnuMap.DefaultValue;

public class ImnuMapTest extends TestCase {

    private Map<Integer, String> nullMap;
    private Map<Integer, String> customMap;
    
    private class IntegerParityDefaultValue implements DefaultValue<String> {

        @Override
        public String defaultValue(Object key) {
            if (key instanceof Integer) {
                Integer k = (Integer) key;
                if (k % 2 == 0) {
                    return "EVEN";
                } else {
                    return "ODD";
                }
            } else {
                return "NAN";
            }
        }
        
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        nullMap = new ImnuMap<Integer, String>();
        customMap = new ImnuMap<Integer, String>(new IntegerParityDefaultValue());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    // tests that get behaves correctly for the default ImnuMap
    public void testGet() {
        nullMap.put(1, "ONE");
        nullMap.put(2, "TWO");
        nullMap.put(4, "FOUR");
        
        assertEquals(nullMap.get(1), "ONE");
        assertEquals(nullMap.get(2), "TWO");
        assertEquals(nullMap.get(3), null);
        assertEquals(nullMap.get(4), "FOUR");
        assertEquals(nullMap.get("foo"), null);
    }
    
    // tests custom DefaultValue implementation
    public void testGetCustom() {
        customMap.put(1, "ONE");
        customMap.put(2, "TWO");
        customMap.put(4, "FOUR");
        
        assertEquals(customMap.get(1), "ONE");
        assertEquals(customMap.get(2), "TWO");
        assertEquals(customMap.get(3), "ODD");
        assertEquals(customMap.get(4), "FOUR");
        assertEquals(customMap.get("foo"), "NAN");
    }
}
