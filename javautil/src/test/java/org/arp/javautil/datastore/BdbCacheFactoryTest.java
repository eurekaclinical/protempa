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
package org.arp.javautil.datastore;

import java.io.File;
import java.io.IOException;
import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.arp.javautil.io.TempDirectoryCreator;
import org.junit.Test;

public class BdbCacheFactoryTest {

    @Test
    public void testNewInstance() throws IOException {
        String envName = BdbUtil.uniqueEnvironment("bdb-store-test", null, 
                FileUtils.getTempDirectory());
        boolean deleteOnExit = true;
        BdbCacheFactory factory = new BdbCacheFactory(envName, deleteOnExit);
        DataStore<String, String> store = factory.newInstance("BdbTest");
        store.put("foo", "bar");
        String bar = store.get("foo");
        Assert.assertEquals("bar", bar);
        store.shutdown();
    }
}
