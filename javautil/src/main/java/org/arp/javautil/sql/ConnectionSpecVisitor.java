package org.arp.javautil.sql;

/**
 *
 * @author Andrew Post
 */
public interface ConnectionSpecVisitor {
    void visit(DataSourceConnectionSpec connectionSpec);

    void visit(DriverManagerConnectionSpec connectionSpec);
}
