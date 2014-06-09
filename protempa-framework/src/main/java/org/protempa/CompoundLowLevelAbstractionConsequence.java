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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.CompoundValuedInterval;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.TemporalParameter;
import org.protempa.proposition.AbstractParameterIntervalSectioner;
import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.value.NominalValue;
import org.protempa.proposition.value.Value;

/**
 * Drools consequence for {@link CompoundLowLevelAbstractionDefinition}s.
 */
final class CompoundLowLevelAbstractionConsequence implements
        Consequence {

    private static final long serialVersionUID = 6456351279290509422L;
    private final CompoundLowLevelAbstractionDefinition cllad;
    private final DerivationsBuilder derivationsBuilder;

    /**
     * Constructor.
     *
     * @param def the {@link CompoundLowLevelAbstractionDefinition} this is a
     * consequence for
     * @param derivationsBuilder the {@link DerivationsBuilder} to add asserted
     * propositions to
     */
    CompoundLowLevelAbstractionConsequence(
            CompoundLowLevelAbstractionDefinition def,
            DerivationsBuilder derivationsBuilder) {
        assert def != null : "def cannot be null";
        this.cllad = def;
        this.derivationsBuilder = derivationsBuilder;
    }

    private void assertDerivedProposition(WorkingMemory workingMemory,
            AbstractParameter derived, Set<AbstractParameter> sources) {
        workingMemory.insert(derived);
        for (AbstractParameter parameter : sources) {
            derivationsBuilder.propositionAsserted(parameter, derived);
        }
        ProtempaUtil.logger().log(Level.FINER,
                "Asserted derived proposition {0}", derived);
    }

    private final static class AbstractParameterWithSourceParameters {

        final AbstractParameter parameter;
        final Set<AbstractParameter> sourceParameters;

        public AbstractParameterWithSourceParameters(
                AbstractParameter parameter,
                Set<AbstractParameter> sourcePropositions) {
            this.parameter = parameter;
            this.sourceParameters = sourcePropositions;
        }
    }

    @Override
    public void evaluate(KnowledgeHelper knowledgeHelper,
            WorkingMemory workingMemory) throws Exception {
        @SuppressWarnings("unchecked")
        List<AbstractParameter> pl = (List<AbstractParameter>) knowledgeHelper
                .get(knowledgeHelper.getDeclaration("result"));

        List<CompoundValuedInterval> intervals =
                new AbstractParameterIntervalSectioner()
                .buildIntervalList(pl);

        List<AbstractParameterWithSourceParameters> derivedProps =
                new ArrayList<>();
        for (CompoundValuedInterval interval : intervals) {
            boolean match = false;
            String lastCheckedValue = null;
            for (Entry<String, List<ClassificationMatrixValue>> e : cllad
                    .getValueClassificationsInt().entrySet()) {
                lastCheckedValue = e.getKey();
                switch (cllad.getValueDefinitionMatchOperator()) {
                    case ALL:
                        match = allMatch(interval, e.getValue());
                        break;
                    case ANY:
                        match = anyMatch(interval, e.getValue());
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "valueDefinitionMatchStrategy must by ALL or ANY");
                }
                if (match) {
                    AbstractParameter result = createAbstractParameter(
                            cllad.getPropositionId(),
                            NominalValue.getInstance(e.getKey()),
                            interval.getInterval(), this.cllad.getContextId());
                    derivedProps.add(new AbstractParameterWithSourceParameters(
                            result, interval.getTemporalPropositions()));
                    // found a matching value, so don't look for any more and
                    // move on to the next interval
                    break;
                }
            }
            // if none of the classifications matched and this is an ALL
            // situation, then default to the last classification defined
//            if (!match
//                    && cllad.getValueDefinitionMatchOperator() == ValueDefinitionMatchOperator.ALL
//                    && lastCheckedValue != null) {
//                AbstractParameter result = createAbstractParameter(
//                        cllad.getId(),
//                        NominalValue.getInstance(lastCheckedValue),
//                        interval.getInterval());
//                derivedProps.add(new AbstractParameterWithSourceParameters(
//                        result, interval.getParameters()));
//            }
        }
        if (cllad.getMinimumNumberOfValues() <= 1) {
            // We're matching all intervals, even if not consecutive.
            for (AbstractParameterWithSourceParameters param : derivedProps) {
                assertDerivedProposition(workingMemory, param.parameter,
                        param.sourceParameters);
            }
        } else {
            // if we need to match multiple consecutive values, and if such a
            // match is present, then the new interval ranges from the start of
            // the first match to the end of the last match
            for (int i = 0; i < derivedProps.size();) {
                int rhs = cllad.getMinimumNumberOfValues() - 1;
                if (i + rhs < derivedProps
                        .size()) {
                    if (rangeMatches(derivedProps, i, i + rhs,
                            cllad.getGapFunctionBetweenValues())) {
                        Sequence<TemporalParameter> seq =
                                new Sequence<>(derivedProps.get(i).parameter.getId());
                        for (int k = i; k < i + cllad.getMinimumNumberOfValues(); k++) {
                            seq.add(derivedProps.get(k).parameter);
                        }
                        Segment<TemporalParameter> seg =
                                new Segment<>(seq);
                        AbstractParameter result =
                                AbstractParameterFactory.getFromAbstraction(
                                cllad.getPropositionId(), seg, null,
                                derivedProps.get(i).parameter.getValue(),
                                null, null, cllad.getContextId());
                        assertDerivedProposition(workingMemory, result,
                                derivedProps.get(i).sourceParameters);
                    }
                }
                if (this.cllad.getSkip() > 0) {
                    i += rhs + this.cllad.getSkip();
                } else {
                    i++;
                }
            }
        }
    }

    private boolean allMatch(CompoundValuedInterval multiInterval,
            List<ClassificationMatrixValue> lowLevelValueDefs) {
        return match(multiInterval, lowLevelValueDefs, false);
    }

    private boolean anyMatch(CompoundValuedInterval multiInterval,
            List<ClassificationMatrixValue> lowLevelValueDefs) {
        return match(multiInterval, lowLevelValueDefs, true);
    }

    private boolean match(CompoundValuedInterval multiInterval,
            List<ClassificationMatrixValue> lowLevelValueDefs, boolean bool) {
        for (Entry<String, Value> e : multiInterval.getValues().entrySet()) {
            String id = e.getKey();
            Value val = e.getValue();
            for (ClassificationMatrixValue cmv : lowLevelValueDefs) {
                if (id.equals(cmv.getPropId())) {
                    if (val.equals(cmv.getValue()) == bool) {
                        return bool;
                    }
                }
            }
        }
        return !bool;
    }

    private static AbstractParameter createAbstractParameter(String propId,
            Value value, Interval interval, String contextId) {
        AbstractParameter result = new AbstractParameter(propId);
        result.setInterval(interval);
        result.setValue(value);
        result.setSourceSystem(SourceSystem.DERIVED);
        result.setContextId(contextId);

        return result;
    }

    private boolean rangeMatches(
            List<AbstractParameterWithSourceParameters> propositions,
            int rangeStart, int rangeEnd, GapFunction gf) {
        Value value = propositions.get(rangeStart).parameter.getValue();
        for (int i = rangeStart + 1; i <= rangeEnd; i++) {
            AbstractParameter prop = propositions.get(i).parameter;
            if (!value.equals(prop.getValue())
                    || !gf.execute(propositions.get(i - 1).parameter, prop)) {
                return false;
            }
        }
        return true;
    }
}
