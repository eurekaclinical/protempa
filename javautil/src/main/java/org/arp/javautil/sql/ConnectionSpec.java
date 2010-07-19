package org.arp.javautil.sql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

public interface ConnectionSpec {
    Connection getOrCreate() throws SQLException;
    Driver getDriver() throws SQLException;
}
