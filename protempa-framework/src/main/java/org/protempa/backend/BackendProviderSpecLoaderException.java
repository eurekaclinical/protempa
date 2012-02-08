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
public class BackendProviderSpecLoaderException extends ProtempaException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5734542815931371751L;

	public BackendProviderSpecLoaderException(Throwable cause) {
        super(cause);
    }

    public BackendProviderSpecLoaderException(String message) {
        super(message);
    }

    public BackendProviderSpecLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public BackendProviderSpecLoaderException() {
    }



}
