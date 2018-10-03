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

import java.util.stream.Stream;

/**
 * An interface to provide functionality needed to provide data for upload to a
 * PROTEMPA schema.
 *
 * @author hrathod
 *
 */
public interface DataProvider {

    /**
     * Get a stream of patients from the data.
     *
     * @return A stream of {@link Patient} objects.
     */
    Stream<Patient> getPatients();

    /**
     * Get a stream of providers from the data.
     *
     * @return A stream of {@link Provider} objects.
     */
    Stream<Provider> getProviders();

    /**
     * Get a stream of encounters from the data.
     *
     * @return A stream of {@link Encounter} objects.
     */
    Stream<Encounter> getEncounters();

    /**
     * Get a stream of ICD9 Diagnostic codes from the data.
     *
     * @return A stream of {@link Icd9Diagnosis} objects.
     */
    Stream<Icd9Diagnosis> getIcd9Diagnoses();

    /**
     * Get a stream of ICD9 Procedure codes from the data.
     *
     * @return A stream of {@link Icd9Procedure} objects.
     */
    Stream<Icd9Procedure> getIcd9Procedures();

    /**
     * Get a stream of medication from the data.
     *
     * @return A stream of {@link Medication} objects.
     */
    Stream<Medication> getMedications();

    /**
     * Get a stream of lab results from the data.
     *
     * @return A stream of {@link Lab} objects.
     */
    Stream<Lab> getLabs();

    /**
     * Get a stream of vitals from the data.
     *
     * @return A stream of {@link Vital} objects.
     */
    Stream<Vital> getVitals();
}
