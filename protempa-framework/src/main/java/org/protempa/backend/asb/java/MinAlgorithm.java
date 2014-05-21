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
package org.protempa.backend.asb.java;

import org.protempa.AbstractAlgorithm;
import org.protempa.AlgorithmArguments;
import org.protempa.Algorithms;
import org.protempa.AlgorithmParameter;
import org.protempa.proposition.Parameter;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Segment;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.Value;
import org.protempa.proposition.value.ValueComparator;
import org.protempa.proposition.value.ValueType;

/**
 * Determines if the minimum value of a segment is at most a defined threshold.
 * The threshold is specified as the <code>minThreshold</code> parameter.
 * 
 * @author Andrew Post
 */
public final class MinAlgorithm extends AbstractAlgorithm {

    private static final long serialVersionUID = 6131613237861460023L;

    public MinAlgorithm(Algorithms algorithms, String id) {
        super(algorithms, id);
        setParameters(new AlgorithmParameter[]{
                    new AlgorithmParameter("maxThreshold", new ValueComparator[]{
                        ValueComparator.LESS_THAN,
                        ValueComparator.LESS_THAN_OR_EQUAL_TO,
                        ValueComparator.EQUAL_TO}, ValueType.NUMERICALVALUE),
                    new AlgorithmParameter("minThreshold", new ValueComparator[]{
                        ValueComparator.GREATER_THAN,
                        ValueComparator.GREATER_THAN_OR_EQUAL_TO,
                        ValueComparator.EQUAL_TO}, ValueType.NUMERICALVALUE)});
        setMinimumNumberOfValues(-1);
    }

    @Override
    public Value compute(Segment<PrimitiveParameter> segment,
            AlgorithmArguments args) {
        Value minVal = null;
        Value minThreshold = args.value("minThreshold");
        ValueComparator minThresholdComp = args.valueComp("minThreshold");
        Value maxThreshold = args.value("maxThreshold");
        ValueComparator maxThresholdComp = args.valueComp("maxThreshold");

        // Calculate minVal.
        for (int i = 0, n = segment.size(); i < n; i++) {
            Parameter param = segment.get(i);
            Value val = param.getValue();
            if (minVal == null
                    || minVal.compare(val) == ValueComparator.GREATER_THAN) {
                minVal = val;
            }
        }

        if (minVal != null
                && (minThresholdComp == null || minThreshold == null || minThresholdComp.compare(minVal, minThreshold))
                && (maxThresholdComp == null || maxThreshold == null || maxThresholdComp.compare(minVal, maxThreshold))) {
            return BooleanValue.TRUE;
        } else {
            return null;
        }
    }
}
