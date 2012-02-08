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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Iterators {

    private Iterators() {
    }

    /**
     * Tests two iterators for equality, meaning that they have the same
     * elements enumerated in the same order.
     *
     * @param itr1
     *            an <code>Iterator</code>.
     * @param itr2
     *            an <code>Iterator</code>.
     * @return <code>true</code> if the two iterators are not
     *         <code>null</code> and equal, <code>false</code> otherwise.
     */
    public static boolean equal(Iterator itr1, Iterator itr2) {
        if (itr1 == null || itr2 == null) {
            return false;
        } else {
            while (itr1.hasNext() && itr2.hasNext()) {
                Object i = itr1.next();
                Object i2 = itr2.next();
                if ((i == null && i2 != null) || !(i.equals(i2))) {
                    return false;
                }
            }
            if (itr1.hasNext() || itr2.hasNext()) {
                return false;
            }
            return true;
        }
    }

    /**
     * Returns an iterator as a list.
     *
     * @param itr
     *            an <code>Iterator</code>.
     * @return a <code>List</code>.
     */
    public static <T> List<T> asList(Iterator<T> itr) {
        List<T> l = new ArrayList<T>();
        if (itr != null) {
            while (itr.hasNext()) {
                l.add(itr.next());
            }
        }
        return l;
    }

    /**
     * Returns an iterator as a collection.
     *
     * @param itr
     *            an <code>Iterator</code>.
     * @return a <code>Collection</code>.
     */
    public static <T> Collection<T> asCollection(Iterator<T> itr) {
        return asList(itr);
    }
}
