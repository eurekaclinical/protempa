package org.protempa.bp.commons.dsb;

import java.util.logging.Level;
import org.protempa.dsb.*;

import org.arp.javautil.sql.ConnectionSpec;
import org.arp.javautil.sql.DriverManagerConnectionSpec;
import org.protempa.backend.BackendInstanceSpec;

/**
 * Provides functionality for specifying connection info to a
 * relational database for access with the Java {@link java.sql.DriverManager}
 * API. This API assumes that the appropriate database driver is
 * already loaded through the JDBC 4.0 {@link java.util.ServiceLoader}-based
 * mechanism or some other way.
 *
 * FIXME this design is wrong -- the specifics of how the database is
 * connected to should be specified by composition not a superclass so that
 * we can exchange this for the {@link DataSource}, servlet, or JBoss mechanisms
 * easily
 * 
 * @author Andrew Post
 */
public abstract class DriverManagerAbstractSchemaAdaptor
        extends AbstractCommonsSchemaAdaptor {

    private ConnectionSpec connectionSpec;

    protected abstract String getDbUrl();

    protected abstract String getUsername();

    protected abstract String getPassword();

    protected abstract String getDriverClass();

    protected ConnectionSpec getConnectionSpec() {
        return this.connectionSpec;
    }

    /**
     * This initializes the connection pool. Must call with <code>super</code>
     * if you override.
     * 
     * @param config
     *            Expected properties: databaseDriver, databaseURL, username,
     *            password.
     */
    @Override
    public void initialize(BackendInstanceSpec config)
            throws SchemaAdaptorInitializationException {
        super.initialize(config);

        registerDriverIfDriverClassIsSpecified();

        this.connectionSpec = new DriverManagerConnectionSpec(getDbUrl(),
                getUsername(), getPassword());

        DSBUtil.logger().log(Level.FINE, "{0} initialized",
                getClass().getName());
    }

    private void registerDriverIfDriverClassIsSpecified() {
        String driverClass = getDriverClass();
        if (driverClass != null) {
            try {
                Class.forName(driverClass);
            } catch (ClassNotFoundException ex) {
                DSBUtil.logger().log(Level.WARNING, 
                    "Could not register database driver " + driverClass, ex);
            }
        }
    }

    @Override
    public final void close() {
		
    }

}
