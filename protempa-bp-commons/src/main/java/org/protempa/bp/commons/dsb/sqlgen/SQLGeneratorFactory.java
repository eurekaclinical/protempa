package org.protempa.bp.commons.dsb.sqlgen;

import org.protempa.bp.commons.dsb.SQLGenerator;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arp.javautil.serviceloader.ServiceLoader;
import org.arp.javautil.sql.ConnectionSpec;
import org.protempa.bp.commons.dsb.RelationalDatabaseDataSourceBackend;

/**
 * Factory for obtaining a {@link SQLGenerator} given the database and
 * JDBC driver that is in use.
 * 
 * @author Andrew Post
 */
public class SQLGeneratorFactory {

    private ConnectionSpec connectionSpec;
    private RelationalDatabaseSpec relationalDatabaseSpec;
    private RelationalDatabaseDataSourceBackend backend;

    /**
     * Creates a new instance with a connection spec.
     * .
     * @param connectionSpec a {@link ConnectionSpec}, cannot be
     * <code>null</code>.
     */
    public SQLGeneratorFactory(ConnectionSpec connectionSpec,
            RelationalDatabaseSpec relationalDatabaseSpec,
            RelationalDatabaseDataSourceBackend backend) {
        assert connectionSpec != null : "connectionSpec cannot be null!";
        assert relationalDatabaseSpec != null :
                "relationalDatabaseSpec cannot be null";
        assert backend != null : "backend cannot be null!";
        this.connectionSpec = connectionSpec;
        this.relationalDatabaseSpec = relationalDatabaseSpec;
        this.backend = backend;
    }

    /**
     * Returns a newly-created {@link SQLGenerator}.
     *
     * @return a {@link SQLGenerator}.
     * @throws IOException if an error occurred loading SQL generator drivers.
     * @throws SQLException if an attempt to query the database for
     * compatibility information failed.
     * @throws ClassNotFoundException if a SQL generator could not be loaded.
     * @throws InstantiationException if an error occurred loading SQL
     * generator drivers.
     * @throws IllegalAccessException if an error occurred loading SQL
     * generator drivers.
     * @throws SQLGeneratorNotFoundException if no compatible SQL generator
     * could be found.
     */
    public SQLGenerator newInstance() throws SQLException,
            SQLGeneratorNotFoundException,
            SQLGeneratorLoadException {
        Logger logger = SQLGenUtil.logger();
        logger.fine("Loading a compatible SQL generator");
        List<Class<? extends ProtempaSQLGenerator>> candidates;
        try {
            candidates = ServiceLoader.load(ProtempaSQLGenerator.class);
        } catch (Exception ex) {
            throw new SQLGeneratorLoadException(
                    "Could not load SQL generators", ex);
        }
        for (Class<? extends ProtempaSQLGenerator> candidate : candidates) {
            ProtempaSQLGenerator candidateInstance;
            try {
                candidateInstance = candidate.newInstance();
            } catch (Exception ex) {
                throw new SQLGeneratorLoadException(
                        "Could not create a new instance of SQL generator "
                        + candidate.getName(), ex);
            }
            candidateInstance.loadDriverIfNeeded();
            /*
             * We get a new connection for each compatibility check so that
             * no state (or a closed connection!) is carried over.
             */
            Connection con = this.connectionSpec.getOrCreate();
            try {
                if (candidateInstance.checkCompatibility(con)) {
                    DatabaseMetaData metaData = con.getMetaData();
                    if (logger.isLoggable(Level.FINE)) {
                        logger.log(Level.FINE,
                                "{0} is compatible with database {1} ({2})",
                                new Object[]{candidateInstance.getClass().getName(),
                                    metaData.getDatabaseProductName(),
                                    metaData.getDatabaseProductVersion()});
                        logger.log(Level.FINE,
                                "{0} is compatible with driver {1} ({2})",
                                new Object[]{candidateInstance.getClass().getName(),
                                    metaData.getDriverName(),
                                    metaData.getDriverVersion()});
                    }
                    candidateInstance.initialize(this.relationalDatabaseSpec,
                            this.connectionSpec, this.backend);
                    logger.fine("SQL generator loaded");
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
        throw new SQLGeneratorNotFoundException();
    }
}
