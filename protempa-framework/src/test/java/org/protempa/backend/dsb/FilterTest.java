package org.protempa.backend.dsb;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2014 Emory University
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
