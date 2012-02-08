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
package org.protempa;

import org.protempa.proposition.value.Unit;

interface PatternFinderUser {

    /**
     * @return Returns the minimumGap.
     */
    Integer getMinimumGapBetweenValues();

    Unit getMinimumGapBetweenValuesUnits();

    /**
     * Sets the minimum distance between instances of this
     * <code>AbstractionDefinition</code> that are concatenable. The default
     * value is <code>Weight.ZERO</code>.
     * 
     * @param maximumGap
     *            The <code>Weight</code> to set. If <code>null</code>, the
     *            <code>minimumGap</code> is set to the default value.
     */
    void setMinimumGapBetweenValues(Integer minimumGapBetweenValues);

    void setMinimumGapBetweenValuesUnits(Unit minimumGapBetweenValuesUnits);

    /**
     * Returns the minimum distance between instances of this
     * <code>AbstractionDefinition</code> that are concatenable. The default
     * value is <code>Weight.ZERO</code>.
     * 
     * @return a <code>Weight</code> object.
     */
    Integer getMaximumGapBetweenValues();

    Unit getMaximumGapBetweenValuesUnits();

    /**
     * Sets the maximum distance between instances of this
     * <code>AbstractionDefinition</code> that are contatenable. The default
     * value is <code>Weight.POS_INFINITY</code>.
     * 
     * @param maximumGap
     *            The <code>Weight</code> to set. If <code>null</code>, the
     *            <code>maximumGap</code> is set to the default value.
     */
    void setMaximumGapBetweenValues(Integer maximumGapBetweenValues);

    void setMaximumGapBetweenValuesUnits(Unit maximumGapBetweenValuesUnits);

    /**
     * Sets the maximum number of sequential values to process.
     * 
     * @param maximumNumberOfValues
     *            an <code>int</code>. If < 0, use the equivalent field in
     *            the algorithm.
     * @see org.protempa.Algorithm#getMaximumNumberOfValues()
     */
    void setMaximumNumberOfValues(int maximumNumberOfValues);

    /**
     * Sets the maximum number of sequential values to process.
     * 
     * @return an <code>int</code> if < 0, use the equivalent field in the
     *         algorithm.
     * @see org.protempa.Algorithm#getMaximumNumberOfValues()
     */
    int getMaximumNumberOfValues();

    /**
     * Sets the minimum number of sequential values to process.
     * 
     * @param minimumNumberOfValues
     *            an <code>int</code>. If < 0, use the equivalent field in
     *            the algorithm.
     * @see org.protempa.Algorithm#getMinimumNumberOfValues()
     */
    void setMinimumNumberOfValues(int l);

    /**
     * Gets the minimum number of sequential values to process.
     * 
     * @return an <code>int</code> if < 0, use the equivalent field in the
     *         algorithm.
     * @see org.protempa.Algorithm#getMinimumNumberOfValues()
     */
    int getMinimumNumberOfValues();

    /**
     * Enable the skip-start restart search directive. After a match, all future
     * segments will have a starting value of at least <code>arg</code> more
     * than the starting value of the matched segment.
     * 
     * @param arg
     *            the value of the skip-start search directive, must be > 0.
     */
    void setSkipStart(int arg);

    /**
     * Disable the skip-start restart search directive.
     */
    void unsetSkipStart();

    /**
     * Return the value of the skip-start restart search directive.
     * 
     * @return the value of the skip-start restart search directive, -1 if it is
     *         disabled.
     */
    int getSkipStart();

    /**
     * Enable the skip-end restart search directive. After a match, the next
     * segment will have an ending value of <code>arg</code> more than the
     * ending value of the matched segment.
     * 
     * @param arg
     *            the value of the skip-end search directive, must be > 0.
     */
    void setSkipEnd(int arg);

    /**
     * Disable the skip-end restart search directive.
     */
    void unsetSkipEnd();

    /**
     * Return the value of the skip-end restart search directive.
     * 
     * @return the value of the skip-end restart search directive, -1 if it is
     *         disabled.
     */
    int getSkipEnd();

    /**
     * Enable the skip restart search directive. After a match, the next segment
     * will have a starting and ending value of <code>arg</code> more than the
     * ending value of the matched segment.
     * 
     * @param arg
     *            the value of the skip restart search directive, must be > 0.
     */
    void setSkip(int arg);

    /**
     * Disable the skip restart search directive.
     */
    void unsetSkip();

    /**
     * Return the value of the skip restart search directive.
     * 
     * @return the value of the skip restart search directive, -1 if it is
     *         disabled.
     */
    int getSkip();

    /**
     * Enable the max-overlapping restart search directive. After a match, the
     * next segment will have a starting and ending value of
     * <code>arg - 1</code> less than the ending value of the matched segment.
     * 
     * @param arg
     *            the value of the max-overlapping restart search directive,
     *            must be > 0.
     */
    void setMaxOverlapping(int arg);

    /**
     * Disable the max-overlapping restart search directive.
     */
    void unsetMaxOverlapping();

    /**
     * Return the value of the max-overlapping restart search directive.
     * 
     * @return the value of the max-overlapping restart serach directive, -1 if
     *         it is disabled.
     */
    int getMaxOverlapping();

    Integer getMinimumDuration();

    Unit getMinimumDurationUnits();

    Integer getMaximumDuration();

    Unit getMaximumDurationUnits();

    String getId();

    /**
     * Gets the sliding window width mode. The default value is
     * {@link SlidingWindowWidthMode.DEFAULT}.
     * 
     * @return a {@link SlidingWindowWidthMode} object (will never be
     *         <code>null</code>).
     */
    SlidingWindowWidthMode getSlidingWindowWidthMode();
}
