/*
 * #%L
 * Protempa Framework
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
package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public class ConfigurationRemoveException extends ProtempaException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2329005766725093529L;

	public ConfigurationRemoveException(Throwable cause) {
        super(cause);
    }

    public ConfigurationRemoveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationRemoveException(String message) {
        super(message);
    }

    public ConfigurationRemoveException() {
    }
}
