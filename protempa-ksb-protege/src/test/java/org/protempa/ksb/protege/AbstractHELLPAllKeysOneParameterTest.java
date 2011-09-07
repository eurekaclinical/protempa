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
            q.setPropIds(new String[]{propId});
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
