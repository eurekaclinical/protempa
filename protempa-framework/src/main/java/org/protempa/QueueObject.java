package org.protempa;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2015 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

/**
 *
 * @author arpost
 */
final class QueueObject {
    List<Proposition> propositions;
    Map<Proposition, Set<Proposition>> forwardDerivations;
    Map<Proposition, Set<Proposition>> backwardDerivations;
    String keyId;
    Map<UniqueId, Proposition> refs;

    QueueObject(String keyId, List<Proposition> propositions, 
            Map<Proposition, Set<Proposition>> forwardDerivations, 
            Map<Proposition, Set<Proposition>> backwardDerivations, Map<UniqueId, Proposition> refs) {
        this.propositions = propositions;
        this.forwardDerivations = forwardDerivations;
        this.backwardDerivations = backwardDerivations;
        this.keyId = keyId;
        this.refs = refs;
    }

    QueueObject() {
    }
    
}
