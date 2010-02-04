package org.protempa.ksb.protege;

import java.io.File;
import org.protempa.AlgorithmSource;
import org.protempa.DataSource;
import org.protempa.KnowledgeSource;
import org.protempa.Protempa;
import org.protempa.SourceFactory;

/**
 *
 * @author Andrew Post
 */
public class AbstractTest {
    static {
        System.setProperty("protempa.inicommonsconfigurations.pathname",
                System.getProperty("user.home") + File.separator +
            ".protempa-protege-tests");
    }
    
    static Protempa protempa;

	static DataSource dataSource;

    static KnowledgeSource knowledgeSource;

    static AlgorithmSource algorithmSource;

    static void initProtempa(String configurationsId)  {
        try {
            SourceFactory sourceFactory = new SourceFactory(configurationsId);
            dataSource = sourceFactory.newDataSourceInstance();
            knowledgeSource = sourceFactory.newKnowledgeSourceInstance();
            algorithmSource = sourceFactory.newAlgorithmSourceInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
		protempa = new Protempa(dataSource, knowledgeSource,
                algorithmSource, false);
    }

    static void shutdownProtempa() {
        protempa.close();
        dataSource.close();
        knowledgeSource.close();
        algorithmSource.close();
    }

	
}
