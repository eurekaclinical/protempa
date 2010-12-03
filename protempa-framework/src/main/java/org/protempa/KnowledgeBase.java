package org.protempa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.proposition.value.ValueSet;

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
     * The abstract parameter definitions in this knowledge base.
     */
    private Set<AbstractionDefinition> abstractionDefinitions;
    /**
     * The primitive parameter definitions in this knowledge base.
     */
    private Set<PrimitiveParameterDefinition> primitiveParameterDefinitions;
    /**
     * Map of primitive parameter id <code>String</code> objects to
     * <code>PrimitiveParameterDefinition</code> objects.
     */
    private Map<String, PrimitiveParameterDefinition> primitiveParameterIdToPrimitiveParameterDefinitionMap;

    /**
     * Value used to create an unique default id for a new abstract parameter
     * definition.
     */
    private int currentKnowledgeDefinitionId;
    /**
     * Map of abstract parameter id <code>String</code> objects to
     * <code>AbstractParameterDefinition</code> objects.
     */
    private Map<String, AbstractionDefinition> abstractionIdToAbstractionDefinitionMap;
    private Set<EventDefinition> eventDefinitions;
    private Map<String, EventDefinition> eventIdToEventDefinitionMap;

    private Set<ConstantDefinition> constantDefinitions;
    private Map<String, ConstantDefinition> constantIdToConstantDefinitionMap;

    private Set<ValueSet> valueSets;
    private Map<String, ValueSet> valueSetIdtoValueSetMap;

    KnowledgeBase() {
        initialize();
    }

    private void initialize() {
        this.abstractionDefinitions = new HashSet<AbstractionDefinition>();
        this.primitiveParameterDefinitions = new HashSet<PrimitiveParameterDefinition>();
        this.abstractionIdToAbstractionDefinitionMap = new HashMap<String, AbstractionDefinition>();
        this.primitiveParameterIdToPrimitiveParameterDefinitionMap = new HashMap<String, PrimitiveParameterDefinition>();
        this.eventDefinitions = new HashSet<EventDefinition>();
        this.eventIdToEventDefinitionMap = new HashMap<String, EventDefinition>();
        this.constantDefinitions = new HashSet<ConstantDefinition>();
        this.constantIdToConstantDefinitionMap = new HashMap<String, ConstantDefinition>();
        this.valueSets = new HashSet<ValueSet>();
        this.valueSetIdtoValueSetMap = new HashMap<String, ValueSet>();
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
        s.writeObject(this.primitiveParameterDefinitions);
        s.writeObject(this.abstractionDefinitions);
        s.writeObject(this.eventDefinitions);
        s.writeObject(this.constantDefinitions);
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

        Set<PrimitiveParameterDefinition> primitiveParameterDefinitions = (Set<PrimitiveParameterDefinition>) s
                .readObject();
        Set<AbstractionDefinition> abstractionDefinitions = (Set<AbstractionDefinition>) s
                .readObject();
        Set<EventDefinition> eventDefinitions = (Set<EventDefinition>) s
                .readObject();
        Set<ConstantDefinition> constantDefinitions = (Set<ConstantDefinition>) s
                .readObject();

        if (primitiveParameterDefinitions != null) {
            for (PrimitiveParameterDefinition def : primitiveParameterDefinitions) {
                if (def != null && !addPrimitiveParameterDefinition(def)) {
                    System.err
                            .println("Could not add de-serialized primitive parameter definition "
                                    + def + ".");
                }
            }
        }

        if (abstractionDefinitions != null) {
            for (AbstractionDefinition def : abstractionDefinitions) {
                if (def != null && !addAbstractionDefinition(def)) {
                    System.err
                            .println("Could not add de-serialized abstract parameter definition "
                                    + def + ".");
                }
            }
        }

        if (eventDefinitions != null) {
            for (EventDefinition def : eventDefinitions) {
                if (def != null && !addEventDefinition(def)) {
                    System.err
                            .println("Could not add de-serialized event definition "
                                    + def + ".");
                }
            }
        }

        if (constantDefinitions != null) {
            for (ConstantDefinition def : constantDefinitions) {
                if (def != null && !addConstantDefinition(def)) {
                    System.err
                            .println("Could not add de-serialized constant definition "
                                    + def + ".");
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
        return !this.abstractionIdToAbstractionDefinitionMap.containsKey(id)
                && !this.eventIdToEventDefinitionMap.containsKey(id)
                && !this.primitiveParameterIdToPrimitiveParameterDefinitionMap
                        .containsKey(id)
                && !this.constantIdToConstantDefinitionMap.containsKey(id);
    }

    public Set<EventDefinition> getEventDefinitions() {
        return Collections.unmodifiableSet(eventDefinitions);
    }

    public boolean hasEventDefinition(String eventId) {
        return getEventDefinition(eventId) != null;
    }

    public EventDefinition getEventDefinition(String eventId) {
        return eventIdToEventDefinitionMap.get(eventId);
    }

    public Set<ConstantDefinition> getConstantDefinitions() {
        return Collections.unmodifiableSet(this.constantDefinitions);
    }

    public boolean hasConstantDefinition(String constantId) {
        return getConstantDefinition(constantId) != null;
    }

    public ConstantDefinition getConstantDefinition(String constantId) {
        return constantIdToConstantDefinitionMap.get(constantId);
    }

    public boolean hasValueSet (String valueSetId) {
        return getValueSet(valueSetId) != null;
    }

    public ValueSet getValueSet(String valueSetId) {
        return valueSetIdtoValueSetMap.get(valueSetId);
    }

    /**
     * Returns all abstraction definitions.
     * 
     * @return an unmodifiable <code>Set</code> of
     *         <code>AbstractionDefinition</code>s.
     */
    public Set<AbstractionDefinition> getAbstractionDefinitions() {
        return Collections.unmodifiableSet(abstractionDefinitions);
    }

    public boolean hasAbstractionDefinition(String paramId) {
        return getAbstractionDefinition(paramId) != null;
    }

    public AbstractionDefinition getAbstractionDefinition(String paramId) {
        return abstractionIdToAbstractionDefinitionMap.get(paramId);
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
        return primitiveParameterIdToPrimitiveParameterDefinitionMap.get(id);
    }

    boolean addConstantDefinition(ConstantDefinition def) {
        assert def != null : "def cannot be null";
        boolean result = this.constantDefinitions.add(def);
        if (result) {
            this.constantIdToConstantDefinitionMap.put(def.getId(), def);
        }
        return result;
    }

    boolean addEventDefinition(EventDefinition def) {
        assert def != null : "def cannot be null";
        boolean result = eventDefinitions.add(def);
        if (result) {
            eventIdToEventDefinitionMap.put(def.getId(), def);
        }
        return result;
    }

    boolean addAbstractionDefinition(AbstractionDefinition def) {
        assert def != null : "def cannot be null";
        boolean result = abstractionDefinitions.add(def);
        if (result) {
            abstractionIdToAbstractionDefinitionMap.put(def.getId(), def);
        }
        return result;
    }

    boolean addPrimitiveParameterDefinition(
            PrimitiveParameterDefinition primitiveParameterDefinition) {
        if (primitiveParameterDefinition == null
                || !primitiveParameterDefinitions
                        .add(primitiveParameterDefinition)) {
            return false;
        }
        primitiveParameterIdToPrimitiveParameterDefinitionMap.put(
                primitiveParameterDefinition.getId(),
                primitiveParameterDefinition);
        return true;
    }

    public Set<PrimitiveParameterDefinition> getPrimitiveParameterDefinitions() {
        return Collections.unmodifiableSet(primitiveParameterDefinitions);
    }

    void clear() {
        this.abstractionDefinitions.clear();
        this.abstractionIdToAbstractionDefinitionMap.clear();
        this.eventDefinitions.clear();
        this.eventIdToEventDefinitionMap.clear();
        this.primitiveParameterDefinitions.clear();
        this.primitiveParameterIdToPrimitiveParameterDefinitionMap.clear();
        this.constantDefinitions.clear();
        this.constantIdToConstantDefinitionMap.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append(this.abstractionDefinitions)
                .append(this.constantDefinitions).append(this.eventDefinitions)
                .append(this.primitiveParameterDefinitions).toString();
    }
}
