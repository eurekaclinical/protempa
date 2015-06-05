package org.protempa.dest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
/**
 *
 * @author Andrew Post
 */
public class DefaultStatisticsBuilder {

    private int numberOfKeys;
    private Map<String, Integer> counts;
    private Map<String, String> childrenToParents;

    public DefaultStatisticsBuilder() {
        this.counts = Collections.emptyMap();
        this.childrenToParents = Collections.emptyMap();
    }
    
    public int getNumberOfKeys() {
        return numberOfKeys;
    }

    public void setNumberOfKeys(int numberOfKeys) {
        this.numberOfKeys = numberOfKeys;
    }

    public Map<String, String> getChildrenToParents() {
        return new HashMap<>(childrenToParents);
    }

    public void setChildrenToParents(Map<String, String> childrenToParents) {
        if (childrenToParents != null) {
            this.childrenToParents = new HashMap<>(childrenToParents);
        } else {
            this.childrenToParents = Collections.emptyMap();
        }
    }

    public Map<String, Integer> getCounts() {
        return new HashMap<>(counts);
    }

    public void setCounts(Map<String, Integer> counts) {
        if (counts != null) {
            this.counts = new HashMap<>(counts);
        } else {
            this.counts = Collections.emptyMap();
        }
    }

    public DefaultStatistics toDefaultStatistics() {
        return new DefaultStatistics(this.numberOfKeys, this.counts, this.childrenToParents);
    }

}
