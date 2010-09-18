package org.protempa;

import java.io.Serializable;

/**
 *
 * @author Andrew Post
 */
public class ReferenceDefinition implements Serializable {
    private static final long serialVersionUID = -8746451343197890264L;
    private String name;
    private String propositionId;

    public ReferenceDefinition(String name, String propositionId) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        if (propositionId == null)
            throw new IllegalArgumentException("propositionId cannot be null");
        this.name = name;
        this.propositionId = propositionId;
    }

    public String getName() {
        return this.name;
    }

    public String getPropositionId() {
        return this.propositionId;
    }

}
