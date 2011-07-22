package org.arp.javautil.datastore;

public final class DataStoreError extends RuntimeException {

    private static final long serialVersionUID = 5090296635307262179L;

    DataStoreError(Throwable t) {
        super(t);
    }
    
    DataStoreError(String message) {
        super(message);
    }
}
