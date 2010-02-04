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

	KnowledgeBase() {
		initialize();
	}

	private void initialize() {
		abstractionDefinitions = new HashSet<AbstractionDefinition>();
		primitiveParameterDefinitions =
                new HashSet<PrimitiveParameterDefinition>();
		abstractionIdToAbstractionDefinitionMap =
                new HashMap<String, AbstractionDefinition>();
		primitiveParameterIdToPrimitiveParameterDefinitionMap =
                new HashMap<String, PrimitiveParameterDefinition>();
		eventDefinitions = new HashSet<EventDefinition>();
		eventIdToEventDefinitionMap = new HashMap<String, EventDefinition>();
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
		s.writeObject(primitiveParameterDefinitions);
		s.writeObject(abstractionDefinitions);
		s.writeObject(eventDefinitions);
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
		Set<AbstractionDefinition> abstractionParameterDefinitions = (Set<AbstractionDefinition>) s
				.readObject();
		Set<EventDefinition> eventDefinitions = (Set<EventDefinition>) s
				.readObject();

		if (primitiveParameterDefinitions != null) {
			for (PrimitiveParameterDefinition def : primitiveParameterDefinitions) {
				if (!addPrimitiveParameterDefinition(def)) {
					System.err
							.println("Could not add de-serialized primitive parameter definition "
									+ def + ".");
				}
			}
		}

		if (abstractionParameterDefinitions != null) {
			for (AbstractionDefinition def : abstractionParameterDefinitions) {
				if (!addAbstractionDefinition(def)) {
					System.err
							.println("Could not add de-serialized abstract parameter definition "
									+ def + ".");
				}
			}
		}

		if (eventDefinitions != null) {
			for (EventDefinition def : eventDefinitions) {
				if (!addEventDefinition(def)) {
					System.err
							.println("Could not add de-serialized event definition "
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
						.containsKey(id);
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
	 *         class, <code>false</code> if not. Returns <code>false</code>
	 *         if the <code>id</code> parameter is <code>null</code>.
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

	boolean addEventDefinition(EventDefinition def) {
		if (def == null) {
			return false;
		}
		boolean result = eventDefinitions.add(def);
		if (result) {
			eventIdToEventDefinitionMap.put(def.getId(), def);
		}
		return result;
	}

	boolean addAbstractionDefinition(AbstractionDefinition def) {
		if (def == null) {
			return false;
		}
		boolean result = abstractionDefinitions.add(def);
		if (result) {
			abstractionIdToAbstractionDefinitionMap.put(def.getId(), def);
		}
		return result;
	}

	boolean removeAbstractionDefinition(AbstractionDefinition def) {
		if (def == null) {
			return false;
		} else {
			if (abstractionDefinitions.remove(def)) {
				abstractionIdToAbstractionDefinitionMap.remove(def.getId());
				return true;
			} else {
				return false;
			}
		}
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String ls = System.getProperty("line.separator");
		StringBuilder buf = new StringBuilder();
		buf.append("PRIMITIVE PARAMETER DEFINITIONS: ");
		buf.append(primitiveParameterDefinitions);
		buf.append(ls);
		buf.append("ABSTRACT PARAMETER DEFINITIONS: ");
		buf.append(abstractionDefinitions);
		buf.append(ls);
		buf.append("EVENT DEFINITIONS: ");
		buf.append(eventDefinitions);
		return buf.toString();
	}
}