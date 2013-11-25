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

import java.util.HashSet;
import java.util.Set;
import org.arp.javautil.arrays.Arrays;
import org.drools.util.StringUtils;

/**
 * A context is a state of affairs that, when interpreted over a time interval,
 * can change the interpretation of data or abstractions.
 *
 * @author Andrew Post
 */
public final class ContextDefinition extends AbstractPropositionDefinition
        implements TemporalPropositionDefinition {

    private static final long serialVersionUID = 1;
    
    private static final TemporalExtendedPropositionDefinition[]
            INDUCED_BY_DEFAULT = 
            new TemporalExtendedPropositionDefinition[0];
    private ContextOffset offset;
    private TemporalExtendedPropositionDefinition[] inducedBy;
    private String[] subContexts;
    private GapFunction gapFunction;
//    private Integer repeatEvery;
//    private Unit repeatEveryUnits;

    public ContextDefinition(String id) {
        super(id);
        this.offset = new ContextOffset();
        this.subContexts = StringUtils.EMPTY_STRING_ARRAY;
        this.gapFunction = GapFunction.DEFAULT;
        this.inducedBy = INDUCED_BY_DEFAULT;
    }

    @Override
    protected void recalculateChildren() {
        String[] old = this.children;
        Set<String> newChildren = new HashSet<>();
        Arrays.addAll(newChildren, getInverseIsA());
        Arrays.addAll(newChildren, this.subContexts);
        for (TemporalExtendedPropositionDefinition tepd : this.inducedBy) {
            newChildren.add(tepd.getPropositionId());
        }
        this.children = newChildren.toArray(new String[newChildren.size()]);
        if (this.changes != null) {
            this.changes.firePropertyChange(CHILDREN_PROPERTY, old,
                    this.children);
        }
    }

    @Override
    public boolean isConcatenable() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public void accept(PropositionDefinitionVisitor propositionVisitor) {
        propositionVisitor.visit(this);
    }

    @Override
    public void acceptChecked(
            PropositionDefinitionCheckedVisitor propositionVisitor)
            throws ProtempaException {
        propositionVisitor.visit(this);
    }

    public void setInducedBy(TemporalExtendedPropositionDefinition[] inducedBy) {
        if (inducedBy == null) {
            inducedBy = INDUCED_BY_DEFAULT;
        } else {
            this.inducedBy = inducedBy.clone();
        }
    }

    /**
     * Returns the id of the proposition definition that induces this context.
     *
     * @return a proposition id {@link String}.
     */
    public TemporalExtendedPropositionDefinition[] getInducedBy() {
        return this.inducedBy.clone();
    }

    /**
     * Returns the start and finish of an interval of this context relative to
     * its inducing interval.
     *
     * @return a temporal {@link ContextOffset}.
     */
    public ContextOffset getOffset() {
        return this.offset;
    }

    /**
     * Sets the start and finish of an interval of this context relative to its
     * inducing interval.
     *
     * @param offset a temporal {@link ContextOffset}.
     */
    public void setOffset(ContextOffset offset) {
        if (offset == null) {
            this.offset = new ContextOffset();
        } else {
            this.offset = offset;
        }
    }

    /**
     * Returns the ids of other context definitions that, together with this
     * one, have a specific meaning when their intervals intersect.
     *
     * @return an array of context proposition ids.
     */
    public String[] getSubContexts() {
        return this.subContexts.clone();
    }

    /**
     * Sets the ids of other context definintions that, together with this one,
     * have a specific meaning when their intervals intersect.
     *
     * @param subContextOf an array of context proposition ids.
     */
    public void setSubContexts(String[] subContextOf) {
        if (subContextOf == null) {
            this.subContexts = StringUtils.EMPTY_STRING_ARRAY;
        } else {
            ProtempaUtil.checkArrayForNullElement(subContextOf, 
                    "subContextOf");
            ProtempaUtil.checkArrayForDuplicates(subContextOf, "subContextOf");
            this.subContexts = subContextOf.clone();
        }
    }

//    /**
//     * If not <code>null</code>, will create new propositions shifted by the
//     * specified duration.
//     * 
//     * @return a duration, or <code>null</code> if not repeating. The default
//     * value is <code>null</code>.
//     */
//    public Integer getRepeatEvery() {
//        return this.repeatEvery;
//    }
//
//    /**
//     * Specify an offset to repeat the induced context periodically. The
//     * induced proposition will be repeated offset by the specified amount.
//     * 
//     * @param repeatEvery the repeat offset. Set to <code>null</code> if 
//     * repeating is not desired.
//     */
//    public void setRepeatEvery(Integer repeatEvery) {
//        this.repeatEvery = repeatEvery;
//    }
//
//    /**
//     * Returns the offset units to repeat the induced context periodically.
//     * 
//     * @return the offset units.
//     */
//    public Unit getRepeatEveryUnits() {
//        return this.repeatEveryUnits;
//    }
//
//    /**
//     * For specifying the units of the offset to repeat the induced context
//     * periodically.
//     * 
//     * @param repeatEveryUnits the offset units.
//     */
//    public void setRepeatEveryUnits(Unit repeatEveryUnits) {
//        this.repeatEveryUnits = repeatEveryUnits;
//    }
    public GapFunction getGapFunction() {
        return gapFunction;
    }

    public void setGapFunction(GapFunction gapFunction) {
        if (gapFunction == null) {
            this.gapFunction = GapFunction.DEFAULT;
        } else {
            this.gapFunction = gapFunction;
        }
    }
}
