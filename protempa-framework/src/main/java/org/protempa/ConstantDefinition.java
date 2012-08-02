/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 Emory University
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

/**
 *
 * @author Andrew Post
 */
public final class ConstantDefinition extends AbstractPropositionDefinition {

    private static final long serialVersionUID = 727799438356160581L;

    public ConstantDefinition(KnowledgeBase kb, String id) {
        super(kb, id);

        kb.addConstantDefinition(this);
    }

    /**
     * By definition, constants are not concatenable.
     *
     * @return <code>false</code>.
     */
    @Override
    public boolean isConcatenable() {
        return false;
    }

    /**
     * By definition, there can be multiple constants with the same id.
     *
     * @return <code>false</code>.
     */
    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    protected void recalculateChildren() {
        String[] old = this.directChildren;
        this.directChildren = getInverseIsA();
        if (this.changes != null) {
            this.changes.firePropertyChange(CHILDREN_PROPERTY, old,
                    this.directChildren);
        }
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
}
