package org.protempa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.arp.javautil.collections.Collections;
import org.protempa.proposition.Proposition;

/**
 *
 * @author Andrew Post
 */
class DerivationsBuilder {

    private Map<Proposition, List<Proposition>> derivations;

    DerivationsBuilder() {
        reset();
    }

    void reset() {
        this.derivations = new HashMap<Proposition, List<Proposition>>();
    }

    Map<Proposition, List<Proposition>> toDerivations() {
        return derivations;
    }

    void propositionAsserted(Proposition oldProposition,
            Proposition newProposition) {
        assert oldProposition != null : "old proposition cannot be null";
        assert newProposition != null : "new proposition cannot be null";
        Collections.putList(derivations, oldProposition, newProposition);
        Collections.putList(derivations, newProposition, oldProposition);
    }
}
