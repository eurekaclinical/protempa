package org.protempa.proposition;

import org.protempa.proposition.value.Granularity;

/**
 * An Interval for completely specified minimum and maximum starts and finishes,
 * and 0 duration.
 * 
 * @author Andrew Post
 */
final class SimpleInterval extends Interval {

    SimpleInterval(Long minStart, Long maxStart,
            Granularity startGranularity, Long minFinish, Long maxFinish,
            Granularity finishGranularity) {
        super(minStart, maxStart, startGranularity, minFinish, maxFinish,
                finishGranularity, null, null, null);
        if (minStart == null) {
            throw new IllegalArgumentException("minStart cannot be null");
        }
        if (maxStart == null) {
            throw new IllegalArgumentException("maxStart cannot be null");
        }
        if (minFinish == null) {
            throw new IllegalArgumentException("minFinish cannot be null");
        }
        if (maxFinish == null) {
            throw new IllegalArgumentException("maxFinish cannot be null");
        }
    }

    SimpleInterval(Long start, Granularity startGranularity, Long finish,
            Granularity finishGranularity) {
        super(start, startGranularity, finish, finishGranularity, null, null);
        if (start == null) {
            throw new IllegalArgumentException("start cannot be null");
        }
        if (finish == null) {
            throw new IllegalArgumentException("finish cannot be null");
        }
    }

    SimpleInterval(Long timestamp, Granularity granularity) {
        super(timestamp, granularity, timestamp, granularity, null, null);
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp cannot be null");
        }
    }
}
