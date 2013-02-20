/*
 * #%L
 * Protempa Test Suite
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
package org.protempa.test;

import java.util.List;

/**
 * An interface to provide functionality needed to provide data for upload to a
 * PROTEMPA schema.
 * 
 * @author hrathod
 * 
 */
public interface DataProvider {
	/**
	 * Get a list of patients from the data.
	 * 
	 * @return A list of {@link Patient} objects.
	 */
	public List<Patient> getPatients();

	/**
	 * Get a list of providers from the data.
	 * 
	 * @return A list of {@link Provider} objects.
	 */
	public List<Provider> getProviders();

	/**
	 * Get a list of encounters from the data.
	 * 
	 * @return A list of {@link Encounter} objects.
	 */
	public List<Encounter> getEncounters();

	/**
	 * Get a list of CPT codes from the data.
	 * 
	 * @return A list of {@link CPT} objects.
	 */
	public List<CPT> getCptCodes();

	/**
	 * Get a list of ICD9 Diagnostic codes from the data.
	 * 
	 * @return A list of {@link Icd9Diagnosis} objects.
	 */
	public List<Icd9Diagnosis> getIcd9Diagnoses();

	/**
	 * Get a list of ICD9 Procedure codes from the data.
	 * 
	 * @return A list of {@link Icd9Procedure} objects.
	 */
	public List<Icd9Procedure> getIcd9Procedures();

	/**
	 * Get a list of medication from the data.
	 * 
	 * @return A list of {@link Medication} objects.
	 */
	public List<Medication> getMedications();

	/**
	 * Get a list of lab results from the data.
	 * 
	 * @return A list of {@link Lab} objects.
	 */
	public List<Lab> getLabs();

	/**
	 * Get a list of vitals from the data.
	 * 
	 * @return A list of {@link Vital} objects.
	 */
	public List<Vital> getVitals();
}
