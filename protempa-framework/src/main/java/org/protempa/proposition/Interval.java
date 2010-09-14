package org.protempa.proposition;

import java.io.Serializable;
import java.util.Arrays;

import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Unit;

import org.arp.javautil.graph.Weight;

/**
 * A representation of an interval, designed according to the definition of an
 * interval in Combi et al. Managing Different Time Granularities of Clinical
 * Information by an Interval-based Temporal Data Model. Meth. Inf. Med.
 * 1995;34:458-74. This representation is also designed to be used in simple
 * temporal problems, as defined in Dechter, R. et al. Temporal Constraint
 * Networks. Artif. Intell. 1991;49:61-95.
 * 
 * @author Andrew Post
 */
public abstract class Interval implements Comparable<Interval>, Serializable {

    private static final long serialVersionUID = -8876433661943628691L;
    private final Start start;
    private final Finish finish;
    protected final Long[] v;
    private Weight[] vw;
    private volatile int hashCode;
    private Long minStart;
    private Long maxStart;
    private Granularity startGranularity;
    private Long minFinish;
    private Long maxFinish;
    private Granularity finishGranularity;
    private Long minLength;
    private Long maxLength;
    private Unit lengthUnit;
    private boolean minLengthComputed;
    private boolean maxLengthComputed;

    /**
     * Create an interval with default values (minimumStart=-inf,
     * maximumStart=+inf, minimumFinish=-inf, maximumFinish=+inf,
     * minimumDuration=0, maximumDuration=+inf).
     *
     * @param description
     */
    Interval() {
        this(null, null, null, null, null, null);
    }

    Interval(Long start, Granularity startGranularity, Long finish,
            Granularity finishGranularity, Long length, Unit lengthUnit) {
        this(start, start, startGranularity, finish, finish, finishGranularity,
                length, length, lengthUnit);
    }

    Interval(Long minStart, Long maxStart, Granularity startGranularity,
            Long minFinish, Long maxFinish, Granularity finishGranularity,
            Long minLength, Long maxLength, Unit distanceUnit) {
        if (minStart != null && maxFinish != null && minStart > maxFinish) {
            throw new IllegalArgumentException(
                    "maxFinish cannot be before minStart; maxFinish="
                    + maxFinish + "; minStart=" + minStart);
        }
        if (minLength != null && minLength < 0) {
            throw new IllegalArgumentException(
                    "minLength must be positive or 0 but was " + minLength);
        }
        if (maxLength != null && maxLength < 0) {
            throw new IllegalArgumentException(
                    "maxLength must be positive or 0 but was " + maxLength);
        }

        this.start = new Start(this);
        this.finish = new Finish(this);

        Long w0 = null;
        Long w1 = null;
        Long w2 = null;
        Long w3 = null;
        Long w4 = 0L;
        Long w5 = null;

        if (startGranularity != null) {
            if (minStart != null) {
                w0 = minStart;
            }
            if (maxStart != null) {
                w1 = startGranularity.latest(maxStart);
            }
        } else {
            if (minStart != null) {
                w0 = minStart;
            }
            if (maxStart != null) {
                w1 = maxStart;
            }
        }

        if (finishGranularity != null) {
            if (minFinish != null) {
                w2 = minFinish;
            }
            if (maxFinish != null) {
                w3 = finishGranularity.latest(maxFinish);
            }
        } else {
            if (minFinish != null) {
                w2 = minFinish;
            }
            if (maxFinish != null) {
                w3 = maxFinish;
            }
        }

        Granularity g1 = null;
        if (startGranularity != null && finishGranularity != null) {
            if (startGranularity.compareTo(finishGranularity) < 0) {
                g1 = startGranularity;
            } else {
                g1 = finishGranularity;
            }
        } else if (startGranularity != null) {
            g1 = startGranularity;
        } else {
            g1 = finishGranularity;
        }
        if (minLength != null) {
            if (g1 != null) {
                w4 = g1.minimumDistance(maxStart, minLength, distanceUnit);
            } else if (minFinish != null && maxStart != null) {
                w4 = minLength.longValue();
            }
            this.minLengthComputed = true;
        }
        if (maxLength != null) {
            if (g1 != null) {
                w5 = g1.maximumDistance(minStart, maxLength, distanceUnit);
            } else if (maxFinish != null && minStart != null) {
                w5 = minLength.longValue();
            }
            this.maxLengthComputed = true;
        }

        this.minStart = minStart;
        this.maxStart = maxStart;
        this.startGranularity = startGranularity;
        this.minFinish = minFinish;
        this.maxFinish = maxFinish;
        this.finishGranularity = finishGranularity;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.lengthUnit = distanceUnit;

        v = new Long[]{w0, w1, w2, w3, w4, w5};
        if ((v[0] != null && v[1] != null && v[0].compareTo(v[1]) > 0)
                || (v[2] != null && v[3] != null && v[2].compareTo(v[3]) > 0)
                || (v[4] != null && v[5] != null && v[4].compareTo(v[5]) > 0)) {
            throw new IllegalArgumentException("Illegal values for interval");
        }
    }

    protected void computeLength() {
        if (!this.minLengthComputed || !this.maxLengthComputed) {
            Long w4 = 0L;
            Long w5 = null;
            Granularity g1 = null;
            Granularity g2 = null;
            if (startGranularity != null && finishGranularity != null) {
                if (startGranularity.compareTo(finishGranularity) < 0) {
                    g1 = startGranularity;
                    g2 = finishGranularity;
                } else {
                    g1 = finishGranularity;
                    g2 = startGranularity;
                }
            } else if (startGranularity != null) {
                g1 = startGranularity;
            } else {
                g1 = finishGranularity;
            }
            if (!this.minLengthComputed) {
                if (this.minLength == null) {
                    if (g1 != null) {
                        lengthUnit = g1.getCorrespondingUnit();
                        if (maxStart != null && minFinish != null) {
                            minLength = g1.distance(maxStart, minFinish, g2,
                                    lengthUnit);
                        }
                    } else if (minFinish != null && maxStart != null) {
                        minLength = minFinish - maxStart;
                    }
                }
                if (g1 != null) {
                    if (maxStart != null && minLength != null) {
                        w4 = g1.minimumDistance(maxStart, minLength,
                                lengthUnit);
                    }
                } else if (minFinish != null && maxStart != null) {
                    w4 = minLength.longValue();
                }
                minLengthComputed = true;
            }
            if (!this.maxLengthComputed) {
                if (this.maxLength == null) {
                    if (g1 != null) {
                        lengthUnit = g1.getCorrespondingUnit();
                        if (minStart != null && maxFinish != null) {
                            maxLength = g1.distance(minStart, maxFinish, g2,
                                    lengthUnit);
                        }

                    } else if (maxFinish != null && minStart != null) {
                        maxLength = maxFinish - minStart;
                    }
                }
                if (g1 != null) {
                    if (minStart != null && maxLength != null) {
                        w5 = g1.maximumDistance(minStart, maxLength,
                                lengthUnit);
                    }
                } else if (maxFinish != null && minStart != null) {
                    w5 = minLength.longValue();
                }
                this.maxLengthComputed = true;
            }
            this.v[4] = w4;
            this.v[5] = w5;
        }
    }

    public Long getMinimumStart() {
        return v[0];
    }

    public Long getMaximumStart() {
        return v[1];
    }

    public Long getMinimumFinish() {
        return v[2];
    }

    public Long getMaximumFinish() {
        return v[3];
    }

    public Long getMinimumLength() {
        computeLength();
        return v[4];
    }

    public Long getMaximumLength() {
        computeLength();
        return v[5];
    }

    public void reset() {
        v[0] = null;
        v[1] = null;
        v[2] = null;
        v[3] = null;
        v[4] = 0L;
        v[5] = null;
    }

    private void initVw() {
        this.vw = new Weight[]{
                    v[0] != null ? new Weight(v[0]) : Weight.NEG_INFINITY,
                    v[1] != null ? new Weight(v[1]) : Weight.POS_INFINITY,
                    v[2] != null ? new Weight(v[2]) : Weight.NEG_INFINITY,
                    v[3] != null ? new Weight(v[3]) : Weight.POS_INFINITY,
                    v[4] != null ? new Weight(v[4]) : Weight.ZERO,
                    v[5] != null ? new Weight(v[5]) : Weight.POS_INFINITY};
    }

    Weight getSpecifiedMinimumStart() {
        if (vw == null) {
            initVw();
        }
        return vw[0];
    }

    Weight getSpecifiedMaximumStart() {
        if (vw == null) {
            initVw();
        }
        return vw[1];
    }

    Weight getSpecifiedMinimumFinish() {
        if (vw == null) {
            initVw();
        }
        return vw[2];
    }

    Weight getSpecifiedMaximumFinish() {
        if (vw == null) {
            initVw();
        }
        return vw[3];
    }

    Weight getSpecifiedMinimumLength() {
        if (vw == null) {
            initVw();
        }
        return vw[4];
    }

    Weight getSpecifiedMaximumLength() {
        if (vw == null) {
            initVw();
        }
        return vw[5];
    }

    /**
     * Get the start vertex of this interval.
     *
     * @return
     */
    Start getStart() {
        return start;
    }

    /**
     * Get the finish vertex of this interval.
     *
     * @return
     */
    Finish getFinish() {
        return finish;
    }

    public boolean isLengthGreaterThan(int duration, Unit durationUnits) {
        if (durationUnits == null || getMinimumStart() == null
                || getMinimumFinish() == null) {
            return getMinimumStart() + duration < getMinimumFinish();
        } else {
            return !Relation.isGreaterThanOrEqualToDuration(durationUnits,
                    getMinimumStart(), getMinimumFinish(), duration);
        }
    }

    public boolean isLengthLessThan(int duration, Unit durationUnits) {
        if (durationUnits == null || getMaximumStart() == null
                || getMaximumFinish() == null) {
            return getMaximumStart() + duration > getMaximumFinish();
        } else {
            return !Relation.isLessThanOrEqualToDuration(durationUnits, this.getMaximumStart(), getMaximumFinish(), duration);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Arrays.asList(v).toString();
    }

    static final class Start implements Serializable {

        private static final long serialVersionUID = 8966134801723368830L;
        private final Interval interval;
        private volatile int hashCode;

        Start(Interval interval) {
            this.interval = interval;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            if (this.hashCode == 0) {
                int result = 17;
                result = 37 * result + this.interval.hashCode();
                this.hashCode = result;
            }
            return hashCode;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "" + interval + " start";
        }
    }

    static final class Finish implements Serializable {

        private static final long serialVersionUID = -3475866103582928640L;
        private final Interval interval;
        private volatile int hashCode;

        Finish(Interval interval) {
            this.interval = interval;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            if (this.hashCode == 0) {
                int result = 17;
                result = 37 * result + this.interval.hashCode();
                result = 37 * result;
                this.hashCode = result;
            }
            return hashCode;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "" + interval + " finish";
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Interval)) {
            return false;
        }

        Interval otherIval = (Interval) other;
        return Arrays.equals(this.v, otherIval.v);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int result = 17;
            result = 37 * result + Arrays.hashCode(this.v);
            this.hashCode = result;
        }

        return this.hashCode;
    }

    public final int compareTo(Interval o) {
        if (this == o) {
            return 0;
        }

        Long start1 = this.getMinimumStart();
        Long finish1 = this.getMaximumFinish();
        Long start2 = o.getMinimumStart();
        Long finish2 = o.getMaximumFinish();

        int result;
        if (start1 == null && start2 != null) {
            result = -1;
        } else if (start1 != null && start2 == null) {
            result = 1;
        } else if (start1 == null && start2 == null) {
            result = 0;
        } else {
            result = start1.compareTo(start2);
        }
        if (result != 0) {
            return result;
        }
        if (finish1 == null && finish2 != null) {
            return 1;
        } else if (finish1 != null && finish2 == null) {
            return -1;
        } else if (finish1 == null && finish2 == null) {
            return 0;
        } else {
            return finish1.compareTo(finish2);
        }
    }

    /**
     * @return the minStart
     */
    public Long getMinStart() {
        return minStart;
    }

    /**
     * @return the maxStart
     */
    public Long getMaxStart() {
        return maxStart;
    }

    /**
     * @return the startGranularity
     */
    public Granularity getStartGranularity() {
        return startGranularity;
    }

    /**
     * @return the minFinish
     */
    public Long getMinFinish() {
        return minFinish;
    }

    /**
     * @return the maxFinish
     */
    public Long getMaxFinish() {
        return maxFinish;
    }

    /**
     * @return the finishGranularity
     */
    public Granularity getFinishGranularity() {
        return finishGranularity;
    }

    /**
     * @return the minDistance
     */
    public Long getMinLength() {
        computeLength();
        return minLength;
    }

    public Long minLengthIn(Unit unit) {
        if (unit != null) {
            Long maxStart = getMaxStart();
            Long minFinish = getMinFinish();
            if (maxStart == null || minFinish == null) {
                return null;
            }

            Granularity startGran = getStartGranularity();
            if (startGran != null) {
                return startGran.distance(maxStart, minFinish,
                        getFinishGranularity(), unit);
            } else {
                Granularity finishGran = getFinishGranularity();
                if (finishGran != null) {
                    return finishGran.distance(maxStart, minFinish, null, unit);
                } else {
                    return minFinish - maxStart;
                }
            }
        } else {
            return getMinLength();
        }
    }

    public Long maxLengthIn(Unit unit) {
        if (unit != null) {
            Long minStart = getMinStart();
            Long maxFinish = getMaxFinish();
            if (minStart == null || maxFinish == null) {
                return null;
            }
            Granularity startGran = getStartGranularity();
            if (startGran != null) {
                return startGran.distance(minStart, maxFinish,
                        getFinishGranularity(), unit);
            } else {
                Granularity finishGran = getFinishGranularity();
                if (finishGran != null) {
                    return finishGran.distance(minStart, maxFinish, null, unit);
                } else {
                    return maxFinish - minStart;
                }
            }
        } else {
            return getMaxLength();
        }
    }

    /**
     * @return the maxDistance
     */
    public Long getMaxLength() {
        computeLength();
        return maxLength;
    }

    /**
     * @return the distanceUnit
     */
    public Unit getLengthUnit() {
        computeLength();
        return lengthUnit;
    }
}
