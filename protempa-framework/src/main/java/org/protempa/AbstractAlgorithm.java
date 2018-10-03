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

import org.protempa.backend.dsb.filter.Filter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Segment;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueType;

/**
 * Provides a skeletal implementation of the <code>Algorithm</code> interface.
 * 
 * @author Andrew Post
 */
public abstract class AbstractAlgorithm implements Algorithm {

    private static final long serialVersionUID = 6566132392629105106L;
    private static final Logger LOGGER = Logger.getLogger(AbstractAlgorithm.class.getName());

    private ValueType inValueType;
    private String id;
    private AlgorithmParameter[] parameters;
    private final Map<String, AlgorithmParameter> parametersMap;
    private int advanceRowSkipEnd;
    private int minNumVal;
    private int maxNumVal;

    public AbstractAlgorithm(Algorithms algorithms, String id) {
        this.id = computeId(algorithms, id, false);
        this.parametersMap = new HashMap<>();
        this.advanceRowSkipEnd = -1;
        if (algorithms != null) {
            algorithms.addAlgorithm(this);
        }
        this.minNumVal = 1;
        this.maxNumVal = Integer.MAX_VALUE;
    }

    private static String computeId(Algorithms algorithms, String id,
            boolean fail) {
        if (id == null || id.length() == 0) {
            return algorithms.getNextAlgorithmObjectId();
        } else if (!algorithms.isUniqueAlgorithmObjectId(id)) {
            if (fail) {
                throw new IllegalArgumentException("id " + id
                        + " is not unique.");
            } else {
                return algorithms.getNextAlgorithmObjectId();
            }
        } else {
            return id;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.Algorithm#getId()
     */
    @Override
    public final String getId() {
        return id;
    }

    /**
     * Sets the default minimum sliding window width. May be overridden by low
     * level abstraction definitions. If negative, the algorithm will process an
     * entire sequence at once by default up to
     * <code>maximumNumberOfValues</code>.
     * 
     * @param val
     */
    public final void setMinimumNumberOfValues(int val) {
        if (val == 0) {
            throw new IllegalArgumentException("val cannot be 0");
        }
        this.minNumVal = val;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.Algorithm#getMinimumNumberOfValues()
     */
    @Override
    public final int getMinimumNumberOfValues() {
        return this.minNumVal;
    }

    /**
     * Sets the default maximum sliding window width. May be overridden by low
     * level abstraction definitions.
     * 
     * @param val
     *            an <code>int</code> > 0.
     * @throws IllegalArgumentException
     *             if val is < 1.
     */
    public final void setMaximumNumberOfValues(int val) {
        if (val < 1) {
            throw new IllegalArgumentException("val cannot be < 1");
        }
        this.maxNumVal = val;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.Algorithm#getMaximumNumberOfValues()
     */
    @Override
    public final int getMaximumNumberOfValues() {
        return this.maxNumVal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.Algorithm#close()
     */
    @Override
    public void close() {
    }

    @Override
    protected void finalize() {
        try {
            close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Could not finalize " + toString(), e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.Algorithm#getInValueType()
     */
    @Override
    public final ValueType getInValueType() {
        return inValueType;
    }

    public final void setInValueType(ValueType inValueType) {
        this.inValueType = inValueType;
    }

    public void setParameters(AlgorithmParameter[] parameters) {
        this.parameters = parameters;
        this.parametersMap.clear();
        if (parameters != null) {
            for (AlgorithmParameter p : parameters) {
                this.parametersMap.put(p.getName(), p);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.Algorithm#getParameters()
     */
    @Override
    public AlgorithmParameter[] getParameters() {
        return this.parameters;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.Algorithm#parameter(java.lang.String)
     */
    @Override
    public AlgorithmParameter parameter(String name) {
        return parametersMap.get(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.Algorithm#createDataSourceConstraint()
     */
    @Override
    public Filter createDataSourceConstraint() {
        return null;
    }

    protected void setAdvanceRowSkipEnd(int advanceRowSkipEnd) {
        this.advanceRowSkipEnd = advanceRowSkipEnd;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.protempa.Algorithm#getAdvanceRowSkipEnd()
     */
    @Override
    public int getAdvanceRowSkipEnd() {
        return advanceRowSkipEnd;
    }

    @Override
    public String toString() {
        return "Algorithm " + getId();
    }

    @Override
    public void initialize(AlgorithmArguments arguments)
            throws AlgorithmInitializationException {
    }

    @Override
    public abstract Value compute(Segment<PrimitiveParameter> segment,
            AlgorithmArguments arguments) throws AlgorithmProcessingException;
}
