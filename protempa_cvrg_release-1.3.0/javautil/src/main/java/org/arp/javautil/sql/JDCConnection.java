/*
 * #%L
 * JavaUtil
 * %%
 * Copyright (C) 2012 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.arp.javautil.sql;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

/**
 * A wrapper driver that implements a connection pool. Adapted from the Java
 * Developer connection:
 * http://java.sun.com/developer/onlineTraining/Programming/JDCBook/conpool.html.
 * Updated for {@link Connection} API changes in Java 1.6. It now will not
 * compile with < 1.6.
 */
public class JDCConnection implements Connection {

    private JDCConnectionPool pool;

    private Connection conn;

    private boolean inuse;

    private long timestamp;

    public JDCConnection(Connection conn, JDCConnectionPool pool) {
            this.conn = conn;
            this.pool = pool;
            this.inuse = false;
            this.timestamp = 0;
    }

    public synchronized boolean lease() {
            if (inuse) {
                    return false;
            } else {
                    inuse = true;
                    timestamp = System.currentTimeMillis();
                    return true;
            }
    }

    public boolean validate() {
            try {
                    conn.getMetaData();
            } catch (Exception e) {
                    return false;
            }
            return true;
    }

    public boolean inUse() {
            return inuse;
    }

    public long getLastUse() {
            return timestamp;
    }

    public void close() throws SQLException {
            pool.returnConnection(this);
    }

    protected void expireLease() {
            inuse = false;
    }

    protected Connection getConnection() {
            return conn;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
            return conn.prepareStatement(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
            return conn.prepareCall(sql);
    }

    public Statement createStatement() throws SQLException {
            return conn.createStatement();
    }

    public String nativeSQL(String sql) throws SQLException {
            return conn.nativeSQL(sql);
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
            conn.setAutoCommit(autoCommit);
    }

    public boolean getAutoCommit() throws SQLException {
            return conn.getAutoCommit();
    }

    public void commit() throws SQLException {
            conn.commit();
    }

    public void rollback() throws SQLException {
            conn.rollback();
    }

    public boolean isClosed() throws SQLException {
            return conn.isClosed();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
            return conn.getMetaData();
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
            conn.setReadOnly(readOnly);
    }

    public boolean isReadOnly() throws SQLException {
            return conn.isReadOnly();
    }

    public void setCatalog(String catalog) throws SQLException {
            conn.setCatalog(catalog);
    }

    public String getCatalog() throws SQLException {
            return conn.getCatalog();
    }

    public void setTransactionIsolation(int level) throws SQLException {
            conn.setTransactionIsolation(level);
    }

    public int getTransactionIsolation() throws SQLException {
            return conn.getTransactionIsolation();
    }

    public SQLWarning getWarnings() throws SQLException {
            return conn.getWarnings();
    }

    public void clearWarnings() throws SQLException {
            conn.clearWarnings();
    }

    public Statement createStatement(int arg0, int arg1) throws SQLException {
            return conn.createStatement(arg0, arg1);
    }

    public Statement createStatement(int arg0, int arg1, int arg2)
                    throws SQLException {
            return conn.createStatement(arg0, arg1, arg2);
    }

    public int getHoldability() throws SQLException {
            return conn.getHoldability();
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
            return conn.getTypeMap();
    }

    public CallableStatement prepareCall(String arg0, int arg1, int arg2)
                    throws SQLException {
            return conn.prepareCall(arg0, arg1, arg2);
    }

    public CallableStatement prepareCall(String arg0, int arg1, int arg2,
                    int arg3) throws SQLException {
            return conn.prepareCall(arg0, arg1, arg2, arg3);
    }

    public PreparedStatement prepareStatement(String arg0, int arg1)
                    throws SQLException {
            return conn.prepareStatement(arg0, arg1);
    }

    public PreparedStatement prepareStatement(String arg0, int[] arg1)
                    throws SQLException {
            return conn.prepareStatement(arg0, arg1);
    }

    public PreparedStatement prepareStatement(String arg0, String[] arg1)
                    throws SQLException {
            return conn.prepareStatement(arg0, arg1);
    }

    public PreparedStatement prepareStatement(String arg0, int arg1, int arg2)
                    throws SQLException {
            return conn.prepareStatement(arg0, arg1, arg2);
    }

    public PreparedStatement prepareStatement(String arg0, int arg1, int arg2,
                    int arg3) throws SQLException {
            return conn.prepareStatement(arg0, arg1, arg2, arg3);
    }

    public void releaseSavepoint(Savepoint arg0) throws SQLException {
            conn.releaseSavepoint(arg0);
    }

    public void rollback(Savepoint arg0) throws SQLException {
            conn.rollback(arg0);
    }

    public void setHoldability(int arg0) throws SQLException {
            conn.setHoldability(arg0);
    }

    public Savepoint setSavepoint() throws SQLException {
            return conn.setSavepoint();
    }

    public Savepoint setSavepoint(String arg0) throws SQLException {
            return conn.setSavepoint(arg0);
    }

    public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
            conn.setTypeMap(arg0);
    }

    public void closeForReal() throws SQLException {
            conn.close();
    }

    public Clob createClob() throws SQLException {
        return conn.createClob();
    }

    public Blob createBlob() throws SQLException {
        return conn.createBlob();
    }

    public NClob createNClob() throws SQLException {
        return conn.createNClob();
    }

    public SQLXML createSQLXML() throws SQLException {
        return conn.createSQLXML();
    }

    public boolean isValid(int timeout) throws SQLException {
        return conn.isValid(timeout);
    }

    public void setClientInfo(String name, String value)
            throws SQLClientInfoException {
        conn.setClientInfo(name, value);
    }

    public void setClientInfo(Properties properties)
            throws SQLClientInfoException {
        conn.setClientInfo(properties);
    }

    public String getClientInfo(String name) throws SQLException {
        return conn.getClientInfo(name);
    }

    public Properties getClientInfo() throws SQLException {
        return conn.getClientInfo();
    }

    public Array createArrayOf(String typeName, Object[] elements)
            throws SQLException {
        return conn.createArrayOf(typeName, elements);
    }

    public Struct createStruct(String typeName, Object[] attributes)
            throws SQLException {
        return conn.createStruct(typeName, attributes);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return conn.unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return conn.isWrapperFor(iface);
    }
}
