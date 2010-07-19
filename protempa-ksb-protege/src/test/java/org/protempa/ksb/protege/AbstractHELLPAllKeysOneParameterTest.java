package org.protempa.ksb.protege;

import java.util.List;

import org.protempa.DataSourceReadException;
import org.protempa.FinderException;
import org.protempa.query.SimpleQuery;
import org.protempa.proposition.Proposition;

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

		public void run() throws FinderException {
			this.numberOfIntervalsFound = 0;
			this.numberOfKeysFound = 0;
			
			try {
                int i = 0;
                while (true) {
                    List<String> keyIds =
                        AbstractHELLPAllKeysOneParameterTest.dataSource
                        .getAllKeyIds(i,1000);
                    if (keyIds.isEmpty()) {
                        break;
                    }
                    for (String keyId : keyIds) {
                        List<Proposition> results = protempa.execute(
                                SimpleQuery.newInstance(keyId,propId))
                                .get(keyId);
                        if (!results.isEmpty()) {
                            numberOfIntervalsFound += results.size();
                            numberOfKeysFound++;
                        }
                    }
                    i += 1000;
                }
            } catch (DataSourceReadException ex) {
                throw new FinderException(ex);
			} catch (FinderException fe) {
				throw fe;
			} catch (Exception e) {
				throw new FinderException("An error occurred", e);
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
