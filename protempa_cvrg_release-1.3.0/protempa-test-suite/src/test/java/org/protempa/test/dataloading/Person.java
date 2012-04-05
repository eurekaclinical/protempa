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
package org.protempa.test.dataloading;

/**
 * A person related to an encounter; for example, a patient or a provider.
 * 
 * @author hrathod
 * 
 */
abstract class Person {
	/**
	 * The unique identifier for the person.
	 */
	private Long id;
	/**
	 * Person's first name.
	 */
	private String firstName;
	/**
	 * Person's last name.
	 */
	private String lastName;

	/**
	 * Get the person's unique identifier.
	 * 
	 * @return The person's unique identifier.
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * Set the person's unique identifier.
	 * 
	 * @param inId The person's unique identifier.
	 */
	public void setId(Long inId) {
		this.id = inId;
	}

	/**
	 * Get the person's first name.
	 * 
	 * @return The person's first name.
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Set the person's first name.
	 * 
	 * @param inFirstName The person's first name.
	 */
	public void setFirstName(String inFirstName) {
		this.firstName = inFirstName;
	}

	/**
	 * Get the person's last name.
	 * 
	 * @return The person's last name.
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Set the person's last name.
	 * 
	 * @param inLastName The person's last name.
	 */
	public void setLastName(String inLastName) {
		this.lastName = inLastName;
	}
}
