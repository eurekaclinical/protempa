/*
 * #%L
 * Protempa Test Suite
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
package org.protempa.test;

/**
 * Exception thrown when the data provider can not properly parse or fetch the
 * data needed.
 * 
 * @author hrathod
 * 
 */
public class DataProviderException extends Exception {

	/**
	 * Needed for serialization purposes.
	 */
	private static final long serialVersionUID = -8824458710265012443L;

	/**
	 * Create the exception using a simple message.
	 * 
	 * @param message The message to set for the exception.
	 */
	DataProviderException(String message) {
		super(message);
	}

	/**
	 * Create the exception using a {@link Throwable} to be used as the root
	 * cause.
	 * 
	 * @param throwable The root cause for this exception.
	 */
	DataProviderException(Throwable throwable) {
		super(throwable);
	}
}
