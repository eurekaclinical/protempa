/*
 * #%L
 * Protempa Commons Backend Provider
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
package org.protempa.bp.commons;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;


/**
 *
 * @author Andrew Post
 */
public class CommonsUtilTest {
    
    /**
     * Test of resourceBundle method, of class CommonsUtil.
     */
    @Test
    public void testResourceBundle() {
        assertNotNull(CommonsUtil.resourceBundle());
    }

}
