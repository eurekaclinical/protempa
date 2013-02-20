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
package org.protempa.proposition.interval;

import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Unit;

/**
 *
 * @author Andrew Post
 */
public class IntervalUtil {

    private IntervalUtil() {
    }
    
    public static long distanceBetween(Interval firstIval,
            Interval secondIval) {
        return distanceBetween(firstIval, secondIval, null);
    }
    
    public static long distanceBetween(Interval firstIval, Interval secondIval, 
            Unit units) {
        if (firstIval == null) {
            throw new IllegalArgumentException("firstIval cannot be null");
        }
        if (secondIval == null) {
            throw new IllegalArgumentException("secondIval cannot be null");
        }
        Granularity gran = firstIval.getFinishGranularity();
        if (units == null) {
            units = gran.getCorrespondingUnit();
        }
        return gran.distance(firstIval.getMinFinish(), 
                secondIval.getMinStart(), gran, units);
    }

    public static String distanceBetweenFormattedShort(Interval firstIval,
            Interval secondIval) {
        return distanceBetweenFormattedShort(firstIval, secondIval, null);
    }

    public static String distanceBetweenFormattedShort(Interval firstIval,
            Interval secondIval, Unit units) {
        if (firstIval == null) {
            throw new IllegalArgumentException("firstIval cannot be null");
        }
        if (secondIval == null) {
            throw new IllegalArgumentException("secondIval cannot be null");
        }
        Granularity gran = firstIval.getFinishGranularity();
        if (units == null) {
            units = gran.getCorrespondingUnit();
        }
        long distance = gran.distance(firstIval.getMinFinish(),
                secondIval.getMinStart(), gran, units);
        return units.getShortFormat().format(distance);
    }
    
    public static String distanceBetweenFormattedMedium(Interval firstIval,
            Interval secondIval) {
        return distanceBetweenFormattedMedium(firstIval, secondIval, null);
    }

    public static String distanceBetweenFormattedMedium(Interval firstIval,
            Interval secondIval, Unit units) {
        if (firstIval == null) {
            throw new IllegalArgumentException("firstIval cannot be null");
        }
        if (secondIval == null) {
            throw new IllegalArgumentException("secondIval cannot be null");
        }
        Granularity gran = firstIval.getFinishGranularity();
        if (units == null) {
            units = gran.getCorrespondingUnit();
        }
        long distance = gran.distance(firstIval.getMinFinish(),
                secondIval.getMinStart(), gran, units);
        return units.getMediumFormat().format(distance);
    }
    
    public static String distanceBetweenFormattedLong(Interval firstIval,
            Interval secondIval) {
        return distanceBetweenFormattedLong(firstIval, secondIval, null);
    }

    public static String distanceBetweenFormattedLong(Interval firstIval,
            Interval secondIval, Unit units) {
        if (firstIval == null) {
            throw new IllegalArgumentException("firstIval cannot be null");
        }
        if (secondIval == null) {
            throw new IllegalArgumentException("secondIval cannot be null");
        }
        Granularity gran = firstIval.getFinishGranularity();
        if (units == null) {
            units = gran.getCorrespondingUnit();
        }
        long distance = gran.distance(firstIval.getMinFinish(),
                secondIval.getMinStart(), gran, units);
        return units.getLongFormat().format(distance);
    }
}
