package org.protempa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A collection of primitive parameter definitions, abstract parameter
 * definitions, and key finding definitions. Primitive parameters are raw data
 * types. Abstract Parameters are abstractions inferred from raw data. Key
 * findings are aggregations of clinical data around found abstract parameters.
 * 
 * TODO add context types.
 * 
 * @author Andrew Post
 */
public final class KnowledgeBase implements Serializable {

    private static final long serialVersionUID = 5988857805118255882L;
    /**
     * Map of primitive parameter id <code>String</code> objects to
     * <code>PrimitiveParameterDefinition</code> objects.
     */
    private Map<String, PrimitiveParameterDefinition> idToPrimitiveParameterDefinitionMap;
    /**
     * Value used to create an unique default id for a new abstract parameter
     * definition.
     */
    private int currentKnowledgeDefinitionId;
    /**
     * Map of abstract parameter id <code>String</code> objects to
     * <code>AbstractParameterDefinition</code> objects.
     */
    private Map<String, AbstractionDefinition> idToAbstractionDefinitionMap;
    private Map<String, EventDefinition> idToEventDefinitionMap;
    private Map<String, ConstantDefinition> idToConstantDefinitionMap;
    private Map<String, ValueSet> idtoValueSetMap;

    KnowledgeBase() {
        initialize();
    }

    private void initialize() {
        this.idToAbstractionDefinitionMap = new HashMap<String, AbstractionDefinition>();
        this.idToPrimitiveParameterDefinitionMap = new HashMap<String, PrimitiveParameterDefinition>();
        this.idToEventDefinitionMap = new HashMap<String, EventDefinition>();
        this.idToConstantDefinitionMap = new HashMap<String, ConstantDefinition>();
        this.idtoValueSetMap = new HashMap<String, ValueSet>();
    }

    /**
     * Overrides default serialization.
     * 
     * @param s
     *            an <code>ObjectOutputStream</code> object.
     * @throws IOException
     *             if serialization failed.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.writeObject(this.idToPrimitiveParameterDefinitionMap.values());
        s.writeObject(this.idToAbstractionDefinitionMap.values());
        s.writeObject(this.idToEventDefinitionMap.values());
        s.writeObject(this.idToConstantDefinitionMap.values());
        s.writeObject(this.idtoValueSetMap.values());
    }

    /**
     * Overrides default de-serialization.
     * 
     * @param s
     *            an <code>ObjectInputStream</code> object.
     * @throws IOException
     *             if de-serialization failed.
     * @throws ClassNotFoundException
     *             if de-serialization failed.
     */
    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        initialize();

        Collection<PrimitiveParameterDefinition> primitiveParameterDefinitions = (Collection<PrimitiveParameterDefinition>) s.readObject();
        Collection<AbstractionDefinition> abstractionDefinitions = (Collection<AbstractionDefinition>) s.readObject();
        Collection<EventDefinition> eventDefinitions = (Collection<EventDefinition>) s.readObject();
        Collection<ConstantDefinition> constantDefinitions = (Collection<ConstantDefinition>) s.readObject();
        Collection<ValueSet> valueSets = (Collection<ValueSet>) s.readObject();

        if (primitiveParameterDefinitions != null) {
            for (PrimitiveParameterDefinition def : primitiveParameterDefinitions) {
                if (def != null && !addPrimitiveParameterDefinition(def)) {
                    System.err.println("Could not add de-serialized primitive parameter definition "
                            + def + ".");
                }
            }
        }

        if (abstractionDefinitions != null) {
            for (AbstractionDefinition def : abstractionDefinitions) {
                if (def != null && !addAbstractionDefinition(def)) {
                    System.err.println("Could not add de-serialized abstract parameter definition "
                            + def + ".");
                }
            }
        }

        if (eventDefinitions != null) {
            for (EventDefinition def : eventDefinitions) {
                if (def != null && !addEventDefinition(def)) {
                    System.err.println("Could not add de-serialized event definition "
                            + def + ".");
                }
            }
        }

        if (constantDefinitions != null) {
            for (ConstantDefinition def : constantDefinitions) {
                if (def != null && !addConstantDefinition(def)) {
                    System.err.println("Could not add de-serialized constant definition "
                            + def + ".");
                }
            }
        }
        if (valueSets != null) {
            for (ValueSet valueSet : valueSets) {
                if (valueSet != null && !addValueSet(valueSet)) {
                    System.err.println("Could not add de-serialized value set "
                            + valueSet + ".");
                }
            }
        }
    }

    String getNextKnowledgeDefinitionObjectId() {
        while (true) {
            String candidate = "PROTEMPA_" + currentKnowledgeDefinitionId++;
            if (isUniqueKnowledgeDefinitionObjectId(candidate)) {
                return candidate;
            }
        }
    }

    boolean isUniqueKnowledgeDefinitionObjectId(String id) {
        return !this.idToAbstractionDefinitionMap.containsKey(id)
                && !this.idToEventDefinitionMap.containsKey(id)
                && !this.idToPrimitiveParameterDefinitionMap.containsKey(id)
                && !this.idToConstantDefinitionMap.containsKey(id);
    }

    public PropositionDefinition getPropositionDefinition(String propId) {
        PropositionDefinition result = getEventDefinition(propId);
        if (result == null) {
            result = getPrimitiveParameterDefinition(propId);
            if (result == null) {
                result = getConstantDefinition(propId);
                if (result == null) {
                    result = getAbstractionDefinition(propId);
                }
            }
        }
        return result;
    }

    public boolean hasEventDefinition(String eventId) {
        return getEventDefinition(eventId) != null;
    }

    public EventDefinition getEventDefinition(String eventId) {
        return idToEventDefinitionMap.get(eventId);
    }

    public boolean hasConstantDefinition(String constantId) {
        return getConstantDefinition(constantId) != null;
    }

    public ConstantDefinition getConstantDefinition(String constantId) {
        return idToConstantDefinitionMap.get(constantId);
    }

    public boolean hasValueSet(String valueSetId) {
        return getValueSet(valueSetId) != null;
    }

    public ValueSet getValueSet(String valueSetId) {
        return idtoValueSetMap.get(valueSetId);
    }

    public boolean hasAbstractionDefinition(String paramId) {
        return getAbstractionDefinition(paramId) != null;
    }

    public AbstractionDefinition getAbstractionDefinition(String paramId) {
        return idToAbstractionDefinitionMap.get(paramId);
    }

    /**
     * Returns whether this knowledge base has a type class with the given id.
     * 
     * @param id
     *            a type class id <code>String</code>.
     * @return <code>true</code> if this knowledge base has the given type
     *         class, <code>false</code> if not. Returns <code>false</code> if
     *         the <code>id</code> parameter is <code>null</code>.
     */
    public boolean hasPrimitiveParameterDefinition(String id) {
        return getPrimitiveParameterDefinition(id) != null;
    }

    /**
     * Gets the type class with the given id.
     * 
     * @param id
     *            a primitive parameter id <code>String</code>.
     * @return a <code>TypeClass</code> object, or <code>null</code> if not
     *         found.
     */
    public PrimitiveParameterDefinition getPrimitiveParameterDefinition(
            String id) {
        return idToPrimitiveParameterDefinitionMap.get(id);
    }

    boolean addConstantDefinition(ConstantDefinition def) {
        assert def != null : "def cannot be null";
        String id = def.getId();
        if (this.idToConstantDefinitionMap.containsKey(id)) {
            return false;
        } else {
            this.idToConstantDefinitionMap.put(id, def);
            return true;
        }

    }

    boolean addEventDefinition(EventDefinition def) {
        assert def != null : "def cannot be null";
        String id = def.getId();
        if (idToEventDefinitionMap.containsKey(id)) {
            return false;
        } else {
            idToEventDefinitionMap.put(id, def);
            return true;
        }
    }

    boolean addAbstractionDefinition(AbstractionDefinition def) {
        assert def != null : "def cannot be null";
        String id = def.getId();
        if (this.idToAbstractionDefinitionMap.containsKey(id)) {
            return false;
        } else {
            idToAbstractionDefinitionMap.put(id, def);
            return true;
        }
    }

    boolean addPrimitiveParameterDefinition(
            PrimitiveParameterDefinition def) {
        assert def != null : "def cannot be null";
        String id = def.getId();
        if (this.idToPrimitiveParameterDefinitionMap.containsKey(id)) {
            return false;
        } else {
            idToPrimitiveParameterDefinitionMap.put(
                    id,
                    def);
            return true;
        }
    }

    boolean addValueSet(ValueSet valueSet) {
        assert valueSet != null : "valueSet cannot be null";
        String id = valueSet.getId();
        if (this.idtoValueSetMap.containsKey(id)) {
            return false;
        } else {
            this.idtoValueSetMap.put(id, valueSet);
            return true;
        }
    }

    void clear() {
        this.idToAbstractionDefinitionMap.clear();
        this.idToEventDefinitionMap.clear();
        this.idToPrimitiveParameterDefinitionMap.clear();
        this.idToConstantDefinitionMap.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append(this.idToAbstractionDefinitionMap.values()).append(this.idToConstantDefinitionMap.values()).append(this.idToEventDefinitionMap.values()).append(this.idToPrimitiveParameterDefinitionMap.values()).toString();
    }
}
