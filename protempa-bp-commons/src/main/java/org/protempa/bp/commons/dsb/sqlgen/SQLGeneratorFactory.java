package org.protempa.bp.commons.dsb.sqlgen;

import org.protempa.bp.commons.dsb.SQLGenerator;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arp.javautil.sql.ConnectionSpec;
import org.protempa.bp.commons.dsb.RelationalDatabaseDataSourceBackend;

/**
 * Factory for obtaining a {@link SQLGenerator} given the database and
 * JDBC driver that are in use.
 * 
 * @author Andrew Post
 */
public final class SQLGeneratorFactory {

    private ConnectionSpec connectionSpec;
    private RelationalDatabaseDataSourceBackend backend;

    /**
     * Creates a new instance with a connection spec.
     *
     * @param connectionSpec a {@link ConnectionSpec}, cannot be
     * <code>null</code>.
     * @param backend an initialized
     * {@link RelationalDatabaseDataSourceBackend}.
     */
    public SQLGeneratorFactory(ConnectionSpec connectionSpec,
            RelationalDatabaseDataSourceBackend backend) {
        assert connectionSpec != null : "connectionSpec cannot be null!";
        assert backend != null : "backend cannot be null!";
        this.connectionSpec = connectionSpec;
        this.backend = backend;
    }

    /**
     * Returns a newly-created {@link SQLGenerator} that is compatible with
     * the database and JDBC driver that are in use.
     *
     * @return a {@link SQLGenerator}.
     * @throws SQLException if an attempt to query the database for
     * compatibility information failed.
     * @throws SQLGeneratorNotFoundException if no compatible SQL generator
     * could be found.
     * @throws SQLGeneratorLoadException if a ProtempaSQLGenerator class
     * specified in a {@link ServiceLoader}'s provider-configuration
     * file cannot be loaded by the the current thread's context class loader.
     */
    public SQLGenerator newInstance() throws SQLException,
            SQLGeneratorNotFoundException,
            SQLGeneratorLoadException {
        Logger logger = SQLGenUtil.logger();
        logger.fine("Loading a compatible SQL generator");
        ServiceLoader<ProtempaSQLGenerator> candidates =
                ServiceLoader.load(ProtempaSQLGenerator.class);
        /*
         * candidates will never be null, even if we mess up and forget to
         * create a provider-configuration file for ProtempaSQLGenerator.
         */
        try {
            for (ProtempaSQLGenerator candidateInstance : candidates) {
                if (!candidateInstance.loadDriverIfNeeded()) {
                    /*
                     * The necessary JDBC driver is not in the classpath, so 
                     * skip this SQL generator.
                     */
                    continue;
                }
                /*
                 * We get a new connection for each compatibility check so that
                 * no state (or a closed connection!) is carried over.
                 */
                Connection con = this.connectionSpec.getOrCreate();
                try {
                    if (candidateInstance.checkCompatibility(con)) {
                        DatabaseMetaData metaData = con.getMetaData();
                        if (logger.isLoggable(Level.FINER)) {
                            logCompatibility(logger, candidateInstance,
                                    metaData);
                        }
                        candidateInstance.initialize(
                                this.backend.getRelationalDatabaseSpec(),
                                this.connectionSpec, this.backend);
                        logger.log(Level.FINE, "SQL generator {0} is loaded",
                                candidateInstance.getClass().getName());
                        return candidateInstance;
                    }
                    con.close();
                    con = null;
                } finally {
                    if (con != null) {
                        try {
                            con.close();
                        } catch (SQLException ex) {
                        }
                    }
                }
            }
        } catch (ServiceConfigurationError sce) {
            throw new SQLGeneratorLoadException(
                    "Could not load SQL generators", sce);
        }
        throw new SQLGeneratorNotFoundException(
                "Could not find a SQL generator that is compatible with your database and available JDBC drivers");
    }

    private static void logCompatibility(Logger logger,
            ProtempaSQLGenerator candidateInstance, DatabaseMetaData metaData)
            throws SQLException {
        logger.log(Level.FINER, "{0} is compatible with database {1} ({2})",
                new Object[]{candidateInstance.getClass().getName(),
                metaData.getDatabaseProductName(),
                metaData.getDatabaseProductVersion()});
        logger.log(Level.FINER, "{0} is compatible with driver {1} ({2})", 
                new Object[]{candidateInstance.getClass().getName(),
                metaData.getDriverName(), metaData.getDriverVersion()});
    }
}
