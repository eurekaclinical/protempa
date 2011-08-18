package org.protempa.bp.commons.dsb.relationaldb;

/**
 *
 * @author Andrew Post
 */
public class SQLGeneratorLoadException extends Exception {

    public SQLGeneratorLoadException() {
    }

    public SQLGeneratorLoadException(Throwable cause) {
        super(cause);
    }

    public SQLGeneratorLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public SQLGeneratorLoadException(String message) {
        super(message);
    }

}
