package org.protempa;

import java.io.Serializable;

/**
 *
 * @author Andrew Post
 */
public class ReferenceDefinition implements Serializable {
    private static final long serialVersionUID = -8746451343197890264L;
    private String name;
    private String[] propositionIds;

    public ReferenceDefinition(String name, String[] propositionIds) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        ProtempaUtil.checkArray(propositionIds, "propositionIds");
        if (propositionIds.length == 0)
            throw new IllegalArgumentException(
                    "propositionIds must have at least one element");
        this.name = name;
        this.propositionIds = propositionIds.clone();
    }

    public String getName() {
        return this.name;
    }

    public String[] getPropositionIds() {
        return this.propositionIds.clone();
    }

}
