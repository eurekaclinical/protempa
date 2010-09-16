package org.protempa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.protempa.proposition.Proposition;

/**
 * For a given proposition, the list of propositions that have been derived
 * from it.
 * 
 * @author Andrew Post
 */
public final class Derivations {
    private Proposition proposition;
    private final List<Proposition> derivations;

    Derivations() {
        this.derivations = new ArrayList<Proposition>();
    }

    /**
     * Sets the proposition of interest.
     *
     * @param proposition a {@link Proposition}.
     */
    void setProposition(Proposition proposition) {
        this.proposition = proposition;
    }

    /**
     * Gets the proposition of interest.
     *
     * @return a {@link Proposition}.
     */
    public Proposition getProposition() {
        return this.proposition;
    }

    /**
     * Adds a derived proposition.
     *
     * @param proposition a {@link Proposition}. Cannot be <code>null</code>.
     */
    void addDerivation(Proposition proposition) {
        assert proposition != null : "proposition cannot be null";
        this.derivations.add(proposition);
    }

    /**
     * Gets the derived propositions.
     *
     * @return an unmodifiable {@link List<Proposition>}.
     */
    public List<Proposition> getDerivations() {
        return Collections.unmodifiableList(this.derivations);
    }
}
