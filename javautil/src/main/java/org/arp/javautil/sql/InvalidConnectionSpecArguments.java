package org.arp.javautil.sql;

/**
 *
 * @author Andrew Post
 */
public class InvalidConnectionSpecArguments extends Exception {

    InvalidConnectionSpecArguments(Throwable cause) {
        super(cause);
    }

    InvalidConnectionSpecArguments(String message, Throwable cause) {
        super(message, cause);
    }

    InvalidConnectionSpecArguments(String message) {
        super(message);
    }

    InvalidConnectionSpecArguments() {
    }

}
