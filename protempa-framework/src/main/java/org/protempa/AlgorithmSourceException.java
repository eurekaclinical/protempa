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
package org.protempa;

/**
 * Thrown when an error occurs in algorithm initialization or processing.
 * @author Andrew Post
 *
 */
public abstract class AlgorithmSourceException extends ProtempaException {
	
	private static final long serialVersionUID = 8105126411793215020L;

	AlgorithmSourceException() {
		super();
	}

	AlgorithmSourceException(String message, Throwable cause) {
		super(message, cause);
	}

	AlgorithmSourceException(String message) {
		super(message);
	}

	AlgorithmSourceException(Throwable cause) {
		super(cause);
	}

}
