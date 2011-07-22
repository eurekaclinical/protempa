package org.arp.javautil.datastore;

/**
 * @deprecated Replaced by {@link DataStoreError}
 */
@Deprecated
public class DatabaseError extends RuntimeException {
    private static final long serialVersionUID = 3861144884838037475L;

    DatabaseError(Throwable t) {
        super(t);
    }
}
