package org.arp.javautil.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Implements getting a connection to a relational database using Java's
 * {@link DataSource} API.
 *
 * @author Andrew Post
 * @see InitialContext
 */
public class DataSourceConnectionSpec implements ConnectionSpec {
    private String user;
    private String password;
    private DataSource dataSource;

    /**
     * Creates an instance that will get connections from the database with the
     * specified JNDI name.
     *
     * @param jndiName a JNDI name {@link String}.
     * @throws NamingException if the specified JNDI name is invalid.
     */
    public DataSourceConnectionSpec(String jndiName) throws NamingException {
        this(jndiName, null, null);
    }

    /**
     * Creates an instance that will get connections from the database with the
     * specified JNDI name using the specified username and password. No
     * environment properties are supplied when looking up the JNDI name. See
     * the javadoc for {@link InitialContext} for a description of
     * environment properties.
     * 
     * @param jndiName a JNDI name {@link String}.
     * @param user a username {@link String}.
     * @param password a password {@link String}.
     * @throws NamingException if an error occurs during JNDI lookup.
     */
    public DataSourceConnectionSpec(String jndiName, 
            String user, String password) throws NamingException {
        this(jndiName, null, user, password);
    }

    /**
     * Creates an instance that will get connections from the database with the
     * specified JNDI name using the specified username and password. The
     * specified environment will be used when looking up the JNDI name. See
     * the javadoc for {@link InitialContext} for a description of
     * environment properties.
     *
     * @param jndiName a JNDI name {@link String}.
     * @param environment a {@link Hashtable} of environment properties.
     * @param user a username {@link String}.
     * @param password a password {@link String}.
     * @throws NamingException if an error occurs during JNDI lookup.
     */
    public DataSourceConnectionSpec(String jndiName,
            Hashtable<?,?> environment,
            String user, String password) throws NamingException {
        if (jndiName == null)
            throw new IllegalArgumentException("jndiName cannot be null");
        this.user = user;
        this.password = password;

        InitialContext ctx = new InitialContext(environment);
        try {
            this.dataSource = (DataSource) ctx.lookup(jndiName);
        } finally {
            ctx.close();
        }
    }

    /**
     * Creates a database connection or gets an existing connection with
     * the JNDI name, username and password specified in the constructor.
     *
     * @return a {@link Connection}.
     *
     * @throws SQLException if an error occurred creating/getting a
     * {@link Connection}, possibly because the JNDI name, username and/or
     * password are invalid.
     */
    @Override
    public Connection getOrCreate() throws SQLException {
        if (this.user == null && this.password == null)
            return this.dataSource.getConnection();
        else
            return this.dataSource.getConnection(this.user, this.password);
    }

    /**
     * Gets the specified data source.
     *
     * @return a {@link DataSource}.
     */
    public DataSource getDataSource() {
        return this.dataSource;
    }

    @Override
    public void accept(ConnectionSpecVisitor connectionSpecVisitor) {
        connectionSpecVisitor.visit(this);
    }

}
