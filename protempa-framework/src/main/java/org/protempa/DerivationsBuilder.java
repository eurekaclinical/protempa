package org.protempa;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arp.javautil.collections.Collections;
import org.protempa.proposition.Proposition;

/**
 *
 * @author Andrew Post
 */
final class DerivationsBuilder implements Serializable {

    private static final long serialVersionUID = -2064122362760283390L;

    private Map<Proposition, List<Proposition>> forwardDerivations;
    private Map<Proposition, List<Proposition>> backwardDerivations;

    DerivationsBuilder() {
        reset();
    }

    void reset() {
        this.forwardDerivations = 
                new HashMap<Proposition, List<Proposition>>();
        this.backwardDerivations = 
                new HashMap<Proposition, List<Proposition>>();
    }
    
    Map<Proposition, List<Proposition>> toForwardDerivations() {
        return this.forwardDerivations;
    }
    
    Map<Proposition, List<Proposition>> toBackwardDerivations() {
        return this.backwardDerivations;
    }
    
    List<Proposition> propositionRetractedForward(Proposition proposition) {
        return this.forwardDerivations.remove(proposition);
    }
    
    void propositionReplaceForward(Proposition prop, Proposition oldProp, 
            Proposition newProp) {
        List<Proposition> props = this.forwardDerivations.get(prop);
        props.remove(oldProp);
        props.add(newProp);
    }
    
    void propositionReplaceBackward(Proposition prop, Proposition oldProp, 
            Proposition newProp) {
        List<Proposition> props = this.backwardDerivations.get(prop);
        props.remove(oldProp);
        props.add(newProp);
    }
    
    List<Proposition> propositionRetractedBackward(Proposition proposition) {
        return this.backwardDerivations.remove(proposition);
    }

    void propositionAsserted(Proposition oldProposition,
            Proposition newProposition) {
        assert oldProposition != null : "old proposition cannot be null";
        assert newProposition != null : "new proposition cannot be null";
        Collections.putList(this.forwardDerivations, oldProposition, 
                newProposition);
        Collections.putList(this.backwardDerivations, newProposition, 
                oldProposition);
    }
    
    void propositionAssertedBackward(Proposition oldProposition,
            Proposition newProposition) {
        assert oldProposition != null : "old proposition cannot be null";
        assert newProposition != null : "new proposition cannot be null";
        Collections.putList(this.backwardDerivations, newProposition, 
                oldProposition);
    }
}
