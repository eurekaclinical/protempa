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

import java.io.IOException;
import java.io.ObjectInputStream;

import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Unit;

import org.arp.javautil.graph.Weight;

/**
 * A constraint representing a temporal abstraction interval. When added to a
 * distance graph, two vertices are created, one marking the start of the
 * interval, and the other marking the end of the interval. Edges in between
 * the two represent the length of the interval.
 * 
 * @author Andrew Post
 */
public final class DefaultInterval extends Interval {

    private ConstraintNetwork cn;
    private boolean constraintNetworkStale = true;
    private boolean simple;

    DefaultInterval(Long minStart, Long maxStart,
            Granularity startGranularity, Long minFinish, Long maxFinish,
            Granularity finishGranularity, Long minLength, Long maxLength,
            Unit lengthUnit) {
        super(minStart, maxStart, startGranularity, minFinish, maxFinish,
                finishGranularity, minLength, maxLength, lengthUnit);
    }

    DefaultInterval(Long start, Granularity startGranularity,
            Long finish, Granularity finishGranularity, Long length,
            Unit lengthUnit) {
        super(start, startGranularity, finish, finishGranularity, length,
                lengthUnit);
    }

    DefaultInterval(Long start, Granularity startGranularity,
            Long finish, Granularity finishGranularity) {
        super(start, startGranularity, finish, finishGranularity, null, null);
    }

    DefaultInterval(Long start, Granularity startGranularity) {
        super(start, startGranularity, null, null, null, null);
    }

    /**
     * Create an interval with default values (minimumStart=-inf,
     * maximumStart=+inf, minimumFinish=-inf, maximumFinish=+inf,
     * minimumDuration=0, maximumDuration=+inf).
     *
     * @param description
     */
    DefaultInterval() {
        super();
    }

    private void calculator() {
        if (constraintNetworkStale) {
            computeLength();
            if (cn == null) {
                cn = new ConstraintNetwork(1);
                if (v[0] == null || v[1] == null || v[2] == null
                        || v[3] == null || v[4] != null || v[5] != null
                        || v[2].compareTo(v[1]) <= 0) {
                    cn.addInterval(this);
                } else {
                    simple = true;
                }
            } else {
                cn.clear();
                if (v[0] == null || v[1] == null || v[2] == null
                        || v[3] == null || v[2].compareTo(v[1]) <= 0) {
                    cn.addInterval(this);
                } else {
                    simple = true;
                }
            }
            constraintNetworkStale = false;
        }

    }

    public boolean isValid() {
        calculator();
        return cn.getConsistent();
    }

    /***************************************************************************
     * MINIMUM START
     **************************************************************************/

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.Interval#getMinimumStart()
     */
    @Override
    public Long getMinimumStart() {
        calculator();
        if (simple) {
            return v[0];
        } else {
            Weight minStart = cn.getMinimumStart();
            return minStart.isInfinity() ? null : minStart.value();
        }
    }

    /***************************************************************************
     * MAXIMUM START
     **************************************************************************/

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.Interval#getMaximumStart()
     */
    @Override
    public Long getMaximumStart() {
        calculator();
        if (simple) {
            return v[1];
        } else {
            Weight maxStart = cn.getMaximumStart();
            return maxStart.isInfinity() ? null : maxStart.value();
        }
    }

    /***************************************************************************
     * MINIMUM FINISH
     **************************************************************************/

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.Interval#getMinimumFinish()
     */
    @Override
    public Long getMinimumFinish() {
        calculator();
        if (simple) {
            return v[2];
        } else {
            Weight minFinish = cn.getMinimumFinish();
            return minFinish.isInfinity() ? null : minFinish.value();
        }
    }

    /***************************************************************************
     * MAXIMUM START
     **************************************************************************/

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.Interval#getMaximumFinish()
     */
    @Override
    public Long getMaximumFinish() {
        calculator();
        if (simple) {
            return v[3];
        } else {
            Weight maxFinish = cn.getMaximumFinish();
            return maxFinish.isInfinity() ? null : maxFinish.value();
        }
    }

    /***************************************************************************
     * MINIMUM DURATION
     **************************************************************************/

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.Interval#getMinimumDuration()
     */
    @Override
    public Long getMinimumLength() {
        calculator();
        if (simple) {
            return v[2] - v[1];
        } else {
            Weight minDur = cn.getMinimumDuration();
            return minDur.isInfinity() ? null : minDur.value();
        }
    }

    /***************************************************************************
     * MAXIMUM DURATION
     **************************************************************************/

    /*
     * (non-Javadoc)
     *
     * @see org.virginia.pbhs.parameters.IInterval#getMaximumDuration()
     */
    @Override
    public Long getMaximumLength() {
        calculator();
        if (simple) {
            return v[3] - v[0];
        } else {
            Weight maxDur = cn.getMaximumDuration();
            return maxDur.isInfinity() ? null : maxDur.value();
        }
    }

    private void readObject(ObjectInputStream s) throws IOException,
            ClassNotFoundException {
        s.defaultReadObject();
        this.constraintNetworkStale = true;
    }
}
