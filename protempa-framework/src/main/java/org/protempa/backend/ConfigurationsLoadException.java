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
 *
 * @author Andrew Post
 */
public final class ConfigurationsLoadException extends ProtempaException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -9180141565868689830L;

	public ConfigurationsLoadException(Throwable cause) {
        super(cause);
    }

    public ConfigurationsLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationsLoadException(String message) {
        super(message);
    }

    public ConfigurationsLoadException() {
    }

}
