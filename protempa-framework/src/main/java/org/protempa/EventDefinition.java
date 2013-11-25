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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;


/**
 * Defines external acts upon an entity such as a patient.
 * 
 * @author Andrew Post
 * 
 */
public final class EventDefinition extends AbstractPropositionDefinition
        implements TemporalPropositionDefinition {

    private static final long serialVersionUID = 5251628049452634144L;

    public EventDefinition(String id) {
        super(id);
    }

    @Override
    public void accept(PropositionDefinitionVisitor processor) {
        if (processor == null) {
            throw new IllegalArgumentException("processor cannot be null.");
        }
        processor.visit(this);
    }

    @Override
    public void acceptChecked(PropositionDefinitionCheckedVisitor processor)
            throws ProtempaException {
        if (processor == null) {
            throw new IllegalArgumentException("processor cannot be null.");
        }
        processor.visit(this);
    }

    /**
     * By definition, events are not concatenable.
     *
     * @return <code>false</code>.
     * @see org.protempa.PropositionDefinition#isConcatenable()
     */
    @Override
    public boolean isConcatenable() {
        return false;
    }

    /**
     * Returns whether intervals of this type are solid, i.e., never hold over
     * properly overlapping intervals. By definition, event intervals are not 
     * solid because events of the same type can and do overlap in time.
     *
     * @return <code>false</code>.
     */
    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    protected void recalculateChildren() {
        String[] old = this.children;
        String[] inverseIsA = getInverseIsA();
        this.children = inverseIsA;
        if (this.changes != null) {
            this.changes.firePropertyChange(CHILDREN_PROPERTY, old,
                    this.children);
        }
    }
}
