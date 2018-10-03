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
import org.junit.Before;
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
public class ProtempaWithPersistenceReprocessTest extends AbstractProtempaWithPersistenceTest {

    /**
     * The ground truth output.
     */

    private static final String TRUTH_OUTPUT_REPROCESS = "output-reprocess.txt";
    private static final String TRUTH_OUTPUT_REPROCESS_CREATE = "output-reprocess-create.txt";
    private static final String TRUTH_OUTPUT_REPROCESS_UPDATE = "output-reprocess-update.txt";
    private static final String TRUTH_OUTPUT_REPROCESS_DELETE = "output-reprocess-delete.txt";

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        runProtempa(new QueryBuilderFactory().getInstance(), QueryMode.REPLACE);
    }
    
    /**
     * Tests the end-to-end execution of Protempa with persistence, reprocessing
     * the output of {@link #testProtempaWithPersistence() }. Only verifies that
     * the final output is correct.
     *
     * @throws java.io.IOException if file writing fails.
     * @throws org.protempa.ProtempaException if the Protempa run fails.
     */
    @Test
    public void testProtempaWithPersistenceReprocessRetrieve() throws IOException, ProtempaException {
        runProtempa(new QueryBuilderReprocessFactory().getInstanceRetrieve(), QueryMode.REPROCESS_RETRIEVE, TRUTH_OUTPUT_REPROCESS);
    }

    @Test
    public void testProtempaWithPersistenceReprocessUpdate() throws IOException, ProtempaException {
        runProtempa(new QueryBuilderReprocessFactory().getInstanceUpdate(), QueryMode.REPROCESS_UPDATE, TRUTH_OUTPUT_REPROCESS_UPDATE);
    }

    @Test
    public void testProtempaWithPersistenceReprocessCreate() throws IOException, ProtempaException {
        runProtempa(new QueryBuilderReprocessFactory().getInstanceCreate(), QueryMode.REPROCESS_CREATE, TRUTH_OUTPUT_REPROCESS_CREATE);
    }

    @Test
    public void testProtempaWithPersistenceReprocessDelete() throws IOException, ProtempaException {
        runProtempa(new QueryBuilderReprocessFactory().getInstanceDelete(), QueryMode.REPROCESS_DELETE, TRUTH_OUTPUT_REPROCESS_DELETE);
    }

}
