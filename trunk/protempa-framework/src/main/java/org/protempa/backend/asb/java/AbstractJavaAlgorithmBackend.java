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
package org.protempa.backend.asb.java;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Properties;

import org.protempa.AbstractAlgorithm;
import org.protempa.backend.asb.AbstractAlgorithmSourceBackend;
import org.protempa.Algorithm;
import org.protempa.Algorithms;

import org.arp.javautil.io.IOUtil;
import org.protempa.AlgorithmSourceReadException;
import org.protempa.backend.AlgorithmSourceBackendInitializationException;
import org.protempa.backend.BackendInitializationException;
import org.protempa.backend.BackendInstanceSpec;

/**
 * For accessing algorithms written in Java as subclasses of
 * {@link AbstractAlgorithm}.
 * 
 * @author Andrew Post
 */
public abstract class AbstractJavaAlgorithmBackend
        extends AbstractAlgorithmSourceBackend {

	private static final String DEFAULT_ALGORITHMS_PROPS =
            "/org/protempa/backend/asb/java/algorithms.properties";

    

//    private final BackendInstanceSpec configuration;

	private final Properties algorithmClasses;

	public AbstractJavaAlgorithmBackend() {
		this(null);
	}

	public AbstractJavaAlgorithmBackend(BackendInstanceSpec config) {
//		this.configuration = config;
        this.algorithmClasses = new Properties();
	}

    protected String getAlgorithmsPropertiesResourceName() {
        return DEFAULT_ALGORITHMS_PROPS;
    }

	/**
	 * Reads the properties file containing the list of algorithm classes.
	 */
	@Override
    public final void initialize(BackendInstanceSpec config)
        throws BackendInitializationException {
		try {
			IOUtil.readPropertiesFromResource(algorithmClasses, getClass(),
					getAlgorithmsPropertiesResourceName());
		} catch (IOException e) {
			throw new AlgorithmSourceBackendInitializationException(
					"Could not initialize " + getClass(), e);
		}
	}

	@Override
    public final Algorithm readAlgorithm(String id, Algorithms algorithms) 
            throws AlgorithmSourceReadException {
		String className = algorithmClasses.getProperty(id);
		if (className != null) {
			return createAlgorithmInstance(id, algorithms, className);
		} else {
			return null;
		}
	}

	/**
	 * name is expected to be a fully-qualified class name in dot-format (e.g.,
	 * my.algorithms.MockAlgorithm).
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected Class<?> getAlgorithmClass(String name) throws Exception {
		return Class.forName(name);
	}

	private final Algorithm createAlgorithmInstance(String id,
			Algorithms algorithms, String className) 
            throws AlgorithmSourceReadException {
		Algorithm result = null;
		try {
			Class<?> c = getAlgorithmClass(className);
			if (c != null) {
				Constructor<?> con = c.getConstructor(Algorithms.class,
						String.class);
				if (con != null) {
					result = (Algorithm) con.newInstance(algorithms, id);
				}
			} else {
                throw new AlgorithmSourceReadException("Could not find algorithm "
                        + className);
            }
			JavaAlgorithmUtil.logger().fine("Created algorithm " + id);
		} catch (Exception e) {
			throw new AlgorithmSourceReadException(
					"Could not find algorithm " + className, e);
		}

		return result;
	}

	@Override
    public final void readAlgorithms(Algorithms algorithms) 
            throws AlgorithmSourceReadException {
		for (Map.Entry<Object, Object> e : algorithmClasses.entrySet()) {
			String algoId = (String) e.getKey();
			String algoClassName = (String) e.getValue();
			Algorithm algorithm = algorithms.getAlgorithm(algoId);
			if (algorithm == null) {
				algorithms.addAlgorithm(createAlgorithmInstance(algoId,
						algorithms, algoClassName));
			}
		}
	}

	@Override
	public void close() {
		algorithmClasses.clear();
	}

}
