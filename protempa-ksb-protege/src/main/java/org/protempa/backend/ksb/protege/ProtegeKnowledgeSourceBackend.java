/*
 * #%L
 * Protempa Protege Knowledge Source Backend
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
package org.protempa.backend.ksb.protege;

import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.event.ProjectListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import org.apache.commons.lang3.ArrayUtils;
import org.drools.util.StringUtils;
import org.protempa.AbstractionDefinition;
import org.protempa.ContextDefinition;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.TemporalPropositionDefinition;
import org.protempa.TermSubsumption;
import org.protempa.ValueSet;
import org.protempa.backend.AbstractCommonsKnowledgeSourceBackend;
import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.KnowledgeSourceBackendInitializationException;
import org.protempa.proposition.value.AbsoluteTimeUnit;
import org.protempa.proposition.value.RelativeHourUnit;
import org.protempa.proposition.value.Unit;
import org.protempa.proposition.value.ValueType;
import org.protempa.query.And;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Abstract class for converting a Protege knowledge base into a PROTEMPA
 * knowledge base.
 *
 * @author Andrew Post
 */
public abstract class ProtegeKnowledgeSourceBackend
        extends AbstractCommonsKnowledgeSourceBackend
        implements ProjectListener {

    private ConnectionManager cm;
    private Units units;
    private InstanceConverterFactory instanceConverterFactory;

    private static enum Units {

        ABSOLUTE(AbsoluteTimeUnit.class), RELATIVE_HOURS(RelativeHourUnit.class);
        private Class<? extends Unit> unitClass;

        Units(Class<? extends Unit> unitClass) {
            this.unitClass = unitClass;
        }

        Class<? extends Unit> getUnitClass() {
            return this.unitClass;
        }
    };

    protected ProtegeKnowledgeSourceBackend() {
    }

    @Override
    public void initialize(BackendInstanceSpec config)
            throws BackendInitializationException {
        super.initialize(config);
        if (this.cm == null) {
            ConnectionManager pcm = initConnectionManager(config);

            pcm.init();
            this.cm = pcm;
            this.instanceConverterFactory = new InstanceConverterFactory(pcm);
        }
    }

    abstract ConnectionManager initConnectionManager(
            BackendInstanceSpec configuration)
            throws KnowledgeSourceBackendInitializationException;

    ConnectionManager getConnectionManager() {
        return this.cm;
    }

    protected void initUnits(String unitsStr)
            throws KnowledgeSourceBackendInitializationException {
        if (unitsStr != null) {
            try {
                this.units = Units.valueOf(unitsStr);
            } catch (Exception e) {
                throw new KnowledgeSourceBackendInitializationException(
                        "Invalid units supplied: " + unitsStr);
            }
        } else {
            Util.logger().fine("No UNITS supplied, using ABSOLUTE");
        }
        if (this.units == null) {
            this.units = Units.ABSOLUTE;
        }
    }

    @Override
    public void close() {
        if (this.cm != null) {
            this.cm.close();
            this.cm = null;
        }
        if (this.instanceConverterFactory != null) {
            this.instanceConverterFactory.reset();
            this.instanceConverterFactory = null;
        }
    }

    @Override
    public PropositionDefinition readPropositionDefinition(String name)
            throws KnowledgeSourceReadException {
        Instance instance = this.cm.getInstance(name);
        if (instance != null) {
            PropositionConverter converter = 
                    this.instanceConverterFactory.getInstance(instance);
            assert converter != null : 
                    "no converter for proposition definintion " + name;
            return converter.convert(instance, this);
        } else {
            return null;
        }
    }

    @Override
    public AbstractionDefinition readAbstractionDefinition(String name)
            throws KnowledgeSourceReadException {
        Instance instance = this.cm.getInstance(name);
        AbstractionConverter ac =
                this.instanceConverterFactory.getAbstractionInstance(instance);
        if (ac == null) {
            return null;
        } else {
            return ac.convert(instance, this);
        }
    }

    @Override
    public String[] getPropositionsByTerm(String termId)
            throws KnowledgeSourceReadException {
        return collectAssociatedNames(termId, "termProposition");
    }

    @Override
    public List<String> getPropositionsByTermSubsumption(
            And<TermSubsumption> termIds) throws KnowledgeSourceReadException {
        List<String> result = new ArrayList<>();
        List<Set<String>> propIdSets = new ArrayList<>();

        Slot termPropositionSlot = this.cm.getSlot("termProposition");

        // collects the set of proposition IDs for each term subsumption
        for (TermSubsumption ts : termIds.getAnded()) {
            Set<String> subsumpPropIds = new HashSet<>();
            for (String termId : ts.getTerms()) {
                Instance termInstance = this.cm.getInstance(termId);
                if (termInstance != null) {
                    Collection<?> props = cm.getOwnSlotValues(termInstance,
                            termPropositionSlot);
                    Iterator<?> it = props.iterator();
                    while (it.hasNext()) {
                        Instance prop = (Instance) it.next();
                        subsumpPropIds.add(prop.getName());
                    }
                }
            }
            propIdSets.add(subsumpPropIds);
        }

        Set<String> matchingPropIds =
                org.arp.javautil.collections.Collections.intersection(
                propIdSets);
        result.addAll(matchingPropIds);

        return result;
    }

    /**
     * Returns the Cls from Protege if a matching Cls is found with the given
     * ancestor
     *
     * @param name Name of the class to fetch
     * @param superClass name of the anscestor
     * @return A Cls containing the given class name
     * @throws KnowledgeSourceReadException
     */
    private Cls readClass(String name, String superClass)
            throws KnowledgeSourceReadException {
        Cls cls = this.cm.getCls(name);
        Cls superCls = this.cm.getCls(superClass);
        if (cls != null && cls.hasSuperclass(superCls)) {
            return cls;
        } else {
            return null;
        }
    }

    private String[] collectAssociatedNames(String id, String slotName) throws KnowledgeSourceReadException {
        Instance instance = cm.getInstance(id);
        return collectAssociatedInstanceNames(instance, slotName);
    }

    private String[] collectAssociatedInstanceNames(Instance instance, String slotName) throws KnowledgeSourceReadException {
        String[] result;
        if (instance != null) {
            Collection<Instance> children =
                    (Collection<Instance>) cm.getOwnSlotValues(instance, slotName);
            result = collectInstanceNames(children);
        } else {
            result = StringUtils.EMPTY_STRING_ARRAY;
        }
        return result;
    }

    private String[] collectInstanceNames(Collection<Instance> instances) {
        String[] result = new String[instances.size()];
        int i = 0;
        for (Iterator<?> itr = instances.iterator(); itr.hasNext();) {
            Instance child = (Instance) itr.next();
            result[i++] = child.getName();
        }
        return result;
    }

    /*
     * PROJECT CHANGED EVENTS. Protege calls projectClosed() when a remote
     * project has changed.
     */
    @Override
    public void formChanged(ProjectEvent arg0) {
        this.instanceConverterFactory.reset();
        fireKnowledgeSourceBackendUpdated();
    }

    @Override
    public void projectClosed(ProjectEvent arg0) {
        this.instanceConverterFactory.reset();
        fireKnowledgeSourceBackendUpdated();
    }

    @Override
    public void projectSaved(ProjectEvent arg0) {
        this.instanceConverterFactory.reset();
        fireKnowledgeSourceBackendUpdated();
    }

    @Override
    public void runtimeClsWidgetCreated(ProjectEvent arg0) {
        this.instanceConverterFactory.reset();
        fireKnowledgeSourceBackendUpdated();
    }

    Unit parseUnit(String protegeUnitStr) {
        switch (this.units) {
            case ABSOLUTE:
                return Util.ABSOLUTE_DURATION_MULTIPLIER.get(protegeUnitStr);
            case RELATIVE_HOURS:
                return Util.RELATIVE_HOURS_DURATION_MULTIPLIER.get(protegeUnitStr);
            default:
                assert false : this.units;
                return null;
        }
    }

    @Override
    public ValueSet readValueSet(String id)
            throws KnowledgeSourceReadException {
        Cls cls = readClass(id, "Value");
        if (cls == null) {
            return null;
        } else {
            ValueType valueType = Util.parseValueSet(cls);
            assert valueType != null : "Could not find value type for " + id;
            return Util.parseValueSet(cls, valueType, cm, this);
        }
    }

    @Override
    public String[] readAbstractedInto(String propId) throws KnowledgeSourceReadException {
        Instance instance = this.cm.getInstance(propId);
        Slot abstractedIntoSlot = this.cm.getSlot("abstractedInto");
        Collection<Instance> abstractedIntos = (Collection<Instance>) this.cm.getOwnSlotValues(instance, abstractedIntoSlot);
        String[] result = new String[abstractedIntos.size()];
        int i = 0;
        for (Instance inst : abstractedIntos) {
            result[i++] = inst.getName();
        }
        return result;
    }

    @Override
    public String[] readIsA(String propId) throws KnowledgeSourceReadException {
        Instance instance = this.cm.getInstance(propId);
        Slot isASlot = this.cm.getSlot("isA");
        Collection<Instance> isAs = (Collection<Instance>) this.cm.getOwnSlotValues(instance, isASlot);
        String[] result = new String[isAs.size()];
        int i = 0;
        for (Instance inst : isAs) {
            result[i++] = inst.getName();
        }
        return result;
    }
    
    @Override
    public ContextDefinition readContextDefinition(String id) throws KnowledgeSourceReadException {
        return null;
    }

    @Override
    public TemporalPropositionDefinition readTemporalPropositionDefinition(String name) throws KnowledgeSourceReadException {
        Instance instance = this.cm.getInstance(name);
        TemporalPropositionConverter ac =
                this.instanceConverterFactory.getTemporalPropositionInstance(instance);
        if (ac == null) {
            return null;
        } else {
            return ac.convert(instance, this);
        }
    }

    @Override
    public String[] readInduces(String propId) throws KnowledgeSourceReadException {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

    @Override
    public String[] readSubContextOfs(String propId) throws KnowledgeSourceReadException {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    
    
    public List<String> getKnowledgeSourceSearchResults(String searchKey) throws KnowledgeSourceReadException {
    	return this.cm.searchInstancesContainingKey(searchKey);
       
    }
}
