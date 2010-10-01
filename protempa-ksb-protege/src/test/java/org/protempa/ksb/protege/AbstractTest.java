package org.protempa.ksb.protege;

import java.io.File;
import org.protempa.AlgorithmSource;
import org.protempa.BackendInitializationException;
import org.protempa.DataSource;
import org.protempa.DataSourceFailedValidationException;
import org.protempa.DataSourceValidationIncompleteException;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.Protempa;
import org.protempa.backend.BackendNewInstanceException;
import org.protempa.backend.BackendProviderSpecLoaderException;
import org.protempa.backend.ConfigurationsLoadException;
import org.protempa.backend.InvalidConfigurationException;

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
        } catch (DataSourceFailedValidationException ex) {
            throw new RuntimeException(ex);
        } catch (DataSourceValidationIncompleteException ex) {
            throw new RuntimeException(ex);
        } catch (ConfigurationsLoadException ex) {
            throw new RuntimeException(ex);
        } catch (BackendInitializationException ex) {
            throw new RuntimeException(ex);
        } catch (BackendNewInstanceException ex) {
            throw new RuntimeException(ex);
        } catch (BackendProviderSpecLoaderException ex) {
            throw new RuntimeException(ex);
        } catch (InvalidConfigurationException ex) {
            throw new RuntimeException(ex);
        } catch (KnowledgeSourceReadException ex) {
            throw new RuntimeException(ex);
        }
    }

    static void shutdownProtempa() {
        protempa.close();
    }
}
