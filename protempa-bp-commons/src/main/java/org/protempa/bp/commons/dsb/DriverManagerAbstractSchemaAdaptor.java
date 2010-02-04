package org.protempa.bp.commons.dsb;

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
 * @author arpost
 */
public abstract class DriverManagerAbstractSchemaAdaptor
        extends AbstractCommonsSchemaAdaptor {

    protected ConnectionSpec creator;

    protected abstract String getDbUrl();

    protected abstract String getUsername();

    protected abstract String getPassword();

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

        creator = new DriverManagerConnectionSpec(getDbUrl(), getUsername(),
                getPassword());

        DSBUtil.logger().fine(getClass().getName() + " initialized");
    }

    @Override
    public final void close() {
		
    }

}
