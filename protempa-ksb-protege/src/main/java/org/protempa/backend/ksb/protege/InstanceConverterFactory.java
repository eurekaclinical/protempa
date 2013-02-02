/*
 * #%L
 * Protempa Protege Knowledge Source Backend
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
package org.protempa.backend.ksb.protege;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.proposition.Proposition;

/**
 * Factory for constructing a PROTEMPA proposition definition from a Protege
 * proposition.
 *
 * @author Andrew Post
 */
final class InstanceConverterFactory {

    private final ConnectionManager connectionManager;
    private final TemporalPropositionConverter primitiveParameterConverter;
    private final TemporalPropositionConverter eventConverter;
    private final PropositionConverter constantConverter;
    private final AbstractionConverter lowLevelAbstractionConverter;
    private final AbstractionConverter sliceConverter;
    private final AbstractionConverter highLevelAbstractionConverter;
    private final AbstractionConverter pairAbstractionConverter;
    private final TemporalPropositionConverter contextConverter;
    private Map<Cls, PropositionConverter> converterMap;
    private Map<Cls, AbstractionConverter> abstractionConverterMap;
    private Map<Cls, TemporalPropositionConverter> temporalPropositionConverterMap;

    InstanceConverterFactory(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.primitiveParameterConverter =
                new PrimitiveParameterConverter();
        this.eventConverter = new EventConverter();
        this.constantConverter = new ConstantConverter();
        this.lowLevelAbstractionConverter = new LowLevelAbstractionConverter();
        this.sliceConverter = new SliceConverter();
        this.highLevelAbstractionConverter =
                new HighLevelAbstractionConverter();
        this.pairAbstractionConverter = new PairAbstractionConverter();
        this.contextConverter = new ContextConverter();
    }

    /**
     * Gets an appropriate {@link PropositionConverter} for constructing a
     * PROTEMPA proposition definition from the given Protege proposition
     * instance.
     *
     * @param proposition a Protege proposition {@link Proposition} instance.
     *
     * @return an appropriate {@link PropositionConverter} object, or
     * <code>null</code> if the given <code>proposition</code> is
     * <code>null</code>.
     * @throws AssertionError if the given <code>proposition</code> does not
     * have a type in the Protege <code>Proposition</code> class hierarchy.
     */
    PropositionConverter getInstance(Instance proposition)
            throws KnowledgeSourceReadException {
        if (proposition == null) {
            return null;
        } else {
            if (this.converterMap == null) {
                populateConverterMap();
            }
            Collection<Cls> types = (Collection<Cls>) proposition.getDirectTypes();
            for (Cls cls : types) {
                PropositionConverter pc = this.converterMap.get(cls);
                if (pc != null) {
                    return pc;
                }
            }
            return null;
        }
    }

    AbstractionConverter getAbstractionInstance(Instance proposition)
            throws KnowledgeSourceReadException {
        if (proposition == null) {
            return null;
        } else {
            if (this.abstractionConverterMap == null) {
                populateConverterMap();
            }
            Collection<Cls> types =
                    (Collection<Cls>) proposition.getDirectTypes();
            for (Cls cls : types) {
                AbstractionConverter ac =
                        this.abstractionConverterMap.get(cls);
                if (ac != null) {
                    return ac;
                }
            }
            return null;
        }
    }

    TemporalPropositionConverter getTemporalPropositionInstance(Instance proposition)
            throws KnowledgeSourceReadException {
        if (proposition == null) {
            return null;
        } else {
            if (this.temporalPropositionConverterMap == null) {
                populateConverterMap();
            }
            Collection<Cls> types =
                    (Collection<Cls>) proposition.getDirectTypes();
            for (Cls cls : types) {
                TemporalPropositionConverter ac =
                        this.temporalPropositionConverterMap.get(cls);
                if (ac != null) {
                    return ac;
                }
            }
            return null;
        }
    }

    void reset() {
        if (this.converterMap != null) {
            this.converterMap.clear();
            this.converterMap = null;
        }
    }

    private void populateConverterMap() throws KnowledgeSourceReadException {
        this.converterMap = new HashMap<Cls, PropositionConverter>();
        this.abstractionConverterMap = new HashMap<Cls, AbstractionConverter>();
        this.temporalPropositionConverterMap = new HashMap<Cls, TemporalPropositionConverter>();
        populateConverterMap0(this.primitiveParameterConverter.getClsName(), this.primitiveParameterConverter);
        populateTemporalPropositionConverterMap0(this.primitiveParameterConverter.getClsName(), this.primitiveParameterConverter);
        populateConverterMap0(this.lowLevelAbstractionConverter.getClsName(), this.lowLevelAbstractionConverter);
        populateAbstractionConverterMap0(this.lowLevelAbstractionConverter.getClsName(), this.lowLevelAbstractionConverter);
        populateTemporalPropositionConverterMap0(this.lowLevelAbstractionConverter.getClsName(), this.lowLevelAbstractionConverter);
        populateConverterMap0(this.sliceConverter.getClsName(), this.sliceConverter);
        populateAbstractionConverterMap0(this.sliceConverter.getClsName(), this.sliceConverter);
        populateTemporalPropositionConverterMap0(this.sliceConverter.getClsName(), this.sliceConverter);
        populateConverterMap0(this.highLevelAbstractionConverter.getClsName(), this.highLevelAbstractionConverter);
        populateAbstractionConverterMap0(this.highLevelAbstractionConverter.getClsName(), this.highLevelAbstractionConverter);
        populateTemporalPropositionConverterMap0(this.highLevelAbstractionConverter.getClsName(), this.highLevelAbstractionConverter);
        populateConverterMap0(this.eventConverter.getClsName(), this.eventConverter);
        populateTemporalPropositionConverterMap0(this.eventConverter.getClsName(), this.eventConverter);
        populateConverterMap0(this.constantConverter.getClsName(), this.constantConverter);
        populateConverterMap0(this.pairAbstractionConverter.getClsName(), this.pairAbstractionConverter);
        populateTemporalPropositionConverterMap0(this.pairAbstractionConverter.getClsName(), this.pairAbstractionConverter);
        populateAbstractionConverterMap0(this.pairAbstractionConverter.getClsName(), this.pairAbstractionConverter);
        populateConverterMap0(this.contextConverter.getClsName(), this.contextConverter);
        populateTemporalPropositionConverterMap0(this.contextConverter.getClsName(), this.contextConverter);

    }

    private void populateConverterMap0(String rootClsName, PropositionConverter converter) throws KnowledgeSourceReadException {
        Cls cls = this.connectionManager.getCls(rootClsName);
        if (cls != null) {
            this.converterMap.put(cls, converter);
            for (Object subCls : cls.getSubclasses()) {
                this.converterMap.put((Cls) subCls, converter);
            }
        }
    }

    private void populateAbstractionConverterMap0(String rootClsName, AbstractionConverter converter) throws KnowledgeSourceReadException {
        Cls cls = this.connectionManager.getCls(rootClsName);
        this.abstractionConverterMap.put(cls, converter);
        if (cls != null) {
            for (Object subCls : cls.getSubclasses()) {
                this.abstractionConverterMap.put((Cls) subCls, converter);
            }
        }
    }

    private void populateTemporalPropositionConverterMap0(String rootClsName, TemporalPropositionConverter converter) throws KnowledgeSourceReadException {
        Cls cls = this.connectionManager.getCls(rootClsName);
        this.temporalPropositionConverterMap.put(cls, converter);
        if (cls != null) {
            for (Object subCls : cls.getSubclasses()) {
                this.temporalPropositionConverterMap.put((Cls) subCls, converter);
            }
        }
    }
}
