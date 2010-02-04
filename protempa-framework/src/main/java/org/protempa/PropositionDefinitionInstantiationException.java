package org.protempa;

/**
 *
 * @author Andrew Post
 */
public class PropositionDefinitionInstantiationException 
        extends ProtempaException {
    private static final long serialVersionUID = 478415270332626688L;

    public PropositionDefinitionInstantiationException(Throwable cause) {
        super(cause);
    }

    public PropositionDefinitionInstantiationException(String message) {
        super(message);
    }

    public PropositionDefinitionInstantiationException(String message,
            Throwable cause) {
        super(message, cause);
    }

    public PropositionDefinitionInstantiationException() {
    }
    
}
