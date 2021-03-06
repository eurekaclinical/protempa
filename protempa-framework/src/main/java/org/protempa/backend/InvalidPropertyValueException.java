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
package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 * Thrown if a property value in a configuration is invalid.
 *
 * @author Andrew Post
 */
public class InvalidPropertyValueException extends ProtempaException {

    private static final long serialVersionUID = -5960840541202474047L;

    public InvalidPropertyValueException(String msg) {
        super(msg);
    }
    
    public InvalidPropertyValueException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
    
    public InvalidPropertyValueException(String name, Object value, Throwable throwable) {
        super("Invalid value " + value + " for property " + name, throwable);
    }
}
