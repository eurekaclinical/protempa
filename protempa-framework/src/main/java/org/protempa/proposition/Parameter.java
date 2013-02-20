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
package org.protempa.proposition;

import org.protempa.proposition.value.Value;

/**
 * Represents a single element of patient data or information, such as a
 * time-stamped raw data value or an inferred interval. A parameter has a data
 * type, value, and range of time over which it is true.
 * 
 * @author Andrew Post
 */
public interface Parameter extends Proposition {

    /**
     * Gets the value of this parameter.
     * 
     * @return the <code>Value</code> of this parameter.
     */
    Value getValue();

    /**
     * Returns this parameter's value formatted as a string. This is equivalent
     * to calling <code>getValue().getFormatted()</code>, but it handles the
     * case where <code>getValue()</code> returns <code>null</code>.
     * 
     * @return a <code>String</code> object, or an empty string if this
     *         parameter's value is <code>null</code>.
     */
    String getValueFormatted();

}
