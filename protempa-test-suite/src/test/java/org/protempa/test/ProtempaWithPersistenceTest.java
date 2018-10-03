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

import java.io.IOException;
import org.junit.Test;
import org.protempa.ProtempaException;
import org.protempa.query.QueryMode;

/**
 * Unit tests for Protempa.
 *
 * Persistent stores go into the directory in the system property
 * <code>java.io.tmpdir</code>.
 *
 * @author Michel Mansour
 */
public class ProtempaWithPersistenceTest extends AbstractProtempaWithPersistenceTest {

    /**
     * The ground truth output.
     */
    private static final String TRUTH_OUTPUT = "output.txt";

    /**
     * Tests the end-to-end execution of Protempa with persistence. Only
     * verifies that the final output is correct.
     *
     * @throws java.io.IOException if file writing fails.
     * @throws org.protempa.ProtempaException if the Protempa run fails.
     */
    @Test
    public void testProtempaWithPersistence() throws IOException, ProtempaException {
        runProtempa(new QueryBuilderFactory().getInstance(), QueryMode.REPLACE, TRUTH_OUTPUT);
    }

}
