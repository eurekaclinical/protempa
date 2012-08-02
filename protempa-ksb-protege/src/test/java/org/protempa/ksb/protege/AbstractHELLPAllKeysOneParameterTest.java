/*
 * #%L
 * Protempa Protege Knowledge Source Backend
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
package org.protempa.ksb.protege;

import java.util.List;
import java.util.Map;

import org.protempa.FinderException;



import org.protempa.proposition.Proposition;
import org.protempa.query.DefaultQueryBuilder;
import org.protempa.query.handler.MappingQueryResultsHandler;

/**
 * This is a test to verify if the number of cases for a parameter or a
 * combination of parameters is correct.
 * 
 * @author Nora Sovarel
 * 
 */
public abstract class AbstractHELLPAllKeysOneParameterTest
        extends AbstractTest {

    protected class TestProtempaRunner {

        private String propId;
        private int numberOfIntervalsFound;
        private int numberOfKeysFound;

        TestProtempaRunner(String propId) {
            this.propId = propId;
        }

        public void run() throws Exception {
            this.numberOfIntervalsFound = 0;
            this.numberOfKeysFound = 0;

            MappingQueryResultsHandler mqrh =
                    new MappingQueryResultsHandler();
            DefaultQueryBuilder q = new DefaultQueryBuilder();
            q.setPropositionIds(new String[]{propId});
            protempa.execute(protempa.buildQuery(q), mqrh);
            Map<String, List<Proposition>> results = mqrh.getResultMap();
            this.numberOfKeysFound = results.size();
            for (List<Proposition> vals : results.values()) {
                this.numberOfIntervalsFound += vals.size();
            }
        }

        int getNumberOfIntervalsFound() {
            return this.numberOfIntervalsFound;
        }

        int getNumberOfKeysFound() {
            return this.numberOfKeysFound;
        }
    }
}
