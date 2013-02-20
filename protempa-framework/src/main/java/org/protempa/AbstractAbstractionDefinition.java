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
package org.protempa;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * Definition of the constraints required to infer an abstract parameter.
 * 
 * @author Andrew Post
 */
public abstract class AbstractAbstractionDefinition 
        extends AbstractPropositionDefinition 
        implements AbstractionDefinition {

    private static final long serialVersionUID = 8075373039175465215L;
    private GapFunction gapFunction = GapFunction.DEFAULT;

    AbstractAbstractionDefinition(String id) {
        super(id);
    }

    @Override
    public GapFunction getGapFunction() {
        return gapFunction;
    }

    public void setGapFunction(GapFunction gapFunction) {
        if (gapFunction == null) {
            gapFunction = GapFunction.DEFAULT;
        }
        GapFunction old = this.gapFunction;
        this.gapFunction = gapFunction;
        if (this.changes != null) {
            this.changes.firePropertyChange("gapFunction", old, this.gapFunction);
        }
    }

    @Override
    public void reset() {
        super.reset();
        gapFunction = GapFunction.DEFAULT;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }
}
