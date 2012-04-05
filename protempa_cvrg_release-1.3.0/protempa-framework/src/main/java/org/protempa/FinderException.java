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
package org.protempa;

/**
 * Thrown when an unexpected error occurs when calling one
 * of Protempa's find methods.
 * 
 * @author Andrew Post
 *
 */
public final class FinderException extends ProtempaException {
	
	private static final long serialVersionUID = 7903820808353618290L;
	
	public FinderException() {
		super();
	}

	public FinderException(String message, Throwable cause) {
		super(message, cause);
	}

	public FinderException(String message) {
		super(message);
	}

	public FinderException(Throwable cause) {
		super(cause);
	}
	
	
	
}
