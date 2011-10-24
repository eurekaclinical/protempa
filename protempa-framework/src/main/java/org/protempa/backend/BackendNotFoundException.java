package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public class BackendNotFoundException extends ProtempaException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 907885917404315085L;

	BackendNotFoundException(String backendId) {
        super("No backend with id " + backendId + " was found");
    }

}
