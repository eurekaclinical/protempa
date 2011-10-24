package org.protempa.backend;

import org.protempa.ProtempaException;

/**
 *
 * @author Andrew Post
 */
public class BackendSpecNotFoundException extends ProtempaException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3415801270469253064L;

	BackendSpecNotFoundException(String id) {
        super("Backend Spec with id " + id + " could not be found.");
    }

    BackendSpecNotFoundException(String id, Throwable throwable) {
        super("Backend Spec with id " + id + " could not be found.", throwable);
    }

}
