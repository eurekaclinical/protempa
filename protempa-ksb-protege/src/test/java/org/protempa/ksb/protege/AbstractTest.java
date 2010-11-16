package org.protempa.ksb.protege;

import java.io.File;

import org.protempa.AlgorithmSource;
import org.protempa.DataSource;
import org.protempa.KnowledgeSource;
import org.protempa.Protempa;
import org.protempa.ProtempaStartupException;

/**
 * 
 * @author Andrew Post
 */
public class AbstractTest {

    static {
        System.setProperty("protempa.inicommonsconfigurations.pathname",
                System.getProperty("user.home") + File.separator
                        + ".protempa-protege-tests");
    }
    static Protempa protempa;
    static DataSource dataSource;
    static KnowledgeSource knowledgeSource;
    static AlgorithmSource algorithmSource;

    static void initProtempa(String configurationsId) {
        try {
            protempa = Protempa.newInstance(configurationsId);
            dataSource = protempa.getDataSource();
            knowledgeSource = protempa.getKnowledgeSource();
            algorithmSource = protempa.getAlgorithmSource();
        } catch (ProtempaStartupException ex) {
            throw new RuntimeException(ex);
        }
    }

    static void shutdownProtempa() {
        protempa.close();
    }
}
