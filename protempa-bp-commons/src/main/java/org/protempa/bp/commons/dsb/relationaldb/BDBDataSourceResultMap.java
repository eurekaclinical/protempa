/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.protempa.bp.commons.dsb.relationaldb;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.arp.javautil.datastore.DataStore;
import org.protempa.DataSourceResultMap;
import org.protempa.datastore.UniqueIdUniqueIdStoreCreator;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.visitor.AbstractPropositionVisitor;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Context;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

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

    BDBDataSourceResultMap(List<Map<String, List<P>>> maps,
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
