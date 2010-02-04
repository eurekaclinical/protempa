package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public class BackendNotFoundException extends ProtempaException {
    BackendNotFoundException(String backendId) {
        super("No backend with id " + backendId + " was found");
    }

}
