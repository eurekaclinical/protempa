/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
package org.protempa.proposition.stats;

import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.value.Unit;

/**
 * Calculates survival based on temporal propositions' maxlength.
 * 
 * @author Andrew Post
 * 
 */
public final class MaxLengthKaplanMeyer extends AbstractKaplanMeyer {

    public MaxLengthKaplanMeyer() {
        super();
    }

    public MaxLengthKaplanMeyer(Unit targetLengthUnit) {
        super(targetLengthUnit);
    }

    @Override
    protected Long length(TemporalProposition temporalProposition,
            Unit targetLengthUnit) {
        return temporalProposition.getInterval().maxLengthIn(targetLengthUnit);
    }
}
