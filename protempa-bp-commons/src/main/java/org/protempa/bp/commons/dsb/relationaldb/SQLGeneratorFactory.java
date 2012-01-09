package org.protempa.bp.commons.dsb.relationaldb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arp.javautil.sql.ConnectionSpec;
import org.protempa.bp.commons.dsb.RelationalDbDataSourceBackend;

/**
 * Factory for obtaining a {@link SQLGenerator} given the database and
 * JDBC driver that are in use.
 *
 * The system property
 * <code>protempa.dsb.relationaldatabase.sqlgenerator</code> can be set with
 * the full class name of an implementation of {@link SQLGenerator} to force
 * the use of a particular SQL generator.
 * 
 * @author Andrew Post
 */
public final class SQLGeneratorFactory {

    private ConnectionSpec connectionSpec;
    private RelationalDbDataSourceBackend backend;

    /**
     * Creates a new instance with a connection spec.
     *
     * @param connectionSpec a {@link ConnectionSpec}, cannot be
     * <code>null</code>.
     * @param backend an initialized
     * {@link RelationalDbDataSourceBackend}.
     */
    public SQLGeneratorFactory(ConnectionSpec connectionSpec,
            RelationalDbDataSourceBackend backend) {
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
     * @throws NoCompatibleSQLGeneratorException if no compatible SQL generator
     * could be found.
     * @throws SQLGeneratorLoadException if a ProtempaSQLGenerator class
     * specified in a {@link ServiceLoader}'s provider-configuration
     * file cannot be loaded by the the current thread's context class loader.
     */
    public SQLGenerator newInstance() throws SQLException,
            NoCompatibleSQLGeneratorException,
            SQLGeneratorLoadException {
        Logger logger = SQLGenUtil.logger();
        logger.fine("Loading a compatible SQL generator");
        
        ServiceLoader<SQLGenerator> candidates =
                ServiceLoader.load(SQLGenerator.class);
        /*
         * candidates will never be null, even if we mess up and forget to
         * create a provider-configuration file for ProtempaSQLGenerator.
         */
        try {
            for (SQLGenerator candidateInstance : candidates) {
                logger.log(Level.FINER, 
                            "Checking compatibility of SQL generator {0}", 
                            candidateInstance.getClass().getName());
                String forcedSQLGenerator =
                        System.getProperty(
                        SQLGenUtil.SYSTEM_PROPERTY_FORCE_SQL_GENERATOR);

                if (forcedSQLGenerator != null) {
                    if (forcedSQLGenerator.equals(
                            candidateInstance.getClass().getName())) {
                        logger.log(Level.INFO,
                                "Forcing use of SQL generator {0}",
                                candidateInstance.getClass().getName());
                        candidateInstance.loadDriverIfNeeded();
                        candidateInstance.initialize(
                                this.backend.getRelationalDatabaseSpec(),
                                this.connectionSpec, this.backend);
                        logger.log(Level.FINE, "SQL generator {0} is loaded",
                                candidateInstance.getClass().getName());
                        return candidateInstance;
                    }
                } else {
                    /*
                     * We get a new connection for each compatibility check so that
                     * no state (or a closed connection!) is carried over.
                     */
                    Connection con = this.connectionSpec.getOrCreate();
                    try {
                        if (candidateInstance.loadDriverIfNeeded() &&
                                candidateInstance.checkCompatibility(con)) {
                            DatabaseMetaData metaData = con.getMetaData();
                            logCompatibility(logger, candidateInstance,
                                    metaData);
                            candidateInstance.initialize(
                                    this.backend.getRelationalDatabaseSpec(),
                                    this.connectionSpec, this.backend);
                            logger.log(Level.FINE, "SQL generator {0} is loaded",
                                    candidateInstance.getClass().getName());
                            return candidateInstance;
                        }
                        logger.log(Level.FINER,
                                "SQL generator {0} is not compatible", 
                                candidateInstance.getClass().getName());
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
            }
        } catch (ServiceConfigurationError sce) {
            throw new SQLGeneratorLoadException(
                    "Could not load SQL generators", sce);
        }
        throw new NoCompatibleSQLGeneratorException(
                "Could not find a SQL generator that is compatible with your database and available JDBC drivers");
    }

    private static void logCompatibility(Logger logger,
            SQLGenerator candidateInstance, DatabaseMetaData metaData)
            throws SQLException {
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER,
                    "{0} is compatible with database {1} ({2})",
                    new Object[]{candidateInstance.getClass().getName(),
                        metaData.getDatabaseProductName(),
                        metaData.getDatabaseProductVersion()});
            logger.log(Level.FINER, "{0} is compatible with driver {1} ({2})",
                    new Object[]{candidateInstance.getClass().getName(),
                        metaData.getDriverName(),
                        metaData.getDriverVersion()});
        }
    }
}
