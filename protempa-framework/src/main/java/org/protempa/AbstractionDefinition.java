package org.protempa;

import java.util.Set;

/**
 * Interface to definitions of the constraints required to infer an abstract
 * parameter.
 * 
 * @author Andrew Post
 * 
 */
public interface AbstractionDefinition extends PropositionDefinition,
        TemporalPropositionDefinition {

    public abstract String getDescription();

    /**
     * Returns all proposition ids from which this abstract parameter is
     * abstracted.
     *
     * @return an unmodifiable <code>Set</code> of proposition id
     *         <code>String</code>s. Guaranteed not null.
     */
    public abstract Set<String> getAbstractedFrom();

    public abstract GapFunction getGapFunction();
}
