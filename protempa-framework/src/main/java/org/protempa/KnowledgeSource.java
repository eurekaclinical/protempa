package org.protempa;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.protempa.backend.BackendNewInstanceException;

/**
 * A read-only "interface" to an externally-maintained knowledge base. The user
 * must specify a backend in the constructor from where information about
 * primitive parameters and abstract parameters can be obtained.
 * 
 * @author Andrew Post
 */
public final class KnowledgeSource
		extends
		AbstractSource<KnowledgeSourceUpdatedEvent,
        KnowledgeSourceBackendUpdatedEvent> {

	/**
	 * PROTEMPA knowledge object model.
	 */
	private KnowledgeBase protempaKnowledgeBase;

	private final BackendManager<KnowledgeSourceBackendUpdatedEvent,
            KnowledgeSource, KnowledgeSourceBackend> backendManager;

	private final Map<Set<String>, Set<String>> leafEventIdCache;

	private final Map<Set<String>, Set<String>> primParamIdCache;

	private final Set<String> notFoundAbstractionDefinitionRequests;

	private final Set<String> notFoundEventDefinitionRequests;

	private final Set<String> notFoundPrimitiveParameterDefinitionRequests;

	public KnowledgeSource(KnowledgeSourceBackend[] backends) {
        super(backends);
		this.backendManager = new 
                BackendManager<KnowledgeSourceBackendUpdatedEvent,
                KnowledgeSource, KnowledgeSourceBackend>(this, backends);
		this.leafEventIdCache = new HashMap<Set<String>, Set<String>>();
		this.primParamIdCache = new HashMap<Set<String>, Set<String>>();
		this.notFoundAbstractionDefinitionRequests = new HashSet<String>();
		this.notFoundEventDefinitionRequests = new HashSet<String>();
		this.notFoundPrimitiveParameterDefinitionRequests =
                new HashSet<String>();
	}

	/**
	 * Connect to the knowledge source backend(s).
	 */
	private void initializeIfNeeded() throws BackendInitializationException,
            BackendNewInstanceException {
        if (isClosed())
            throw new IllegalStateException("Knowledge source already closed!");
		this.backendManager.initializeIfNeeded();
		if (this.backendManager.getBackends() != null
				&& this.protempaKnowledgeBase == null) {
			this.protempaKnowledgeBase = new KnowledgeBase();
		}
	}

	/**
	 * Returns the specified event definition.
	 * 
	 * @param id
	 *            an event definition id {@link String}.
	 * @return an {@link EventDefinition}, or <code>null</code> if none was
	 *         found with the given <code>id</code>.
	 */
	public EventDefinition readEventDefinition(String id) 
            throws KnowledgeSourceReadException {
		EventDefinition result = null;
		if (!this.notFoundEventDefinitionRequests.contains(id)) {
			if (protempaKnowledgeBase != null) {
				result = protempaKnowledgeBase.getEventDefinition(id);
			}
			if (result == null
                    && !this.notFoundEventDefinitionRequests.contains(id)) {
                try {
                    initializeIfNeeded();
                } catch (BackendInitializationException ex) {
                    throw new KnowledgeSourceReadException(ex);
                } catch (BackendNewInstanceException ex) {
                    throw new KnowledgeSourceReadException(ex);
                }
                if (this.backendManager.getBackends() != null) {
                    for (KnowledgeSourceBackend backend :
                        this.backendManager.getBackends()) {
                        result = backend.readEventDefinition(id,
                                protempaKnowledgeBase);
                        if (result != null) {
                            return result;
                        }
                    }
                    this.notFoundEventDefinitionRequests.add(id);
                }
			}
		}

		return result;
	}

	/**
	 * Returns the specified proposition definition.
	 * 
	 * @param id
	 *            a proposition definition id {@link String}.
	 * @return a {@link PropositionDefinition}, or <code>null</code> if none
	 *         was found with the given <code>id</code>.
	 */
	public PropositionDefinition readPropositionDefinition(String id) 
            throws KnowledgeSourceReadException {
		PropositionDefinition result = readAbstractionDefinition(id);
		if (result == null) {
			result = readPrimitiveParameterDefinition(id);
			if (result == null) {
				result = readEventDefinition(id);
			}
		}

		return result;
	}

	/**
	 * Read the primitive parameter definition with the given id.
	 * 
	 * @param id
	 *            a primitive parameter definition id <code>String</code>.
	 * @return a {@link PrimitiveParameterDefinition} object, or
	 *         <code>null</code> if none was found with the given
	 *         <code>id</code>.
	 */
	public PrimitiveParameterDefinition readPrimitiveParameterDefinition(
			String id) {
		PrimitiveParameterDefinition result = null;
		if (!this.notFoundPrimitiveParameterDefinitionRequests.contains(id)) {
			if (protempaKnowledgeBase != null) {
				result = protempaKnowledgeBase
						.getPrimitiveParameterDefinition(id);
			}

			if (result == null) {
				try {
					initializeIfNeeded();
					if (this.backendManager.getBackends() != null) {
						for (KnowledgeSourceBackend backend : this.backendManager
								.getBackends()) {
							result = backend.readPrimitiveParameterDefinition(
									id, protempaKnowledgeBase);
							if (result != null) {
								return result;
							}
						}
						this.notFoundPrimitiveParameterDefinitionRequests
								.add(id);
					}
				} catch (Exception e) {
					ProtempaUtil
							.logger()
							.log(
									Level.SEVERE,
									"An error occurred reading the primitive parameter definitions.",
									e);
				}
			}
		}

		return result;
	}

	/**
	 * Read the abstraction definition with the given id.
	 * 
	 * @param id
	 *            an abstraction definition id.
	 * @return an {@link AbstractionDefinition} object, or <code>null</code>
	 *         if none was found with the given <code>id</code>.
	 */
	public AbstractionDefinition readAbstractionDefinition(String id) {
		AbstractionDefinition result = null;
		if (!this.notFoundAbstractionDefinitionRequests.contains(id)) {
			if (protempaKnowledgeBase != null) {
				result = protempaKnowledgeBase.getAbstractionDefinition(id);
			}
			if (result == null && 
                    !this.notFoundAbstractionDefinitionRequests.contains(id)) {
				try {
					initializeIfNeeded();
					if (this.backendManager.getBackends() != null) {
						for (KnowledgeSourceBackend backend : this.backendManager
								.getBackends()) {
							result = backend.readAbstractionDefinition(id,
									protempaKnowledgeBase);
							if (result != null) {
								return result;
							}
						}
						this.notFoundAbstractionDefinitionRequests.add(id);
					}
				} catch (Exception e) {
					ProtempaUtil.logger().log(
							Level.SEVERE,
							"An error occurred reading abstract parameter definition "
									+ id + ".", e);
				}
			}
		}
		return result;
	}

    @Override
	public void close() {
		clear();
		this.backendManager.close();
		this.protempaKnowledgeBase = null;
        super.close();
	}

	public void clear() {
		if (this.protempaKnowledgeBase != null) {
			this.protempaKnowledgeBase.clear();
			this.leafEventIdCache.clear();
			this.primParamIdCache.clear();
			this.notFoundAbstractionDefinitionRequests.clear();
			this.notFoundEventDefinitionRequests.clear();
			this.notFoundPrimitiveParameterDefinitionRequests.clear();
		}
	}

	public Set<String> leafPropositionIds(String propId) 
            throws KnowledgeSourceReadException {
		return leafPropositionIds(Collections.singleton(propId));
	}

	public Set<String> leafPropositionIds(Set<String> propIds) 
            throws KnowledgeSourceReadException {
		Set<String> primParamIds = new HashSet<String>(
				primitiveParameterIds(propIds));
		Set<String> eventIds = leafEventIds(propIds);
		primParamIds.addAll(eventIds);

		return primParamIds;
	}

	/**
	 * Returns the set of primitive parameter ids needed to find the given
	 * propositions. If a primitive parameter id is passed in, it is included in
	 * the returned set.
	 * 
	 * @param propIds
	 *            a <code>Set</code> of proposition id <code>String</code>s.
	 *            If <code>null</code>, we return an empty <code>Set</code>.
	 * @return an unmodifiable <code>Set</code> of primitive parameter id
	 *         <code>String</code>s. Guaranteed not to return
	 *         <code>null</code>.
	 */
	public Set<String> primitiveParameterIds(Set<String> propIds) {
		Set<String> cachedResult = this.primParamIdCache.get(propIds);
		if (cachedResult != null) {
			return cachedResult;
		} else {
			Set<String> result = new HashSet<String>();
			if (propIds != null) {
				primitiveParameterIdsHelper(propIds.toArray(new String[propIds
						.size()]), result);
				result = Collections.unmodifiableSet(result);
				this.primParamIdCache.put(propIds, result);
			}
			return result;
		}
	}

	/**
	 * Helper method for finding primitive parameter ids.
	 * 
	 * @param paramIds
	 *            a {@link Set} of proposition id {@link String}s, must not be
	 *            <code>null</code>.
	 * @param result
	 *            a {@link Set} for storing the primitive parameter ids to
	 *            return, must not be <code>null</code>.
	 */
	private void primitiveParameterIdsHelper(String[] paramIds,
			Set<String> result) {
		for (String paramId : paramIds) {
			PrimitiveParameterDefinition primParamDef =
                    readPrimitiveParameterDefinition(paramId);
			if (primParamDef != null) {
				String[] primParamDefInverseIsA = primParamDef.getInverseIsA();
				if (primParamDefInverseIsA == null
						|| primParamDefInverseIsA.length == 0) {
					result.add(paramId);
				} else {
					primitiveParameterIdsHelper(primParamDefInverseIsA, result);
				}
			} else {
				AbstractionDefinition def = readAbstractionDefinition(paramId);
				if (def != null) {
					primitiveParameterIdsHelper(def.getInverseIsA(), result);
					Set<String> abstractedFrom = def.getAbstractedFrom();
					primitiveParameterIdsHelper(abstractedFrom
							.toArray(new String[abstractedFrom.size()]), result);
				}
			}
		}
	}

	/**
	 * Returns the set of primitive parameter ids needed to find instances of
	 * the given abstraction.
	 * 
	 * @param abstractionId
	 *            an abstraction id <code>String</code>.
	 * @return a newly-created <code>Set</code> of primitive parameter id
	 *         <code>String</code>s. Guaranteed not to return
	 *         <code>null</code>.
	 */
	public Set<String> primitiveParameterIds(String abstractionId) {
		return primitiveParameterIds(Collections.singleton(abstractionId));
	}

	/**
	 * Given a set of abstraction and event ids, this method navigates the event
	 * is-a hierarchy and collects and returns the set of event ids for
	 * retrieval from the data source (e.g., the events at the leaves of the
	 * tree).
	 * 
	 * @param abstractionAndEventIds
	 *            a <code>Set</code> of abstraction and event id
	 *            <code>String</code>s.
	 * @return a newly-created unmodifiable <code>Set</code> of event id
	 *         <code>String</code>s. Guaranteed not to return
	 *         <code>null</code>.
	 */
	public Set<String> leafEventIds(Set<String> abstractionAndEventIds) 
            throws KnowledgeSourceReadException {
		Set<String> cachedResult = this.leafEventIdCache
				.get(abstractionAndEventIds);
		if (cachedResult != null) {
			return cachedResult;
		} else {
			Set<String> result = new HashSet<String>();
			if (abstractionAndEventIds != null) {
				leafEventIdsHelper(abstractionAndEventIds
						.toArray(new String[abstractionAndEventIds.size()]),
						result);
				result = Collections.unmodifiableSet(result);
				this.leafEventIdCache.put(abstractionAndEventIds, result);
			}
			return result;
		}
	}

	/**
	 * Given an abstraction or event id, this method navigates the event is-a
	 * hierarchy and collects and returns the set of event ids for retrieval
	 * from the data source (e.g., the events at the leaves of the tree).
	 * 
	 * @param abstractionOrEventId
	 *            an abstraction or event id <code>String</code>.
	 * @return a newly-created unmodifiable <code>Set</code> of event id
	 *         <code>String</code>s. Guaranteed not to return
	 *         <code>null</code>.
	 */
	public Set<String> leafEventIds(String abstractionOrEventId) 
            throws KnowledgeSourceReadException {
		return leafEventIds(Collections.singleton(abstractionOrEventId));
	}

	/**
	 * Actually gets the leaf event ids. This exists so that we can recurse
	 * through the is-a hierarchy and aggregate the results in one set.
	 * 
	 * @param abstractionAndEventIds
	 *            a <code>Set</code> of abstraction and event id
	 *            <code>String</code>s.
	 * @param result
	 *            a non-<code>null</code> <code>Set</code> in which to
	 *            aggregate leaf event ids.
	 */
	private void leafEventIdsHelper(String[] abstractionAndEventIds,
			Set<String> result) throws KnowledgeSourceReadException {
		if (abstractionAndEventIds != null) {
			for (String abstractParameterOrEventId : abstractionAndEventIds) {
				EventDefinition eventDef = this
						.readEventDefinition(abstractParameterOrEventId);
				if (eventDef != null) {
					String[] inverseIsA = eventDef.getInverseIsA();
					if (inverseIsA.length == 0) {
						result.add(eventDef.getId());
					} else {
						leafEventIdsHelper(inverseIsA, result);
					}
				} else {
					AbstractionDefinition apDef = 
                            readAbstractionDefinition(
                            abstractParameterOrEventId);
					if (apDef != null) {
						Set<String> af = apDef.getAbstractedFrom();
						leafEventIdsHelper(af.toArray(new String[af.size()]),
								result);
					}
				}
			}
		}
	}

	public void backendUpdated(KnowledgeSourceBackendUpdatedEvent event) {
		clear();
		fireKnowledgeSourceUpdated();
	}

	/**
	 * Notifies registered listeners that the knowledge source has been updated.
     *
     * @see KnowledgeSourceUpdatedEvent
     * @see SourceListener
	 */
	private void fireKnowledgeSourceUpdated() {
		fireSourceUpdated(new KnowledgeSourceUpdatedEvent(this));
	}
}
