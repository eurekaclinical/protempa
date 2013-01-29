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
 * Thrown when an error occurs translating abstraction definitions into rules.
 *
 * @author Andrew Post
 */
public abstract class RuleCreationException extends ProtempaException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2627168079306834270L;

	protected RuleCreationException() {
		super();
	}

	protected RuleCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	protected RuleCreationException(String message) {
		super(message);
	}

	protected RuleCreationException(Throwable cause) {
		super(cause);
	}
}
