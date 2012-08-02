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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * 
 * @author Andrew Post
 * 
 */
public final class SliceDefinition extends AbstractAbstractionDefinition {

    private static final long serialVersionUID = 3339167247610447388L;
    private final Set<String> abstractedFrom;
    /**
     * The lower limit of the range of this slice (inclusive).
     */
    private int minIndex;
    /**
     * The upper limit of the range of this slice (exclusive).
     */
    private int maxIndex = Integer.MAX_VALUE;

    public SliceDefinition(String id) {
        super(id);
        this.abstractedFrom = new HashSet<String>(2);
    }

    /**
     * Sets the lower limit of the range for this slice (inclusive).
     *
     * @param minIndex
     *            the lower limit of the range for this slice (inclusive).
     */
    public void setMinIndex(int minIndex) {
        if (minIndex >= 0) {
            this.minIndex = minIndex;
        }
    }

    /**
     * Gets the lower limit of the range for this slice (inclusive). The default
     * value is <code>0</code>.
     *
     * @return the lower limit of the range for this slice (inclusive).
     */
    public int getMinIndex() {
        return minIndex;
    }

    /**
     * Sets the upper limit of the range for this slice (exclusive).
     *
     * @param maxIndex
     *            the upper limit of the range for this slice (exclusive).
     */
    public void setMaxIndex(int maxIndex) {
        if (maxIndex >= 0) {
            this.maxIndex = maxIndex;
        }
    }

    /**
     * Gets the upper limit of the range for this slice (exclusive). The default
     * value is <code>Integer.MAX_VALUE</code>.
     *
     * @return the upper limit of the range for this slice (exclusive).
     */
    public int getMaxIndex() {
        return maxIndex;
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

    @Override
    public Set<String> getAbstractedFrom() {
        return Collections.unmodifiableSet(this.abstractedFrom);
    }

    /**
     * Adds a proposition id from which this slice definition is abstracted.
     *
     * @param propId
     *            a proposition id <code>String</code> for an abstract
     *            parameter definition.
     */
    public boolean addAbstractedFrom(String propId) {
        if (propId != null) {
            boolean result = this.abstractedFrom.add(propId);
            if (result) {
                recalculateChildren();
            }
            return result;
        } else {
            return false;
        }
    }

    @Override
    public void reset() {
        super.reset();
        minIndex = 0;
        maxIndex = Integer.MAX_VALUE;
        abstractedFrom.clear();
        recalculateChildren();
    }

    /**
     * By definition, slice abstractions are not concatenable.
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
     * properly overlapping intervals. By definition, slice abstraction 
     * intervals are not solid.
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
        this.directChildren = this.abstractedFrom.toArray(new String[this.abstractedFrom.size()]);
        if (this.changes != null) {
            this.changes.firePropertyChange(CHILDREN_PROPERTY, old,
                    this.directChildren);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append("minIndex", this.minIndex).append("maxIndex", this.maxIndex).append("abstractedFrom", this.abstractedFrom).toString();
    }
}
