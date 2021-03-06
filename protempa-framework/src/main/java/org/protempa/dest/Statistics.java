package org.protempa.dest;

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
 * Collect various statistics about the data in the dataset previously
 * written out.
 */
public interface Statistics {
    
    int getNumberOfKeys() throws StatisticsException;
    
    Map<String, String> getChildrenToParents() throws StatisticsException;
    
    Map<String, String> getChildrenToParents(String[] propIds) throws StatisticsException;

    Map<String, Integer> getCounts() throws StatisticsException;
    
    Map<String, Integer> getCounts(String[] propIds) throws StatisticsException;
}
