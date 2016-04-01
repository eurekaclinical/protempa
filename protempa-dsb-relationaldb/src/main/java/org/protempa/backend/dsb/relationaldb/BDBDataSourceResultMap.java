/*
 * #%L
 * Protempa Commons Backend Provider
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protempa.backend.dsb.relationaldb;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eurekaclinical.datastore.DataStore;
import org.protempa.DataSourceResultMap;
import org.protempa.datastore.UniqueIdUniqueIdStoreCreator;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Context;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.visitor.AbstractPropositionVisitor;

/**
 *
 * @author Andrew Post
 */
public class BDBDataSourceResultMap<P extends Proposition> 
        extends DataSourceResultMap<P> {
    private Map<String, List<DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>>>> refs;
    private Map<String, String> propIdToEntitySpecs;
    private final ReferenceSetter refSetter;
    private Set<String> keyIds;

    BDBDataSourceResultMap(List<DataStore<String, List<P>>> maps,
        Map<String, List<DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>>>> refs,
        Map<String, String> propIdToEntitySpecs, Set<String> keyIds) {
        super(maps);
        assert refs != null : "refs cannot be null";
        assert propIdToEntitySpecs != null : "propIdToEntitySpecs cannot be null";
        assert keyIds != null : "keyIds cannot be null";
        this.refs = refs;
        this.refSetter = new ReferenceSetter();
        this.propIdToEntitySpecs = propIdToEntitySpecs;
        this.keyIds = Collections.unmodifiableSet(keyIds);
    }
    
    private static class ReferenceSetter extends AbstractPropositionVisitor {
        List<UniqueIdUniqueIdStoreCreator.Reference> refsList;

        @Override
        public void visit(AbstractParameter abstractParameter) {
            if (this.refsList != null) {
                for (UniqueIdUniqueIdStoreCreator.Reference ref : this.refsList) {
                    abstractParameter.addReference(ref.getName(), ref.getUniqueId());
                }
            }
            
        }

        @Override
        public void visit(Event event) {
            if (this.refsList != null) {
                for (UniqueIdUniqueIdStoreCreator.Reference ref : this.refsList) {
                    event.addReference(ref.getName(), ref.getUniqueId());
                }
            }
        }

        @Override
        public void visit(PrimitiveParameter primitiveParameter) {
            if (this.refsList != null) {
                for (UniqueIdUniqueIdStoreCreator.Reference ref : this.refsList) {
                    primitiveParameter.addReference(ref.getName(), ref.getUniqueId());
                }
            }
        }

        @Override
        public void visit(Constant constant) {
            if (this.refsList != null) {
                for (UniqueIdUniqueIdStoreCreator.Reference ref : this.refsList) {
                    constant.addReference(ref.getName(), ref.getUniqueId());
                }
            }
        }

        @Override
        public void visit(Context context) {
            if (this.refsList != null) {
                for (UniqueIdUniqueIdStoreCreator.Reference ref : this.refsList) {
                    context.addReference(ref.getName(), ref.getUniqueId());
                }
            }
        }
        
    }

    @Override
    public Set<String> keySet() {
        return this.keyIds;
    }
    
    @Override
    public List<P> get(Object o) {
        List<P> result = super.get(o);
        if (result != null) {
            addReferences(result, this.refSetter);
            return Collections.unmodifiableList(result);
        } else {
            return null;
        }
    }

    @Override
    public Collection<List<P>> values() {
        Collection<List<P>> result = super.values();
        for (List<P> props : result) {
            addReferences(props, this.refSetter);
        }
        return Collections.unmodifiableCollection(result);
    }

    @Override
    public Set<Entry<String, List<P>>> entrySet() {
        Set<Entry<String, List<P>>> result = super.entrySet();
        for (Entry<String, List<P>> me : result) {
            addReferences(me.getValue(), this.refSetter);
        }
        return Collections.unmodifiableSet(result);
    }

    private void addReferences(List<P> props, ReferenceSetter refSetter) {
        for (P p : props) {
            String propId = p.getId();
            UniqueId uid = p.getUniqueId();
            String entitySpecName = this.propIdToEntitySpecs.get(propId);
            List<DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>>> r =
                this.refs.get(entitySpecName);
            if (r != null && p.getReferenceNames().length == 0) {
                for (DataStore<UniqueId, List<UniqueIdUniqueIdStoreCreator.Reference>> refCache : r) {
                    refSetter.refsList = refCache.get(uid);
                    p.accept(refSetter);
                    refSetter.refsList = null;
                }
            }
        }
    }
}
