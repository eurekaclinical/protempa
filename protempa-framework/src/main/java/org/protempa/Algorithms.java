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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The set of defined algorithms. A maximum of <code>Integer.MAX_VALUE</code>
 * algorithms may be defined.
 * 
 * @author Andrew Post
 */
public final class Algorithms implements Serializable {

	private static final long serialVersionUID = -7814170547543723861L;

	/**
	 * Caches the algorithms to avoid creating a new set all of the time.
	 */
	private transient Set<Algorithm> algorithms;

	/**
	 * Map of algorithm id <code>String</code> objects to
	 * <code>Algorithm</code> objects.
	 */
	private Map<String, Algorithm> idsToAlgorithms;

	/**
	 * Value used to create an unique default id for a new algorithm.
	 */
	private int currentAlgorithmId;

	Algorithms() {
		initialize();
	}

	/**
	 * Initializes the object.
	 */
	private void initialize() {
		idsToAlgorithms = new HashMap<String, Algorithm>();
	}

	/**
	 * Generates an unused algorithm id.
	 * 
	 * @return an algorithm id <code>String</code>.
	 */
	String getNextAlgorithmObjectId() {
		if (idsToAlgorithms.size() == Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Maximum number of algorithm objects reached");
		}
		while (true) {
			String candidate = "ALGORITHM_" + currentAlgorithmId++;
			if (isUniqueAlgorithmObjectId(candidate)) {
				return candidate;
			}
		}
	}

	/**
	 * Returns whether the given algorithm id is not already being used.
	 * 
	 * @param id
	 *            an algorithm id <code>String</code>.
	 * @return <code>true</code> if the given algorithm id is not being used,
	 *         <code>false</code> if it is.
	 */
	boolean isUniqueAlgorithmObjectId(String id) {
		return !idsToAlgorithms.containsKey(id);
	}

	/**
	 * Returns all algorithms.
	 * 
	 * @return a newly-created <code>Set</code> of <code>Algorithm</code>
	 *         objects.
	 */
	public Set<Algorithm> getAlgorithms() {
		if (algorithms == null) {
			algorithms = Collections.unmodifiableSet(new HashSet<Algorithm>(
					this.idsToAlgorithms.values()));
		}
		return algorithms;
	}

	/**
	 * Returns whether an algorithm exists with the given id.
	 * 
	 * @param id
	 *            an algorithm id <code>String</code>.
	 * @return <code>true</code> if the algorithm exists, <code>false</code>
	 *         otherwise.
	 */
	public boolean hasAlgorithm(String id) {
		return getAlgorithm(id) != null;
	}

	/**
	 * Returns the algorithm with a given id.
	 * 
	 * @param id
	 *            an algorithm id <code>String</code>.
	 * @return an <code>Algorithm</code> object, or <code>null</code> if no
	 *         algorithm with the given id exists or the given id is
	 *         <code>null</code>.
	 */
	public Algorithm getAlgorithm(String id) {
		return idsToAlgorithms.get(id);
	}

	/**
	 * Adds a new algorithm.
	 * 
	 * @param algorithm
	 *            an <code>Algorithm</code> object.
	 * @return <code>true</code> if successful, <code>false</code> if not.
	 */
	public boolean addAlgorithm(Algorithm algorithm) {
		if (algorithm == null || idsToAlgorithms.containsKey(algorithm.getId())) {
			return false;
		}

		idsToAlgorithms.put(algorithm.getId(), algorithm);
		algorithms = null;
		return true;
	}

	/**
	 * Closes and removes an algorithm.
	 * 
	 * @param algorithm
	 *            an <code>Algorithm</code> object.
	 * @return <code>true</code> if successful, <code>false</code> if not.
	 */
	boolean removeAlgorithm(Algorithm algorithm) {
		if (algorithm != null) {
			algorithm.close();
		}
		if (idsToAlgorithms.remove(algorithm.getId()) != null) {
			algorithms = null;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Closes and removes all algorithms.
	 */
	void closeAndClear() {
		for (Algorithm a : this.idsToAlgorithms.values()) {
			a.close();
		}
		idsToAlgorithms.clear();
		algorithms = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ALGORITHMS: " + this.idsToAlgorithms.values();
	}
}
