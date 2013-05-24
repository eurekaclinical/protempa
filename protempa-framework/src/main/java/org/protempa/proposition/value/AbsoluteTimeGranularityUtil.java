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

import java.util.Date;
import java.util.Map;
import org.apache.commons.collections.map.ReferenceMap;

/**
 *
 * @author Andrew Post
 */
public class AbsoluteTimeGranularityUtil {
    @SuppressWarnings("unchecked")
    private static final Map<Long, Date> cache = new ReferenceMap();
    
    private AbsoluteTimeGranularityUtil() {
        
    }
    
    public static Date asDate(Long position) {
        if (position != null) {
            Date cached = cache.get(position);
            if (cached == null) {
                cached = new Date(position);
                cache.put(position, cached);
            }
            return cached;
        } else {
            return null;
        }
    }
    
    public static Long asPosition(Date date) {
        if (date != null) {
            return date.getTime();
        } else {
            return null;
        }
    }
}
