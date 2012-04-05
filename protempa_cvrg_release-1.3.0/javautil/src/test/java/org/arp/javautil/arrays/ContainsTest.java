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
package org.arp.javautil.arrays;


import junit.framework.TestCase;

/**
 * Test for the contains method.
 * 
 * @author Andrew Post
 */
public class ContainsTest extends TestCase {

	public void testContainsObjectTrue() {
		assertTrue(Arrays.contains(new String[] { "hello", "world", "!" },
				"world"));
	}

	public void testContainsObjectFalse() {
		assertFalse(Arrays.contains(new String[] { "hello", "world", "!" },
				"goodbye"));
	}

	public void testContainsObjectNullArray() {
		assertFalse(Arrays.contains(null, "world"));
	}

	public void testContainsObjectNullTrue() {
		assertTrue(Arrays.contains(new String[] { "hello", null, "!" }, null));
	}

	public void testContainsObjectNullFalse() {
		assertTrue(Arrays.contains(new String[] { "hello", null, "!" }, "!"));
	}

}
