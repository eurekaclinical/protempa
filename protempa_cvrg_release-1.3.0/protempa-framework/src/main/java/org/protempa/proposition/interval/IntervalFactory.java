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
package org.protempa.proposition.interval;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.ReferenceMap;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.Granularity;

/**
 * A factory for creating {@link Interval} objects. A subclass of
 * {@link Interval} will be returned that is optimized for the arguments
 * that are provided to this factory's <code>getInstance</code> methods.
 * 
 * @author Andrew Post
 */
public final class IntervalFactory {

    private static class DefaultIntervalContainer {

        private static Interval defaultInterval = new DefaultInterval();
    }
    @SuppressWarnings("unchecked")
    private static final Map<List<Object>, Interval> cache =
            new ReferenceMap();

    /**
     * Returns an interval specified by the given minimum start, maximum start,
     * minimum finish and maximum finish and corresponding granularities.
     * The interpretation of the starts and finishes depends on the
     * {@link Granularity} implementations specifiied by the
     * <code>startGran</code> and <code>finishGran</code> arguments. For
     * example, if <code>startGran</code> is an instance of 
     * {@link AbsoluteTimeGranularity}, <code>minStart</code> and
     * <code>maxStart</code> are interpreted as date/times.
     *
     * @param minStart the earliest start of the interval, represented as a
     * {@link Long}. If <code>null</code>, the earliest start will be
     * unbounded.
     * @param maxStart the latest start of the interval, represented as a
     * {@link Long}. if <code>null</code>, the latest start will be unbounded.
     * @param startGran the {@link Granularity} of the <code>minStart</code>
     * and <code>maxStart</code>.
     * @param minFinish the earliest finish of the interval, represented as a
     * {@link Long}. If <code>null</code>, the earliest finish will be
     * unbounded.
     * @param maxFinish the latest finish of the interval, represented as a
     * {@link Long}. If <code>null</code>, the latest finish will be
     * unbounded.
     * @param finishGran the {@link Granularity} of the <code>minFinish</code>
     * and <code>maxFinish</code>.
     * @return an {@link Interval}.
     */
    public Interval getInstance(Long minStart, Long maxStart,
            Granularity startGran, Long minFinish, Long maxFinish,
            Granularity finishGran) {
        List<Object> key = Arrays.asList(new Object[]{minStart, maxStart,
                    startGran, minFinish, maxFinish, finishGran});
        Interval result = cache.get(key);
        if (result == null) {
            if (minStart == null || maxStart == null || minFinish == null
                    || maxFinish == null) {
                result = new DefaultInterval(minStart, maxStart, startGran,
                        minFinish, maxFinish, finishGran, null, null, null);
            } else {
                result = new SimpleInterval(minStart, maxStart,
                        startGran, minFinish, maxFinish, finishGran);
            }
            cache.put(key, result);
        }
        return result;
    }

    /**
     * Returns at interval specified by the given start and finish and
     * granularities.
     *
     * @param start a {@link Long} representing the start of the interval. If
     * <code>null</code>, the <code>start</code> will be unbounded.
     * @param startGran the {@link Granularity} of the start of the interval.
     * The <code>start</code> parameter's interpretation depends on the
     * {@link Granularity} implementation provided.
     * @param finish a {@link Long} representing the finish of the interval.
     * If <code>null</code>, the <code>finish</code> will be unbounded.
     * @param finishGran the {@link Granularity} of the finish of the interval.
     * The <code>start</code> parameter's interpretation depends on the
     * {@link Granularity} implementation provided.
     * @return an {@link Interval}.
     */
    public Interval getInstance(Long start, Granularity startGran,
            Long finish, Granularity finishGran) {
        List<Object> key = Arrays.asList(new Object[]{start, startGran,
                    finish, finishGran});
        Interval result = cache.get(key);
        if (result == null) {
            if (start == null || finish == null) {
                result = new DefaultInterval(start, startGran, finish, finishGran);
            } else {
                result = new SimpleInterval(start, startGran, finish, finishGran);
            }
            cache.put(key, result);
        }
        return result;
    }

    /**
     * Returns an interval representing a position on the timeline or other
     * axis at a specified granularity. The interpretation of the
     * <code>position</code> parameter depends on what implementation of
     * {@link Granularity} is provided. For example, if the granularity
     * implementation is {@link AbsoluteTimeGranularity}, the
     * <code>position</code> is intepreted as a timestamp.
     *
     * @param position a {@ling Long} representing a single position on the
     * timeline or other axis. If <code>null</code>, the interval will be
     * unbounded.
     * @param gran a {@link Granularity}.
     * @return an {@link Interval}.
     */
    public Interval getInstance(Long position, Granularity gran) {
        List<Object> key = Arrays.asList(new Object[]{position, gran});
        Interval result = cache.get(key);
        if (result == null) {
            if (position == null) {
                result = new DefaultInterval(position, gran, position, gran);
            } else {
                result = new SimpleInterval(position, gran);
            }
            cache.put(key, result);
        }
        return result;
    }

    /**
     * Returns an unbounded interval.
     *
     * @return an {@link Interval}.
     */
    public Interval getInstance() {
        return DefaultIntervalContainer.defaultInterval;
    }
}
