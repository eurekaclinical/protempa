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


import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.value.Unit;

/**
 * @author Andrew Post
 */
public final class LowLevelAbstractionFinder {

    private static final HorizontalTemporalInference HTI =
            new HorizontalTemporalInference();

    /**
     * Private constructor.
     */
    private LowLevelAbstractionFinder() {
        super();
    }

    /**
     * Return the first segment of the given sequence to be searched by the
     * given <code>PatternFinderUser</code>. If the minimum and maximum
     * pattern lengths of the <code>PatternFinderUser</code> are unset, the
     * first segment will be the entire sequence.
     *
     * @param def
     *            a <code>PatternFinderUser</code> object.
     * @param sequence
     *            a <code>Sequence</code> of <code>PrimitiveParameter</code>
     *            objects.
     * @return a <code>Segment</code> of <code>PrimitiveParameter</code>
     *         objects.
     */
    private static Segment<PrimitiveParameter> firstSegment(
            PatternFinderUser def, Sequence<PrimitiveParameter> sequence,
            Algorithm algorithm, int maxPatternLength) {
        int size = sequence.size();
        int minPatternLength = minPatternLength(algorithm, def);
        if (size > 0 && size >= minPatternLength) {
            int x = 0;
            int y = minPatternLength < 1 ? size - 1 : minPatternLength - 1;
            if (x <= y) {
                return resetSegmentHelper(def, sequence, x, y,
                        new Segment<PrimitiveParameter>(sequence, x, y),
                        algorithm, maxPatternLength);
            }
        }
        return null;
    }

    /**
     * Return the next segment to be searched if the most recently searched
     * segment matched this rule. The returned segment is calculated according
     * to the this rule's search parameters.
     *
     * @param ts
     *            the sequence being searched.
     * @param seg
     *            the most recently searched segment.
     * @param lastMatch
     *            the most recent segment to match this rule.
     * @return the next segment that should be searched, or null if there are no
     *         more segments to search.
     */
    private static Segment<PrimitiveParameter> nextSegmentAfterMatch(
            PatternFinderUser def, Segment<PrimitiveParameter> seg,
            Algorithm algorithm, int minPatternLength, int maxPatternLength) {
        if (seg == null) {
            return null;
        }

        int arg;
        int x = seg.getFirstIndex();
        int y = seg.getLastIndex();

        Segment<PrimitiveParameter> nextSeg = null;
        if ((arg = def.getMaxOverlapping()) > 0) {
            int myCurrentColumn = y - (arg + 1);
            if (myCurrentColumn - 1 > x) {
                nextSeg = resetSegment(def, seg, myCurrentColumn, getYIndex(
                        def, myCurrentColumn, seg), algorithm,
                        minPatternLength, maxPatternLength);
            } else {
                nextSeg = resetSegment(def, seg, getXIndex(def, x, seg),
                        getYIndex(def, y + 1, seg), algorithm,
                        minPatternLength, maxPatternLength);
            }
        } else {
            nextSeg = resetSegment(def, seg, getXIndex(def, x, seg), getYIndex(
                    def, y + 1, seg), algorithm, minPatternLength,
                    maxPatternLength);
        }

        return nextSeg;
    }

    private static Segment<PrimitiveParameter> resetSegment(
            PatternFinderUser def, Segment<PrimitiveParameter> seg, int x,
            int y, Algorithm algorithm, int minPatternLength,
            int maxPatternLength) {
        if (minPatternLength < 1) {
            return null;
        } else {
            y = Math.max(y, x + minPatternLength - 1);
            Sequence<PrimitiveParameter> seq = seg.getSequence();
            seg = seg.resetState(seq, x, y);
            return resetSegmentHelper(def, seq, x, y, seg, algorithm,
                    maxPatternLength);
        }
    }

    private static int maxPatternLength(Algorithm algorithm,
            PatternFinderUser def) {
        switch (def.getSlidingWindowWidthMode()) {
            case RANGE:
                return def.getMaximumNumberOfValues();
            case ALL:
                return Integer.MAX_VALUE;
            default:
                return algorithm.getMaximumNumberOfValues();
        }
    }

    private static int minPatternLength(Algorithm algorithm,
            PatternFinderUser def) {
        switch (def.getSlidingWindowWidthMode()) {
            case RANGE:
                return def.getMinimumNumberOfValues();
            case ALL:
                return -1;
            default:
                return algorithm.getMinimumNumberOfValues();
        }
    }

    /**
     * @param def
     * @param sequence
     * @param y
     * @param seg
     * @return
     */
    private static Segment<PrimitiveParameter> resetSegmentHelper(
            PatternFinderUser def, Sequence<PrimitiveParameter> sequence,
            int x, int y, Segment<PrimitiveParameter> seg, Algorithm algorithm,
            int maxPatternLength) {
        Interval segIval = null;
        int yMinusXPlusOne;
        if (seg != null) {
            segIval = seg.getInterval();
            yMinusXPlusOne = y - x + 1;
        } else {
            yMinusXPlusOne = 0;
        }
        int minDur = def.getMinimumDuration();
        Unit minDurUnits = def.getMinimumDurationUnits();
        Integer maxDur = def.getMaximumDuration();
        Unit maxDurUnits = def.getMaximumDurationUnits();
        while (segIval != null
                && segIval.isLengthLessThan(minDur, minDurUnits)) {
            y++;
            yMinusXPlusOne++;
            if (yMinusXPlusOne > maxPatternLength) {
                seg = null;
            } else {
                seg = seg.resetState(sequence, x, y);
            }
            if (seg != null) {
                segIval = seg.getInterval();
            } else {
                segIval = null;
            }
        }
        if (seg != null
                && segIval != null
                && ((maxDur != null && segIval.isLengthGreaterThan(maxDur,
                maxDurUnits)) || yMinusXPlusOne > maxPatternLength)) {
            seg = null;
        }

        return seg;
    }

    /**
     * Return the first value of the next segment to be searched.
     *
     * @param x
     *            the first value of the most recently searched segment.
     * @param lastMatch
     *            the most recent segment to match this rule.
     * @return the first value of the next segment to search.
     */
    private static int getXIndex(PatternFinderUser def, int x,
            Segment<PrimitiveParameter> lastMatch) {
        int skipStart = def.getSkipStart();
        if (lastMatch != null && skipStart > 0) {
            return Math.max(x, lastMatch.getFirstIndex() + skipStart);
        } else if (lastMatch != null && def.getSkip() > 0) {
            return Math.max(x, lastMatch.getLastIndex() + def.getSkip());
        } else {
            return x;
        }
    }

    /**
     * Return the last value of the next segment to be searched.
     *
     * @param y
     *            the last value of the most recently searched segment.
     * @param lastMatch
     *            the most recent segment to match this rule.
     * @return the last value of the next segment to search.
     */
    private static int getYIndex(PatternFinderUser def, int y,
            Segment<PrimitiveParameter> lastMatch) {
        int skipEnd = def.getSkipEnd();
        if (lastMatch != null && skipEnd > 0) {
            return Math.max(y, lastMatch.getLastIndex() + skipEnd);
        } else if (lastMatch != null && def.getSkip() > 0) {
            return Math.max(y, lastMatch.getLastIndex() + def.getSkip());
        } else {
            return y;
        }
    }

    private static int advanceRowSearchDirectives(Algorithm algorithm,
            Segment<PrimitiveParameter> seg,
            Segment<PrimitiveParameter> lastMatch) {
        if (algorithm != null && lastMatch != null
                && seg.getFirstIndex() == lastMatch.getFirstIndex()
                && algorithm.getAdvanceRowSkipEnd() >= 0) {
            return lastMatch.getLastIndex() + algorithm.getAdvanceRowSkipEnd()
                    - 1;
        } else {
            return seg.getFirstIndex();
        }
    }

    private static Segment<PrimitiveParameter> advanceRow(
            PatternFinderUser def, Segment<PrimitiveParameter> seg,
            Segment<PrimitiveParameter> lastMatch, Algorithm algorithm,
            int minPatternLength, int maxPatternLength) {
        if (seg == null || minPatternLength < 1) {
            return null;
        }
        int x = advanceRowSearchDirectives(algorithm, seg, lastMatch);
        int seqSizeMinus1 = seg.getSequence().size() - 1;
        Segment<PrimitiveParameter> nextSeg = null;
        while (nextSeg == null && x < seqSizeMinus1) {
            x++;
            nextSeg = resetSegment(def, seg, getXIndex(def, x, lastMatch),
                    getYIndex(def, x, lastMatch), algorithm, minPatternLength,
                    maxPatternLength);
        }

        return nextSeg;
    }

    static void process(Sequence<PrimitiveParameter> seq,
            LowLevelAbstractionDefinition def, Algorithm algorithm,
            ObjectAsserter objAsserter, DerivationsBuilder derivationsBuilder)
            throws AlgorithmInitializationException,
            AlgorithmProcessingException {
        if (def == null || seq == null) {
            return;
        }
        int minPatternLength = minPatternLength(algorithm, def);
        int maxPatternLength = maxPatternLength(algorithm, def);
        Segment<PrimitiveParameter> seg = firstSegment(def, seq, algorithm,
                maxPatternLength);

        String id = def.getPropositionId();
        GapFunction gf = def.getGapFunction();

        if (seg != null) {
            Segment<PrimitiveParameter> lastSeg = null;
            LowLevelAbstractionValueDefinition prevFoundValue = null;
            LowLevelAbstractionValueDefinition foundValue = null;
            do {
                if ((foundValue = def.satisfiedBy(seg, algorithm)) != null) {
                    Segment<PrimitiveParameter> nextSeg = null;
                    do {
                        if (lastSeg != null
                                && foundValue.equals(prevFoundValue)
                                && (HTI.execute(def, lastSeg, seg)
                                || gf.execute(lastSeg, seg))) {
                            lastSeg.resetState(seq,
                                    Math.min(lastSeg.getFirstIndex(),
                                    seg.getFirstIndex()),
                                    Math.max(lastSeg.getLastIndex(),
                                    seg.getLastIndex()));
                        } else {
                            if (lastSeg != null) {
                                Proposition proposition = AbstractParameterFactory.getFromAbstraction(id,
                                        lastSeg, null, prevFoundValue.getValue(),
                                        null, null, def.getContextId());
                                
                                objAsserter.assertObject(proposition);
                                for (Proposition prop : lastSeg) {
                                    derivationsBuilder.propositionAsserted(prop, proposition);
                                }
                            }
                            lastSeg = new Segment<PrimitiveParameter>(seg);
                        }
                        prevFoundValue = foundValue;
                    } while ((nextSeg = nextSegmentAfterMatch(def, seg,
                            algorithm, minPatternLength, maxPatternLength)) != null
                            && (foundValue =
                            def.satisfiedBy(nextSeg, algorithm)) != null);
                }
            } while (advanceRow(def, seg, lastSeg, algorithm, minPatternLength,
                    maxPatternLength) != null);
            if (lastSeg != null) {
                Proposition proposition = 
                        AbstractParameterFactory.getFromAbstraction(id,
                        lastSeg, null, prevFoundValue.getValue(), null, null,
                        def.getContextId());
                
                objAsserter.assertObject(proposition);
                for (Proposition prop : lastSeg) {
                    derivationsBuilder.propositionAsserted(prop, proposition);
                }
                
                lastSeg = null;
            }
        }
    }
}
