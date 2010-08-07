package org.protempa.bp.commons.dsb;

import org.protempa.bp.commons.dsb.sqlgen.SQLGenerator;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.arp.javautil.serviceloader.ServiceLoader;
import org.arp.javautil.sql.ConnectionSpec;

/**
 * Factory for obtaining a {@link SQLGenerator} given the database and
 * JDBC driver that is in use.
 * 
 * @author Andrew Post
 */
class SQLGeneratorFactory {
    private ConnectionSpec connectionSpec;

    /**
     * Creates a new instance with a connection spec.
     * .
     * @param connectionSpec a {@link ConnectionSpec}, cannot be
     * <code>null</code>.
     */
    SQLGeneratorFactory(ConnectionSpec connectionSpec) {
        assert connectionSpec != null : "connectionSpec cannot be null!";
        this.connectionSpec = connectionSpec;
    }

    /**
     * Returns a newly-created {@link SQLGenerator}.
     *
     * @return a {@link SQLGenerator}.
     * @throws IOException if an error occurred loading SQL generator drivers.
     * @throws SQLException if an attempt to query the database for
     * compatibility information failed.
     * @throws ClassNotFoundException if an error occurred loading SQL
     * generator drivers.
     * @throws InstantiationException if an error occurred loading SQL
     * generator drivers.
     * @throws IllegalAccessException if an error occurred loading SQL
     * generator drivers.
     * @throws SQLGeneratorNotFoundException if no compatible SQL generator
     * driver could be found.
     */
    SQLGenerator newInstance() throws IOException, SQLException,
            ClassNotFoundException, InstantiationException,
            IllegalAccessException, SQLGeneratorNotFoundException {

        List<Class<? extends SQLGenerator>> candidates =
                ServiceLoader.load(SQLGenerator.class);
        for (Class<? extends SQLGenerator> candidate : candidates) {
            SQLGenerator candidateInstance = candidate.newInstance();
            candidateInstance.loadDriverIfNeeded();
            /*
             * We get a new connection for each compatibility check so that
             * no state (or a closed connection!) is carried over.
             */
            Connection con = this.connectionSpec.getOrCreate();
            try {
                if (candidateInstance.checkCompatibility(con))
                    return candidateInstance;
            } finally {
                con.close();
            }
        }
        throw new SQLGeneratorNotFoundException();
    }
}
