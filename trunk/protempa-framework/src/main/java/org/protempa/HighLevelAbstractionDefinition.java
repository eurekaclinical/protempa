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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import org.protempa.proposition.interval.Relation;

/**
 * Definition of an extended parameter that's a high-level abstraction.
 * 
 * @author Andrew Post
 */
public final class HighLevelAbstractionDefinition 
        extends AbstractAbstractionDefinition {

    private static final long serialVersionUID = -2434163106247371362L;
    private Offsets temporalOffset;
    private boolean concatenable;
    private transient Set<ExtendedPropositionDefinition> defs;
    private transient Set<String> defsAsList;
    private transient boolean defsAsListOutdated;
    private transient Map<List<TemporalExtendedPropositionDefinition>, Relation> defPairsMap;
    private boolean solid;

    public HighLevelAbstractionDefinition(String id) {
        super(id);
        initInstance();
        this.concatenable = true;
        this.solid = true;
    }

    private void initInstance() {
        defs = new HashSet<ExtendedPropositionDefinition>();
        defsAsList = new HashSet<String>();
        defsAsListOutdated = true;
        defPairsMap = new HashMap<List<TemporalExtendedPropositionDefinition>, Relation>();
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
     * Sets the relation between the two temporal extended proposition
     * definitions.
     *
     * @param lhsDef
     *            a {@link TemporalExtendedPropositionDefinition}.
     * @param rhsDef
     *            a {@link TemporalExtendedPropositionDefinition}.
     * @param relation
     *            a {@link Relation}.
     * @return <code>true</code> if setting the relation was successful,
     *         <code>false</code> otherwise (e.g., if one or both of the
     *         temporal extended proposition definitions is not already part of
     *         this abstraction definition, or if any of the arguments are
     *         <code>null</code>).
     */
    public boolean setRelation(TemporalExtendedPropositionDefinition lhsDef,
            TemporalExtendedPropositionDefinition rhsDef, Relation relation) {
        if (lhsDef == null || rhsDef == null || relation == null) {
            return false;
        }

        if (!defs.contains(lhsDef) || !defs.contains(rhsDef)) {
            return false;
        }

        List<TemporalExtendedPropositionDefinition> rulePair = Arrays.asList(
                lhsDef, rhsDef);

        defPairsMap.put(rulePair, relation);
        return true;
    }

    /**
     * Removes a relation between two temporal extended proposition definitions.
     *
     * @param lhsRule
     *            a {@link TemporalExtendedPropositionDefinition}.
     * @param rhsRule
     *            a {@link TemporalExtendedPropositionDefinition}.
     * @return <code>true</code> if removing the relation was successful,
     *         <code>false</code> otherwise (e.g., if either argument is
     *         <code>null</code>, the arguments are not part of this
     *         abstraction definition, or if no relation was set for them).
     */
    public boolean removeRelation(
            TemporalExtendedPropositionDefinition lhsRule,
            TemporalExtendedPropositionDefinition rhsRule) {
        List<TemporalExtendedPropositionDefinition> key = Arrays.asList(
                lhsRule, rhsRule);
        if (defPairsMap.remove(key) != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the extended proposition definitions that are part of this
     * abstraction definition.
     *
     * @return a {@link Set} of {@link ExtendedPropositionDefinition}s.
     */
    public Set<ExtendedPropositionDefinition> getExtendedPropositionDefinitions() {
        return Collections.unmodifiableSet(defs);
    }

    @Override
    public Set<String> getAbstractedFrom() {
        if (this.defsAsListOutdated) {
            this.defsAsList.clear();
            for (ExtendedPropositionDefinition epd : this.defs) {
                this.defsAsList.add(epd.getPropositionId());
            }
            this.defsAsListOutdated = false;
        }
        return Collections.unmodifiableSet(this.defsAsList);
    }

    /**
     * Adds an extended proposition definition.
     *
     * @param def
     *            an {@link ExtendedPropositionDefinition}.
     * @return <code>true</code> if successfully added, <code>false</code>
     *         otherwise (e.g., the {@link ExtendedPropositionDefinition} is
     *         <code>null</code> or is already part of this abstraction
     *         definition.
     */
    public boolean add(ExtendedPropositionDefinition def) {
        if (def != null && this.defs.add(def)) {
            recalculateChildren();
            this.defsAsListOutdated = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the relation between two temporal extended proposition definitions.
     *
     * @param lhsDef
     *            a {@link TemporalExtendedPropositionDefinition}.
     * @param rhsDef
     *            a {@link TemporalExtendedPropositionDefinition}.
     * @return a <code>Relation</code>, or <code>null</code> if none was
     *         found.
     */
    public Relation getRelation(TemporalExtendedPropositionDefinition lhsDef,
            TemporalExtendedPropositionDefinition rhsDef) {
        return this.defPairsMap.get(Arrays.asList(lhsDef, rhsDef));
    }

    public Relation getRelation(
            List<TemporalExtendedPropositionDefinition> epdPair) {
        return this.defPairsMap.get(epdPair);
    }

    /**
     * Removes all relations involving a temporal extended proposition
     * definition.
     *
     * @param def
     *            a {@link TemporalExtendedPropositionDefinition}.
     * @return <code>true</code> if all such relations were removed,
     *         <code>false</code> otherwise.
     */
    public boolean removeAllRelations(TemporalExtendedPropositionDefinition def) {
        for (Iterator<List<TemporalExtendedPropositionDefinition>> itr = this.defPairsMap.keySet().iterator(); itr.hasNext();) {
            List<TemporalExtendedPropositionDefinition> pair = itr.next();
            if (pair.get(0) == def || pair.get(1) == def) {
                itr.remove();
            }
        }
        return true;
    }

    /**
     * Removes an extended proposition definition, and all relations involving
     * it.
     *
     * @param def
     *            an {@link ExtendedPropositionDefinition}.
     * @return <code>true</code> if successful, <code>false</code>
     *         otherwise.
     */
    public boolean remove(ExtendedPropositionDefinition def) {
        if (def instanceof TemporalExtendedPropositionDefinition) {
            if (removeAllRelations((TemporalExtendedPropositionDefinition) def)) {
                return true;
            } else {
                return false;
            }
        }
        boolean result = defs.remove(def);
        if (result) {
            recalculateChildren();
            this.defsAsListOutdated = true;
        }
        return result;
    }

    /**
     * Gets the temporal extended proposition definitions involved in relations
     * as pairs.
     *
     * @return an unmodifiable {@link Set} of {@link List}s of two
     *         {@link TemporalExtendedPropositionDefinition}s, with the first
     *         element the left-hand side of a relation, and the second element
     *         the right-hand side of a relation.
     */
    public Set<List<TemporalExtendedPropositionDefinition>> getTemporalExtendedPropositionDefinitionPairs() {
        return Collections.unmodifiableSet(defPairsMap.keySet());
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeObject(defPairsMap);
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
        initInstance();

        Map<List<TemporalExtendedPropositionDefinition>, Relation> rulePairsMap = (Map<List<TemporalExtendedPropositionDefinition>, Relation>) s.readObject();

        for (List<TemporalExtendedPropositionDefinition> rulePair : rulePairsMap.keySet()) {
            add(rulePair.get(0));
            add(rulePair.get(1));
        }

        for (Map.Entry<List<TemporalExtendedPropositionDefinition>, Relation> me : rulePairsMap.entrySet()) {
            List<TemporalExtendedPropositionDefinition> rulep = me.getKey();

            setRelation(rulep.get(0), rulep.get(1), me.getValue());
        }
    }

    public Offsets getTemporalOffset() {
        return temporalOffset;
    }

    public void setTemporalOffset(Offsets temporalOffset) {
        Offsets old = temporalOffset;
        this.temporalOffset = temporalOffset;
        if (this.changes != null) {
            this.changes.firePropertyChange("temporalOffset", old,
                    this.temporalOffset);
        }
    }

    @Override
    public void reset() {
        super.reset();
        setTemporalOffset(null);
        initInstance();
    }

    /**
     * Sets whether this type of high-level abstraction is concatenable.
     *
     * @param concatenable
     *            <code>true</code> if concatenable, <code>false</code> if
     *            not.
     */
    public void setConcatenable(boolean concatenable) {
        this.concatenable = concatenable;
    }
    
    /**
     * Sets whether intervals of this type are solid, i.e., never hold over
     * properly overlapping intervals.
     * 
     * @param solid <code>true</code> or <code>false</code>.
     */
    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    /**
     * Returns whether this type of high-level abstraction is concatenable.
     *
     * The default for high-level abstractions is concatenable.
     *
     * @return <code>true</code> if concatenable, <code>false</code> if not.
     * @see org.protempa.PropositionDefinition#isConcatenable()
     */
    @Override
    public boolean isConcatenable() {
        return this.concatenable;
    }

    /**
     * Returns whether intervals of this type are solid, i.e., never hold over
     * properly overlapping intervals. By default, high-level abstraction 
     * intervals are solid.
     *
     * @return <code>true</code> or <code>false</code>.
     */
    @Override
    public boolean isSolid() {
        return this.solid;
    }

    @Override
    protected void recalculateChildren() {
        String[] old = this.children;
        Set<String> abstractedFrom = getAbstractedFrom();
        Set<String> directChildrenLocal = new HashSet<String>(abstractedFrom);
        String[] inverseIsA = getInverseIsA();
        for (String propId : inverseIsA) {
            directChildrenLocal.add(propId);
        }
        this.children = directChildrenLocal.toArray(
                new String[directChildrenLocal.size()]);
        if (this.changes != null) {
            this.changes.firePropertyChange(CHILDREN_PROPERTY, old,
                    this.children);
        }
    }

    @Override
    public String toString() {
        ReflectionToStringBuilder builder = 
                new ReflectionToStringBuilder(this);
        builder.setAppendTransients(true);
        return builder.toString();
    }
}
