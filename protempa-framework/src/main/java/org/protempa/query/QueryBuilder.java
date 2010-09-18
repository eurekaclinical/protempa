package org.protempa.query;

/**
 *
 * @author Andrew Post
 */
public interface QueryBuilder {
    Query build() throws QueryBuildException;
}
