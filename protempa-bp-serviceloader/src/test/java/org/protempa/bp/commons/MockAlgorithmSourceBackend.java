/*
 * #%L
 * Protempa Commons Backend Provider
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
package org.protempa.bp.commons;

import org.protempa.backend.AbstractCommonsAlgorithmSourceBackend;
import org.protempa.backend.annotations.BackendInfo;
import org.protempa.Algorithm;
import org.protempa.AlgorithmSourceReadException;
import org.protempa.Algorithms;

@BackendInfo(displayName = "Mock Algorithm Source Backend")
public class MockAlgorithmSourceBackend 
        extends AbstractCommonsAlgorithmSourceBackend {

    public Algorithm readAlgorithm(String id, Algorithms algorithms)
            throws AlgorithmSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void readAlgorithms(Algorithms algorithms)
            throws AlgorithmSourceReadException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
