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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Segment;
import org.protempa.proposition.value.Unit;
import org.protempa.proposition.value.ValueType;

/**
 * Definition of low-level abstraction.
 * 
 * @author Andrew Post
 */
public final class LowLevelAbstractionDefinition 
        extends AbstractAbstractionDefinition implements PatternFinderUser,
        ParameterDefinition {

    private static final long serialVersionUID = -3829737731539676713L;
    private final Set<String> paramIds;
    private Integer minimumDuration;
    private Unit minimumDurationUnits;
    private Integer maximumDuration;
    private Unit maximumDurationUnits;
    private final List<LowLevelAbstractionValueDefinition> valueDefinitions;
    private ValueType valueType;
    private String algorithmId;
    private String contextId;
    
    /**
     * Stores the value of the skip-start restart search directive. Value must
     * be > 0, -1 if disabled.
     */
    private int skipStart;
    /**
     * Stores the value of the skip-end restart search directive. Value must be >
     * 0, -1 if disabled. The default is for segments after a match to always
     * extend past the last match by at least 1 element.
     */
    private int skipEnd;
    /**
     * Stores the value of the skip restart search directive. Value must be > 0,
     * -1 if disabled.
     */
    private int skip;
    /**
     * Stores the value of the max-overlapping restart search directive. Value
     * must be > 0, -1 if disabled.
     */
    private int maxOverlapping;
    private SlidingWindowWidthMode slidingWindowWidthMode;
    /**
     * Stores the maximum number of sequential values to process.
     */
    private int maximumNumberOfValues;
    /**
     * Stores the maximum number of sequential values to process.
     */
    private int minimumNumberOfValues;
    private boolean concatenable;
    private final MinMaxGapBetweenValues gapBtwValues;
    private static final ValueType DEFAULT_VALUE_TYPE = ValueType.NOMINALVALUE;

    public LowLevelAbstractionDefinition(String id) {
        super(id);
        this.paramIds = new LinkedHashSet<>(1);
        this.valueDefinitions = new ArrayList<>(3);
        this.valueType = DEFAULT_VALUE_TYPE;
        this.gapBtwValues = new MinMaxGapBetweenValues();
        this.slidingWindowWidthMode = SlidingWindowWidthMode.ALL;
        this.maximumNumberOfValues = Integer.MAX_VALUE;
        this.minimumNumberOfValues = 1;
        this.maxOverlapping = -1;
        this.skip = -1;
        this.skipEnd = 1;
        this.skipStart = -1;
        this.minimumDuration = 0;
        this.concatenable = true;
    }

    @Override
    public final Integer getMinimumGapBetweenValues() {
        return this.gapBtwValues.getMinimumGapBetweenValues();
    }

    @Override
    public final Unit getMinimumGapBetweenValuesUnits() {
        return this.gapBtwValues.getMinimumGapBetweenValuesUnits();
    }

    @Override
    public final void setMinimumGapBetweenValues(Integer minimumGapBetweenValues) {
        this.gapBtwValues.setMinimumGapBetweenValues(minimumGapBetweenValues);
    }

    @Override
    public final void setMinimumGapBetweenValuesUnits(Unit units) {
        this.gapBtwValues.setMinimumGapBetweenValuesUnits(units);
    }

    @Override
    public final Integer getMaximumGapBetweenValues() {
        return this.gapBtwValues.getMaximumGapBetweenValues();
    }

    @Override
    public final Unit getMaximumGapBetweenValuesUnits() {

        return this.gapBtwValues.getMaximumGapBetweenValuesUnits();
    }

    @Override
    public final void setMaximumGapBetweenValues(Integer maximumGapBetweenValues) {
        this.gapBtwValues.setMaximumGapBetweenValues(maximumGapBetweenValues);
    }

    @Override
    public final void setMaximumGapBetweenValuesUnits(Unit units) {
        this.gapBtwValues.setMaximumGapBetweenValuesUnits(units);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.protempa.PatternFinderUser#setMaximumNumberOfValues(int)
     */
    @Override
    public final void setMaximumNumberOfValues(int l) {
        if (l < 1) {
            throw new IllegalArgumentException(
                    "maximumNumberOfValues must be > 0, but was " + l);
        }
        this.maximumNumberOfValues = l;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.protempa.PatternFinderUser#getMaximumNumberOfValues()
     */
    @Override
    public final int getMaximumNumberOfValues() {
        return this.maximumNumberOfValues;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.protempa.PatternFinderUser#setMinimumNumberOfValues(int)
     */
    @Override
    public final void setMinimumNumberOfValues(int l) {
        if (l < 1) {
            throw new IllegalArgumentException(
                    "minimumNumberOfValues must be > 0, but was " + l);
        }
        this.minimumNumberOfValues = l;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.protempa.PatternFinderUser#getMinimumNumberOfValues()
     */
    @Override
    public final int getMinimumNumberOfValues() {
        return this.minimumNumberOfValues;
    }

    /**
     * Enable the skip-start restart search directive. After a match, the next
     * segment will have a starting value of <code>arg</code> more than the
     * starting value of the matched segment.
     *
     * @param arg
     *            the value of the skip-start search directive, must be > 0.
     */
    @Override
    public final void setSkipStart(int arg) {
        skipStart = arg;
    }

    /**
     * Disable the skip-start restart search directive.
     */
    @Override
    public final void unsetSkipStart() {
        skipStart = -1;
    }

    /**
     * Return the value of the skip-start restart search directive.
     *
     * @return the value of the skip-start restart search directive, -1 if it is
     *         disabled.
     */
    @Override
    public final int getSkipStart() {
        return skipStart;
    }

    /**
     * Enable the skip-end restart search directive. After a match, the next
     * segment will have an ending value of <code>arg</code> more than the
     * ending value of the matched segment.
     *
     * @param arg
     *            the value of the skip-end search directive, must be > 0.
     */
    @Override
    public final void setSkipEnd(int arg) {
        skipEnd = arg;
    }

    /**
     * Disable the skip-end restart search directive.
     */
    @Override
    public final void unsetSkipEnd() {
        skipEnd = -1;
    }

    /**
     * Return the value of the skip-end restart search directive.
     *
     * @return the value of the skip-end restart search directive, -1 if it is
     *         disabled.
     */
    @Override
    public final int getSkipEnd() {
        return skipEnd;
    }

    /**
     * Enable the skip restart search directive. After a match, the next segment
     * will have a starting and ending value of <code>arg</code> more than the
     * ending value of the matched segment.
     *
     * @param arg
     *            the value of the skip restart search directive, must be > 0.
     */
    @Override
    public final void setSkip(int arg) {
        skip = arg;
    }

    /**
     * Disable the skip restart search directive.
     */
    @Override
    public final void unsetSkip() {
        skip = -1;
    }

    /**
     * Return the value of the skip restart search directive.
     *
     * @return the value of the skip restart search directive, -1 if it is
     *         disabled.
     */
    @Override
    public final int getSkip() {
        return skip;
    }

    /**
     * Enable the max-overlapping restart search directive. After a match, the
     * next segment will have a starting and ending value of
     * <code>arg - 1</code> less than the ending value of the matched segment.
     *
     * @param arg
     *            the value of the max-overlapping restart search directive,
     *            must be > 0.
     */
    @Override
    public final void setMaxOverlapping(int arg) {
        maxOverlapping = arg;
    }

    /**
     * Disable the max-overlapping restart search directive.
     */
    @Override
    public final void unsetMaxOverlapping() {
        maxOverlapping = -1;
    }

    /**
     * Return the value of the max-overlapping restart search directive.
     *
     * @return the value of the max-overlapping restart serach directive, -1 if
     *         it is disabled.
     */
    @Override
    public final int getMaxOverlapping() {
        return maxOverlapping;
    }

    /**
     * Gets the sliding window width mode. The default value is
     * {@link SlidingWindowWidthMode.ALL}.
     *
     * @return a {@link SlidingWindowWidthMode} object (will never be
     *         <code>null</code>).
     */
    @Override
    public SlidingWindowWidthMode getSlidingWindowWidthMode() {
        return slidingWindowWidthMode;
    }

    /**
     * Sets the sliding window width mode. If the <code>algorithmId</code> is
     * <code>null</code>, a value of {@link SlidingWindowWidthMode.DEFAULT}
     * is not permitted.
     *
     * @param slidingWindowWidthMode
     *            a {@link SlidingWindowWidthMode}. Cannot be <code>null</code>.
     */
    public void setSlidingWindowWidthMode(
            SlidingWindowWidthMode slidingWindowWidthMode) {
        if (slidingWindowWidthMode == null) {
            throw new IllegalArgumentException(
                    "slidingWindowWidthMode cannot be null");
        }
        if (slidingWindowWidthMode == SlidingWindowWidthMode.DEFAULT
                && this.algorithmId == null) {
            throw new IllegalArgumentException(
                    "slidingWindowWidthMode cannot be DEFAULT when no algorithm has been specified.");
        }

        this.slidingWindowWidthMode = slidingWindowWidthMode;
    }

    public boolean addValueDefinition(LowLevelAbstractionValueDefinition d) {
        return this.valueDefinitions.add(d);
    }

    public List<LowLevelAbstractionValueDefinition> getValueDefinitions() {
        return Collections.unmodifiableList(this.valueDefinitions);
    }

    public LowLevelAbstractionValueDefinition[] getValueDefinitions(String id) {
        List<LowLevelAbstractionValueDefinition> result = new ArrayList<>();
        for (int i = 0, n = this.valueDefinitions.size(); i < n; i++) {
            LowLevelAbstractionValueDefinition def = (LowLevelAbstractionValueDefinition) valueDefinitions.get(i);
            if (def.getId().equals(id)) {
                result.add(def);
            }
        }
        return result.toArray(new LowLevelAbstractionValueDefinition[result.size()]);
    }

    /**
     * Adds a primitive parameter from which this abstraction is inferred.
     *
     * @param paramId
     *            a primitive parameter id <code>String</code>.
     * @return <code>true</code> if adding the parameter id was successful,
     *         <code>false</code> otherwise (e.g., <code>paramId</code> was
     *         <code>null</code>).
     */
    public boolean addPrimitiveParameterId(String paramId) {
        boolean result = this.paramIds.add(paramId);
        if (result) {
            recalculateChildren();
        }
        return result;
    }

    /**
     * Removes a primitive parameter from which this abstraction is inferred.
     *
     * @param paramId
     *            a primitive parameter id <code>String</code>.
     * @return <code>true</code> if removingg the parameter id was successful,
     *         <code>false</code> otherwise (e.g., <code>paramId</code> was
     *         <code>null</code> or not previously added to this abstraction
     *         definition).
     */
    public boolean removePrimitiveParameterId(String paramId) {
        boolean result = this.paramIds.remove(paramId);
        if (result) {
            recalculateChildren();
        }
        return result;
    }

    @Override
    public Set<String> getAbstractedFrom() {
        return java.util.Collections.unmodifiableSet(this.paramIds);
    }

    public void setValueType(ValueType c) {
        if (c == null) {
            c = DEFAULT_VALUE_TYPE;
        }
        ValueType old = this.valueType;
        this.valueType = c;
    }

    @Override
    public ValueType getValueType() {
        return this.valueType;
    }

    @Override
    public void accept(PropositionDefinitionVisitor processor) {
        if (processor == null) {
            throw new IllegalArgumentException("processor cannot be null.");
        }
        processor.visit(this);
    }

    @Override
    public void acceptChecked(PropositionDefinitionCheckedVisitor processor)
            throws ProtempaException {
        if (processor == null) {
            throw new IllegalArgumentException("processor cannot be null.");
        }
        processor.visit(this);
    }

    public final void setMinimumDuration(Integer minDuration) {
        if (minDuration == null || minDuration.compareTo(0) < 0) {
            this.minimumDuration = 0;
        } else {
            this.minimumDuration = minDuration;
        }
    }

    public final void setMinimumDurationUnits(Unit minDurationUnits) {
        this.minimumDurationUnits = minDurationUnits;
    }

    public final void setMaximumDuration(Integer maxDuration) {
        if (maxDuration != null && maxDuration.compareTo(0) < 0) {
            this.maximumDuration = 0;
        } else {
            this.maximumDuration = maxDuration;
        }
    }

    public final void setMaximumDurationUnits(Unit maxDurationUnits) {
        this.maximumDurationUnits = maxDurationUnits;
    }

    @Override
    public final Integer getMinimumDuration() {
        return this.minimumDuration;
    }

    @Override
    public final Unit getMinimumDurationUnits() {
        return this.minimumDurationUnits;
    }

    @Override
    public final Integer getMaximumDuration() {
        return this.maximumDuration;
    }

    @Override
    public final Unit getMaximumDurationUnits() {
        return this.maximumDurationUnits;
    }

    public String getAlgorithmId() {
        return algorithmId;
    }

    public void setAlgorithmId(String algorithmId) {
        String old = this.algorithmId;
        this.algorithmId = algorithmId;
    }

    @Override
    public void reset() {
        super.reset();
        setAlgorithmId(null);
        this.paramIds.clear();
        this.minimumDuration = 0;
        this.maximumDuration = null;
        this.valueDefinitions.clear();
        setValueType(null);
        recalculateChildren();
    }

    private boolean satisfiesGapBetweenValues(
            Segment<PrimitiveParameter> segment) {
        PrimitiveParameter eprev = segment.get(0);
        for (int i = 1, n = segment.size(); i < n; i++) {
            PrimitiveParameter e = segment.get(i);
            if (!this.gapBtwValues.satisfiesGap(eprev, e)) {
                return false;
            }
            eprev = e;
        }
        return true;
    }

    /**
     * Test whether or not the given time series satisfies the constraints of
     * this detector and an optional algorithm. If no algorithm is specified,
     * then this test just uses the detector's constraints.
     *
     * @param segment
     *            a time series <code>Segment</code>.
     * @param algorithm
     *            an <code>Algorithm</code>, or <code>null</code> to
     *            specify no algorithm.
     * @return <code>true</code> if the time series segment satisfies the
     *         constraints of this detector, <code>false</code> otherwise
     * @throws AlgorithmProcessingException
     * @throws AlgorithmInitializationException
     */
    final LowLevelAbstractionValueDefinition satisfiedBy(
            Segment<PrimitiveParameter> segment, Algorithm algorithm)
            throws AlgorithmInitializationException,
            AlgorithmProcessingException {
        if (satisfiesGapBetweenValues(segment)) {
            for (LowLevelAbstractionValueDefinition valueDef : this.valueDefinitions) {
                if (valueDef.satisfiedBy(segment, algorithm)) {
                    return valueDef;
                }
            }
        }
        return null;
    }

    /**
     * Sets whether this type of low-level abstraction is concatenable.
     *
     * @param concatenable
     *            <code>true</code> if concatenable, <code>false</code> if
     *            not.
     */
    public void setConcatenable(boolean concatenable) {
        this.concatenable = concatenable;
    }

    /**
     * Returns whether this type of low-level abstraction is concatenable.
     *
     * The default for low-level abstractions is concatenable.
     *
     * @return <code>true</code> if concatenable, <code>false</code> if not.
     */
    @Override
    public boolean isConcatenable() {
        return this.concatenable;
    }

    /**
     * Returns whether intervals of this type are solid, i.e., never hold over
     * properly overlapping intervals. By definition, low-level abstraction 
     * intervals are solid.
     *
     * @return <code>true</code>.
     */
    @Override
    public boolean isSolid() {
        return true;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }
    
    

    @Override
    protected void recalculateChildren() {
        String[] old = this.children;
        this.children = this.paramIds.toArray(new String[this.paramIds.size()]);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }
}
