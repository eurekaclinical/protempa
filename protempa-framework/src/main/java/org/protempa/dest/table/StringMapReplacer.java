package org.protempa.dest.table;

/*-
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2017 Emory University
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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author arpost
 */
public class StringMapReplacer implements Replacer<String> {

    private final Map<String, String> replace;

    public StringMapReplacer(Map<String, String> inReplace) {
        if (inReplace != null) {
            this.replace = new HashMap<>(inReplace);
        } else {
            this.replace = new HashMap<>();
        }
    }

    public Map<String, String> getReplace() {
        return new HashMap<>(replace);
    }

    @Override
    public String replace(String inValue) {
        if (this.replace != null) {
            return this.replace.getOrDefault(inValue, inValue);
        } else {
            return inValue;
        }
    }

}
