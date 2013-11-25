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
package org.protempa.proposition.value;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Andrew Post
 * 
 */
public class ListValueTest {

    @Test
    public void testParse() {
        long[] l = {1L, 2L, 3L, 4L};
        @SuppressWarnings("unchecked")
		ValueList<NumberValue> v = (ValueList<NumberValue>) ValueType.VALUELIST.parse("[1,2,3,4]");
        assertEquals(l.length, v.size());
        for (int i = 0, n = l.length; i < n; i++) {
            assertEquals(l[i], (v.get(i)).longValue());
        }
    }

    @Test
    public void testInEquals() {
        ValueList<NominalValue> l = ValueList.getInstance(
                NominalValue.getInstance("foo"),
                NominalValue.getInstance("bar"),
                NominalValue.getInstance("baz")
        );
        NominalValue bar = NominalValue.getInstance("bar");
        assertEquals(ValueComparator.IN, bar.compare(l));
    }

    @Test
    public void testInIs() {
        ValueList<NominalValue> l = ValueList.getInstance(
                NominalValue.getInstance("foo"),
                NominalValue.getInstance("bar"),
                NominalValue.getInstance("baz")
        );
        NominalValue bar = NominalValue.getInstance("bar");
        assertTrue(ValueComparator.IN.test(bar.compare(l)));
    }

    @Test
    public void testNotInEquals() {
        ValueList<NominalValue> l = ValueList.getInstance(
                NominalValue.getInstance("foo"),
                NominalValue.getInstance("bar"),
                NominalValue.getInstance("baz")
        );
        NominalValue foo2 = NominalValue.getInstance("foo2");
        assertEquals(ValueComparator.NOT_IN, foo2.compare(l));
    }

    @Test
    public void testNotInIs() {
        ValueList<NominalValue> l = ValueList.getInstance(
                NominalValue.getInstance("foo"),
                NominalValue.getInstance("bar"),
                NominalValue.getInstance("baz")
        );
        NominalValue foo2 = NominalValue.getInstance("foo2");
        assertTrue(ValueComparator.NOT_IN.test(foo2.compare(l)));
    }

    @Test
    public void testListEquals() {
        ValueList<NominalValue> l1 = ValueList.getInstance(
                NominalValue.getInstance("foo"),
                NominalValue.getInstance("bar"),
                NominalValue.getInstance("baz")
        );
        ValueList<NominalValue> l2 = ValueList.getInstance(
                NominalValue.getInstance("foo"),
                NominalValue.getInstance("bar"),
                NominalValue.getInstance("baz")
        );
        assertTrue(ValueComparator.NOT_IN.test(l1.compare(l2)));
    }

    @Test
    public void testListNotEqualDifferentOrder() {
        ValueList<NominalValue> l1 = ValueList.getInstance(
                NominalValue.getInstance("foo"),
                NominalValue.getInstance("bar"),
                NominalValue.getInstance("baz")
        );
        ValueList<NominalValue> l2 = ValueList.getInstance(
                NominalValue.getInstance("foo"),
                NominalValue.getInstance("baz"),
                NominalValue.getInstance("bar")
        );
        assertTrue(ValueComparator.NOT_EQUAL_TO.test(l1.compare(l2)));
    }

    @Test
    public void testListNotEqualDifferentSize1() {
        ValueList<NominalValue> l1 = ValueList.getInstance(
                NominalValue.getInstance("foo"),
                NominalValue.getInstance("bar"),
                NominalValue.getInstance("baz")
        );
        ValueList<NominalValue> l2 = ValueList.getInstance(
                NominalValue.getInstance("foo")
        );
        assertTrue(ValueComparator.NOT_EQUAL_TO.test(l1.compare(l2)));
    }

    @Test
    public void testListNotEqualDifferentSize2() {
        ValueList<NominalValue> l2 = ValueList.getInstance(
                NominalValue.getInstance("foo"),
                NominalValue.getInstance("bar"),
                NominalValue.getInstance("baz")
        );
        ValueList<NominalValue> l1 = ValueList.getInstance(
                NominalValue.getInstance("foo")
        );
        assertTrue(ValueComparator.NOT_EQUAL_TO.test(l1.compare(l2)));
    }

    @Test
    public void testListNotEqualDifferentSize3() {
        ValueList<NominalValue> l2 = ValueList.getInstance(
                NominalValue.getInstance("foo"),
                NominalValue.getInstance("bar"),
                NominalValue.getInstance("baz")
        );
        ValueList<NominalValue> l1 = ValueList.getInstance(
        );
        assertTrue(ValueComparator.NOT_EQUAL_TO.test(l1.compare(l2)));
    }

    @Test
    public void testListCompareToNull() {
        ValueList<NominalValue> l1 = ValueList.getInstance(
                NominalValue.getInstance("foo"),
                NominalValue.getInstance("bar"),
                NominalValue.getInstance("baz")
        );
        assertTrue(ValueComparator.UNKNOWN.test(l1.compare(null)));
    }
}
