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
package org.protempa.proposition;

import org.protempa.proposition.visitor.AbstractPropositionVisitor;
import java.util.Collections;

import org.protempa.DataSourceBackendSourceSystem;
import org.protempa.ProtempaTestCase;

public class PropositionVisitorTest extends ProtempaTestCase {

    private static class PrimitiveParameterVisitor extends AbstractPropositionVisitor {

        boolean found;

        @Override
        public void visit(PrimitiveParameter primitiveParameter) {
            this.found = true;
        }

    }

    public void testPrimitiveParameter() throws Exception {
        PrimitiveParameterVisitor v = new PrimitiveParameterVisitor();
        PrimitiveParameter p = new PrimitiveParameter("test", getUid());
        p.setSourceSystem(DataSourceBackendSourceSystem.getInstance("TEST"));
        v.visit(Collections.singleton(p));
        assertTrue(v.found);
    }

}
