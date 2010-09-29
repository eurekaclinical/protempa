package org.protempa;

import java.util.EventObject;

/**
 *
 * @author Andrew Post
 */
public final class UnrecoverableBackendErrorEvent extends EventObject {
    private static final long serialVersionUID = 8614376655840239013L;

    public UnrecoverableBackendErrorEvent(Object o) {
        super(o);
    }
}
